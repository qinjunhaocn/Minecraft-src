/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe
extends CustomRecipe {
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.ingredientCount() < 2) {
            return false;
        }
        boolean $$2 = false;
        boolean $$3 = false;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.getItem() instanceof DyeItem) {
                $$2 = true;
                continue;
            }
            if (STAR_INGREDIENT.test($$5)) {
                if ($$3) {
                    return false;
                }
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$3 && $$2;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        IntArrayList $$2 = new IntArrayList();
        ItemStack $$3 = null;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            Item $$6 = $$5.getItem();
            if ($$6 instanceof DyeItem) {
                DyeItem $$7 = (DyeItem)$$6;
                $$2.add($$7.getDyeColor().getFireworkColor());
                continue;
            }
            if (!STAR_INGREDIENT.test($$5)) continue;
            $$3 = $$5.copyWithCount(1);
        }
        if ($$3 == null || $$2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        $$3.update(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT, $$2, FireworkExplosion::withFadeColors);
        return $$3;
    }

    @Override
    public RecipeSerializer<FireworkStarFadeRecipe> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
}

