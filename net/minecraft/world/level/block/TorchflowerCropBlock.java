/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchflowerCropBlock
extends CropBlock {
    public static final MapCodec<TorchflowerCropBlock> CODEC = TorchflowerCropBlock.simpleCodec(TorchflowerCropBlock::new);
    public static final int MAX_AGE = 1;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    private static final VoxelShape[] SHAPES = Block.a(1, $$0 -> Block.column(6.0, 0.0, 6 + $$0 * 4));
    private static final int BONEMEAL_INCREASE = 1;

    public MapCodec<TorchflowerCropBlock> codec() {
        return CODEC;
    }

    public TorchflowerCropBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[this.getAge($$0)];
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 2;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return Items.TORCHFLOWER_SEEDS;
    }

    @Override
    public BlockState getStateForAge(int $$0) {
        if ($$0 == 2) {
            return Blocks.TORCHFLOWER.defaultBlockState();
        }
        return super.getStateForAge($$0);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(3) != 0) {
            super.randomTick($$0, $$1, $$2, $$3);
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level $$0) {
        return 1;
    }
}

