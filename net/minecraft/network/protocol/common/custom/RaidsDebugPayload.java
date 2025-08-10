/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RaidsDebugPayload(List<BlockPos> raidCenters) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, RaidsDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(RaidsDebugPayload::write, RaidsDebugPayload::new);
    public static final CustomPacketPayload.Type<RaidsDebugPayload> TYPE = CustomPacketPayload.createType("debug/raids");

    private RaidsDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readList(BlockPos.STREAM_CODEC));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.raidCenters, BlockPos.STREAM_CODEC);
    }

    public CustomPacketPayload.Type<RaidsDebugPayload> type() {
        return TYPE;
    }
}

