/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public class ClientboundBundlePacket
extends BundlePacket<ClientGamePacketListener> {
    public ClientboundBundlePacket(Iterable<Packet<? super ClientGamePacketListener>> $$0) {
        super($$0);
    }

    @Override
    public PacketType<ClientboundBundlePacket> type() {
        return GamePacketTypes.CLIENTBOUND_BUNDLE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBundlePacket(this);
    }
}

