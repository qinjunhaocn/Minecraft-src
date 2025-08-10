/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class HugeMushroomBlock
extends Block {
    public static final MapCodec<HugeMushroomBlock> CODEC = HugeMushroomBlock.simpleCodec(HugeMushroomBlock::new);
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public MapCodec<HugeMushroomBlock> codec() {
        return CODEC;
    }

    public HugeMushroomBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, true)).setValue(EAST, true)).setValue(SOUTH, true)).setValue(WEST, true)).setValue(UP, true)).setValue(DOWN, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, !$$1.getBlockState($$2.below()).is(this))).setValue(UP, !$$1.getBlockState($$2.above()).is(this))).setValue(NORTH, !$$1.getBlockState($$2.north()).is(this))).setValue(EAST, !$$1.getBlockState($$2.east()).is(this))).setValue(SOUTH, !$$1.getBlockState($$2.south()).is(this))).setValue(WEST, !$$1.getBlockState($$2.west()).is(this));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$6.is(this)) {
            return (BlockState)$$0.setValue(PROPERTY_BY_DIRECTION.get($$4), false);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.NORTH)), $$0.getValue(NORTH))).setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.SOUTH)), $$0.getValue(SOUTH))).setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.EAST)), $$0.getValue(EAST))).setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.WEST)), $$0.getValue(WEST))).setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.UP)), $$0.getValue(UP))).setValue(PROPERTY_BY_DIRECTION.get($$1.rotate(Direction.DOWN)), $$0.getValue(DOWN));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)$$0.setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.NORTH)), $$0.getValue(NORTH))).setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.SOUTH)), $$0.getValue(SOUTH))).setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.EAST)), $$0.getValue(EAST))).setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.WEST)), $$0.getValue(WEST))).setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.UP)), $$0.getValue(UP))).setValue(PROPERTY_BY_DIRECTION.get($$1.mirror(Direction.DOWN)), $$0.getValue(DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}

