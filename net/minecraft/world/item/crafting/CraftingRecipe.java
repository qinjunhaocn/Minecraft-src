/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public interface CraftingRecipe
extends Recipe<CraftingInput> {
    @Override
    default public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer();

    public CraftingBookCategory category();

    default public NonNullList<ItemStack> getRemainingItems(CraftingInput $$0) {
        return CraftingRecipe.defaultCraftingReminder($$0);
    }

    public static NonNullList<ItemStack> defaultCraftingReminder(CraftingInput $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.withSize($$0.size(), ItemStack.EMPTY);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            Item $$3 = $$0.getItem($$2).getItem();
            $$1.set($$2, $$3.getCraftingRemainder());
        }
        return $$1;
    }

    @Override
    default public RecipeBookCategory recipeBookCategory() {
        return switch (this.category()) {
            default -> throw new MatchException(null, null);
            case CraftingBookCategory.BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case CraftingBookCategory.EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case CraftingBookCategory.REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case CraftingBookCategory.MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }
}

