/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public final class LevelType
extends Enum<LevelType> {
    public static final /* enum */ LevelType DEFAULT = new LevelType(0, WorldPresets.NORMAL);
    public static final /* enum */ LevelType FLAT = new LevelType(1, WorldPresets.FLAT);
    public static final /* enum */ LevelType LARGE_BIOMES = new LevelType(2, WorldPresets.LARGE_BIOMES);
    public static final /* enum */ LevelType AMPLIFIED = new LevelType(3, WorldPresets.AMPLIFIED);
    private final int index;
    private final Component name;
    private static final /* synthetic */ LevelType[] $VALUES;

    public static LevelType[] values() {
        return (LevelType[])$VALUES.clone();
    }

    public static LevelType valueOf(String $$0) {
        return Enum.valueOf(LevelType.class, $$0);
    }

    private LevelType(int $$0, ResourceKey<WorldPreset> $$1) {
        this.index = $$0;
        this.name = Component.translatable($$1.location().toLanguageKey("generator"));
    }

    public Component getName() {
        return this.name;
    }

    public int getDtoIndex() {
        return this.index;
    }

    private static /* synthetic */ LevelType[] c() {
        return new LevelType[]{DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED};
    }

    static {
        $VALUES = LevelType.c();
    }
}

