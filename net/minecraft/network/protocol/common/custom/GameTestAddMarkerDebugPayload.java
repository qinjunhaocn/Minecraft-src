/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GameTestAddMarkerDebugPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, GameTestAddMarkerDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GameTestAddMarkerDebugPayload::write, GameTestAddMarkerDebugPayload::new);
    public static final CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_test_add_marker");

    private GameTestAddMarkerDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readBlockPos(), $$0.readInt(), $$0.readUtf(), $$0.readInt());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeInt(this.color);
        $$0.writeUtf(this.text);
        $$0.writeInt(this.durationMs);
    }

    public CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> type() {
        return TYPE;
    }
}

