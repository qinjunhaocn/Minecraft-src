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

public record ClientboundChunkBatchFinishedPacket(int batchSize) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundChunkBatchFinishedPacket> STREAM_CODEC = Packet.codec(ClientboundChunkBatchFinishedPacket::write, ClientboundChunkBatchFinishedPacket::new);

    private ClientboundChunkBatchFinishedPacket(FriendlyByteBuf $$0) {
        this($$0.readVarInt());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.batchSize);
    }

    @Override
    public PacketType<ClientboundChunkBatchFinishedPacket> type() {
        return GamePacketTypes.CLIENTBOUND_CHUNK_BATCH_FINISHED;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleChunkBatchFinished(this);
    }
}

