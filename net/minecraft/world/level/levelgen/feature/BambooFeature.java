/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class BambooFeature
extends Feature<ProbabilityFeatureConfiguration> {
    private static final BlockState BAMBOO_TRUNK = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooStalkBlock.AGE, 1)).setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE)).setValue(BambooStalkBlock.STAGE, 0);
    private static final BlockState BAMBOO_FINAL_LARGE = (BlockState)((BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE)).setValue(BambooStalkBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_LARGE = (BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_SMALL = (BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL);

    public BambooFeature(Codec<ProbabilityFeatureConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> $$0) {
        int $$1 = 0;
        BlockPos $$2 = $$0.origin();
        WorldGenLevel $$3 = $$0.level();
        RandomSource $$4 = $$0.random();
        ProbabilityFeatureConfiguration $$5 = $$0.config();
        BlockPos.MutableBlockPos $$6 = $$2.mutable();
        BlockPos.MutableBlockPos $$7 = $$2.mutable();
        if ($$3.isEmptyBlock($$6)) {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive($$3, $$6)) {
                int $$8 = $$4.nextInt(12) + 5;
                if ($$4.nextFloat() < $$5.probability) {
                    int $$9 = $$4.nextInt(4) + 1;
                    for (int $$10 = $$2.getX() - $$9; $$10 <= $$2.getX() + $$9; ++$$10) {
                        for (int $$11 = $$2.getZ() - $$9; $$11 <= $$2.getZ() + $$9; ++$$11) {
                            int $$13;
                            int $$12 = $$10 - $$2.getX();
                            if ($$12 * $$12 + ($$13 = $$11 - $$2.getZ()) * $$13 > $$9 * $$9) continue;
                            $$7.set($$10, $$3.getHeight(Heightmap.Types.WORLD_SURFACE, $$10, $$11) - 1, $$11);
                            if (!BambooFeature.isDirt($$3.getBlockState($$7))) continue;
                            $$3.setBlock($$7, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                    }
                }
                for (int $$14 = 0; $$14 < $$8 && $$3.isEmptyBlock($$6); ++$$14) {
                    $$3.setBlock($$6, BAMBOO_TRUNK, 2);
                    $$6.move(Direction.UP, 1);
                }
                if ($$6.getY() - $$2.getY() >= 3) {
                    $$3.setBlock($$6, BAMBOO_FINAL_LARGE, 2);
                    $$3.setBlock($$6.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
                    $$3.setBlock($$6.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
                }
            }
            ++$$1;
        }
        return $$1 > 0;
    }
}

