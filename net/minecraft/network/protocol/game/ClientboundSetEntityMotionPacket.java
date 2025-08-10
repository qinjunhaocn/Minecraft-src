/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetEntityMotionPacket> STREAM_CODEC = Packet.codec(ClientboundSetEntityMotionPacket::write, ClientboundSetEntityMotionPacket::new);
    private final int id;
    private final int xa;
    private final int ya;
    private final int za;

    public ClientboundSetEntityMotionPacket(Entity $$0) {
        this($$0.getId(), $$0.getDeltaMovement());
    }

    public ClientboundSetEntityMotionPacket(int $$0, Vec3 $$1) {
        this.id = $$0;
        double $$2 = 3.9;
        double $$3 = Mth.clamp($$1.x, -3.9, 3.9);
        double $$4 = Mth.clamp($$1.y, -3.9, 3.9);
        double $$5 = Mth.clamp($$1.z, -3.9, 3.9);
        this.xa = (int)($$3 * 8000.0);
        this.ya = (int)($$4 * 8000.0);
        this.za = (int)($$5 * 8000.0);
    }

    private ClientboundSetEntityMotionPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        this.xa = $$0.readShort();
        this.ya = $$0.readShort();
        this.za = $$0.readShort();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeShort(this.xa);
        $$0.writeShort(this.ya);
        $$0.writeShort(this.za);
    }

    @Override
    public PacketType<ClientboundSetEntityMotionPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_ENTITY_MOTION;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetEntityMotion(this);
    }

    public int getId() {
        return this.id;
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
}

