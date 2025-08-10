/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem
extends Container {
    public ItemStack getTheItem();

    default public ItemStack splitTheItem(int $$0) {
        return this.getTheItem().split($$0);
    }

    public void setTheItem(ItemStack var1);

    default public ItemStack removeTheItem() {
        return this.splitTheItem(this.getMaxStackSize());
    }

    @Override
    default public int getContainerSize() {
        return 1;
    }

    @Override
    default public boolean isEmpty() {
        return this.getTheItem().isEmpty();
    }

    @Override
    default public void clearContent() {
        this.removeTheItem();
    }

    @Override
    default public ItemStack removeItemNoUpdate(int $$0) {
        return this.removeItem($$0, this.getMaxStackSize());
    }

    @Override
    default public ItemStack getItem(int $$0) {
        return $$0 == 0 ? this.getTheItem() : ItemStack.EMPTY;
    }

    @Override
    default public ItemStack removeItem(int $$0, int $$1) {
        if ($$0 != 0) {
            return ItemStack.EMPTY;
        }
        return this.splitTheItem($$1);
    }

    @Override
    default public void setItem(int $$0, ItemStack $$1) {
        if ($$0 == 0) {
            this.setTheItem($$1);
        }
    }

    public static interface BlockContainerSingleItem
    extends ContainerSingleItem {
        public BlockEntity getContainerBlockEntity();

        @Override
        default public boolean stillValid(Player $$0) {
            return Container.stillValidBlockEntity(this.getContainerBlockEntity(), $$0);
        }
    }
}

