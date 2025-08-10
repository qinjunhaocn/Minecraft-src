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
import net.minecraft.world.level.ChunkPos;

public record ClientboundForgetLevelChunkPacket(ChunkPos pos) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundForgetLevelChunkPacket> STREAM_CODEC = Packet.codec(ClientboundForgetLevelChunkPacket::write, ClientboundForgetLevelChunkPacket::new);

    private ClientboundForgetLevelChunkPacket(FriendlyByteBuf $$0) {
        this($$0.readChunkPos());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeChunkPos(this.pos);
    }

    @Override
    public PacketType<ClientboundForgetLevelChunkPacket> type() {
        return GamePacketTypes.CLIENTBOUND_FORGET_LEVEL_CHUNK;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleForgetLevelChunk(this);
    }
}

