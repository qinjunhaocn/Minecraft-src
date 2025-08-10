/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HiveDebugPayload(HiveInfo hiveInfo) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, HiveDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(HiveDebugPayload::write, HiveDebugPayload::new);
    public static final CustomPacketPayload.Type<HiveDebugPayload> TYPE = CustomPacketPayload.createType("debug/hive");

    private HiveDebugPayload(FriendlyByteBuf $$0) {
        this(new HiveInfo($$0));
    }

    private void write(FriendlyByteBuf $$0) {
        this.hiveInfo.write($$0);
    }

    public CustomPacketPayload.Type<HiveDebugPayload> type() {
        return TYPE;
    }

    public record HiveInfo(BlockPos pos, String hiveType, int occupantCount, int honeyLevel, boolean sedated) {
        public HiveInfo(FriendlyByteBuf $$0) {
            this($$0.readBlockPos(), $$0.readUtf(), $$0.readInt(), $$0.readInt(), $$0.readBoolean());
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeBlockPos(this.pos);
            $$0.writeUtf(this.hiveType);
            $$0.writeInt(this.occupantCount);
            $$0.writeInt(this.honeyLevel);
            $$0.writeBoolean(this.sedated);
        }
    }
}

