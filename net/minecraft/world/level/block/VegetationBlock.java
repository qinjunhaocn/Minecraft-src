/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class VegetationBlock
extends Block {
    protected VegetationBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract MapCodec<? extends VegetationBlock> codec();

    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(BlockTags.DIRT) || $$0.is(Blocks.FARMLAND);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (!$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockPos $$3 = $$2.below();
        return this.mayPlaceOn($$1.getBlockState($$3), $$1, $$3);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return $$0.getFluidState().isEmpty();
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        if ($$1 == PathComputationType.AIR && !this.hasCollision) {
            return true;
        }
        return super.isPathfindable($$0, $$1);
    }
}

