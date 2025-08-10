/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PoiTicketCountDebugPayload(BlockPos pos, int freeTicketCount) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PoiTicketCountDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PoiTicketCountDebugPayload::write, PoiTicketCountDebugPayload::new);
    public static final CustomPacketPayload.Type<PoiTicketCountDebugPayload> TYPE = CustomPacketPayload.createType("debug/poi_ticket_count");

    private PoiTicketCountDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readBlockPos(), $$0.readInt());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeBlockPos(this.pos);
        $$0.writeInt(this.freeTicketCount);
    }

    public CustomPacketPayload.Type<PoiTicketCountDebugPayload> type() {
        return TYPE;
    }
}

