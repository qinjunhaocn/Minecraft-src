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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock
extends PipeBlock {
    public static final MapCodec<ChorusPlantBlock> CODEC = ChorusPlantBlock.simpleCodec(ChorusPlantBlock::new);

    public MapCodec<ChorusPlantBlock> codec() {
        return CODEC;
    }

    protected ChorusPlantBlock(BlockBehaviour.Properties $$0) {
        super(10.0f, $$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return ChorusPlantBlock.getStateWithConnections($$0.getLevel(), $$0.getClickedPos(), this.defaultBlockState());
    }

    public static BlockState getStateWithConnections(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        BlockState $$3 = $$0.getBlockState($$1.below());
        BlockState $$4 = $$0.getBlockState($$1.above());
        BlockState $$5 = $$0.getBlockState($$1.north());
        BlockState $$6 = $$0.getBlockState($$1.east());
        BlockState $$7 = $$0.getBlockState($$1.south());
        BlockState $$8 = $$0.getBlockState($$1.west());
        Block $$9 = $$2.getBlock();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)$$2.trySetValue(DOWN, $$3.is($$9) || $$3.is(Blocks.CHORUS_FLOWER) || $$3.is(Blocks.END_STONE))).trySetValue(UP, $$4.is($$9) || $$4.is(Blocks.CHORUS_FLOWER))).trySetValue(NORTH, $$5.is($$9) || $$5.is(Blocks.CHORUS_FLOWER))).trySetValue(EAST, $$6.is($$9) || $$6.is(Blocks.CHORUS_FLOWER))).trySetValue(SOUTH, $$7.is($$9) || $$7.is(Blocks.CHORUS_FLOWER))).trySetValue(WEST, $$8.is($$9) || $$8.is(Blocks.CHORUS_FLOWER));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (!$$0.canSurvive($$1, $$3)) {
            $$2.scheduleTick($$3, this, 1);
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        boolean $$8 = $$6.is(this) || $$6.is(Blocks.CHORUS_FLOWER) || $$4 == Direction.DOWN && $$6.is(Blocks.END_STONE);
        return (BlockState)$$0.setValue((Property)PROPERTY_BY_DIRECTION.get($$4), $$8);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2.below());
        boolean $$4 = !$$1.getBlockState($$2.above()).isAir() && !$$3.isAir();
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            BlockPos $$6 = $$2.relative($$5);
            BlockState $$7 = $$1.getBlockState($$6);
            if (!$$7.is(this)) continue;
            if ($$4) {
                return false;
            }
            BlockState $$8 = $$1.getBlockState($$6.below());
            if (!$$8.is(this) && !$$8.is(Blocks.END_STONE)) continue;
            return true;
        }
        return $$3.is(this) || $$3.is(Blocks.END_STONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

