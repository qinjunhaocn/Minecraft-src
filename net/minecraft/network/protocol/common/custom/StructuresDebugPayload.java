/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record StructuresDebugPayload(ResourceKey<Level> dimension, BoundingBox mainBB, List<PieceInfo> pieces) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, StructuresDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(StructuresDebugPayload::write, StructuresDebugPayload::new);
    public static final CustomPacketPayload.Type<StructuresDebugPayload> TYPE = CustomPacketPayload.createType("debug/structures");

    private StructuresDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readResourceKey(Registries.DIMENSION), StructuresDebugPayload.readBoundingBox($$0), $$0.readList(PieceInfo::new));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeResourceKey(this.dimension);
        StructuresDebugPayload.writeBoundingBox($$0, this.mainBB);
        $$0.writeCollection(this.pieces, ($$1, $$2) -> $$2.write($$0));
    }

    public CustomPacketPayload.Type<StructuresDebugPayload> type() {
        return TYPE;
    }

    static BoundingBox readBoundingBox(FriendlyByteBuf $$0) {
        return new BoundingBox($$0.readInt(), $$0.readInt(), $$0.readInt(), $$0.readInt(), $$0.readInt(), $$0.readInt());
    }

    static void writeBoundingBox(FriendlyByteBuf $$0, BoundingBox $$1) {
        $$0.writeInt($$1.minX());
        $$0.writeInt($$1.minY());
        $$0.writeInt($$1.minZ());
        $$0.writeInt($$1.maxX());
        $$0.writeInt($$1.maxY());
        $$0.writeInt($$1.maxZ());
    }

    public record PieceInfo(BoundingBox boundingBox, boolean isStart) {
        public PieceInfo(FriendlyByteBuf $$0) {
            this(StructuresDebugPayload.readBoundingBox($$0), $$0.readBoolean());
        }

        public void write(FriendlyByteBuf $$0) {
            StructuresDebugPayload.writeBoundingBox($$0, this.boundingBox);
            $$0.writeBoolean(this.isStart);
        }
    }
}

