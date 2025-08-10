/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GoalDebugPayload(int entityId, BlockPos pos, List<DebugGoal> goals) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, GoalDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GoalDebugPayload::write, GoalDebugPayload::new);
    public static final CustomPacketPayload.Type<GoalDebugPayload> TYPE = CustomPacketPayload.createType("debug/goal_selector");

    private GoalDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readInt(), $$0.readBlockPos(), $$0.readList(DebugGoal::new));
    }

    private void write(FriendlyByteBuf $$02) {
        $$02.writeInt(this.entityId);
        $$02.writeBlockPos(this.pos);
        $$02.writeCollection(this.goals, ($$0, $$1) -> $$1.write((FriendlyByteBuf)((Object)$$0)));
    }

    public CustomPacketPayload.Type<GoalDebugPayload> type() {
        return TYPE;
    }

    public record DebugGoal(int priority, boolean isRunning, String name) {
        public DebugGoal(FriendlyByteBuf $$0) {
            this($$0.readInt(), $$0.readBoolean(), $$0.readUtf(255));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeInt(this.priority);
            $$0.writeBoolean(this.isRunning);
            $$0.writeUtf(this.name);
        }
    }
}

