/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

public class FixedBiomeSource
extends BiomeSource
implements BiomeManager.NoiseBiomeSource {
    public static final MapCodec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, $$0 -> $$0.biome).stable();
    private final Holder<Biome> biome;

    public FixedBiomeSource(Holder<Biome> $$0) {
        this.biome = $$0;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(this.biome);
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2, Climate.Sampler $$3) {
        return this.biome;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        return this.biome;
    }

    @Override
    @Nullable
    public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int $$0, int $$1, int $$2, int $$3, int $$4, Predicate<Holder<Biome>> $$5, RandomSource $$6, boolean $$7, Climate.Sampler $$8) {
        if ($$5.test(this.biome)) {
            if ($$7) {
                return Pair.of((Object)new BlockPos($$0, $$1, $$2), this.biome);
            }
            return Pair.of((Object)new BlockPos($$0 - $$3 + $$6.nextInt($$3 * 2 + 1), $$1, $$2 - $$3 + $$6.nextInt($$3 * 2 + 1)), this.biome);
        }
        return null;
    }

    @Override
    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(BlockPos $$0, int $$1, int $$2, int $$3, Predicate<Holder<Biome>> $$4, Climate.Sampler $$5, LevelReader $$6) {
        return $$4.test(this.biome) ? Pair.of((Object)$$0, this.biome) : null;
    }

    @Override
    public Set<Holder<Biome>> getBiomesWithin(int $$0, int $$1, int $$2, int $$3, Climate.Sampler $$4) {
        return Sets.newHashSet(Set.of(this.biome));
    }
}

