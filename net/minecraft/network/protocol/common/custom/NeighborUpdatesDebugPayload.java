/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NeighborUpdatesDebugPayload(long time, BlockPos pos) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, NeighborUpdatesDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(NeighborUpdatesDebugPayload::write, NeighborUpdatesDebugPayload::new);
    public static final CustomPacketPayload.Type<NeighborUpdatesDebugPayload> TYPE = CustomPacketPayload.createType("debug/neighbors_update");

    private NeighborUpdatesDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readVarLong(), $$0.readBlockPos());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeVarLong(this.time);
        $$0.writeBlockPos(this.pos);
    }

    public CustomPacketPayload.Type<NeighborUpdatesDebugPayload> type() {
        return TYPE;
    }
}

