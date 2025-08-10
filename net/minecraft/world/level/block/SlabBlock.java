/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final MapCodec<SlabBlock> CODEC = SlabBlock.simpleCodec(SlabBlock::new);
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_BOTTOM = Block.column(16.0, 0.0, 8.0);
    private static final VoxelShape SHAPE_TOP = Block.column(16.0, 8.0, 16.0);

    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    public SlabBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, false));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return $$0.getValue(TYPE) != SlabType.DOUBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(TYPE, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return switch ($$0.getValue(TYPE)) {
            default -> throw new MatchException(null, null);
            case SlabType.TOP -> SHAPE_TOP;
            case SlabType.BOTTOM -> SHAPE_BOTTOM;
            case SlabType.DOUBLE -> Shapes.block();
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        BlockState $$2 = $$0.getLevel().getBlockState($$1);
        if ($$2.is(this)) {
            return (BlockState)((BlockState)$$2.setValue(TYPE, SlabType.DOUBLE)).setValue(WATERLOGGED, false);
        }
        FluidState $$3 = $$0.getLevel().getFluidState($$1);
        BlockState $$4 = (BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
        Direction $$5 = $$0.getClickedFace();
        if ($$5 == Direction.DOWN || $$5 != Direction.UP && $$0.getClickLocation().y - (double)$$1.getY() > 0.5) {
            return (BlockState)$$4.setValue(TYPE, SlabType.TOP);
        }
        return $$4;
    }

    @Override
    protected boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        ItemStack $$2 = $$1.getItemInHand();
        SlabType $$3 = $$0.getValue(TYPE);
        if ($$3 == SlabType.DOUBLE || !$$2.is(this.asItem())) {
            return false;
        }
        if ($$1.replacingClickedOnBlock()) {
            boolean $$4 = $$1.getClickLocation().y - (double)$$1.getClickedPos().getY() > 0.5;
            Direction $$5 = $$1.getClickedFace();
            if ($$3 == SlabType.BOTTOM) {
                return $$5 == Direction.UP || $$4 && $$5.getAxis().isHorizontal();
            }
            return $$5 == Direction.DOWN || !$$4 && $$5.getAxis().isHorizontal();
        }
        return true;
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if ($$2.getValue(TYPE) != SlabType.DOUBLE) {
            return SimpleWaterloggedBlock.super.placeLiquid($$0, $$1, $$2, $$3);
        }
        return false;
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, Fluid $$4) {
        if ($$3.getValue(TYPE) != SlabType.DOUBLE) {
            return SimpleWaterloggedBlock.super.canPlaceLiquid($$0, $$1, $$2, $$3, $$4);
        }
        return false;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        switch ($$1) {
            case LAND: {
                return false;
            }
            case WATER: {
                return $$0.getFluidState().is(FluidTags.WATER);
            }
            case AIR: {
                return false;
            }
        }
        return false;
    }
}

