/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerDuplicateRecipe
extends CustomRecipe {
    public BannerDuplicateRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.ingredientCount() != 2) {
            return false;
        }
        DyeColor $$2 = null;
        boolean $$3 = false;
        boolean $$4 = false;
        for (int $$5 = 0; $$5 < $$0.size(); ++$$5) {
            ItemStack $$6 = $$0.getItem($$5);
            if ($$6.isEmpty()) continue;
            Item $$7 = $$6.getItem();
            if ($$7 instanceof BannerItem) {
                BannerItem $$8 = (BannerItem)$$7;
                if ($$2 == null) {
                    $$2 = $$8.getColor();
                } else if ($$2 != $$8.getColor()) {
                    return false;
                }
            } else {
                return false;
            }
            int $$10 = $$6.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().size();
            if ($$10 > 6) {
                return false;
            }
            if ($$10 > 0) {
                if ($$4) {
                    return false;
                }
                $$4 = true;
                continue;
            }
            if ($$3) {
                return false;
            }
            $$3 = true;
        }
        return $$4 && $$3;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            int $$4;
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.isEmpty() || ($$4 = $$3.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().size()) <= 0 || $$4 > 6) continue;
            return $$3.copyWithCount(1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.withSize($$0.size(), ItemStack.EMPTY);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.isEmpty()) continue;
            ItemStack $$4 = $$3.getItem().getCraftingRemainder();
            if (!$$4.isEmpty()) {
                $$1.set($$2, $$4);
                continue;
            }
            if ($$3.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().isEmpty()) continue;
            $$1.set($$2, $$3.copyWithCount(1));
        }
        return $$1;
    }

    @Override
    public RecipeSerializer<BannerDuplicateRecipe> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }
}

