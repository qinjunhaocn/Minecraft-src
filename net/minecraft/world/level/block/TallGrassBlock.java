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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallGrassBlock
extends VegetationBlock
implements BonemealableBlock {
    public static final MapCodec<TallGrassBlock> CODEC = TallGrassBlock.simpleCodec(TallGrassBlock::new);
    private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 13.0);

    public MapCodec<TallGrassBlock> codec() {
        return CODEC;
    }

    protected TallGrassBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return TallGrassBlock.getGrownBlock($$2).defaultBlockState().canSurvive($$0, $$1) && $$0.isEmptyBlock($$1.above());
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        DoublePlantBlock.placeAt($$0, TallGrassBlock.getGrownBlock($$3).defaultBlockState(), $$2, 2);
    }

    private static DoublePlantBlock getGrownBlock(BlockState $$0) {
        return (DoublePlantBlock)($$0.is(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
    }
}

