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

public record ClientboundSetTitleTextPacket(Component text) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetTitleTextPacket> STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetTitleTextPacket::text, ClientboundSetTitleTextPacket::new);

    @Override
    public PacketType<ClientboundSetTitleTextPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_TITLE_TEXT;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.setTitleText(this);
    }
}

