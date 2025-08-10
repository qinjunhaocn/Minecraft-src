/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;

public interface ServerboundPacketListener
extends PacketListener {
    @Override
    default public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }
}

