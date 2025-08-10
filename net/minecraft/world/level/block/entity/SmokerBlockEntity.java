/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerBlockEntity
extends AbstractFurnaceBlockEntity {
    public SmokerBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SMOKER, $$0, $$1, RecipeType.SMOKING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.smoker");
    }

    @Override
    protected int getBurnDuration(FuelValues $$0, ItemStack $$1) {
        return super.getBurnDuration($$0, $$1) / 2;
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new SmokerMenu($$0, $$1, this, this.dataAccess);
    }
}

