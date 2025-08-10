/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record GameEventDebugPayload(ResourceKey<GameEvent> gameEventType, Vec3 pos) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, GameEventDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GameEventDebugPayload::write, GameEventDebugPayload::new);
    public static final CustomPacketPayload.Type<GameEventDebugPayload> TYPE = CustomPacketPayload.createType("debug/game_event");

    private GameEventDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readResourceKey(Registries.GAME_EVENT), $$0.readVec3());
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeResourceKey(this.gameEventType);
        $$0.writeVec3(this.pos);
    }

    public CustomPacketPayload.Type<GameEventDebugPayload> type() {
        return TYPE;
    }
}

