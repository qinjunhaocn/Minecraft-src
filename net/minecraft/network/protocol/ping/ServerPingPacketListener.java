/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.ping;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

public interface ServerPingPacketListener
extends PacketListener {
    public void handlePingRequest(ServerboundPingRequestPacket var1);
}

