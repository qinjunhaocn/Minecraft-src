/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public abstract class BundleDelimiterPacket<T extends PacketListener>
implements Packet<T> {
    @Override
    public final void handle(T $$0) {
        throw new AssertionError((Object)"This packet should be handled by pipeline");
    }

    @Override
    public abstract PacketType<? extends BundleDelimiterPacket<T>> type();
}

