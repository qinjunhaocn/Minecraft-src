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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConduitBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<ConduitBlock> CODEC = ConduitBlock.simpleCodec(ConduitBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.cube(6.0);

    public MapCodec<ConduitBlock> codec() {
        return CODEC;
    }

    public ConduitBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ConduitBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return ConduitBlock.createTickerHelper($$2, BlockEntityType.CONDUIT, $$0.isClientSide ? ConduitBlockEntity::clientTick : ConduitBlockEntity::serverTick);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$1.is(FluidTags.WATER) && $$1.getAmount() == 8);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

