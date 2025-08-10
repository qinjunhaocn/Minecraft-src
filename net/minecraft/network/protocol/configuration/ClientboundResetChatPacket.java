/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ConfigurationPacketTypes;

public class ClientboundResetChatPacket
implements Packet<ClientConfigurationPacketListener> {
    public static final ClientboundResetChatPacket INSTANCE = new ClientboundResetChatPacket();
    public static final StreamCodec<ByteBuf, ClientboundResetChatPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientboundResetChatPacket() {
    }

    @Override
    public PacketType<ClientboundResetChatPacket> type() {
        return ConfigurationPacketTypes.CLIENTBOUND_RESET_CHAT;
    }

    @Override
    public void handle(ClientConfigurationPacketListener $$0) {
        $$0.handleResetChat(this);
    }
}

