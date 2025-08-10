/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownEnderpearl
extends ThrowableItemProjectile {
    private long ticketTimer = 0L;

    public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> $$0, Level $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)$$0, $$1);
    }

    public ThrownEnderpearl(Level $$0, LivingEntity $$1, ItemStack $$2) {
        super(EntityType.ENDER_PEARL, $$1, $$0, $$2);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void setOwner(@Nullable EntityReference<Entity> $$0) {
        this.deregisterFromCurrentOwner();
        super.setOwner($$0);
        this.registerToCurrentOwner();
    }

    private void deregisterFromCurrentOwner() {
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayer) {
            ServerPlayer $$0 = (ServerPlayer)entity;
            $$0.deregisterEnderPearl(this);
        }
    }

    private void registerToCurrentOwner() {
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayer) {
            ServerPlayer $$0 = (ServerPlayer)entity;
            $$0.registerEnderPearl(this);
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @Nullable
    public Entity getOwner() {
        void $$1;
        Level level;
        if (this.owner == null || !((level = this.level()) instanceof ServerLevel)) {
            return super.getOwner();
        }
        ServerLevel $$0 = (ServerLevel)level;
        return this.owner.getEntity(arg_0 -> ThrownEnderpearl.lambda$getOwner$0((ServerLevel)$$1, arg_0), Entity.class);
    }

    @Nullable
    private static Entity findOwnerInAnyDimension(ServerLevel $$0, UUID $$1) {
        Entity $$2 = $$0.getEntity($$1);
        if ($$2 != null) {
            return $$2;
        }
        for (ServerLevel $$3 : $$0.getServer().getAllLevels()) {
            if ($$3 == $$0 || ($$2 = $$3.getEntity($$1)) == null) continue;
            return $$2;
        }
        return null;
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        $$0.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0f);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected void onHit(HitResult $$0) {
        void $$3;
        block14: {
            block13: {
                super.onHit($$0);
                for (int $$1 = 0; $$1 < 32; ++$$1) {
                    this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
                }
                Level level = this.level();
                if (!(level instanceof ServerLevel)) break block13;
                ServerLevel $$2 = (ServerLevel)level;
                if (!this.isRemoved()) break block14;
            }
            return;
        }
        Entity $$4 = this.getOwner();
        if ($$4 == null || !ThrownEnderpearl.isAllowedToTeleportOwner($$4, (Level)$$3)) {
            this.discard();
            return;
        }
        Vec3 $$5 = this.oldPosition();
        if ($$4 instanceof ServerPlayer) {
            ServerPlayer $$6 = (ServerPlayer)$$4;
            if ($$6.connection.isAcceptingMessages()) {
                ServerPlayer $$8;
                Endermite $$7;
                if (this.random.nextFloat() < 0.05f && $$3.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && ($$7 = EntityType.ENDERMITE.create((Level)$$3, EntitySpawnReason.TRIGGERED)) != null) {
                    $$7.snapTo($$4.getX(), $$4.getY(), $$4.getZ(), $$4.getYRot(), $$4.getXRot());
                    $$3.addFreshEntity($$7);
                }
                if (this.isOnPortalCooldown()) {
                    $$4.setPortalCooldown();
                }
                if (($$8 = $$6.teleport(new TeleportTransition((ServerLevel)$$3, $$5, Vec3.ZERO, 0.0f, 0.0f, Relative.a(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING))) != null) {
                    $$8.resetFallDistance();
                    $$8.resetCurrentImpulseContext();
                    $$8.hurtServer($$6.level(), this.damageSources().enderPearl(), 5.0f);
                }
                this.playSound((Level)$$3, $$5);
            }
        } else {
            Entity $$9 = $$4.teleport(new TeleportTransition((ServerLevel)$$3, $$5, $$4.getDeltaMovement(), $$4.getYRot(), $$4.getXRot(), TeleportTransition.DO_NOTHING));
            if ($$9 != null) {
                $$9.resetFallDistance();
            }
            this.playSound((Level)$$3, $$5);
        }
        this.discard();
    }

    private static boolean isAllowedToTeleportOwner(Entity $$0, Level $$1) {
        if ($$0.level().dimension() == $$1.dimension()) {
            if ($$0 instanceof LivingEntity) {
                LivingEntity $$2 = (LivingEntity)$$0;
                return $$2.isAlive() && !$$2.isSleeping();
            }
            return $$0.isAlive();
        }
        return $$0.canUsePortal(true);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void tick() {
        $$0 = SectionPos.blockToSectionCoord(this.position().x());
        $$1 = SectionPos.blockToSectionCoord(this.position().z());
        $$2 = this.getOwner();
        if (!($$2 instanceof ServerPlayer)) ** GOTO lbl-1000
        $$3 = (ServerPlayer)$$2;
        if (!$$2.isAlive() && $$3.level().getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard();
        } else lbl-1000:
        // 2 sources

        {
            super.tick();
        }
        if (!this.isAlive()) {
            return;
        }
        $$4 = BlockPos.containing(this.position());
        if ((--this.ticketTimer <= 0L || $$0 != SectionPos.blockToSectionCoord($$4.getX()) || $$1 != SectionPos.blockToSectionCoord($$4.getZ())) && $$2 instanceof ServerPlayer) {
            $$5 = (ServerPlayer)$$2;
            this.ticketTimer = $$5.registerAndUpdateEnderPearlTicket(this);
        }
    }

    private void playSound(Level $$0, Vec3 $$1) {
        $$0.playSound(null, $$1.x, $$1.y, $$1.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
    }

    @Override
    @Nullable
    public Entity teleport(TeleportTransition $$0) {
        Entity $$1 = super.teleport($$0);
        if ($$1 != null) {
            $$1.placePortalTicket(BlockPos.containing($$1.position()));
        }
        return $$1;
    }

    @Override
    public boolean canTeleport(Level $$0, Level $$1) {
        Entity entity;
        if ($$0.dimension() == Level.END && $$1.dimension() == Level.OVERWORLD && (entity = this.getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$2 = (ServerPlayer)entity;
            return super.canTeleport($$0, $$1) && $$2.seenCredits;
        }
        return super.canTeleport($$0, $$1);
    }

    @Override
    protected void onInsideBlock(BlockState $$0) {
        Entity entity;
        super.onInsideBlock($$0);
        if ($$0.is(Blocks.END_GATEWAY) && (entity = this.getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$1 = (ServerPlayer)entity;
            $$1.onInsideBlock($$0);
        }
    }

    @Override
    public void onRemoval(Entity.RemovalReason $$0) {
        if ($$0 != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            this.deregisterFromCurrentOwner();
        }
        super.onRemoval($$0);
    }

    @Override
    public void onAboveBubbleColumn(boolean $$0, BlockPos $$1) {
        Entity.handleOnAboveBubbleColumn(this, $$0, $$1);
    }

    @Override
    public void onInsideBubbleColumn(boolean $$0) {
        Entity.handleOnInsideBubbleColumn(this, $$0);
    }

    private static /* synthetic */ Entity lambda$getOwner$0(ServerLevel $$0, UUID $$1) {
        return ThrownEnderpearl.findOwnerInAnyDimension($$0, $$1);
    }
}

