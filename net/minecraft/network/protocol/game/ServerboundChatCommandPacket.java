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

public record ServerboundChatCommandPacket(String command) implements Packet<ServerGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandPacket> STREAM_CODEC = Packet.codec(ServerboundChatCommandPacket::write, ServerboundChatCommandPacket::new);

    private ServerboundChatCommandPacket(FriendlyByteBuf $$0) {
        this($$0.readUtf());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.command);
    }

    @Override
    public PacketType<ServerboundChatCommandPacket> type() {
        return GamePacketTypes.SERVERBOUND_CHAT_COMMAND;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleChatCommand(this);
    }
}

