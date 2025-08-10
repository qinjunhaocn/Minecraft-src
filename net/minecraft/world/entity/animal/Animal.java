/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class Animal
extends AgeableMob {
    protected static final int PARENT_AGE_AFTER_BREEDING = 6000;
    private static final int DEFAULT_IN_LOVE_TIME = 0;
    private int inLove = 0;
    @Nullable
    private EntityReference<ServerPlayer> loveCause;

    protected Animal(EntityType<? extends Animal> $$0, Level $$1) {
        super((EntityType<? extends AgeableMob>)$$0, $$1);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0f);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f);
    }

    public static AttributeSupplier.Builder createAnimalAttributes() {
        return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10.0);
    }

    @Override
    protected void customServerAiStep(ServerLevel $$0) {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        super.customServerAiStep($$0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double $$0 = this.random.nextGaussian() * 0.02;
                double $$1 = this.random.nextGaussian() * 0.02;
                double $$2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$0, $$1, $$2);
            }
        }
    }

    @Override
    protected void actuallyHurt(ServerLevel $$0, DamageSource $$1, float $$2) {
        this.resetLove();
        super.actuallyHurt($$0, $$1, $$2);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if ($$1.getBlockState($$0.below()).is(Blocks.GRASS_BLOCK)) {
            return 10.0f;
        }
        return $$1.getPathfindingCostFromLightLevels($$0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putInt("InLove", this.inLove);
        EntityReference.store(this.loveCause, $$0, "LoveCause");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.inLove = $$0.getIntOr("InLove", 0);
        this.loveCause = EntityReference.read($$0, "LoveCause");
    }

    public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> $$0, LevelAccessor $$1, EntitySpawnReason $$2, BlockPos $$3, RandomSource $$4) {
        boolean $$5 = EntitySpawnReason.ignoresLightRequirements($$2) || Animal.isBrightEnoughToSpawn($$1, $$3);
        return $$1.getBlockState($$3.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && $$5;
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter $$0, BlockPos $$1) {
        return $$0.getRawBrightness($$1, 0) > 8;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return false;
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel $$0) {
        return 1 + this.random.nextInt(3);
    }

    public abstract boolean isFood(ItemStack var1);

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (this.isFood($$2)) {
            int $$3 = this.getAge();
            if ($$0 instanceof ServerPlayer) {
                ServerPlayer $$4 = (ServerPlayer)$$0;
                if ($$3 == 0 && this.canFallInLove()) {
                    this.usePlayerItem($$0, $$1, $$2);
                    this.setInLove($$4);
                    this.playEatingSound();
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
            if (this.isBaby()) {
                this.usePlayerItem($$0, $$1, $$2);
                this.ageUp(Animal.getSpeedUpSecondsWhenFeeding(-$$3), true);
                this.playEatingSound();
                return InteractionResult.SUCCESS;
            }
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract($$0, $$1);
    }

    protected void playEatingSound() {
    }

    protected void usePlayerItem(Player $$0, InteractionHand $$1, ItemStack $$2) {
        int $$3 = $$2.getCount();
        UseRemainder $$4 = $$2.get(DataComponents.USE_REMAINDER);
        $$2.consume(1, $$0);
        if ($$4 != null) {
            ItemStack $$5 = $$4.convertIntoRemainder($$2, $$3, $$0.hasInfiniteMaterials(), $$0::handleExtraItemsCreatedOnUse);
            $$0.setItemInHand($$1, $$5);
        }
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable Player $$0) {
        this.inLove = 600;
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$1 = (ServerPlayer)$$0;
            this.loveCause = new EntityReference<ServerPlayer>($$1);
        }
        this.level().broadcastEntityEvent(this, (byte)18);
    }

    public void setInLoveTime(int $$0) {
        this.inLove = $$0;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        return EntityReference.get(this.loveCause, this.level()::getPlayerByUUID, ServerPlayer.class);
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(Animal $$0) {
        if ($$0 == this) {
            return false;
        }
        if ($$0.getClass() != this.getClass()) {
            return false;
        }
        return this.isInLove() && $$0.isInLove();
    }

    public void spawnChildFromBreeding(ServerLevel $$0, Animal $$1) {
        AgeableMob $$2 = this.getBreedOffspring($$0, $$1);
        if ($$2 == null) {
            return;
        }
        $$2.setBaby(true);
        $$2.snapTo(this.getX(), this.getY(), this.getZ(), 0.0f, 0.0f);
        this.finalizeSpawnChildFromBreeding($$0, $$1, $$2);
        $$0.addFreshEntityWithPassengers($$2);
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel $$0, Animal $$1, @Nullable AgeableMob $$22) {
        Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable($$1.getLoveCause())).ifPresent($$2 -> {
            $$2.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger((ServerPlayer)$$2, this, $$1, $$22);
        });
        this.setAge(6000);
        $$1.setAge(6000);
        this.resetLove();
        $$1.resetLove();
        $$0.broadcastEntityEvent(this, (byte)18);
        if ($$0.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            $$0.addFreshEntity(new ExperienceOrb($$0, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 18) {
            for (int $$1 = 0; $$1 < 7; ++$$1) {
                double $$2 = this.random.nextGaussian() * 0.02;
                double $$3 = this.random.nextGaussian() * 0.02;
                double $$4 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$2, $$3, $$4);
            }
        } else {
            super.handleEntityEvent($$0);
        }
    }
}

