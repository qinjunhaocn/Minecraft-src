/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record SingleRecipeInput(ItemStack item) implements RecipeInput
{
    @Override
    public ItemStack getItem(int $$0) {
        if ($$0 != 0) {
            throw new IllegalArgumentException("No item for index " + $$0);
        }
        return this.item;
    }

    @Override
    public int size() {
        return 1;
    }
}

