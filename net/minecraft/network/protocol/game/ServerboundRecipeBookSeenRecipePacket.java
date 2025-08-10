/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ServerboundRecipeBookSeenRecipePacket(RecipeDisplayId recipe) implements Packet<ServerGamePacketListener>
{
    public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookSeenRecipePacket> STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC, ServerboundRecipeBookSeenRecipePacket::recipe, ServerboundRecipeBookSeenRecipePacket::new);

    @Override
    public PacketType<ServerboundRecipeBookSeenRecipePacket> type() {
        return GamePacketTypes.SERVERBOUND_RECIPE_BOOK_SEEN_RECIPE;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleRecipeBookSeenRecipePacket(this);
    }
}

