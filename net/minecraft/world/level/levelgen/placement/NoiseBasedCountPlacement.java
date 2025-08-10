/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class NoiseBasedCountPlacement
extends RepeatingPlacement {
    public static final MapCodec<NoiseBasedCountPlacement> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.INT.fieldOf("noise_to_count_ratio").forGetter($$0 -> $$0.noiseToCountRatio), (App)Codec.DOUBLE.fieldOf("noise_factor").forGetter($$0 -> $$0.noiseFactor), (App)Codec.DOUBLE.fieldOf("noise_offset").orElse((Object)0.0).forGetter($$0 -> $$0.noiseOffset)).apply((Applicative)$$02, NoiseBasedCountPlacement::new));
    private final int noiseToCountRatio;
    private final double noiseFactor;
    private final double noiseOffset;

    private NoiseBasedCountPlacement(int $$0, double $$1, double $$2) {
        this.noiseToCountRatio = $$0;
        this.noiseFactor = $$1;
        this.noiseOffset = $$2;
    }

    public static NoiseBasedCountPlacement of(int $$0, double $$1, double $$2) {
        return new NoiseBasedCountPlacement($$0, $$1, $$2);
    }

    @Override
    protected int count(RandomSource $$0, BlockPos $$1) {
        double $$2 = Biome.BIOME_INFO_NOISE.getValue((double)$$1.getX() / this.noiseFactor, (double)$$1.getZ() / this.noiseFactor, false);
        return (int)Math.ceil(($$2 + this.noiseOffset) * (double)this.noiseToCountRatio);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.NOISE_BASED_COUNT;
    }
}

