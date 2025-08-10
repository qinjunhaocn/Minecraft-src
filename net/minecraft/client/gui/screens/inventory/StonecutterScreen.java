/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class StonecutterScreen
extends AbstractContainerScreen<StonecutterMenu> {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final ResourceLocation RECIPE_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final ResourceLocation RECIPE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final ResourceLocation RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/recipe");
    private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/stonecutter.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 52;
    private static final int RECIPES_Y = 14;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public StonecutterScreen(StonecutterMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        $$0.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        $$0.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        int $$6 = (int)(41.0f * this.scrollOffs);
        ResourceLocation $$7 = this.isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$7, $$4 + 119, $$5 + 15 + $$6, 12, 15);
        int $$8 = this.leftPos + 52;
        int $$9 = this.topPos + 14;
        int $$10 = this.startIndex + 12;
        this.renderButtons($$0, $$2, $$3, $$8, $$9, $$10);
        this.renderRecipes($$0, $$8, $$9, $$10);
    }

    @Override
    protected void renderTooltip(GuiGraphics $$0, int $$1, int $$2) {
        super.renderTooltip($$0, $$1, $$2);
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;
            SelectableRecipe.SingleInputSet<StonecutterRecipe> $$6 = ((StonecutterMenu)this.menu).getVisibleRecipes();
            for (int $$7 = this.startIndex; $$7 < $$5 && $$7 < $$6.size(); ++$$7) {
                int $$8 = $$7 - this.startIndex;
                int $$9 = $$3 + $$8 % 4 * 16;
                int $$10 = $$4 + $$8 / 4 * 18 + 2;
                if ($$1 < $$9 || $$1 >= $$9 + 16 || $$2 < $$10 || $$2 >= $$10 + 18) continue;
                ContextMap $$11 = SlotDisplayContext.fromLevel(this.minecraft.level);
                SlotDisplay $$12 = $$6.entries().get($$7).recipe().optionDisplay();
                $$0.setTooltipForNextFrame(this.font, $$12.resolveForFirstStack($$11), $$1, $$2);
            }
        }
    }

    private void renderButtons(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = this.startIndex; $$6 < $$5 && $$6 < ((StonecutterMenu)this.menu).getNumberOfVisibleRecipes(); ++$$6) {
            ResourceLocation $$13;
            int $$7 = $$6 - this.startIndex;
            int $$8 = $$3 + $$7 % 4 * 16;
            int $$9 = $$7 / 4;
            int $$10 = $$4 + $$9 * 18 + 2;
            if ($$6 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
                ResourceLocation $$11 = RECIPE_SELECTED_SPRITE;
            } else if ($$1 >= $$8 && $$2 >= $$10 && $$1 < $$8 + 16 && $$2 < $$10 + 18) {
                ResourceLocation $$12 = RECIPE_HIGHLIGHTED_SPRITE;
            } else {
                $$13 = RECIPE_SPRITE;
            }
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$13, $$8, $$10 - 1, 16, 18);
        }
    }

    private void renderRecipes(GuiGraphics $$0, int $$1, int $$2, int $$3) {
        SelectableRecipe.SingleInputSet<StonecutterRecipe> $$4 = ((StonecutterMenu)this.menu).getVisibleRecipes();
        ContextMap $$5 = SlotDisplayContext.fromLevel(this.minecraft.level);
        for (int $$6 = this.startIndex; $$6 < $$3 && $$6 < $$4.size(); ++$$6) {
            int $$7 = $$6 - this.startIndex;
            int $$8 = $$1 + $$7 % 4 * 16;
            int $$9 = $$7 / 4;
            int $$10 = $$2 + $$9 * 18 + 2;
            SlotDisplay $$11 = $$4.entries().get($$6).recipe().optionDisplay();
            $$0.renderItem($$11.resolveForFirstStack($$5), $$8, $$10);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int $$3 = this.leftPos + 52;
            int $$4 = this.topPos + 14;
            int $$5 = this.startIndex + 12;
            for (int $$6 = this.startIndex; $$6 < $$5; ++$$6) {
                int $$7 = $$6 - this.startIndex;
                double $$8 = $$0 - (double)($$3 + $$7 % 4 * 16);
                double $$9 = $$1 - (double)($$4 + $$7 / 4 * 18);
                if (!($$8 >= 0.0) || !($$9 >= 0.0) || !($$8 < 16.0) || !($$9 < 18.0) || !((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, $$6)) continue;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, $$6);
                return true;
            }
            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if ($$0 >= (double)$$3 && $$0 < (double)($$3 + 12) && $$1 >= (double)$$4 && $$1 < (double)($$4 + 54)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling && this.isScrollBarActive()) {
            int $$5 = this.topPos + 14;
            int $$6 = $$5 + 54;
            this.scrollOffs = ((float)$$1 - (float)$$5 - 7.5f) / ((float)($$6 - $$5) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * 4;
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (super.mouseScrolled($$0, $$1, $$2, $$3)) {
            return true;
        }
        if (this.isScrollBarActive()) {
            int $$4 = this.getOffscreenRows();
            float $$5 = (float)$$3 / (float)$$4;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$5, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)$$4) + 0.5) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && ((StonecutterMenu)this.menu).getNumberOfVisibleRecipes() > 12;
    }

    protected int getOffscreenRows() {
        return (((StonecutterMenu)this.menu).getNumberOfVisibleRecipes() + 4 - 1) / 4 - 3;
    }

    private void containerChanged() {
        this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0f;
            this.startIndex = 0;
        }
    }
}

