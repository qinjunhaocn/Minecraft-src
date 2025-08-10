/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.stats.RecipeBookSettings;

public record ClientboundRecipeBookSettingsPacket(RecipeBookSettings bookSettings) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ClientboundRecipeBookSettingsPacket> STREAM_CODEC = StreamCodec.composite(RecipeBookSettings.STREAM_CODEC, ClientboundRecipeBookSettingsPacket::bookSettings, ClientboundRecipeBookSettingsPacket::new);

    @Override
    public PacketType<ClientboundRecipeBookSettingsPacket> type() {
        return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_SETTINGS;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRecipeBookSettings(this);
    }
}

