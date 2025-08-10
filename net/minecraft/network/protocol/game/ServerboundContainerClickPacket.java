/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.inventory.ClickType;

public record ServerboundContainerClickPacket(int containerId, int stateId, short slotNum, byte buttonNum, ClickType clickType, Int2ObjectMap<HashedStack> changedSlots, HashedStack carriedItem) implements Packet<ServerGamePacketListener>
{
    private static final int MAX_SLOT_COUNT = 128;
    private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<HashedStack>> SLOTS_STREAM_CODEC = ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue), HashedStack.STREAM_CODEC, 128);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundContainerClickPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ServerboundContainerClickPacket::containerId, ByteBufCodecs.VAR_INT, ServerboundContainerClickPacket::stateId, ByteBufCodecs.SHORT, ServerboundContainerClickPacket::slotNum, ByteBufCodecs.BYTE, ServerboundContainerClickPacket::buttonNum, ClickType.STREAM_CODEC, ServerboundContainerClickPacket::clickType, SLOTS_STREAM_CODEC, ServerboundContainerClickPacket::changedSlots, HashedStack.STREAM_CODEC, ServerboundContainerClickPacket::carriedItem, ServerboundContainerClickPacket::new);

    public ServerboundContainerClickPacket {
        $$5 = Int2ObjectMaps.unmodifiable($$5);
    }

    @Override
    public PacketType<ServerboundContainerClickPacket> type() {
        return GamePacketTypes.SERVERBOUND_CONTAINER_CLICK;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleContainerClick(this);
    }
}

