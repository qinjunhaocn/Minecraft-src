/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens.recipebook;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class CraftingRecipeBookComponent
extends RecipeBookComponent<AbstractCraftingMenu> {
    private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
    private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of((Object)((Object)new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.CRAFTING)), (Object)((Object)new RecipeBookComponent.TabInfo(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT)), (Object)((Object)new RecipeBookComponent.TabInfo(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS)), (Object)((Object)new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC)), (Object)((Object)new RecipeBookComponent.TabInfo(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE)));

    public CraftingRecipeBookComponent(AbstractCraftingMenu $$0) {
        super($$0, TABS);
    }

    @Override
    protected boolean isCraftingSlot(Slot $$0) {
        return ((AbstractCraftingMenu)this.menu).getResultSlot() == $$0 || ((AbstractCraftingMenu)this.menu).getInputGridSlots().contains($$0);
    }

    private boolean canDisplay(RecipeDisplay $$0) {
        int $$1 = ((AbstractCraftingMenu)this.menu).getGridWidth();
        int $$2 = ((AbstractCraftingMenu)this.menu).getGridHeight();
        RecipeDisplay recipeDisplay = $$0;
        Objects.requireNonNull(recipeDisplay);
        RecipeDisplay recipeDisplay2 = recipeDisplay;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, (int)n)) {
            case 0 -> {
                ShapedCraftingRecipeDisplay $$3 = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                if ($$1 >= $$3.width() && $$2 >= $$3.height()) {
                    yield true;
                }
                yield false;
            }
            case 1 -> {
                ShapelessCraftingRecipeDisplay $$4 = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                if ($$1 * $$2 >= $$4.ingredients().size()) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots $$0, RecipeDisplay $$1, ContextMap $$2) {
        $$0.setResult(((AbstractCraftingMenu)this.menu).getResultSlot(), $$2, $$1.result());
        RecipeDisplay recipeDisplay = $$1;
        Objects.requireNonNull(recipeDisplay);
        RecipeDisplay recipeDisplay2 = recipeDisplay;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, (int)n)) {
            case 0: {
                ShapedCraftingRecipeDisplay $$32 = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                List<Slot> $$42 = ((AbstractCraftingMenu)this.menu).getInputGridSlots();
                PlaceRecipeHelper.placeRecipe(((AbstractCraftingMenu)this.menu).getGridWidth(), ((AbstractCraftingMenu)this.menu).getGridHeight(), $$32.width(), $$32.height(), $$32.ingredients(), ($$3, $$4, $$5, $$6) -> {
                    Slot $$7 = (Slot)$$42.get($$4);
                    $$0.setInput($$7, $$2, (SlotDisplay)$$3);
                });
                break;
            }
            case 1: {
                ShapelessCraftingRecipeDisplay $$52 = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                List<Slot> $$62 = ((AbstractCraftingMenu)this.menu).getInputGridSlots();
                int $$7 = Math.min($$52.ingredients().size(), $$62.size());
                for (int $$8 = 0; $$8 < $$7; ++$$8) {
                    $$0.setInput($$62.get($$8), $$2, $$52.ingredients().get($$8));
                }
                break;
            }
        }
    }

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
    }

    @Override
    protected Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection $$0, StackedItemContents $$1) {
        $$0.selectRecipes($$1, this::canDisplay);
    }
}

