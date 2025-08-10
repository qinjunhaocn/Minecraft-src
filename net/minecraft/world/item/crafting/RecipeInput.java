/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public interface RecipeInput {
    public ItemStack getItem(int var1);

    public int size();

    default public boolean isEmpty() {
        for (int $$0 = 0; $$0 < this.size(); ++$$0) {
            if (this.getItem($$0).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

