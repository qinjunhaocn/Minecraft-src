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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class EndRodBlock
extends RodBlock {
    public static final MapCodec<EndRodBlock> CODEC = EndRodBlock.simpleCodec(EndRodBlock::new);

    public MapCodec<EndRodBlock> codec() {
        return CODEC;
    }

    protected EndRodBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$1 = $$0.getClickedFace();
        BlockState $$2 = $$0.getLevel().getBlockState($$0.getClickedPos().relative($$1.getOpposite()));
        if ($$2.is(this) && $$2.getValue(FACING) == $$1) {
            return (BlockState)this.defaultBlockState().setValue(FACING, $$1.getOpposite());
        }
        return (BlockState)this.defaultBlockState().setValue(FACING, $$1);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        Direction $$4 = (Direction)$$0.getValue(FACING);
        double $$5 = (double)$$2.getX() + 0.55 - (double)($$3.nextFloat() * 0.1f);
        double $$6 = (double)$$2.getY() + 0.55 - (double)($$3.nextFloat() * 0.1f);
        double $$7 = (double)$$2.getZ() + 0.55 - (double)($$3.nextFloat() * 0.1f);
        double $$8 = 0.4f - ($$3.nextFloat() + $$3.nextFloat()) * 0.4f;
        if ($$3.nextInt(5) == 0) {
            $$1.addParticle(ParticleTypes.END_ROD, $$5 + (double)$$4.getStepX() * $$8, $$6 + (double)$$4.getStepY() * $$8, $$7 + (double)$$4.getStepZ() * $$8, $$3.nextGaussian() * 0.005, $$3.nextGaussian() * 0.005, $$3.nextGaussian() * 0.005);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING);
    }
}

