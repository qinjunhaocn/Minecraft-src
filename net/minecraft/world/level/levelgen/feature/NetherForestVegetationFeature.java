/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;

public class NetherForestVegetationFeature
extends Feature<NetherForestVegetationConfig> {
    public NetherForestVegetationFeature(Codec<NetherForestVegetationConfig> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<NetherForestVegetationConfig> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        BlockState $$3 = $$1.getBlockState($$2.below());
        NetherForestVegetationConfig $$4 = $$0.config();
        RandomSource $$5 = $$0.random();
        if (!$$3.is(BlockTags.NYLIUM)) {
            return false;
        }
        int $$6 = $$2.getY();
        if ($$6 < $$1.getMinY() + 1 || $$6 + 1 > $$1.getMaxY()) {
            return false;
        }
        int $$7 = 0;
        for (int $$8 = 0; $$8 < $$4.spreadWidth * $$4.spreadWidth; ++$$8) {
            BlockPos $$9 = $$2.offset($$5.nextInt($$4.spreadWidth) - $$5.nextInt($$4.spreadWidth), $$5.nextInt($$4.spreadHeight) - $$5.nextInt($$4.spreadHeight), $$5.nextInt($$4.spreadWidth) - $$5.nextInt($$4.spreadWidth));
            BlockState $$10 = $$4.stateProvider.getState($$5, $$9);
            if (!$$1.isEmptyBlock($$9) || $$9.getY() <= $$1.getMinY() || !$$10.canSurvive($$1, $$9)) continue;
            $$1.setBlock($$9, $$10, 2);
            ++$$7;
        }
        return $$7 > 0;
    }
}

