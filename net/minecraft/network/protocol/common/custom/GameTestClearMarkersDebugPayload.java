/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GameTestClearMarkersDebugPayload() implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, GameTestClearMarkersDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GameTestClearMarkersDebugPayload::write, GameTestClearMarkersDebugPayload::new);
    public static final CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_test_clear");

    private GameTestClearMarkersDebugPayload(FriendlyByteBuf $$0) {
        this();
    }

    private void write(FriendlyByteBuf $$0) {
    }

    public CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> type() {
        return TYPE;
    }
}

