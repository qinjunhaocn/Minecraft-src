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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class PumpkinBlock
extends Block {
    public static final MapCodec<PumpkinBlock> CODEC = PumpkinBlock.simpleCodec(PumpkinBlock::new);

    public MapCodec<PumpkinBlock> codec() {
        return CODEC;
    }

    protected PumpkinBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        if (!$$0.is(Items.SHEARS)) {
            return super.useItemOn($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
        if ($$2.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Direction $$7 = $$6.getDirection();
        Direction $$8 = $$7.getAxis() == Direction.Axis.Y ? $$4.getDirection().getOpposite() : $$7;
        $$2.playSound(null, $$3, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f);
        $$2.setBlock($$3, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, $$8), 11);
        ItemEntity $$9 = new ItemEntity($$2, (double)$$3.getX() + 0.5 + (double)$$8.getStepX() * 0.65, (double)$$3.getY() + 0.1, (double)$$3.getZ() + 0.5 + (double)$$8.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
        $$9.setDeltaMovement(0.05 * (double)$$8.getStepX() + $$2.random.nextDouble() * 0.02, 0.05, 0.05 * (double)$$8.getStepZ() + $$2.random.nextDouble() * 0.02);
        $$2.addFreshEntity($$9);
        $$0.hurtAndBreak(1, (LivingEntity)$$4, LivingEntity.getSlotForHand($$5));
        $$2.gameEvent((Entity)$$4, GameEvent.SHEAR, $$3);
        $$4.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
        return InteractionResult.SUCCESS;
    }
}

