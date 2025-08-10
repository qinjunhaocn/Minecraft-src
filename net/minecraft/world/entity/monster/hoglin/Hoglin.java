/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.world.entity.monster.hoglin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Hoglin
extends Animal
implements Enemy,
HoglinBase {
    private static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(Hoglin.class, EntityDataSerializers.BOOLEAN);
    private static final int MAX_HEALTH = 40;
    private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3f;
    private static final int ATTACK_KNOCKBACK = 1;
    private static final float KNOCKBACK_RESISTANCE = 0.6f;
    private static final int ATTACK_DAMAGE = 6;
    private static final float BABY_ATTACK_DAMAGE = 0.5f;
    private static final boolean DEFAULT_IMMUNE_TO_ZOMBIFICATION = false;
    private static final int DEFAULT_TIME_IN_OVERWORLD = 0;
    private static final boolean DEFAULT_CANNOT_BE_HUNTED = false;
    public static final int CONVERSION_TIME = 300;
    private int attackAnimationRemainingTicks;
    private int timeInOverworld = 0;
    private boolean cannotBeHunted = false;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Hoglin>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT, SensorType.HOGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.PACIFIED, MemoryModuleType.IS_PANICKING);

    public Hoglin(EntityType<? extends Hoglin> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.xpReward = 5;
    }

    @VisibleForTesting
    public void setTimeInOverworld(int $$0) {
        this.timeInOverworld = $$0;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.KNOCKBACK_RESISTANCE, 0.6f).add(Attributes.ATTACK_KNOCKBACK, 1.0).add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean doHurtTarget(ServerLevel $$0, Entity $$1) {
        void $$3;
        if (!($$1 instanceof LivingEntity)) {
            return false;
        }
        LivingEntity $$2 = (LivingEntity)$$1;
        this.attackAnimationRemainingTicks = 10;
        this.level().broadcastEntityEvent(this, (byte)4);
        this.makeSound(SoundEvents.HOGLIN_ATTACK);
        HoglinAi.onHitTarget(this, (LivingEntity)$$3);
        return HoglinBase.hurtAndThrowTarget($$0, this, (LivingEntity)$$3);
    }

    @Override
    protected void blockedByItem(LivingEntity $$0) {
        if (this.isAdult()) {
            HoglinBase.throwTarget(this, $$0);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
        Entity entity;
        boolean $$3 = super.hurtServer($$0, $$1, $$2);
        if ($$3 && (entity = $$1.getEntity()) instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)entity;
            HoglinAi.wasHurtBy($$0, this, $$4);
        }
        return $$3;
    }

    protected Brain.Provider<Hoglin> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> $$0) {
        return HoglinAi.makeBrain(this.brainProvider().makeBrain($$0));
    }

    public Brain<Hoglin> getBrain() {
        return super.getBrain();
    }

    @Override
    protected void customServerAiStep(ServerLevel $$0) {
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("hoglinBrain");
        this.getBrain().tick($$0, this);
        $$1.pop();
        HoglinAi.updateActivity(this);
        if (this.isConverting()) {
            ++this.timeInOverworld;
            if (this.timeInOverworld > 300) {
                this.makeSound(SoundEvents.HOGLIN_CONVERTED_TO_ZOMBIFIED);
                this.finishConversion();
            }
        } else {
            this.timeInOverworld = 0;
        }
    }

    @Override
    public void aiStep() {
        if (this.attackAnimationRemainingTicks > 0) {
            --this.attackAnimationRemainingTicks;
        }
        super.aiStep();
    }

    @Override
    protected void ageBoundaryReached() {
        if (this.isBaby()) {
            this.xpReward = 3;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5);
        } else {
            this.xpReward = 5;
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0);
        }
    }

    public static boolean checkHoglinSpawnRules(EntityType<Hoglin> $$0, LevelAccessor $$1, EntitySpawnReason $$2, BlockPos $$3, RandomSource $$4) {
        return !$$1.getBlockState($$3.below()).is(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, EntitySpawnReason $$2, @Nullable SpawnGroupData $$3) {
        if ($$0.getRandom().nextFloat() < 0.2f) {
            this.setBaby(true);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.isPersistenceRequired();
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        if (HoglinAi.isPosNearNearestRepellent(this, $$0)) {
            return -1.0f;
        }
        if ($$1.getBlockState($$0.below()).is(Blocks.CRIMSON_NYLIUM)) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        InteractionResult $$2 = super.mobInteract($$0, $$1);
        if ($$2.consumesAction()) {
            this.setPersistenceRequired();
        }
        return $$2;
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 4) {
            this.attackAnimationRemainingTicks = 10;
            this.makeSound(SoundEvents.HOGLIN_ATTACK);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    @Override
    public int getAttackAnimationRemainingTicks() {
        return this.attackAnimationRemainingTicks;
    }

    @Override
    public boolean shouldDropExperience() {
        return true;
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel $$0) {
        return this.xpReward;
    }

    private void finishConversion() {
        this.convertTo(EntityType.ZOGLIN, ConversionParams.single(this, true, false), $$0 -> $$0.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0)));
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(ItemTags.HOGLIN_FOOD);
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("IsImmuneToZombification", this.isImmuneToZombification());
        $$0.putInt("TimeInOverworld", this.timeInOverworld);
        $$0.putBoolean("CannotBeHunted", this.cannotBeHunted);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.setImmuneToZombification($$0.getBooleanOr("IsImmuneToZombification", false));
        this.timeInOverworld = $$0.getIntOr("TimeInOverworld", 0);
        this.setCannotBeHunted($$0.getBooleanOr("CannotBeHunted", false));
    }

    public void setImmuneToZombification(boolean $$0) {
        this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, $$0);
    }

    private boolean isImmuneToZombification() {
        return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
    }

    public boolean isConverting() {
        return !this.level().dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
    }

    private void setCannotBeHunted(boolean $$0) {
        this.cannotBeHunted = $$0;
    }

    public boolean canBeHunted() {
        return this.isAdult() && !this.cannotBeHunted;
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        Hoglin $$2 = EntityType.HOGLIN.create($$0, EntitySpawnReason.BREEDING);
        if ($$2 != null) {
            $$2.setPersistenceRequired();
        }
        return $$2;
    }

    @Override
    public boolean canFallInLove() {
        return !HoglinAi.isPacified(this) && super.canFallInLove();
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.level().isClientSide) {
            return null;
        }
        return HoglinAi.getSoundForCurrentActivity(this).orElse(null);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.HOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOGLIN_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
        this.playSound(SoundEvents.HOGLIN_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }
}

