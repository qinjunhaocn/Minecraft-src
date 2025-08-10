/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StairBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final MapCodec<StairBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockState.CODEC.fieldOf("base_state").forGetter($$0 -> $$0.baseState), StairBlock.propertiesCodec()).apply((Applicative)$$02, StairBlock::new));
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_OUTER = Shapes.or(Block.column(16.0, 0.0, 8.0), Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0));
    private static final VoxelShape SHAPE_STRAIGHT = Shapes.or(SHAPE_OUTER, Shapes.rotate(SHAPE_OUTER, OctahedralGroup.fromXYAngles(Quadrant.R0, Quadrant.R90)));
    private static final VoxelShape SHAPE_INNER = Shapes.or(SHAPE_STRAIGHT, Shapes.rotate(SHAPE_STRAIGHT, OctahedralGroup.fromXYAngles(Quadrant.R0, Quadrant.R90)));
    private static final Map<Direction, VoxelShape> SHAPE_BOTTOM_OUTER = Shapes.rotateHorizontal(SHAPE_OUTER);
    private static final Map<Direction, VoxelShape> SHAPE_BOTTOM_STRAIGHT = Shapes.rotateHorizontal(SHAPE_STRAIGHT);
    private static final Map<Direction, VoxelShape> SHAPE_BOTTOM_INNER = Shapes.rotateHorizontal(SHAPE_INNER);
    private static final Map<Direction, VoxelShape> SHAPE_TOP_OUTER = Shapes.rotateHorizontal(Shapes.rotate(SHAPE_OUTER, OctahedralGroup.INVERT_Y));
    private static final Map<Direction, VoxelShape> SHAPE_TOP_STRAIGHT = Shapes.rotateHorizontal(Shapes.rotate(SHAPE_STRAIGHT, OctahedralGroup.INVERT_Y));
    private static final Map<Direction, VoxelShape> SHAPE_TOP_INNER = Shapes.rotateHorizontal(Shapes.rotate(SHAPE_INNER, OctahedralGroup.INVERT_Y));
    private final Block base;
    protected final BlockState baseState;

    public MapCodec<? extends StairBlock> codec() {
        return CODEC;
    }

    protected StairBlock(BlockState $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HALF, Half.BOTTOM)).setValue(SHAPE, StairsShape.STRAIGHT)).setValue(WATERLOGGED, false));
        this.base = $$0.getBlock();
        this.baseState = $$0;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        boolean $$4 = $$0.getValue(HALF) == Half.BOTTOM;
        Direction $$5 = $$0.getValue(FACING);
        return (switch ($$0.getValue(SHAPE)) {
            default -> throw new MatchException(null, null);
            case StairsShape.STRAIGHT -> {
                if ($$4) {
                    yield SHAPE_BOTTOM_STRAIGHT;
                }
                yield SHAPE_TOP_STRAIGHT;
            }
            case StairsShape.INNER_RIGHT, StairsShape.INNER_LEFT -> {
                if ($$4) {
                    yield SHAPE_BOTTOM_INNER;
                }
                yield SHAPE_TOP_INNER;
            }
            case StairsShape.OUTER_LEFT, StairsShape.OUTER_RIGHT -> $$4 ? SHAPE_BOTTOM_OUTER : SHAPE_TOP_OUTER;
        }).get(switch ($$0.getValue(SHAPE)) {
            default -> throw new MatchException(null, null);
            case StairsShape.STRAIGHT, StairsShape.OUTER_LEFT, StairsShape.INNER_RIGHT -> $$5;
            case StairsShape.INNER_LEFT -> $$5.getCounterClockWise();
            case StairsShape.OUTER_RIGHT -> $$5.getClockWise();
        });
    }

    @Override
    public float getExplosionResistance() {
        return this.base.getExplosionResistance();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getClickedFace();
        BlockPos $$2 = $$0.getClickedPos();
        FluidState $$3 = $$0.getLevel().getFluidState($$2);
        BlockState $$4 = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection())).setValue(HALF, $$1 == Direction.DOWN || $$1 != Direction.UP && $$0.getClickLocation().y - (double)$$2.getY() > 0.5 ? Half.TOP : Half.BOTTOM)).setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
        return (BlockState)$$4.setValue(SHAPE, StairBlock.getStairsShape($$4, $$0.getLevel(), $$2));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if ($$4.getAxis().isHorizontal()) {
            return (BlockState)$$0.setValue(SHAPE, StairBlock.getStairsShape($$0, $$1, $$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private static StairsShape getStairsShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        Direction $$7;
        Direction $$5;
        Direction $$3 = $$0.getValue(FACING);
        BlockState $$4 = $$1.getBlockState($$2.relative($$3));
        if (StairBlock.isStairs($$4) && $$0.getValue(HALF) == $$4.getValue(HALF) && ($$5 = $$4.getValue(FACING)).getAxis() != $$0.getValue(FACING).getAxis() && StairBlock.canTakeShape($$0, $$1, $$2, $$5.getOpposite())) {
            if ($$5 == $$3.getCounterClockWise()) {
                return StairsShape.OUTER_LEFT;
            }
            return StairsShape.OUTER_RIGHT;
        }
        BlockState $$6 = $$1.getBlockState($$2.relative($$3.getOpposite()));
        if (StairBlock.isStairs($$6) && $$0.getValue(HALF) == $$6.getValue(HALF) && ($$7 = $$6.getValue(FACING)).getAxis() != $$0.getValue(FACING).getAxis() && StairBlock.canTakeShape($$0, $$1, $$2, $$7)) {
            if ($$7 == $$3.getCounterClockWise()) {
                return StairsShape.INNER_LEFT;
            }
            return StairsShape.INNER_RIGHT;
        }
        return StairsShape.STRAIGHT;
    }

    private static boolean canTakeShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        BlockState $$4 = $$1.getBlockState($$2.relative($$3));
        return !StairBlock.isStairs($$4) || $$4.getValue(FACING) != $$0.getValue(FACING) || $$4.getValue(HALF) != $$0.getValue(HALF);
    }

    public static boolean isStairs(BlockState $$0) {
        return $$0.getBlock() instanceof StairBlock;
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        Direction $$2 = $$0.getValue(FACING);
        StairsShape $$3 = $$0.getValue(SHAPE);
        switch ($$1) {
            case LEFT_RIGHT: {
                if ($$2.getAxis() != Direction.Axis.Z) break;
                switch ($$3) {
                    case INNER_LEFT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                    }
                    case INNER_RIGHT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                    }
                    case OUTER_LEFT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                    }
                    case OUTER_RIGHT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                    }
                }
                return $$0.rotate(Rotation.CLOCKWISE_180);
            }
            case FRONT_BACK: {
                if ($$2.getAxis() != Direction.Axis.X) break;
                switch ($$3) {
                    case INNER_LEFT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                    }
                    case INNER_RIGHT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                    }
                    case OUTER_LEFT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                    }
                    case OUTER_RIGHT: {
                        return (BlockState)$$0.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                    }
                    case STRAIGHT: {
                        return $$0.rotate(Rotation.CLOCKWISE_180);
                    }
                }
                break;
            }
        }
        return super.mirror($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, HALF, SHAPE, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

