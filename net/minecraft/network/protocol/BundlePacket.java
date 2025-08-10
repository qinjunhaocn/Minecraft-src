/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public abstract class BundlePacket<T extends PacketListener>
implements Packet<T> {
    private final Iterable<Packet<? super T>> packets;

    protected BundlePacket(Iterable<Packet<? super T>> $$0) {
        this.packets = $$0;
    }

    public final Iterable<Packet<? super T>> subPackets() {
        return this.packets;
    }

    @Override
    public abstract PacketType<? extends BundlePacket<T>> type();
}

