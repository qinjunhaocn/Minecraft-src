/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.pathfinder.Path;

public record PathfindingDebugPayload(int entityId, Path path, float maxNodeDistance) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, PathfindingDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PathfindingDebugPayload::write, PathfindingDebugPayload::new);
    public static final CustomPacketPayload.Type<PathfindingDebugPayload> TYPE = CustomPacketPayload.createType("debug/path");

    private PathfindingDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readInt(), Path.createFromStream($$0), $$0.readFloat());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeInt(this.entityId);
        this.path.writeToStream($$0);
        $$0.writeFloat(this.maxNodeDistance);
    }

    public CustomPacketPayload.Type<PathfindingDebugPayload> type() {
        return TYPE;
    }
}

