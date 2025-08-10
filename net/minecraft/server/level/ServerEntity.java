/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TOLERANCE_LEVEL_ROTATION = 1;
    private static final double TOLERANCE_LEVEL_POSITION = 7.62939453125E-6;
    public static final int FORCED_POS_UPDATE_PERIOD = 60;
    private static final int FORCED_TELEPORT_PERIOD = 400;
    private final ServerLevel level;
    private final Entity entity;
    private final int updateInterval;
    private final boolean trackDelta;
    private final Consumer<Packet<?>> broadcast;
    private final BiConsumer<Packet<?>, List<UUID>> broadcastWithIgnore;
    private final VecDeltaCodec positionCodec = new VecDeltaCodec();
    private byte lastSentYRot;
    private byte lastSentXRot;
    private byte lastSentYHeadRot;
    private Vec3 lastSentMovement;
    private int tickCount;
    private int teleportDelay;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean wasRiding;
    private boolean wasOnGround;
    @Nullable
    private List<SynchedEntityData.DataValue<?>> trackedDataValues;

    public ServerEntity(ServerLevel $$0, Entity $$1, int $$2, boolean $$3, Consumer<Packet<?>> $$4, BiConsumer<Packet<?>, List<UUID>> $$5) {
        this.level = $$0;
        this.broadcast = $$4;
        this.entity = $$1;
        this.updateInterval = $$2;
        this.trackDelta = $$3;
        this.broadcastWithIgnore = $$5;
        this.positionCodec.setBase($$1.trackingPosition());
        this.lastSentMovement = $$1.getDeltaMovement();
        this.lastSentYRot = Mth.packDegrees($$1.getYRot());
        this.lastSentXRot = Mth.packDegrees($$1.getXRot());
        this.lastSentYHeadRot = Mth.packDegrees($$1.getYHeadRot());
        this.wasOnGround = $$1.onGround();
        this.trackedDataValues = $$1.getEntityData().getNonDefaultValues();
    }

    public void sendChanges() {
        Entity entity;
        List<Entity> $$0 = this.entity.getPassengers();
        if (!$$0.equals(this.lastPassengers)) {
            List $$1 = this.mountedOrDismounted($$0).map(Entity::getUUID).toList();
            this.broadcastWithIgnore.accept(new ClientboundSetPassengersPacket(this.entity), $$1);
            this.lastPassengers = $$0;
        }
        if ((entity = this.entity) instanceof ItemFrame) {
            ItemFrame $$2 = (ItemFrame)entity;
            if (this.tickCount % 10 == 0) {
                MapId $$4;
                MapItemSavedData $$5;
                ItemStack $$3 = $$2.getItem();
                if ($$3.getItem() instanceof MapItem && ($$5 = MapItem.getSavedData($$4 = $$3.get(DataComponents.MAP_ID), (Level)this.level)) != null) {
                    for (ServerPlayer serverPlayer : this.level.players()) {
                        $$5.tickCarriedBy(serverPlayer, $$3);
                        Packet<?> $$7 = $$5.getUpdatePacket($$4, serverPlayer);
                        if ($$7 == null) continue;
                        serverPlayer.connection.send($$7);
                    }
                }
                this.sendDirtyEntityData();
            }
        }
        if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
            boolean $$10;
            byte $$8 = Mth.packDegrees(this.entity.getYRot());
            byte $$9 = Mth.packDegrees(this.entity.getXRot());
            boolean bl = $$10 = Math.abs($$8 - this.lastSentYRot) >= 1 || Math.abs($$9 - this.lastSentXRot) >= 1;
            if (this.entity.isPassenger()) {
                if ($$10) {
                    this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), $$8, $$9, this.entity.onGround()));
                    this.lastSentYRot = $$8;
                    this.lastSentXRot = $$9;
                }
                this.positionCodec.setBase(this.entity.trackingPosition());
                this.sendDirtyEntityData();
                this.wasRiding = true;
            } else {
                AbstractMinecart $$11;
                MinecartBehavior minecartBehavior;
                Entity entity2 = this.entity;
                if (entity2 instanceof AbstractMinecart && (minecartBehavior = ($$11 = (AbstractMinecart)entity2).getBehavior()) instanceof NewMinecartBehavior) {
                    NewMinecartBehavior $$12 = (NewMinecartBehavior)minecartBehavior;
                    this.handleMinecartPosRot($$12, $$8, $$9, $$10);
                } else {
                    Vec3 $$23;
                    double $$24;
                    boolean $$22;
                    ++this.teleportDelay;
                    Vec3 vec3 = this.entity.trackingPosition();
                    boolean $$14 = this.positionCodec.delta(vec3).lengthSqr() >= 7.62939453125E-6;
                    Packet<ClientGamePacketListener> $$15 = null;
                    boolean $$16 = $$14 || this.tickCount % 60 == 0;
                    boolean $$17 = false;
                    boolean $$18 = false;
                    long $$19 = this.positionCodec.encodeX(vec3);
                    long $$20 = this.positionCodec.encodeY(vec3);
                    long $$21 = this.positionCodec.encodeZ(vec3);
                    boolean bl2 = $$22 = $$19 < -32768L || $$19 > 32767L || $$20 < -32768L || $$20 > 32767L || $$21 < -32768L || $$21 > 32767L;
                    if (this.entity.getRequiresPrecisePosition() || $$22 || this.teleportDelay > 400 || this.wasRiding || this.wasOnGround != this.entity.onGround()) {
                        this.wasOnGround = this.entity.onGround();
                        this.teleportDelay = 0;
                        $$15 = ClientboundEntityPositionSyncPacket.of(this.entity);
                        $$17 = true;
                        $$18 = true;
                    } else if ($$16 && $$10 || this.entity instanceof AbstractArrow) {
                        $$15 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)$$19, (short)$$20, (short)$$21, $$8, $$9, this.entity.onGround());
                        $$17 = true;
                        $$18 = true;
                    } else if ($$16) {
                        $$15 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)$$19, (short)$$20, (short)$$21, this.entity.onGround());
                        $$17 = true;
                    } else if ($$10) {
                        $$15 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), $$8, $$9, this.entity.onGround());
                        $$18 = true;
                    }
                    if ((this.entity.hasImpulse || this.trackDelta || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && (($$24 = ($$23 = this.entity.getDeltaMovement()).distanceToSqr(this.lastSentMovement)) > 1.0E-7 || $$24 > 0.0 && $$23.lengthSqr() == 0.0)) {
                        this.lastSentMovement = $$23;
                        Entity entity3 = this.entity;
                        if (entity3 instanceof AbstractHurtingProjectile) {
                            AbstractHurtingProjectile $$25 = (AbstractHurtingProjectile)entity3;
                            this.broadcast.accept(new ClientboundBundlePacket(List.of((Object)new ClientboundSetEntityMotionPacket(this.entity.getId(), this.lastSentMovement), (Object)new ClientboundProjectilePowerPacket($$25.getId(), $$25.accelerationPower))));
                        } else {
                            this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.lastSentMovement));
                        }
                    }
                    if ($$15 != null) {
                        this.broadcast.accept($$15);
                    }
                    this.sendDirtyEntityData();
                    if ($$17) {
                        this.positionCodec.setBase(vec3);
                    }
                    if ($$18) {
                        this.lastSentYRot = $$8;
                        this.lastSentXRot = $$9;
                    }
                    this.wasRiding = false;
                }
            }
            byte $$26 = Mth.packDegrees(this.entity.getYHeadRot());
            if (Math.abs($$26 - this.lastSentYHeadRot) >= 1) {
                this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, $$26));
                this.lastSentYHeadRot = $$26;
            }
            this.entity.hasImpulse = false;
        }
        ++this.tickCount;
        if (this.entity.hurtMarked) {
            this.entity.hurtMarked = false;
            this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
        }
    }

    private Stream<Entity> mountedOrDismounted(List<Entity> $$02) {
        return Streams.concat(this.lastPassengers.stream().filter($$1 -> !$$02.contains($$1)), $$02.stream().filter($$0 -> !this.lastPassengers.contains($$0)));
    }

    private void handleMinecartPosRot(NewMinecartBehavior $$0, byte $$1, byte $$2, boolean $$3) {
        this.sendDirtyEntityData();
        if ($$0.lerpSteps.isEmpty()) {
            boolean $$8;
            Vec3 $$4 = this.entity.getDeltaMovement();
            double $$5 = $$4.distanceToSqr(this.lastSentMovement);
            Vec3 $$6 = this.entity.trackingPosition();
            boolean $$7 = this.positionCodec.delta($$6).lengthSqr() >= 7.62939453125E-6;
            boolean bl = $$8 = $$7 || this.tickCount % 60 == 0;
            if ($$8 || $$3 || $$5 > 1.0E-7) {
                this.broadcast.accept(new ClientboundMoveMinecartPacket(this.entity.getId(), List.of((Object)((Object)new NewMinecartBehavior.MinecartStep(this.entity.position(), this.entity.getDeltaMovement(), this.entity.getYRot(), this.entity.getXRot(), 1.0f)))));
            }
        } else {
            this.broadcast.accept(new ClientboundMoveMinecartPacket(this.entity.getId(), List.copyOf($$0.lerpSteps)));
            $$0.lerpSteps.clear();
        }
        this.lastSentYRot = $$1;
        this.lastSentXRot = $$2;
        this.positionCodec.setBase(this.entity.position());
    }

    public void removePairing(ServerPlayer $$0) {
        this.entity.stopSeenByPlayer($$0);
        $$0.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
    }

    public void addPairing(ServerPlayer $$0) {
        ArrayList<Packet<? super ClientGamePacketListener>> $$1 = new ArrayList<Packet<? super ClientGamePacketListener>>();
        this.sendPairingData($$0, $$1::add);
        $$0.connection.send(new ClientboundBundlePacket((Iterable<Packet<? super ClientGamePacketListener>>)$$1));
        this.entity.startSeenByPlayer($$0);
    }

    public void sendPairingData(ServerPlayer $$0, Consumer<Packet<ClientGamePacketListener>> $$1) {
        Leashable $$9;
        LivingEntity $$3;
        Object $$4;
        Entity entity;
        if (this.entity.isRemoved()) {
            LOGGER.warn("Fetching packet for removed entity {}", (Object)this.entity);
        }
        Packet<ClientGamePacketListener> $$2 = this.entity.getAddEntityPacket(this);
        $$1.accept($$2);
        if (this.trackedDataValues != null) {
            $$1.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
        }
        if ((entity = this.entity) instanceof LivingEntity && !($$4 = ($$3 = (LivingEntity)entity).getAttributes().getSyncableAttributes()).isEmpty()) {
            $$1.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), (Collection<AttributeInstance>)$$4));
        }
        if (($$4 = this.entity) instanceof LivingEntity) {
            LivingEntity $$5 = (LivingEntity)$$4;
            ArrayList<Pair<EquipmentSlot, ItemStack>> $$6 = Lists.newArrayList();
            for (EquipmentSlot $$7 : EquipmentSlot.VALUES) {
                ItemStack $$8 = $$5.getItemBySlot($$7);
                if ($$8.isEmpty()) continue;
                $$6.add((Pair<EquipmentSlot, ItemStack>)Pair.of((Object)$$7, (Object)$$8.copy()));
            }
            if (!$$6.isEmpty()) {
                $$1.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), $$6));
            }
        }
        if (!this.entity.getPassengers().isEmpty()) {
            $$1.accept(new ClientboundSetPassengersPacket(this.entity));
        }
        if (this.entity.isPassenger()) {
            $$1.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
        }
        if ((entity = this.entity) instanceof Leashable && ($$9 = (Leashable)((Object)entity)).isLeashed()) {
            $$1.accept(new ClientboundSetEntityLinkPacket(this.entity, $$9.getLeashHolder()));
        }
    }

    public Vec3 getPositionBase() {
        return this.positionCodec.getBase();
    }

    public Vec3 getLastSentMovement() {
        return this.lastSentMovement;
    }

    public float getLastSentXRot() {
        return Mth.unpackDegrees(this.lastSentXRot);
    }

    public float getLastSentYRot() {
        return Mth.unpackDegrees(this.lastSentYRot);
    }

    public float getLastSentYHeadRot() {
        return Mth.unpackDegrees(this.lastSentYHeadRot);
    }

    private void sendDirtyEntityData() {
        SynchedEntityData $$0 = this.entity.getEntityData();
        List<SynchedEntityData.DataValue<?>> $$1 = $$0.packDirty();
        if ($$1 != null) {
            this.trackedDataValues = $$0.getNonDefaultValues();
            this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), $$1));
        }
        if (this.entity instanceof LivingEntity) {
            Set<AttributeInstance> $$2 = ((LivingEntity)this.entity).getAttributes().getAttributesToSync();
            if (!$$2.isEmpty()) {
                this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), $$2));
            }
            $$2.clear();
        }
    }

    private void broadcastAndSend(Packet<?> $$0) {
        this.broadcast.accept($$0);
        if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send($$0);
        }
    }
}

