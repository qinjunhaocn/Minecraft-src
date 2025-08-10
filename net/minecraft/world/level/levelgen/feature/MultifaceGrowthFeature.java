/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;

public class MultifaceGrowthFeature
extends Feature<MultifaceGrowthConfiguration> {
    public MultifaceGrowthFeature(Codec<MultifaceGrowthConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<MultifaceGrowthConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        RandomSource $$3 = $$0.random();
        MultifaceGrowthConfiguration $$4 = $$0.config();
        if (!MultifaceGrowthFeature.isAirOrWater($$1.getBlockState($$2))) {
            return false;
        }
        List<Direction> $$5 = $$4.getShuffledDirections($$3);
        if (MultifaceGrowthFeature.placeGrowthIfPossible($$1, $$2, $$1.getBlockState($$2), $$4, $$3, $$5)) {
            return true;
        }
        BlockPos.MutableBlockPos $$6 = $$2.mutable();
        block0: for (Direction $$7 : $$5) {
            $$6.set($$2);
            List<Direction> $$8 = $$4.getShuffledDirectionsExcept($$3, $$7.getOpposite());
            for (int $$9 = 0; $$9 < $$4.searchRange; ++$$9) {
                $$6.setWithOffset((Vec3i)$$2, $$7);
                BlockState $$10 = $$1.getBlockState($$6);
                if (!MultifaceGrowthFeature.isAirOrWater($$10) && !$$10.is($$4.placeBlock)) continue block0;
                if (!MultifaceGrowthFeature.placeGrowthIfPossible($$1, $$6, $$10, $$4, $$3, $$8)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean placeGrowthIfPossible(WorldGenLevel $$0, BlockPos $$1, BlockState $$2, MultifaceGrowthConfiguration $$3, RandomSource $$4, List<Direction> $$5) {
        BlockPos.MutableBlockPos $$6 = $$1.mutable();
        for (Direction $$7 : $$5) {
            BlockState $$8 = $$0.getBlockState($$6.setWithOffset((Vec3i)$$1, $$7));
            if (!$$8.is($$3.canBePlacedOn)) continue;
            BlockState $$9 = $$3.placeBlock.getStateForPlacement($$2, $$0, $$1, $$7);
            if ($$9 == null) {
                return false;
            }
            $$0.setBlock($$1, $$9, 3);
            $$0.getChunk($$1).markPosForPostprocessing($$1);
            if ($$4.nextFloat() < $$3.chanceOfSpreading) {
                $$3.placeBlock.getSpreader().spreadFromFaceTowardRandomDirection($$9, $$0, $$1, $$7, $$4, true);
            }
            return true;
        }
        return false;
    }

    private static boolean isAirOrWater(BlockState $$0) {
        return $$0.isAir() || $$0.is(Blocks.WATER);
    }
}

