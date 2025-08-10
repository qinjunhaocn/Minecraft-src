/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HopperMenu
extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 5;
    private final Container hopper;

    public HopperMenu(int $$0, Inventory $$1) {
        this($$0, $$1, new SimpleContainer(5));
    }

    public HopperMenu(int $$0, Inventory $$1, Container $$2) {
        super(MenuType.HOPPER, $$0);
        this.hopper = $$2;
        HopperMenu.checkContainerSize($$2, 5);
        $$2.startOpen($$1.player);
        for (int $$3 = 0; $$3 < 5; ++$$3) {
            this.addSlot(new Slot($$2, $$3, 44 + $$3 * 18, 20));
        }
        this.addStandardInventorySlots($$1, 8, 51);
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.hopper.stillValid($$0);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 < this.hopper.getContainerSize() ? !this.moveItemStackTo($$4, this.hopper.getContainerSize(), this.slots.size(), true) : !this.moveItemStackTo($$4, 0, this.hopper.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.setByPlayer(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
        }
        return $$2;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.hopper.stopOpen($$0);
    }
}

