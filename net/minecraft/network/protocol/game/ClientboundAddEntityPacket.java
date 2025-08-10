/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddEntityPacket> STREAM_CODEC = Packet.codec(ClientboundAddEntityPacket::write, ClientboundAddEntityPacket::new);
    private static final double MAGICAL_QUANTIZATION = 8000.0;
    private static final double LIMIT = 3.9;
    private final int id;
    private final UUID uuid;
    private final EntityType<?> type;
    private final double x;
    private final double y;
    private final double z;
    private final int xa;
    private final int ya;
    private final int za;
    private final byte xRot;
    private final byte yRot;
    private final byte yHeadRot;
    private final int data;

    public ClientboundAddEntityPacket(Entity $$0, ServerEntity $$1) {
        this($$0, $$1, 0);
    }

    public ClientboundAddEntityPacket(Entity $$0, ServerEntity $$1, int $$2) {
        this($$0.getId(), $$0.getUUID(), $$1.getPositionBase().x(), $$1.getPositionBase().y(), $$1.getPositionBase().z(), $$1.getLastSentXRot(), $$1.getLastSentYRot(), $$0.getType(), $$2, $$1.getLastSentMovement(), $$1.getLastSentYHeadRot());
    }

    public ClientboundAddEntityPacket(Entity $$0, int $$1, BlockPos $$2) {
        this($$0.getId(), $$0.getUUID(), $$2.getX(), $$2.getY(), $$2.getZ(), $$0.getXRot(), $$0.getYRot(), $$0.getType(), $$1, $$0.getDeltaMovement(), $$0.getYHeadRot());
    }

    public ClientboundAddEntityPacket(int $$0, UUID $$1, double $$2, double $$3, double $$4, float $$5, float $$6, EntityType<?> $$7, int $$8, Vec3 $$9, double $$10) {
        this.id = $$0;
        this.uuid = $$1;
        this.x = $$2;
        this.y = $$3;
        this.z = $$4;
        this.xRot = Mth.packDegrees($$5);
        this.yRot = Mth.packDegrees($$6);
        this.yHeadRot = Mth.packDegrees((float)$$10);
        this.type = $$7;
        this.data = $$8;
        this.xa = (int)(Mth.clamp($$9.x, -3.9, 3.9) * 8000.0);
        this.ya = (int)(Mth.clamp($$9.y, -3.9, 3.9) * 8000.0);
        this.za = (int)(Mth.clamp($$9.z, -3.9, 3.9) * 8000.0);
    }

    private ClientboundAddEntityPacket(RegistryFriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.uuid = $$0.readUUID();
        this.type = (EntityType)ByteBufCodecs.registry(Registries.ENTITY_TYPE).decode($$0);
        this.x = $$0.readDouble();
        this.y = $$0.readDouble();
        this.z = $$0.readDouble();
        this.xRot = $$0.readByte();
        this.yRot = $$0.readByte();
        this.yHeadRot = $$0.readByte();
        this.data = $$0.readVarInt();
        this.xa = $$0.readShort();
        this.ya = $$0.readShort();
        this.za = $$0.readShort();
    }

    private void write(RegistryFriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeUUID(this.uuid);
        ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode($$0, this.type);
        $$0.writeDouble(this.x);
        $$0.writeDouble(this.y);
        $$0.writeDouble(this.z);
        $$0.writeByte(this.xRot);
        $$0.writeByte(this.yRot);
        $$0.writeByte(this.yHeadRot);
        $$0.writeVarInt(this.data);
        $$0.writeShort(this.xa);
        $$0.writeShort(this.ya);
        $$0.writeShort(this.za);
    }

    @Override
    public PacketType<ClientboundAddEntityPacket> type() {
        return GamePacketTypes.CLIENTBOUND_ADD_ENTITY;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAddEntity(this);
    }

    public int getId() {
        return this.id;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getXa() {
        return (double)this.xa / 8000.0;
    }

    public double getYa() {
        return (double)this.ya / 8000.0;
    }

    public double getZa() {
        return (double)this.za / 8000.0;
    }

    public float getXRot() {
        return Mth.unpackDegrees(this.xRot);
    }

    public float getYRot() {
        return Mth.unpackDegrees(this.yRot);
    }

    public float getYHeadRot() {
        return Mth.unpackDegrees(this.yHeadRot);
    }

    public int getData() {
        return this.data;
    }
}

