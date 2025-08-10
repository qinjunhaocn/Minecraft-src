/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.presets;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

public class WorldPreset {
    public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter($$0 -> $$0.dimensions)).apply((Applicative)$$02, WorldPreset::new)).validate(WorldPreset::requireOverworld);
    public static final Codec<Holder<WorldPreset>> CODEC = RegistryFileCodec.create(Registries.WORLD_PRESET, DIRECT_CODEC);
    private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;

    public WorldPreset(Map<ResourceKey<LevelStem>, LevelStem> $$0) {
        this.dimensions = $$0;
    }

    private ImmutableMap<ResourceKey<LevelStem>, LevelStem> dimensionsInOrder() {
        ImmutableMap.Builder $$0 = ImmutableMap.builder();
        WorldDimensions.keysInOrder(this.dimensions.keySet().stream()).forEach($$1 -> {
            LevelStem $$2 = this.dimensions.get($$1);
            if ($$2 != null) {
                $$0.put($$1, $$2);
            }
        });
        return $$0.build();
    }

    public WorldDimensions createWorldDimensions() {
        return new WorldDimensions(this.dimensionsInOrder());
    }

    public Optional<LevelStem> overworld() {
        return Optional.ofNullable(this.dimensions.get(LevelStem.OVERWORLD));
    }

    private static DataResult<WorldPreset> requireOverworld(WorldPreset $$0) {
        if ($$0.overworld().isEmpty()) {
            return DataResult.error(() -> "Missing overworld dimension");
        }
        return DataResult.success((Object)$$0, (Lifecycle)Lifecycle.stable());
    }
}

