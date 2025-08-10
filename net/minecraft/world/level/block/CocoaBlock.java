/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CocoaBlock
extends HorizontalDirectionalBlock
implements BonemealableBlock {
    public static final MapCodec<CocoaBlock> CODEC = CocoaBlock.simpleCodec(CocoaBlock::new);
    public static final int MAX_AGE = 2;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    private static final List<Map<Direction, VoxelShape>> SHAPES = IntStream.rangeClosed(0, 2).mapToObj($$0 -> Shapes.rotateHorizontal(Block.column(4 + $$0 * 2, 7 - $$0 * 2, 12.0).move(0.0, 0.0, (double)($$0 - 5) / 16.0).optimize())).toList();

    public MapCodec<CocoaBlock> codec() {
        return CODEC;
    }

    public CocoaBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AGE, 0));
    }

    @Override
    protected boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(AGE) < 2;
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        int $$4;
        if ($$1.random.nextInt(5) == 0 && ($$4 = $$0.getValue(AGE).intValue()) < 2) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$4 + 1), 2);
        }
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2.relative((Direction)$$0.getValue(FACING)));
        return $$3.is(BlockTags.JUNGLE_LOGS);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(AGE)).get($$0.getValue(FACING));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = this.defaultBlockState();
        Level $$2 = $$0.getLevel();
        BlockPos $$3 = $$0.getClickedPos();
        for (Direction $$4 : $$0.f()) {
            if (!$$4.getAxis().isHorizontal() || !($$1 = (BlockState)$$1.setValue(FACING, $$4)).canSurvive($$2, $$3)) continue;
            return $$1;
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == $$0.getValue(FACING) && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return $$2.getValue(AGE) < 2;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        $$0.setBlock($$2, (BlockState)$$3.setValue(AGE, $$3.getValue(AGE) + 1), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, AGE);
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

