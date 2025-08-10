/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastingRecipe
extends AbstractCookingRecipe {
    public BlastingRecipe(String $$0, CookingBookCategory $$1, Ingredient $$2, ItemStack $$3, float $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected Item furnaceIcon() {
        return Items.BLAST_FURNACE;
    }

    @Override
    public RecipeSerializer<BlastingRecipe> getSerializer() {
        return RecipeSerializer.BLASTING_RECIPE;
    }

    @Override
    public RecipeType<BlastingRecipe> getType() {
        return RecipeType.BLASTING;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return switch (this.category()) {
            default -> throw new MatchException(null, null);
            case CookingBookCategory.BLOCKS -> RecipeBookCategories.BLAST_FURNACE_BLOCKS;
            case CookingBookCategory.FOOD, CookingBookCategory.MISC -> RecipeBookCategories.BLAST_FURNACE_MISC;
        };
    }
}

