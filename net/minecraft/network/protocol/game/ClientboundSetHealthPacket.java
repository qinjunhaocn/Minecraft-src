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

public class ClientboundSetHealthPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetHealthPacket> STREAM_CODEC = Packet.codec(ClientboundSetHealthPacket::write, ClientboundSetHealthPacket::new);
    private final float health;
    private final int food;
    private final float saturation;

    public ClientboundSetHealthPacket(float $$0, int $$1, float $$2) {
        this.health = $$0;
        this.food = $$1;
        this.saturation = $$2;
    }

    private ClientboundSetHealthPacket(FriendlyByteBuf $$0) {
        this.health = $$0.readFloat();
        this.food = $$0.readVarInt();
        this.saturation = $$0.readFloat();
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.health);
        $$0.writeVarInt(this.food);
        $$0.writeFloat(this.saturation);
    }

    @Override
    public PacketType<ClientboundSetHealthPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_HEALTH;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }
}

