/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FallenTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

public class FallenTreeFeature
extends Feature<FallenTreeConfiguration> {
    private static final int STUMP_HEIGHT = 1;
    private static final int STUMP_HEIGHT_PLUS_EMPTY_SPACE = 2;
    private static final int FALLEN_LOG_MAX_FALL_HEIGHT_TO_GROUND = 5;
    private static final int FALLEN_LOG_MAX_GROUND_GAP = 2;
    private static final int FALLEN_LOG_MAX_SPACE_FROM_STUMP = 2;
    private static final int BLOCK_UPDATE_FLAGS = 19;

    public FallenTreeFeature(Codec<FallenTreeConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<FallenTreeConfiguration> $$0) {
        this.placeFallenTree($$0.config(), $$0.origin(), $$0.level(), $$0.random());
        return true;
    }

    private void placeFallenTree(FallenTreeConfiguration $$0, BlockPos $$1, WorldGenLevel $$2, RandomSource $$3) {
        this.placeStump($$0, $$2, $$3, $$1.mutable());
        Direction $$4 = Direction.Plane.HORIZONTAL.getRandomDirection($$3);
        int $$5 = $$0.logLength.sample($$3) - 2;
        BlockPos.MutableBlockPos $$6 = $$1.relative($$4, 2 + $$3.nextInt(2)).mutable();
        this.setGroundHeightForFallenLogStartPos($$2, $$6);
        if (this.canPlaceEntireFallenLog($$2, $$5, $$6, $$4)) {
            this.placeFallenLog($$0, $$2, $$3, $$5, $$6, $$4);
        }
    }

    private void setGroundHeightForFallenLogStartPos(WorldGenLevel $$0, BlockPos.MutableBlockPos $$1) {
        $$1.move(Direction.UP, 1);
        for (int $$2 = 0; $$2 < 6; ++$$2) {
            if (this.mayPlaceOn($$0, $$1)) {
                return;
            }
            $$1.move(Direction.DOWN);
        }
    }

    private void placeStump(FallenTreeConfiguration $$0, WorldGenLevel $$1, RandomSource $$2, BlockPos.MutableBlockPos $$3) {
        BlockPos $$4 = this.placeLogBlock($$0, $$1, $$2, $$3, Function.identity());
        this.decorateLogs($$1, $$2, Set.of((Object)$$4), $$0.stumpDecorators);
    }

    private boolean canPlaceEntireFallenLog(WorldGenLevel $$0, int $$1, BlockPos.MutableBlockPos $$2, Direction $$3) {
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$1; ++$$5) {
            if (!TreeFeature.validTreePos($$0, $$2)) {
                return false;
            }
            if (!this.isOverSolidGround($$0, $$2)) {
                if (++$$4 > 2) {
                    return false;
                }
            } else {
                $$4 = 0;
            }
            $$2.move($$3);
        }
        $$2.move($$3.getOpposite(), $$1);
        return true;
    }

    private void placeFallenLog(FallenTreeConfiguration $$0, WorldGenLevel $$1, RandomSource $$2, int $$3, BlockPos.MutableBlockPos $$4, Direction $$5) {
        HashSet<BlockPos> $$6 = new HashSet<BlockPos>();
        for (int $$7 = 0; $$7 < $$3; ++$$7) {
            $$6.add(this.placeLogBlock($$0, $$1, $$2, $$4, FallenTreeFeature.getSidewaysStateModifier($$5)));
            $$4.move($$5);
        }
        this.decorateLogs($$1, $$2, $$6, $$0.logDecorators);
    }

    private boolean mayPlaceOn(LevelAccessor $$0, BlockPos $$1) {
        return TreeFeature.validTreePos($$0, $$1) && this.isOverSolidGround($$0, $$1);
    }

    private boolean isOverSolidGround(LevelAccessor $$0, BlockPos $$1) {
        return $$0.getBlockState($$1.below()).isFaceSturdy($$0, $$1, Direction.UP);
    }

    private BlockPos placeLogBlock(FallenTreeConfiguration $$0, WorldGenLevel $$1, RandomSource $$2, BlockPos.MutableBlockPos $$3, Function<BlockState, BlockState> $$4) {
        $$1.setBlock($$3, $$4.apply($$0.trunkProvider.getState($$2, $$3)), 3);
        this.markAboveForPostProcessing($$1, $$3);
        return $$3.immutable();
    }

    private void decorateLogs(WorldGenLevel $$0, RandomSource $$12, Set<BlockPos> $$2, List<TreeDecorator> $$3) {
        if (!$$3.isEmpty()) {
            TreeDecorator.Context $$4 = new TreeDecorator.Context($$0, this.getDecorationSetter($$0), $$12, $$2, Set.of(), Set.of());
            $$3.forEach($$1 -> $$1.place($$4));
        }
    }

    private BiConsumer<BlockPos, BlockState> getDecorationSetter(WorldGenLevel $$0) {
        return ($$1, $$2) -> $$0.setBlock((BlockPos)$$1, (BlockState)$$2, 19);
    }

    private static Function<BlockState, BlockState> getSidewaysStateModifier(Direction $$0) {
        return $$1 -> (BlockState)$$1.trySetValue(RotatedPillarBlock.AXIS, $$0.getAxis());
    }
}

