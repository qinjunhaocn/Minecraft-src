/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;

public record ClientboundSetScorePacket(String owner, String objectiveName, int score, Optional<Component> display, Optional<NumberFormat> numberFormat) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetScorePacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClientboundSetScorePacket::owner, ByteBufCodecs.STRING_UTF8, ClientboundSetScorePacket::objectiveName, ByteBufCodecs.VAR_INT, ClientboundSetScorePacket::score, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, ClientboundSetScorePacket::display, NumberFormatTypes.OPTIONAL_STREAM_CODEC, ClientboundSetScorePacket::numberFormat, ClientboundSetScorePacket::new);

    @Override
    public PacketType<ClientboundSetScorePacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_SCORE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetScore(this);
    }
}

