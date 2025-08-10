/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.ping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.ping.ClientPongPacketListener;
import net.minecraft.network.protocol.ping.PingPacketTypes;

public record ClientboundPongResponsePacket(long time) implements Packet<ClientPongPacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundPongResponsePacket> STREAM_CODEC = Packet.codec(ClientboundPongResponsePacket::write, ClientboundPongResponsePacket::new);

    private ClientboundPongResponsePacket(FriendlyByteBuf $$0) {
        this($$0.readLong());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeLong(this.time);
    }

    @Override
    public PacketType<ClientboundPongResponsePacket> type() {
        return PingPacketTypes.CLIENTBOUND_PONG_RESPONSE;
    }

    @Override
    public void handle(ClientPongPacketListener $$0) {
        $$0.handlePongResponse(this);
    }
}

