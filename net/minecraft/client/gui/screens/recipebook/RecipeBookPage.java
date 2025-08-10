/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeBookPage {
    public static final int ITEMS_PER_PAGE = 20;
    private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_forward"), ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted"));
    private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_backward"), ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted"));
    private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
    @Nullable
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay;
    private Minecraft minecraft;
    private final RecipeBookComponent<?> parent;
    private List<RecipeCollection> recipeCollections = ImmutableList.of();
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private ClientRecipeBook recipeBook;
    @Nullable
    private RecipeDisplayId lastClickedRecipe;
    @Nullable
    private RecipeCollection lastClickedRecipeCollection;
    private boolean isFiltering;

    public RecipeBookPage(RecipeBookComponent<?> $$0, SlotSelectTime $$1, boolean $$2) {
        this.parent = $$0;
        this.overlay = new OverlayRecipeComponent($$1, $$2);
        for (int $$3 = 0; $$3 < 20; ++$$3) {
            this.buttons.add(new RecipeButton($$1));
        }
    }

    public void init(Minecraft $$0, int $$1, int $$2) {
        this.minecraft = $$0;
        this.recipeBook = $$0.player.getRecipeBook();
        for (int $$3 = 0; $$3 < this.buttons.size(); ++$$3) {
            this.buttons.get($$3).setPosition($$1 + 11 + 25 * ($$3 % 5), $$2 + 31 + 25 * ($$3 / 5));
        }
        this.forwardButton = new StateSwitchingButton($$1 + 93, $$2 + 137, 12, 17, false);
        this.forwardButton.initTextureValues(PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton($$1 + 38, $$2 + 137, 12, 17, true);
        this.backButton.initTextureValues(PAGE_BACKWARD_SPRITES);
    }

    public void updateCollections(List<RecipeCollection> $$0, boolean $$1, boolean $$2) {
        this.recipeCollections = $$0;
        this.isFiltering = $$2;
        this.totalPages = (int)Math.ceil((double)$$0.size() / 20.0);
        if (this.totalPages <= this.currentPage || $$1) {
            this.currentPage = 0;
        }
        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int $$0 = 20 * this.currentPage;
        ContextMap $$1 = SlotDisplayContext.fromLevel(this.minecraft.level);
        for (int $$2 = 0; $$2 < this.buttons.size(); ++$$2) {
            RecipeButton $$3 = this.buttons.get($$2);
            if ($$0 + $$2 < this.recipeCollections.size()) {
                RecipeCollection $$4 = this.recipeCollections.get($$0 + $$2);
                $$3.init($$4, this.isFiltering, this, $$1);
                $$3.visible = true;
                continue;
            }
            $$3.visible = false;
        }
        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, float $$5) {
        if (this.totalPages > 1) {
            MutableComponent $$6 = Component.a("gui.recipebook.page", this.currentPage + 1, this.totalPages);
            int $$7 = this.minecraft.font.width($$6);
            $$0.drawString(this.minecraft.font, $$6, $$1 - $$7 / 2 + 73, $$2 + 141, -1);
        }
        this.hoveredButton = null;
        for (RecipeButton $$8 : this.buttons) {
            $$8.render($$0, $$3, $$4, $$5);
            if (!$$8.visible || !$$8.isHoveredOrFocused()) continue;
            this.hoveredButton = $$8;
        }
        this.backButton.render($$0, $$3, $$4, $$5);
        this.forwardButton.render($$0, $$3, $$4, $$5);
        $$0.nextStratum();
        this.overlay.render($$0, $$3, $$4, $$5);
    }

    public void renderTooltip(GuiGraphics $$0, int $$1, int $$2) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            ItemStack $$3 = this.hoveredButton.getDisplayStack();
            ResourceLocation $$4 = $$3.get(DataComponents.TOOLTIP_STYLE);
            $$0.setComponentTooltipForNextFrame(this.minecraft.font, this.hoveredButton.getTooltipText($$3), $$1, $$2, $$4);
        }
    }

    @Nullable
    public RecipeDisplayId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(double $$0, double $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked($$0, $$1, $$2)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }
            return true;
        }
        if (this.forwardButton.mouseClicked($$0, $$1, $$2)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        if (this.backButton.mouseClicked($$0, $$1, $$2)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        ContextMap $$7 = SlotDisplayContext.fromLevel(this.minecraft.level);
        for (RecipeButton $$8 : this.buttons) {
            if (!$$8.mouseClicked($$0, $$1, $$2)) continue;
            if ($$2 == 0) {
                this.lastClickedRecipe = $$8.getCurrentRecipe();
                this.lastClickedRecipeCollection = $$8.getCollection();
            } else if ($$2 == 1 && !this.overlay.isVisible() && !$$8.isOnlyOption()) {
                this.overlay.init($$8.getCollection(), $$7, this.isFiltering, $$8.getX(), $$8.getY(), $$3 + $$5 / 2, $$4 + 13 + $$6 / 2, $$8.getWidth());
            }
            return true;
        }
        return false;
    }

    public void recipeShown(RecipeDisplayId $$0) {
        this.parent.recipeShown($$0);
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void listButtons(Consumer<AbstractWidget> $$0) {
        $$0.accept(this.forwardButton);
        $$0.accept(this.backButton);
        this.buttons.forEach($$0);
    }
}

