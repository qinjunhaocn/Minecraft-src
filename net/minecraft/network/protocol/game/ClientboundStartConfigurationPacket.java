/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public class ClientboundStartConfigurationPacket
implements Packet<ClientGamePacketListener> {
    public static final ClientboundStartConfigurationPacket INSTANCE = new ClientboundStartConfigurationPacket();
    public static final StreamCodec<ByteBuf, ClientboundStartConfigurationPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ClientboundStartConfigurationPacket() {
    }

    @Override
    public PacketType<ClientboundStartConfigurationPacket> type() {
        return GamePacketTypes.CLIENTBOUND_START_CONFIGURATION;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleConfigurationStart(this);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
}

