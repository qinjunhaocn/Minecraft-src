/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network;

import net.minecraft.network.PacketListener;

public interface TickablePacketListener
extends PacketListener {
    public void tick();
}

