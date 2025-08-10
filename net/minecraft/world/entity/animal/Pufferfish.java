/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import java.util.List;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Pufferfish
extends AbstractFish {
    private static final EntityDataAccessor<Integer> PUFF_STATE = SynchedEntityData.defineId(Pufferfish.class, EntityDataSerializers.INT);
    int inflateCounter;
    int deflateTimer;
    private static final TargetingConditions.Selector SCARY_MOB = ($$0, $$1) -> {
        Player $$2;
        if ($$0 instanceof Player && ($$2 = (Player)$$0).isCreative()) {
            return false;
        }
        return !$$0.getType().is(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
    };
    static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(SCARY_MOB);
    public static final int STATE_SMALL = 0;
    public static final int STATE_MID = 1;
    public static final int STATE_FULL = 2;
    private static final int DEFAULT_PUFF_STATE = 0;

    public Pufferfish(EntityType<? extends Pufferfish> $$0, Level $$1) {
        super((EntityType<? extends AbstractFish>)$$0, $$1);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(PUFF_STATE, 0);
    }

    public int getPuffState() {
        return this.entityData.get(PUFF_STATE);
    }

    public void setPuffState(int $$0) {
        this.entityData.set(PUFF_STATE, $$0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (PUFF_STATE.equals($$0)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("PuffState", this.getPuffState());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.setPuffState(Math.min($$0.getIntOr("PuffState", 0), 2));
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PufferfishPuffGoal(this));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
            if (this.inflateCounter > 0) {
                if (this.getPuffState() == 0) {
                    this.makeSound(SoundEvents.PUFFER_FISH_BLOW_UP);
                    this.setPuffState(1);
                } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
                    this.makeSound(SoundEvents.PUFFER_FISH_BLOW_UP);
                    this.setPuffState(2);
                }
                ++this.inflateCounter;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTimer > 60 && this.getPuffState() == 2) {
                    this.makeSound(SoundEvents.PUFFER_FISH_BLOW_OUT);
                    this.setPuffState(1);
                } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
                    this.makeSound(SoundEvents.PUFFER_FISH_BLOW_OUT);
                    this.setPuffState(0);
                }
                ++this.deflateTimer;
            }
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$0 = (ServerLevel)level;
            if (this.isAlive() && this.getPuffState() > 0) {
                List<Mob> $$12 = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3), $$1 -> TARGETING_CONDITIONS.test($$0, this, (LivingEntity)$$1));
                for (Mob $$2 : $$12) {
                    if (!$$2.isAlive()) continue;
                    this.touch($$0, $$2);
                }
            }
        }
    }

    private void touch(ServerLevel $$0, Mob $$1) {
        int $$2 = this.getPuffState();
        if ($$1.hurtServer($$0, this.damageSources().mobAttack(this), 1 + $$2)) {
            $$1.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * $$2, 0), this);
            this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0f, 1.0f);
        }
    }

    @Override
    public void playerTouch(Player $$0) {
        int $$1 = this.getPuffState();
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$2 = (ServerPlayer)$$0;
            if ($$1 > 0 && $$0.hurtServer($$2.level(), this.damageSources().mobAttack(this), 1 + $$1)) {
                if (!this.isSilent()) {
                    $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0f));
                }
                $$0.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * $$1, 0), this);
            }
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.PUFFER_FISH_FLOP;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose $$0) {
        return super.getDefaultDimensions($$0).scale(Pufferfish.getScale(this.getPuffState()));
    }

    private static float getScale(int $$0) {
        switch ($$0) {
            case 1: {
                return 0.7f;
            }
            case 0: {
                return 0.5f;
            }
        }
        return 1.0f;
    }

    static class PufferfishPuffGoal
    extends Goal {
        private final Pufferfish fish;

        public PufferfishPuffGoal(Pufferfish $$0) {
            this.fish = $$0;
        }

        @Override
        public boolean canUse() {
            List<LivingEntity> $$02 = this.fish.level().getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0), $$0 -> TARGETING_CONDITIONS.test(PufferfishPuffGoal.getServerLevel(this.fish), this.fish, (LivingEntity)$$0));
            return !$$02.isEmpty();
        }

        @Override
        public void start() {
            this.fish.inflateCounter = 1;
            this.fish.deflateTimer = 0;
        }

        @Override
        public void stop() {
            this.fish.inflateCounter = 0;
        }
    }
}

