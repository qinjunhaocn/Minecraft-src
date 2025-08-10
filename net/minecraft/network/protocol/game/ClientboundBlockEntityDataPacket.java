/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundBlockEntityDataPacket::getPos, ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE), ClientboundBlockEntityDataPacket::getType, ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundBlockEntityDataPacket::getTag, ClientboundBlockEntityDataPacket::new);
    private final BlockPos pos;
    private final BlockEntityType<?> type;
    private final CompoundTag tag;

    public static ClientboundBlockEntityDataPacket create(BlockEntity $$0, BiFunction<BlockEntity, RegistryAccess, CompoundTag> $$1) {
        RegistryAccess $$2 = $$0.getLevel().registryAccess();
        return new ClientboundBlockEntityDataPacket($$0.getBlockPos(), $$0.getType(), $$1.apply($$0, $$2));
    }

    public static ClientboundBlockEntityDataPacket create(BlockEntity $$0) {
        return ClientboundBlockEntityDataPacket.create($$0, BlockEntity::getUpdateTag);
    }

    private ClientboundBlockEntityDataPacket(BlockPos $$0, BlockEntityType<?> $$1, CompoundTag $$2) {
        this.pos = $$0;
        this.type = $$1;
        this.tag = $$2;
    }

    @Override
    public PacketType<ClientboundBlockEntityDataPacket> type() {
        return GamePacketTypes.CLIENTBOUND_BLOCK_ENTITY_DATA;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleBlockEntityData(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    public CompoundTag getTag() {
        return this.tag;
    }
}

