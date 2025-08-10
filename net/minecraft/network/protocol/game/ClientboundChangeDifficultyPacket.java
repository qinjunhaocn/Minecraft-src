/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.Difficulty;

public record ClientboundChangeDifficultyPacket(Difficulty difficulty, boolean locked) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<ByteBuf, ClientboundChangeDifficultyPacket> STREAM_CODEC = StreamCodec.composite(Difficulty.STREAM_CODEC, ClientboundChangeDifficultyPacket::difficulty, ByteBufCodecs.BOOL, ClientboundChangeDifficultyPacket::locked, ClientboundChangeDifficultyPacket::new);

    @Override
    public PacketType<ClientboundChangeDifficultyPacket> type() {
        return GamePacketTypes.CLIENTBOUND_CHANGE_DIFFICULTY;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleChangeDifficulty(this);
    }
}

