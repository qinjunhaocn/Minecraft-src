/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;

public record WorldGenSettings(WorldOptions options, WorldDimensions dimensions) {
    public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)WorldOptions.CODEC.forGetter(WorldGenSettings::options), (App)WorldDimensions.CODEC.forGetter(WorldGenSettings::dimensions)).apply((Applicative)$$0, $$0.stable(WorldGenSettings::new)));

    public static <T> DataResult<T> encode(DynamicOps<T> $$0, WorldOptions $$1, WorldDimensions $$2) {
        return CODEC.encodeStart($$0, (Object)new WorldGenSettings($$1, $$2));
    }

    public static <T> DataResult<T> encode(DynamicOps<T> $$0, WorldOptions $$1, RegistryAccess $$2) {
        return WorldGenSettings.encode($$0, $$1, new WorldDimensions((Registry<LevelStem>)$$2.lookupOrThrow(Registries.LEVEL_STEM)));
    }
}

