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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;

public class DoublePlantBlock
extends VegetationBlock {
    public static final MapCodec<DoublePlantBlock> CODEC = DoublePlantBlock.simpleCodec(DoublePlantBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public MapCodec<? extends DoublePlantBlock> codec() {
        return CODEC;
    }

    public DoublePlantBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        DoubleBlockHalf $$8 = $$0.getValue(HALF);
        if (!($$4.getAxis() != Direction.Axis.Y || $$8 == DoubleBlockHalf.LOWER != ($$4 == Direction.UP) || $$6.is(this) && $$6.getValue(HALF) != $$8)) {
            return Blocks.AIR.defaultBlockState();
        }
        if ($$8 == DoubleBlockHalf.LOWER && $$4 == Direction.DOWN && !$$0.canSurvive($$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$1 = $$0.getClickedPos();
        Level $$2 = $$0.getLevel();
        if ($$1.getY() < $$2.getMaxY() && $$2.getBlockState($$1.above()).canBeReplaced($$0)) {
            return super.getStateForPlacement($$0);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockPos $$5 = $$1.above();
        $$0.setBlock($$5, DoublePlantBlock.copyWaterloggedFrom($$0, $$5, (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)), 3);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        if ($$0.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState $$3 = $$1.getBlockState($$2.below());
            return $$3.is(this) && $$3.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive($$0, $$1, $$2);
    }

    public static void placeAt(LevelAccessor $$0, BlockState $$1, BlockPos $$2, int $$3) {
        BlockPos $$4 = $$2.above();
        $$0.setBlock($$2, DoublePlantBlock.copyWaterloggedFrom($$0, $$2, (BlockState)$$1.setValue(HALF, DoubleBlockHalf.LOWER)), $$3);
        $$0.setBlock($$4, DoublePlantBlock.copyWaterloggedFrom($$0, $$4, (BlockState)$$1.setValue(HALF, DoubleBlockHalf.UPPER)), $$3);
    }

    public static BlockState copyWaterloggedFrom(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        if ($$2.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return (BlockState)$$2.setValue(BlockStateProperties.WATERLOGGED, $$0.isWaterAt($$1));
        }
        return $$2;
    }

    @Override
    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$0.isClientSide) {
            if ($$3.preventsBlockDrops()) {
                DoublePlantBlock.preventDropFromBottomPart($$0, $$1, $$2, $$3);
            } else {
                DoublePlantBlock.dropResources($$2, $$0, $$1, null, $$3, $$3.getMainHandItem());
            }
        }
        return super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        super.playerDestroy($$0, $$1, $$2, Blocks.AIR.defaultBlockState(), $$4, $$5);
    }

    protected static void preventDropFromBottomPart(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockPos $$5;
        BlockState $$6;
        DoubleBlockHalf $$4 = $$2.getValue(HALF);
        if ($$4 == DoubleBlockHalf.UPPER && ($$6 = $$0.getBlockState($$5 = $$1.below())).is($$2.getBlock()) && $$6.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState $$7 = $$6.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            $$0.setBlock($$5, $$7, 35);
            $$0.levelEvent($$3, 2001, $$5, Block.getId($$6));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(HALF);
    }

    @Override
    protected long getSeed(BlockState $$0, BlockPos $$1) {
        return Mth.getSeed($$1.getX(), $$1.below($$0.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), $$1.getZ());
    }
}

