/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.animal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Turtle;
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
import net.minecraft.world.phys.Vec3;

public class Ocelot
extends Animal {
    public static final double CROUCH_SPEED_MOD = 0.6;
    public static final double WALK_SPEED_MOD = 0.8;
    public static final double SPRINT_SPEED_MOD = 1.33;
    private static final EntityDataAccessor<Boolean> DATA_TRUSTING = SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);
    private static final boolean DEFAULT_TRUSTING = false;
    @Nullable
    private OcelotAvoidEntityGoal<Player> ocelotAvoidPlayersGoal;
    @Nullable
    private OcelotTemptGoal temptGoal;

    public Ocelot(EntityType<? extends Ocelot> $$0, Level $$1) {
        super((EntityType<? extends Animal>)$$0, $$1);
        this.reassessTrustingGoals();
    }

    boolean isTrusting() {
        return this.entityData.get(DATA_TRUSTING);
    }

    private void setTrusting(boolean $$0) {
        this.entityData.set(DATA_TRUSTING, $$0);
        this.reassessTrustingGoals();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putBoolean("Trusting", this.isTrusting());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.setTrusting($$0.getBooleanOr("Trusting", false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(DATA_TRUSTING, false);
    }

    @Override
    protected void registerGoals() {
        this.temptGoal = new OcelotTemptGoal(this, 0.6, $$0 -> $$0.is(ItemTags.OCELOT_FOOD), true);
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(9, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8, 1.0000001E-5f));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Chicken>((Mob)this, Chicken.class, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public void customServerAiStep(ServerLevel $$0) {
        if (this.getMoveControl().hasWanted()) {
            double $$1 = this.getMoveControl().getSpeedModifier();
            if ($$1 == 0.6) {
                this.setPose(Pose.CROUCHING);
                this.setSprinting(false);
            } else if ($$1 == 1.33) {
                this.setPose(Pose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(Pose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
        }
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return !this.isTrusting() && this.tickCount > 2400;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.OCELOT_AMBIENT;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 900;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.OCELOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.OCELOT_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if ((this.temptGoal == null || this.temptGoal.isRunning()) && !this.isTrusting() && this.isFood($$2) && $$0.distanceToSqr(this) < 9.0) {
            this.usePlayerItem($$0, $$1, $$2);
            if (!this.level().isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.spawnTrustingParticles(true);
                    this.level().broadcastEntityEvent(this, (byte)41);
                } else {
                    this.spawnTrustingParticles(false);
                    this.level().broadcastEntityEvent(this, (byte)40);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract($$0, $$1);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 41) {
            this.spawnTrustingParticles(true);
        } else if ($$0 == 40) {
            this.spawnTrustingParticles(false);
        } else {
            super.handleEntityEvent($$0);
        }
    }

    private void spawnTrustingParticles(boolean $$0) {
        SimpleParticleType $$1 = ParticleTypes.HEART;
        if (!$$0) {
            $$1 = ParticleTypes.SMOKE;
        }
        for (int $$2 = 0; $$2 < 7; ++$$2) {
            double $$3 = this.random.nextGaussian() * 0.02;
            double $$4 = this.random.nextGaussian() * 0.02;
            double $$5 = this.random.nextGaussian() * 0.02;
            this.level().addParticle($$1, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), $$3, $$4, $$5);
        }
    }

    protected void reassessTrustingGoals() {
        if (this.ocelotAvoidPlayersGoal == null) {
            this.ocelotAvoidPlayersGoal = new OcelotAvoidEntityGoal<Player>(this, Player.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.ocelotAvoidPlayersGoal);
        if (!this.isTrusting()) {
            this.goalSelector.addGoal(4, this.ocelotAvoidPlayersGoal);
        }
    }

    @Override
    @Nullable
    public Ocelot getBreedOffspring(ServerLevel $$0, AgeableMob $$1) {
        return EntityType.OCELOT.create($$0, EntitySpawnReason.BREEDING);
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return $$0.is(ItemTags.OCELOT_FOOD);
    }

    public static boolean checkOcelotSpawnRules(EntityType<Ocelot> $$0, LevelAccessor $$1, EntitySpawnReason $$2, BlockPos $$3, RandomSource $$4) {
        return $$4.nextInt(3) != 0;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        if ($$0.isUnobstructed(this) && !$$0.containsAnyLiquid(this.getBoundingBox())) {
            BlockPos $$1 = this.blockPosition();
            if ($$1.getY() < $$0.getSeaLevel()) {
                return false;
            }
            BlockState $$2 = $$0.getBlockState($$1.below());
            if ($$2.is(Blocks.GRASS_BLOCK) || $$2.is(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, EntitySpawnReason $$2, @Nullable SpawnGroupData $$3) {
        if ($$3 == null) {
            $$3 = new AgeableMob.AgeableMobGroupData(1.0f);
        }
        return super.finalizeSpawn($$0, $$1, $$2, $$3);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Override
    public boolean isSteppingCarefully() {
        return this.isCrouching() || super.isSteppingCarefully();
    }

    @Override
    @Nullable
    public /* synthetic */ AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return this.getBreedOffspring(serverLevel, ageableMob);
    }

    static class OcelotTemptGoal
    extends TemptGoal {
        private final Ocelot ocelot;

        public OcelotTemptGoal(Ocelot $$0, double $$1, Predicate<ItemStack> $$2, boolean $$3) {
            super($$0, $$1, $$2, $$3);
            this.ocelot = $$0;
        }

        @Override
        protected boolean canScare() {
            return super.canScare() && !this.ocelot.isTrusting();
        }
    }

    static class OcelotAvoidEntityGoal<T extends LivingEntity>
    extends AvoidEntityGoal<T> {
        private final Ocelot ocelot;

        public OcelotAvoidEntityGoal(Ocelot $$0, Class<T> $$1, float $$2, double $$3, double $$4) {
            super($$0, $$1, $$2, $$3, $$4, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.ocelot = $$0;
        }

        @Override
        public boolean canUse() {
            return !this.ocelot.isTrusting() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.ocelot.isTrusting() && super.canContinueToUse();
        }
    }
}

