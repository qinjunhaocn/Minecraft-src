/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public record ClientboundRecipeBookAddPacket(List<Entry> entries, boolean replace) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRecipeBookAddPacket> STREAM_CODEC = StreamCodec.composite(Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRecipeBookAddPacket::entries, ByteBufCodecs.BOOL, ClientboundRecipeBookAddPacket::replace, ClientboundRecipeBookAddPacket::new);

    @Override
    public PacketType<ClientboundRecipeBookAddPacket> type() {
        return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_ADD;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleRecipeBookAdd(this);
    }

    public record Entry(RecipeDisplayEntry contents, byte flags) {
        public static final byte FLAG_NOTIFICATION = 1;
        public static final byte FLAG_HIGHLIGHT = 2;
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(RecipeDisplayEntry.STREAM_CODEC, Entry::contents, ByteBufCodecs.BYTE, Entry::flags, Entry::new);

        public Entry(RecipeDisplayEntry $$0, boolean $$1, boolean $$2) {
            this($$0, (byte)(($$1 ? 1 : 0) | ($$2 ? 2 : 0)));
        }

        public boolean notification() {
            return (this.flags & 1) != 0;
        }

        public boolean highlight() {
            return (this.flags & 2) != 0;
        }
    }
}

