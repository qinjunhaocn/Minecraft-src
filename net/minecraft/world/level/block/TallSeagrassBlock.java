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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallSeagrassBlock
extends DoublePlantBlock
implements LiquidBlockContainer {
    public static final MapCodec<TallSeagrassBlock> CODEC = TallSeagrassBlock.simpleCodec(TallSeagrassBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
    private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 16.0);

    public MapCodec<TallSeagrassBlock> codec() {
        return CODEC;
    }

    public TallSeagrassBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.isFaceSturdy($$1, $$2, Direction.UP) && !$$0.is(Blocks.MAGMA_BLOCK);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return new ItemStack(Blocks.SEAGRASS);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$2;
        BlockState $$1 = super.getStateForPlacement($$0);
        if ($$1 != null && ($$2 = $$0.getLevel().getFluidState($$0.getClickedPos().above())).is(FluidTags.WATER) && $$2.getAmount() == 8) {
            return $$1;
        }
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        if ($$0.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState $$3 = $$1.getBlockState($$2.below());
            return $$3.is(this) && $$3.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        FluidState $$4 = $$1.getFluidState($$2);
        return super.canSurvive($$0, $$1, $$2) && $$4.is(FluidTags.WATER) && $$4.getAmount() == 8;
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        return Fluids.WATER.getSource(false);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, Fluid $$4) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        return false;
    }
}

