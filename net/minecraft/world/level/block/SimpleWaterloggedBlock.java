/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface SimpleWaterloggedBlock
extends BucketPickup,
LiquidBlockContainer {
    @Override
    default public boolean canPlaceLiquid(@Nullable LivingEntity $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, Fluid $$4) {
        return $$4 == Fluids.WATER;
    }

    @Override
    default public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if (!$$2.getValue(BlockStateProperties.WATERLOGGED).booleanValue() && $$3.getType() == Fluids.WATER) {
            if (!$$0.isClientSide()) {
                $$0.setBlock($$1, (BlockState)$$2.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
            }
            return true;
        }
        return false;
    }

    @Override
    default public ItemStack pickupBlock(@Nullable LivingEntity $$0, LevelAccessor $$1, BlockPos $$2, BlockState $$3) {
        if ($$3.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
            $$1.setBlock($$2, (BlockState)$$3.setValue(BlockStateProperties.WATERLOGGED, false), 3);
            if (!$$3.canSurvive($$1, $$2)) {
                $$1.destroyBlock($$2, true);
            }
            return new ItemStack(Items.WATER_BUCKET);
        }
        return ItemStack.EMPTY;
    }

    @Override
    default public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}

