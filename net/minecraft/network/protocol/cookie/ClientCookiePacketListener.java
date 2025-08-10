/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.cookie;

import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;

public interface ClientCookiePacketListener
extends ClientboundPacketListener {
    public void handleRequestCookie(ClientboundCookieRequestPacket var1);
}

