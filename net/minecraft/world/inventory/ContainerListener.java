/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface ContainerListener {
    public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3);

    public void dataChanged(AbstractContainerMenu var1, int var2, int var3);
}

