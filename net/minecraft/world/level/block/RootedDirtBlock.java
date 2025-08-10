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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RootedDirtBlock
extends Block
implements BonemealableBlock {
    public static final MapCodec<RootedDirtBlock> CODEC = RootedDirtBlock.simpleCodec(RootedDirtBlock::new);

    public MapCodec<RootedDirtBlock> codec() {
        return CODEC;
    }

    public RootedDirtBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return $$0.getBlockState($$1.below()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        $$0.setBlockAndUpdate($$2.below(), Blocks.HANGING_ROOTS.defaultBlockState());
    }

    @Override
    public BlockPos getParticlePos(BlockPos $$0) {
        return $$0.below();
    }
}

