/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe
extends CustomRecipe {
    public ArmorDyeRecipe(CraftingBookCategory $$0) {
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
            if ($$5.is(ItemTags.DYEABLE)) {
                if ($$2) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if ($$5.getItem() instanceof DyeItem) {
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$3 && $$2;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        ArrayList<DyeItem> $$2 = new ArrayList<DyeItem>();
        ItemStack $$3 = ItemStack.EMPTY;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.is(ItemTags.DYEABLE)) {
                if (!$$3.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                $$3 = $$5.copy();
                continue;
            }
            Item item = $$5.getItem();
            if (item instanceof DyeItem) {
                DyeItem $$6 = (DyeItem)item;
                $$2.add($$6);
                continue;
            }
            return ItemStack.EMPTY;
        }
        if ($$3.isEmpty() || $$2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return DyedItemColor.applyDyes($$3, $$2);
    }

    @Override
    public RecipeSerializer<ArmorDyeRecipe> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}

