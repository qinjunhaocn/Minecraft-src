/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public abstract class AbstractRecipeBookScreen<T extends RecipeBookMenu>
extends AbstractContainerScreen<T>
implements RecipeUpdateListener {
    private final RecipeBookComponent<?> recipeBookComponent;
    private boolean widthTooNarrow;

    public AbstractRecipeBookScreen(T $$0, RecipeBookComponent<?> $$1, Inventory $$2, Component $$3) {
        super($$0, $$2, $$3);
        this.recipeBookComponent = $$1;
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.initButton();
    }

    protected abstract ScreenPosition getRecipeBookButtonPosition();

    private void initButton() {
        ScreenPosition $$02 = this.getRecipeBookButtonPosition();
        this.addRenderableWidget(new ImageButton($$02.x(), $$02.y(), 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, $$0 -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ScreenPosition $$1 = this.getRecipeBookButtonPosition();
            $$0.setPosition($$1.x(), $$1.y());
            this.onRecipeBookButtonClick();
        }));
        this.addWidget(this.recipeBookComponent);
    }

    protected void onRecipeBookButtonClick() {
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBackground($$0, $$1, $$2, $$3);
        } else {
            super.renderContents($$0, $$1, $$2, $$3);
        }
        $$0.nextStratum();
        this.recipeBookComponent.render($$0, $$1, $$2, $$3);
        $$0.nextStratum();
        this.renderCarriedItem($$0, $$1, $$2);
        this.renderSnapbackItem($$0);
        this.renderTooltip($$0, $$1, $$2);
        this.recipeBookComponent.renderTooltip($$0, $$1, $$2, this.hoveredSlot);
    }

    @Override
    protected void renderSlots(GuiGraphics $$0) {
        super.renderSlots($$0);
        this.recipeBookComponent.renderGhostRecipe($$0, this.isBiggerResultSlot());
    }

    protected boolean isBiggerResultSlot() {
        return true;
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (this.recipeBookComponent.a($$0, $$1)) {
            return true;
        }
        return super.a($$0, $$1);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.recipeBookComponent.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.recipeBookComponent.mouseClicked($$0, $$1, $$2)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        if (this.widthTooNarrow && this.recipeBookComponent.isVisible()) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    protected boolean isHovering(int $$0, int $$1, int $$2, int $$3, double $$4, double $$5) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside($$0, $$1, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, $$4) && $$5;
    }

    @Override
    protected void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
        super.slotClicked($$0, $$1, $$2, $$3);
        this.recipeBookComponent.slotClicked($$0);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public void fillGhostRecipe(RecipeDisplay $$0) {
        this.recipeBookComponent.fillGhostRecipe($$0);
    }
}

