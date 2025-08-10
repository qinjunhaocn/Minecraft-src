/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ServerboundDebugSampleSubscriptionPacket(RemoteDebugSampleType sampleType) implements Packet<ServerGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ServerboundDebugSampleSubscriptionPacket> STREAM_CODEC = Packet.codec(ServerboundDebugSampleSubscriptionPacket::write, ServerboundDebugSampleSubscriptionPacket::new);

    private ServerboundDebugSampleSubscriptionPacket(FriendlyByteBuf $$0) {
        this($$0.readEnum(RemoteDebugSampleType.class));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.sampleType);
    }

    @Override
    public PacketType<ServerboundDebugSampleSubscriptionPacket> type() {
        return GamePacketTypes.SERVERBOUND_DEBUG_SAMPLE_SUBSCRIPTION;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleDebugSampleSubscription(this);
    }
}

