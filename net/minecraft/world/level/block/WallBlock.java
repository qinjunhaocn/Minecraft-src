/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final MapCodec<WallBlock> CODEC = WallBlock.simpleCodec(WallBlock::new);
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallSide> EAST = BlockStateProperties.EAST_WALL;
    public static final EnumProperty<WallSide> NORTH = BlockStateProperties.NORTH_WALL;
    public static final EnumProperty<WallSide> SOUTH = BlockStateProperties.SOUTH_WALL;
    public static final EnumProperty<WallSide> WEST = BlockStateProperties.WEST_WALL;
    public static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of((Object)Direction.NORTH, NORTH, (Object)Direction.EAST, EAST, (Object)Direction.SOUTH, SOUTH, (Object)Direction.WEST, WEST)));
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Function<BlockState, VoxelShape> shapes;
    private final Function<BlockState, VoxelShape> collisionShapes;
    private static final VoxelShape TEST_SHAPE_POST = Block.column(2.0, 0.0, 16.0);
    private static final Map<Direction, VoxelShape> TEST_SHAPES_WALL = Shapes.rotateHorizontal(Block.boxZ(2.0, 16.0, 0.0, 9.0));

    public MapCodec<WallBlock> codec() {
        return CODEC;
    }

    public WallBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH, WallSide.NONE)).setValue(EAST, WallSide.NONE)).setValue(SOUTH, WallSide.NONE)).setValue(WEST, WallSide.NONE)).setValue(WATERLOGGED, false));
        this.shapes = this.makeShapes(16.0f, 14.0f);
        this.collisionShapes = this.makeShapes(24.0f, 24.0f);
    }

    private Function<BlockState, VoxelShape> makeShapes(float $$0, float $$1) {
        VoxelShape $$2 = Block.column(8.0, 0.0, $$0);
        int $$32 = 6;
        Map<Direction, VoxelShape> $$4 = Shapes.rotateHorizontal(Block.boxZ(6.0, 0.0, $$1, 0.0, 11.0));
        Map<Direction, VoxelShape> $$5 = Shapes.rotateHorizontal(Block.boxZ(6.0, 0.0, $$0, 0.0, 11.0));
        return this.a($$3 -> {
            VoxelShape $$4 = $$3.getValue(UP) != false ? $$2 : Shapes.empty();
            for (Map.Entry<Direction, EnumProperty<WallSide>> $$5 : PROPERTY_BY_DIRECTION.entrySet()) {
                $$4 = Shapes.or($$4, switch ((WallSide)$$3.getValue($$5.getValue())) {
                    default -> throw new MatchException(null, null);
                    case WallSide.NONE -> Shapes.empty();
                    case WallSide.LOW -> (VoxelShape)$$4.get($$5.getKey());
                    case WallSide.TALL -> (VoxelShape)$$5.get($$5.getKey());
                });
            }
            return $$4;
        }, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.collisionShapes.apply($$0);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }

    private boolean connectsTo(BlockState $$0, boolean $$1, Direction $$2) {
        Block $$3 = $$0.getBlock();
        boolean $$4 = $$3 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection($$0, $$2);
        return $$0.is(BlockTags.WALLS) || !WallBlock.isExceptionForConnection($$0) && $$1 || $$3 instanceof IronBarsBlock || $$4;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        FluidState $$3 = $$0.getLevel().getFluidState($$0.getClickedPos());
        BlockPos $$4 = $$2.north();
        BlockPos $$5 = $$2.east();
        BlockPos $$6 = $$2.south();
        BlockPos $$7 = $$2.west();
        BlockPos $$8 = $$2.above();
        BlockState $$9 = $$1.getBlockState($$4);
        BlockState $$10 = $$1.getBlockState($$5);
        BlockState $$11 = $$1.getBlockState($$6);
        BlockState $$12 = $$1.getBlockState($$7);
        BlockState $$13 = $$1.getBlockState($$8);
        boolean $$14 = this.connectsTo($$9, $$9.isFaceSturdy($$1, $$4, Direction.SOUTH), Direction.SOUTH);
        boolean $$15 = this.connectsTo($$10, $$10.isFaceSturdy($$1, $$5, Direction.WEST), Direction.WEST);
        boolean $$16 = this.connectsTo($$11, $$11.isFaceSturdy($$1, $$6, Direction.NORTH), Direction.NORTH);
        boolean $$17 = this.connectsTo($$12, $$12.isFaceSturdy($$1, $$7, Direction.EAST), Direction.EAST);
        BlockState $$18 = (BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
        return this.updateShape($$1, $$18, $$8, $$13, $$14, $$15, $$16, $$17);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        if ($$4 == Direction.DOWN) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        if ($$4 == Direction.UP) {
            return this.topUpdate($$1, $$0, $$5, $$6);
        }
        return this.sideUpdate($$1, $$3, $$0, $$5, $$6, $$4);
    }

    private static boolean isConnected(BlockState $$0, Property<WallSide> $$1) {
        return $$0.getValue($$1) != WallSide.NONE;
    }

    private static boolean isCovered(VoxelShape $$0, VoxelShape $$1) {
        return !Shapes.joinIsNotEmpty($$1, $$0, BooleanOp.ONLY_FIRST);
    }

    private BlockState topUpdate(LevelReader $$0, BlockState $$1, BlockPos $$2, BlockState $$3) {
        boolean $$4 = WallBlock.isConnected($$1, NORTH);
        boolean $$5 = WallBlock.isConnected($$1, EAST);
        boolean $$6 = WallBlock.isConnected($$1, SOUTH);
        boolean $$7 = WallBlock.isConnected($$1, WEST);
        return this.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private BlockState sideUpdate(LevelReader $$0, BlockPos $$1, BlockState $$2, BlockPos $$3, BlockState $$4, Direction $$5) {
        Direction $$6 = $$5.getOpposite();
        boolean $$7 = $$5 == Direction.NORTH ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, NORTH);
        boolean $$8 = $$5 == Direction.EAST ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, EAST);
        boolean $$9 = $$5 == Direction.SOUTH ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, SOUTH);
        boolean $$10 = $$5 == Direction.WEST ? this.connectsTo($$4, $$4.isFaceSturdy($$0, $$3, $$6), $$6) : WallBlock.isConnected($$2, WEST);
        BlockPos $$11 = $$1.above();
        BlockState $$12 = $$0.getBlockState($$11);
        return this.updateShape($$0, $$2, $$11, $$12, $$7, $$8, $$9, $$10);
    }

    private BlockState updateShape(LevelReader $$0, BlockState $$1, BlockPos $$2, BlockState $$3, boolean $$4, boolean $$5, boolean $$6, boolean $$7) {
        VoxelShape $$8 = $$3.getCollisionShape($$0, $$2).getFaceShape(Direction.DOWN);
        BlockState $$9 = this.updateSides($$1, $$4, $$5, $$6, $$7, $$8);
        return (BlockState)$$9.setValue(UP, this.shouldRaisePost($$9, $$3, $$8));
    }

    private boolean shouldRaisePost(BlockState $$0, BlockState $$1, VoxelShape $$2) {
        boolean $$13;
        boolean $$12;
        boolean $$3;
        boolean bl = $$3 = $$1.getBlock() instanceof WallBlock && $$1.getValue(UP) != false;
        if ($$3) {
            return true;
        }
        WallSide $$4 = $$0.getValue(NORTH);
        WallSide $$5 = $$0.getValue(SOUTH);
        WallSide $$6 = $$0.getValue(EAST);
        WallSide $$7 = $$0.getValue(WEST);
        boolean $$8 = $$5 == WallSide.NONE;
        boolean $$9 = $$7 == WallSide.NONE;
        boolean $$10 = $$6 == WallSide.NONE;
        boolean $$11 = $$4 == WallSide.NONE;
        boolean bl2 = $$12 = $$11 && $$8 && $$9 && $$10 || $$11 != $$8 || $$9 != $$10;
        if ($$12) {
            return true;
        }
        boolean bl3 = $$13 = $$4 == WallSide.TALL && $$5 == WallSide.TALL || $$6 == WallSide.TALL && $$7 == WallSide.TALL;
        if ($$13) {
            return false;
        }
        return $$1.is(BlockTags.WALL_POST_OVERRIDE) || WallBlock.isCovered($$2, TEST_SHAPE_POST);
    }

    private BlockState updateSides(BlockState $$0, boolean $$1, boolean $$2, boolean $$3, boolean $$4, VoxelShape $$5) {
        return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, this.makeWallState($$1, $$5, TEST_SHAPES_WALL.get(Direction.NORTH)))).setValue(EAST, this.makeWallState($$2, $$5, TEST_SHAPES_WALL.get(Direction.EAST)))).setValue(SOUTH, this.makeWallState($$3, $$5, TEST_SHAPES_WALL.get(Direction.SOUTH)))).setValue(WEST, this.makeWallState($$4, $$5, TEST_SHAPES_WALL.get(Direction.WEST)));
    }

    private WallSide makeWallState(boolean $$0, VoxelShape $$1, VoxelShape $$2) {
        if ($$0) {
            if (WallBlock.isCovered($$1, $$2)) {
                return WallSide.TALL;
            }
            return WallSide.LOW;
        }
        return WallSide.NONE;
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return $$0.getValue(WATERLOGGED) == false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(EAST, $$0.getValue(WEST))).setValue(SOUTH, $$0.getValue(NORTH))).setValue(WEST, $$0.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(EAST))).setValue(EAST, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(WEST))).setValue(EAST, $$0.getValue(NORTH))).setValue(SOUTH, $$0.getValue(EAST))).setValue(WEST, $$0.getValue(SOUTH));
            }
        }
        return $$0;
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        switch ($$1) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)$$0.setValue(NORTH, $$0.getValue(SOUTH))).setValue(SOUTH, $$0.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)$$0.setValue(EAST, $$0.getValue(WEST))).setValue(WEST, $$0.getValue(EAST));
            }
        }
        return super.mirror($$0, $$1);
    }
}

