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
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock
extends RedstoneTorchBlock {
    public static final MapCodec<RedstoneWallTorchBlock> CODEC = RedstoneWallTorchBlock.simpleCodec(RedstoneWallTorchBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public MapCodec<RedstoneWallTorchBlock> codec() {
        return CODEC;
    }

    protected RedstoneWallTorchBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return WallTorchBlock.getShape($$0);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return WallTorchBlock.canSurvive($$1, $$2, $$0.getValue(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4.getOpposite() == $$0.getValue(FACING) && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return $$0;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = Blocks.WALL_TORCH.getStateForPlacement($$0);
        return $$1 == null ? null : (BlockState)this.defaultBlockState().setValue(FACING, $$1.getValue(FACING));
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        Direction $$4 = $$0.getValue(FACING).getOpposite();
        double $$5 = 0.27;
        double $$6 = (double)$$2.getX() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2 + 0.27 * (double)$$4.getStepX();
        double $$7 = (double)$$2.getY() + 0.7 + ($$3.nextDouble() - 0.5) * 0.2 + 0.22;
        double $$8 = (double)$$2.getZ() + 0.5 + ($$3.nextDouble() - 0.5) * 0.2 + 0.27 * (double)$$4.getStepZ();
        $$1.addParticle(DustParticleOptions.REDSTONE, $$6, $$7, $$8, 0.0, 0.0, 0.0);
    }

    @Override
    protected boolean hasNeighborSignal(Level $$0, BlockPos $$1, BlockState $$2) {
        Direction $$3 = $$2.getValue(FACING).getOpposite();
        return $$0.hasSignal($$1.relative($$3), $$3);
    }

    @Override
    protected int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(LIT).booleanValue() && $$0.getValue(FACING) != $$3) {
            return 15;
        }
        return 0;
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, LIT);
    }

    @Override
    @Nullable
    protected Orientation randomOrientation(Level $$0, BlockState $$1) {
        return ExperimentalRedstoneUtils.initialOrientation($$0, $$1.getValue(FACING).getOpposite(), Direction.UP);
    }
}

