/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class MemoryServerHandshakePacketListenerImpl
implements ServerHandshakePacketListener {
    private final MinecraftServer server;
    private final Connection connection;

    public MemoryServerHandshakePacketListenerImpl(MinecraftServer $$0, Connection $$1) {
        this.server = $$0;
        this.connection = $$1;
    }

    @Override
    public void handleIntention(ClientIntentionPacket $$0) {
        if ($$0.intention() != ClientIntent.LOGIN) {
            throw new UnsupportedOperationException("Invalid intention " + String.valueOf((Object)$$0.intention()));
        }
        this.connection.setupInboundProtocol(LoginProtocols.SERVERBOUND, new ServerLoginPacketListenerImpl(this.server, this.connection, false));
        this.connection.setupOutboundProtocol(LoginProtocols.CLIENTBOUND);
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}

