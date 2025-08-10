/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe
extends CustomRecipe {
    public TippedArrowRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.width() != 3 || $$0.height() != 3 || $$0.ingredientCount() != 9) {
            return false;
        }
        for (int $$2 = 0; $$2 < $$0.height(); ++$$2) {
            for (int $$3 = 0; $$3 < $$0.width(); ++$$3) {
                ItemStack $$4 = $$0.getItem($$3, $$2);
                if ($$4.isEmpty()) {
                    return false;
                }
                if (!($$3 == 1 && $$2 == 1 ? !$$4.is(Items.LINGERING_POTION) : !$$4.is(Items.ARROW))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        ItemStack $$2 = $$0.getItem(1, 1);
        if (!$$2.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        }
        ItemStack $$3 = new ItemStack(Items.TIPPED_ARROW, 8);
        $$3.set(DataComponents.POTION_CONTENTS, $$2.get(DataComponents.POTION_CONTENTS));
        return $$3;
    }

    @Override
    public RecipeSerializer<TippedArrowRecipe> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}

