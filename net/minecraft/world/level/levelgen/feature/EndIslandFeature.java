/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndIslandFeature
extends Feature<NoneFeatureConfiguration> {
    public EndIslandFeature(Codec<NoneFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        RandomSource $$2 = $$0.random();
        BlockPos $$3 = $$0.origin();
        float $$4 = (float)$$2.nextInt(3) + 4.0f;
        int $$5 = 0;
        while ($$4 > 0.5f) {
            for (int $$6 = Mth.floor(-$$4); $$6 <= Mth.ceil($$4); ++$$6) {
                for (int $$7 = Mth.floor(-$$4); $$7 <= Mth.ceil($$4); ++$$7) {
                    if (!((float)($$6 * $$6 + $$7 * $$7) <= ($$4 + 1.0f) * ($$4 + 1.0f))) continue;
                    this.setBlock($$1, $$3.offset($$6, $$5, $$7), Blocks.END_STONE.defaultBlockState());
                }
            }
            $$4 -= (float)$$2.nextInt(2) + 0.5f;
            --$$5;
        }
        return true;
    }
}

