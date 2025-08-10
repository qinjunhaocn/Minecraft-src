/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PoiRemovedDebugPayload(BlockPos pos) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PoiRemovedDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PoiRemovedDebugPayload::write, PoiRemovedDebugPayload::new);
    public static final CustomPacketPayload.Type<PoiRemovedDebugPayload> TYPE = CustomPacketPayload.createType("debug/poi_removed");

    private PoiRemovedDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readBlockPos());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
    }

    public CustomPacketPayload.Type<PoiRemovedDebugPayload> type() {
        return TYPE;
    }
}

