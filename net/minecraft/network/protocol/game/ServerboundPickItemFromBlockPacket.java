/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public record ServerboundPickItemFromBlockPacket(BlockPos pos, boolean includeData) implements Packet<ServerGamePacketListener>
{
    public static final StreamCodec<ByteBuf, ServerboundPickItemFromBlockPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ServerboundPickItemFromBlockPacket::pos, ByteBufCodecs.BOOL, ServerboundPickItemFromBlockPacket::includeData, ServerboundPickItemFromBlockPacket::new);

    @Override
    public PacketType<ServerboundPickItemFromBlockPacket> type() {
        return GamePacketTypes.SERVERBOUND_PICK_ITEM_FROM_BLOCK;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handlePickItemFromBlock(this);
    }
}

