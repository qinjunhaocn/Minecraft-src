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
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirtPathBlock
extends Block {
    public static final MapCodec<DirtPathBlock> CODEC = DirtPathBlock.simpleCodec(DirtPathBlock::new);
    private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 15.0);

    public MapCodec<DirtPathBlock> codec() {
        return CODEC;
    }

    protected DirtPathBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState $$0) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        if (!this.defaultBlockState().canSurvive($$0.getLevel(), $$0.getClickedPos())) {
            return Block.pushEntitiesUp(this.defaultBlockState(), Blocks.DIRT.defaultBlockState(), $$0.getLevel(), $$0.getClickedPos());
        }
        return super.getStateForPlacement($$0);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == Direction.UP && !$$0.canSurvive($$1, $$3)) {
            $$2.scheduleTick($$3, this, 1);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        FarmBlock.turnToDirt(null, $$0, $$1, $$2);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2.above());
        return !$$3.isSolid() || $$3.getBlock() instanceof FenceGateBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState $$0, PathComputationType $$1) {
        return false;
    }
}

