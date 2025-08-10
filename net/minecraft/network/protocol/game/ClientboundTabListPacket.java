/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public record ClientboundTabListPacket(Component header, Component footer) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTabListPacket> STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundTabListPacket::header, ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundTabListPacket::footer, ClientboundTabListPacket::new);

    @Override
    public PacketType<ClientboundTabListPacket> type() {
        return GamePacketTypes.CLIENTBOUND_TAB_LIST;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleTabListCustomisation(this);
    }
}

