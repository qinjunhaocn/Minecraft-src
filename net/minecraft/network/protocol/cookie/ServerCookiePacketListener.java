/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.cookie;

import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerCookiePacketListener
extends ServerPacketListener {
    public void handleCookieResponse(ServerboundCookieResponsePacket var1);
}

