/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

public class DecoratedPotRecipe
extends CustomRecipe {
    public DecoratedPotRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    private static ItemStack back(CraftingInput $$0) {
        return $$0.getItem(1, 0);
    }

    private static ItemStack left(CraftingInput $$0) {
        return $$0.getItem(0, 1);
    }

    private static ItemStack right(CraftingInput $$0) {
        return $$0.getItem(2, 1);
    }

    private static ItemStack front(CraftingInput $$0) {
        return $$0.getItem(1, 2);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.width() != 3 || $$0.height() != 3 || $$0.ingredientCount() != 4) {
            return false;
        }
        return DecoratedPotRecipe.back($$0).is(ItemTags.DECORATED_POT_INGREDIENTS) && DecoratedPotRecipe.left($$0).is(ItemTags.DECORATED_POT_INGREDIENTS) && DecoratedPotRecipe.right($$0).is(ItemTags.DECORATED_POT_INGREDIENTS) && DecoratedPotRecipe.front($$0).is(ItemTags.DECORATED_POT_INGREDIENTS);
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        PotDecorations $$2 = new PotDecorations(DecoratedPotRecipe.back($$0).getItem(), DecoratedPotRecipe.left($$0).getItem(), DecoratedPotRecipe.right($$0).getItem(), DecoratedPotRecipe.front($$0).getItem());
        return DecoratedPotBlockEntity.createDecoratedPotItem($$2);
    }

    @Override
    public RecipeSerializer<DecoratedPotRecipe> getSerializer() {
        return RecipeSerializer.DECORATED_POT_RECIPE;
    }
}

