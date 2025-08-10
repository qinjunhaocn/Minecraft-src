/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventTags {
    public static final TagKey<GameEvent> VIBRATIONS = GameEventTags.create("vibrations");
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = GameEventTags.create("warden_can_listen");
    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN = GameEventTags.create("shrieker_can_listen");
    public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = GameEventTags.create("ignore_vibrations_sneaking");
    public static final TagKey<GameEvent> ALLAY_CAN_LISTEN = GameEventTags.create("allay_can_listen");

    private static TagKey<GameEvent> create(String $$0) {
        return TagKey.create(Registries.GAME_EVENT, ResourceLocation.withDefaultNamespace($$0));
    }
}

