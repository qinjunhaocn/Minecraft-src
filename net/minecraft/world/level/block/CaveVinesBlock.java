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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class CaveVinesBlock
extends GrowingPlantHeadBlock
implements CaveVines {
    public static final MapCodec<CaveVinesBlock> CODEC = CaveVinesBlock.simpleCodec(CaveVinesBlock::new);
    private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11f;

    public MapCodec<CaveVinesBlock> codec() {
        return CODEC;
    }

    public CaveVinesBlock(BlockBehaviour.Properties $$0) {
        super($$0, Direction.DOWN, SHAPE, false, 0.1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(BERRIES, false));
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource $$0) {
        return 1;
    }

    @Override
    protected boolean canGrowInto(BlockState $$0) {
        return $$0.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return Blocks.CAVE_VINES_PLANT;
    }

    @Override
    protected BlockState updateBodyAfterConvertedFromHead(BlockState $$0, BlockState $$1) {
        return (BlockState)$$1.setValue(BERRIES, $$0.getValue(BERRIES));
    }

    @Override
    protected BlockState getGrowIntoState(BlockState $$0, RandomSource $$1) {
        return (BlockState)super.getGrowIntoState($$0, $$1).setValue(BERRIES, $$1.nextFloat() < 0.11f);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return new ItemStack(Items.GLOW_BERRIES);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        return CaveVines.use($$3, $$0, $$1, $$2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        super.createBlockStateDefinition($$0);
        $$0.a(BERRIES);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        return $$2.getValue(BERRIES) == false;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        $$0.setBlock($$2, (BlockState)$$3.setValue(BERRIES, true), 2);
    }
}

