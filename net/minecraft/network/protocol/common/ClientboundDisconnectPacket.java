/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.CommonPacketTypes;

public record ClientboundDisconnectPacket(Component reason) implements Packet<ClientCommonPacketListener>
{
    public static final StreamCodec<ByteBuf, ClientboundDisconnectPacket> STREAM_CODEC = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.map(ClientboundDisconnectPacket::new, ClientboundDisconnectPacket::reason);

    @Override
    public PacketType<ClientboundDisconnectPacket> type() {
        return CommonPacketTypes.CLIENTBOUND_DISCONNECT;
    }

    @Override
    public void handle(ClientCommonPacketListener $$0) {
        $$0.handleDisconnect(this);
    }
}

