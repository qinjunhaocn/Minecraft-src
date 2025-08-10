/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet
extends Projectile {
    private static final double SPEED = 0.15;
    @Nullable
    private EntityReference<Entity> finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;

    public ShulkerBullet(EntityType<? extends ShulkerBullet> $$0, Level $$1) {
        super((EntityType<? extends Projectile>)$$0, $$1);
        this.noPhysics = true;
    }

    public ShulkerBullet(Level $$0, LivingEntity $$1, Entity $$2, Direction.Axis $$3) {
        this((EntityType<? extends ShulkerBullet>)EntityType.SHULKER_BULLET, $$0);
        this.setOwner($$1);
        Vec3 $$4 = $$1.getBoundingBox().getCenter();
        this.snapTo($$4.x, $$4.y, $$4.z, this.getYRot(), this.getXRot());
        this.finalTarget = new EntityReference<Entity>($$2);
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection($$3, $$2);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        if (this.finalTarget != null) {
            $$0.store("Target", UUIDUtil.CODEC, this.finalTarget.getUUID());
        }
        $$0.storeNullable("Dir", Direction.LEGACY_ID_CODEC, this.currentMoveDirection);
        $$0.putInt("Steps", this.flightSteps);
        $$0.putDouble("TXD", this.targetDeltaX);
        $$0.putDouble("TYD", this.targetDeltaY);
        $$0.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.flightSteps = $$0.getIntOr("Steps", 0);
        this.targetDeltaX = $$0.getDoubleOr("TXD", 0.0);
        this.targetDeltaY = $$0.getDoubleOr("TYD", 0.0);
        this.targetDeltaZ = $$0.getDoubleOr("TZD", 0.0);
        this.currentMoveDirection = $$0.read("Dir", Direction.LEGACY_ID_CODEC).orElse(null);
        this.finalTarget = EntityReference.read($$0, "Target");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
    }

    @Nullable
    private Direction getMoveDirection() {
        return this.currentMoveDirection;
    }

    private void setMoveDirection(@Nullable Direction $$0) {
        this.currentMoveDirection = $$0;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis $$0, @Nullable Entity $$1) {
        BlockPos $$4;
        double $$2 = 0.5;
        if ($$1 == null) {
            BlockPos $$3 = this.blockPosition().below();
        } else {
            $$2 = (double)$$1.getBbHeight() * 0.5;
            $$4 = BlockPos.containing($$1.getX(), $$1.getY() + $$2, $$1.getZ());
        }
        double $$5 = (double)$$4.getX() + 0.5;
        double $$6 = (double)$$4.getY() + $$2;
        double $$7 = (double)$$4.getZ() + 0.5;
        Direction $$8 = null;
        if (!$$4.closerToCenterThan(this.position(), 2.0)) {
            BlockPos $$9 = this.blockPosition();
            ArrayList<Direction> $$10 = Lists.newArrayList();
            if ($$0 != Direction.Axis.X) {
                if ($$9.getX() < $$4.getX() && this.level().isEmptyBlock($$9.east())) {
                    $$10.add(Direction.EAST);
                } else if ($$9.getX() > $$4.getX() && this.level().isEmptyBlock($$9.west())) {
                    $$10.add(Direction.WEST);
                }
            }
            if ($$0 != Direction.Axis.Y) {
                if ($$9.getY() < $$4.getY() && this.level().isEmptyBlock($$9.above())) {
                    $$10.add(Direction.UP);
                } else if ($$9.getY() > $$4.getY() && this.level().isEmptyBlock($$9.below())) {
                    $$10.add(Direction.DOWN);
                }
            }
            if ($$0 != Direction.Axis.Z) {
                if ($$9.getZ() < $$4.getZ() && this.level().isEmptyBlock($$9.south())) {
                    $$10.add(Direction.SOUTH);
                } else if ($$9.getZ() > $$4.getZ() && this.level().isEmptyBlock($$9.north())) {
                    $$10.add(Direction.NORTH);
                }
            }
            $$8 = Direction.getRandom(this.random);
            if ($$10.isEmpty()) {
                for (int $$11 = 5; !this.level().isEmptyBlock($$9.relative($$8)) && $$11 > 0; --$$11) {
                    $$8 = Direction.getRandom(this.random);
                }
            } else {
                $$8 = (Direction)$$10.get(this.random.nextInt($$10.size()));
            }
            $$5 = this.getX() + (double)$$8.getStepX();
            $$6 = this.getY() + (double)$$8.getStepY();
            $$7 = this.getZ() + (double)$$8.getStepZ();
        }
        this.setMoveDirection($$8);
        double $$12 = $$5 - this.getX();
        double $$13 = $$6 - this.getY();
        double $$14 = $$7 - this.getZ();
        double $$15 = Math.sqrt($$12 * $$12 + $$13 * $$13 + $$14 * $$14);
        if ($$15 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = $$12 / $$15 * 0.15;
            this.targetDeltaY = $$13 / $$15 * 0.15;
            this.targetDeltaZ = $$14 / $$15 * 0.15;
        }
        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        super.tick();
        Entity $$0 = !this.level().isClientSide() ? EntityReference.get(this.finalTarget, this.level(), Entity.class) : null;
        HitResult $$1 = null;
        if (!this.level().isClientSide) {
            if ($$0 == null) {
                this.finalTarget = null;
            }
            if (!($$0 == null || !$$0.isAlive() || $$0 instanceof Player && $$0.isSpectator())) {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                Vec3 $$2 = this.getDeltaMovement();
                this.setDeltaMovement($$2.add((this.targetDeltaX - $$2.x) * 0.2, (this.targetDeltaY - $$2.y) * 0.2, (this.targetDeltaZ - $$2.z) * 0.2));
            } else {
                this.applyGravity();
            }
            $$1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        }
        Vec3 $$3 = this.getDeltaMovement();
        this.setPos(this.position().add($$3));
        this.applyEffectsFromBlocks();
        if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
            this.handlePortal();
        }
        if ($$1 != null && this.isAlive() && $$1.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf($$1);
        }
        ProjectileUtil.rotateTowardsMovement(this, 0.5f);
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX() - $$3.x, this.getY() - $$3.y + 0.15, this.getZ() - $$3.z, 0.0, 0.0, 0.0);
        } else if ($$0 != null) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis(), $$0);
                }
            }
            if (this.currentMoveDirection != null) {
                BlockPos $$4 = this.blockPosition();
                Direction.Axis $$5 = this.currentMoveDirection.getAxis();
                if (this.level().loadedAndEntityCanStandOn($$4.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection($$5, $$0);
                } else {
                    BlockPos $$6 = $$0.blockPosition();
                    if ($$5 == Direction.Axis.X && $$4.getX() == $$6.getX() || $$5 == Direction.Axis.Z && $$4.getZ() == $$6.getZ() || $$5 == Direction.Axis.Y && $$4.getY() == $$6.getY()) {
                        this.selectNextMoveDirection($$5, $$0);
                    }
                }
            }
        }
    }

    @Override
    protected boolean isAffectedByBlocks() {
        return !this.isRemoved();
    }

    @Override
    protected boolean canHitEntity(Entity $$0) {
        return super.canHitEntity($$0) && !$$0.noPhysics;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        return $$0 < 16384.0;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0f;
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        Entity $$1 = $$0.getEntity();
        Entity $$2 = this.getOwner();
        LivingEntity $$3 = $$2 instanceof LivingEntity ? (LivingEntity)$$2 : null;
        DamageSource $$4 = this.damageSources().mobProjectile(this, $$3);
        boolean $$5 = $$1.hurtOrSimulate($$4, 4.0f);
        if ($$5) {
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel $$6 = (ServerLevel)level;
                EnchantmentHelper.doPostAttackEffects($$6, $$1, $$4);
            }
            if ($$1 instanceof LivingEntity) {
                LivingEntity $$7 = (LivingEntity)$$1;
                $$7.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200), MoreObjects.firstNonNull($$2, this));
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        super.onHitBlock($$0);
        ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f);
    }

    private void destroy() {
        this.discard();
        this.level().gameEvent(GameEvent.ENTITY_DAMAGE, this.position(), GameEvent.Context.of(this));
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        this.destroy();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurtClient(DamageSource $$0) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
        this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0f, 1.0f);
        $$0.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2, 0.2, 0.2, 0.0);
        this.destroy();
        return true;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        double $$1 = $$0.getXa();
        double $$2 = $$0.getYa();
        double $$3 = $$0.getZa();
        this.setDeltaMovement($$1, $$2, $$3);
    }
}

