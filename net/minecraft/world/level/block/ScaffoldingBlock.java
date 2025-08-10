/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final MapCodec<ScaffoldingBlock> CODEC = ScaffoldingBlock.simpleCodec(ScaffoldingBlock::new);
    private static final int TICK_DELAY = 1;
    private static final VoxelShape SHAPE_STABLE = Shapes.or(Block.column(16.0, 14.0, 16.0), Shapes.rotateHorizontal(Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0)).values().stream().reduce(Shapes.empty(), Shapes::or));
    private static final VoxelShape SHAPE_UNSTABLE_BOTTOM = Block.column(16.0, 0.0, 2.0);
    private static final VoxelShape SHAPE_UNSTABLE = Shapes.a(SHAPE_STABLE, SHAPE_UNSTABLE_BOTTOM, Shapes.rotateHorizontal(Block.boxZ(16.0, 0.0, 2.0, 0.0, 2.0)).values().stream().reduce(Shapes.empty(), Shapes::or));
    private static final VoxelShape SHAPE_BELOW_BLOCK = Shapes.block().move(0.0, -1.0, 0.0).optimize();
    public static final int STABILITY_MAX_DISTANCE = 7;
    public static final IntegerProperty DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public MapCodec<ScaffoldingBlock> codec() {
        return CODEC;
    }

    protected ScaffoldingBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(WATERLOGGED, false)).setValue(BOTTOM, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if (!$$3.isHoldingItem($$0.getBlock().asItem())) {
            return $$0.getValue(BOTTOM) != false ? SHAPE_UNSTABLE : SHAPE_STABLE;
        }
        return Shapes.block();
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.block();
    }

    @Override
    protected boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return $$1.getItemInHand().is(this.asItem());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        int $$3 = ScaffoldingBlock.getDistance($$2, $$1);
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$2.getFluidState($$1).getType() == Fluids.WATER)).setValue(DISTANCE, $$3)).setValue(BOTTOM, this.isBottom($$2, $$1, $$3));
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if (!$$1.isClientSide) {
            $$1.scheduleTick($$2, this, 1);
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if (!$$1.isClientSide()) {
            $$2.scheduleTick($$3, this, 1);
        }
        return $$0;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = ScaffoldingBlock.getDistance($$1, $$2);
        BlockState $$5 = (BlockState)((BlockState)$$0.setValue(DISTANCE, $$4)).setValue(BOTTOM, this.isBottom($$1, $$2, $$4));
        if ($$5.getValue(DISTANCE) == 7) {
            if ($$0.getValue(DISTANCE) == 7) {
                FallingBlockEntity.fall($$1, $$2, $$5);
            } else {
                $$1.destroyBlock($$2, true);
            }
        } else if ($$0 != $$5) {
            $$1.setBlock($$2, $$5, 3);
        }
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return ScaffoldingBlock.getDistance($$1, $$2) < 7;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$3.isPlacement()) {
            return Shapes.empty();
        }
        if (!$$3.isAbove(Shapes.block(), $$2, true) || $$3.isDescending()) {
            if ($$0.getValue(DISTANCE) != 0 && $$0.getValue(BOTTOM).booleanValue() && $$3.isAbove(SHAPE_BELOW_BLOCK, $$2, true)) {
                return SHAPE_UNSTABLE_BOTTOM;
            }
            return Shapes.empty();
        }
        return SHAPE_STABLE;
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    private boolean isBottom(BlockGetter $$0, BlockPos $$1, int $$2) {
        return $$2 > 0 && !$$0.getBlockState($$1.below()).is(this);
    }

    public static int getDistance(BlockGetter $$0, BlockPos $$1) {
        Direction $$5;
        BlockState $$6;
        BlockPos.MutableBlockPos $$2 = $$1.mutable().move(Direction.DOWN);
        BlockState $$3 = $$0.getBlockState($$2);
        int $$4 = 7;
        if ($$3.is(Blocks.SCAFFOLDING)) {
            $$4 = $$3.getValue(DISTANCE);
        } else if ($$3.isFaceSturdy($$0, $$2, Direction.UP)) {
            return 0;
        }
        Iterator<Direction> iterator = Direction.Plane.HORIZONTAL.iterator();
        while (iterator.hasNext() && (!($$6 = $$0.getBlockState($$2.setWithOffset((Vec3i)$$1, $$5 = iterator.next()))).is(Blocks.SCAFFOLDING) || ($$4 = Math.min($$4, $$6.getValue(DISTANCE) + 1)) != 1)) {
        }
        return $$4;
    }
}

