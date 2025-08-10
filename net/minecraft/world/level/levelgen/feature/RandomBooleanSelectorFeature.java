/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;

public class RandomBooleanSelectorFeature
extends Feature<RandomBooleanFeatureConfiguration> {
    public RandomBooleanSelectorFeature(Codec<RandomBooleanFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomBooleanFeatureConfiguration> $$0) {
        RandomSource $$1 = $$0.random();
        RandomBooleanFeatureConfiguration $$2 = $$0.config();
        WorldGenLevel $$3 = $$0.level();
        ChunkGenerator $$4 = $$0.chunkGenerator();
        BlockPos $$5 = $$0.origin();
        boolean $$6 = $$1.nextBoolean();
        return ($$6 ? $$2.featureTrue : $$2.featureFalse).value().place($$3, $$4, $$1, $$5);
    }
}

