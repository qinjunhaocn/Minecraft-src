/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.status;

import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.ping.ClientPongPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;

public interface ClientStatusPacketListener
extends ClientPongPacketListener,
ClientboundPacketListener {
    @Override
    default public ConnectionProtocol protocol() {
        return ConnectionProtocol.STATUS;
    }

    public void handleStatusResponse(ClientboundStatusResponsePacket var1);
}

