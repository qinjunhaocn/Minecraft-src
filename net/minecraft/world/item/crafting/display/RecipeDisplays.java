/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;

public class RecipeDisplays {
    public static RecipeDisplay.Type<?> bootstrap(Registry<RecipeDisplay.Type<?>> $$0) {
        Registry.register($$0, "crafting_shapeless", ShapelessCraftingRecipeDisplay.TYPE);
        Registry.register($$0, "crafting_shaped", ShapedCraftingRecipeDisplay.TYPE);
        Registry.register($$0, "furnace", FurnaceRecipeDisplay.TYPE);
        Registry.register($$0, "stonecutter", StonecutterRecipeDisplay.TYPE);
        return Registry.register($$0, "smithing", SmithingRecipeDisplay.TYPE);
    }
}

