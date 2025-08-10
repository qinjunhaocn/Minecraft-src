/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(CraftingBookCategory $$0) {
        super("", $$0, ShapedRecipePattern.a(Map.of((Object)Character.valueOf('#'), (Object)Ingredient.of(Items.PAPER), (Object)Character.valueOf('x'), (Object)Ingredient.of(Items.FILLED_MAP)), "###", "#x#", "###"), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if (!super.matches($$0, $$1)) {
            return false;
        }
        ItemStack $$2 = MapExtendingRecipe.findFilledMap($$0);
        if ($$2.isEmpty()) {
            return false;
        }
        MapItemSavedData $$3 = MapItem.getSavedData($$2, $$1);
        if ($$3 == null) {
            return false;
        }
        if ($$3.isExplorationMap()) {
            return false;
        }
        return $$3.scale < 4;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        ItemStack $$2 = MapExtendingRecipe.findFilledMap($$0).copyWithCount(1);
        $$2.set(DataComponents.MAP_POST_PROCESSING, MapPostProcessing.SCALE);
        return $$2;
    }

    private static ItemStack findFilledMap(CraftingInput $$0) {
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            ItemStack $$2 = $$0.getItem($$1);
            if (!$$2.has(DataComponents.MAP_ID)) continue;
            return $$2;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<MapExtendingRecipe> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}

