/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public interface ServerPlayerConnection {
    public ServerPlayer getPlayer();

    public void send(Packet<?> var1);
}

