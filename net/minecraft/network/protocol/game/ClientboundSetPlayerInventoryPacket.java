/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.item.ItemStack;

public record ClientboundSetPlayerInventoryPacket(int slot, ItemStack contents) implements Packet<ClientGamePacketListener>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetPlayerInventoryPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundSetPlayerInventoryPacket::slot, ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSetPlayerInventoryPacket::contents, ClientboundSetPlayerInventoryPacket::new);

    @Override
    public PacketType<ClientboundSetPlayerInventoryPacket> type() {
        return GamePacketTypes.CLIENTBOUND_SET_PLAYER_INVENTORY;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetPlayerInventory(this);
    }
}

