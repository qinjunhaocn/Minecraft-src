/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseCoralPlantTypeBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 4.0);

    protected BaseCoralPlantTypeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
    }

    protected abstract MapCodec<? extends BaseCoralPlantTypeBlock> codec();

    protected void tryScheduleDieTick(BlockState $$0, BlockGetter $$1, ScheduledTickAccess $$2, RandomSource $$3, BlockPos $$4) {
        if (!BaseCoralPlantTypeBlock.scanForWater($$0, $$1, $$4)) {
            $$2.scheduleTick($$4, this, 60 + $$3.nextInt(40));
        }
    }

    protected static boolean scanForWater(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return true;
        }
        for (Direction $$3 : Direction.values()) {
            if (!$$1.getFluidState($$2.relative($$3)).is(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$1.is(FluidTags.WATER) && $$1.getAmount() == 8);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if ($$4 == Direction.DOWN && !this.canSurvive($$0, $$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockPos $$3 = $$2.below();
        return $$1.getBlockState($$3).isFaceSturdy($$1, $$3, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }
}

