/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresetTags {
    public static final TagKey<WorldPreset> NORMAL = WorldPresetTags.create("normal");
    public static final TagKey<WorldPreset> EXTENDED = WorldPresetTags.create("extended");

    private WorldPresetTags() {
    }

    private static TagKey<WorldPreset> create(String $$0) {
        return TagKey.create(Registries.WORLD_PRESET, ResourceLocation.withDefaultNamespace($$0));
    }
}

