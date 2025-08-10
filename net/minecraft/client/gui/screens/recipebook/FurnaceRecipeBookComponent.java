/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public class FurnaceRecipeBookComponent
extends RecipeBookComponent<AbstractFurnaceMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted"));
    private final Component recipeFilterName;

    public FurnaceRecipeBookComponent(AbstractFurnaceMenu $$0, Component $$1, List<RecipeBookComponent.TabInfo> $$2) {
        super($$0, $$2);
        this.recipeFilterName = $$1;
    }

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(FILTER_SPRITES);
    }

    @Override
    protected boolean isCraftingSlot(Slot $$0) {
        return switch ($$0.index) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots $$0, RecipeDisplay $$1, ContextMap $$2) {
        $$0.setResult(((AbstractFurnaceMenu)this.menu).getResultSlot(), $$2, $$1.result());
        if ($$1 instanceof FurnaceRecipeDisplay) {
            FurnaceRecipeDisplay $$3 = (FurnaceRecipeDisplay)$$1;
            $$0.setInput((Slot)((AbstractFurnaceMenu)this.menu).slots.get(0), $$2, $$3.ingredient());
            Slot $$4 = (Slot)((AbstractFurnaceMenu)this.menu).slots.get(1);
            if ($$4.getItem().isEmpty()) {
                $$0.setInput($$4, $$2, $$3.fuel());
            }
        }
    }

    @Override
    protected Component getRecipeFilterName() {
        return this.recipeFilterName;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection $$02, StackedItemContents $$1) {
        $$02.selectRecipes($$1, $$0 -> $$0 instanceof FurnaceRecipeDisplay);
    }
}

