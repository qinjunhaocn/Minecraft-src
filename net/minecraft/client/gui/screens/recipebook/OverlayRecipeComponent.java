/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class OverlayRecipeComponent
implements Renderable,
GuiEventListener {
    private static final ResourceLocation OVERLAY_RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/overlay_recipe");
    private static final int MAX_ROW = 4;
    private static final int MAX_ROW_LARGE = 5;
    private static final float ITEM_RENDER_SCALE = 0.375f;
    public static final int BUTTON_SIZE = 25;
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private boolean isVisible;
    private int x;
    private int y;
    private RecipeCollection collection = RecipeCollection.EMPTY;
    @Nullable
    private RecipeDisplayId lastRecipeClicked;
    final SlotSelectTime slotSelectTime;
    private final boolean isFurnaceMenu;

    public OverlayRecipeComponent(SlotSelectTime $$0, boolean $$1) {
        this.slotSelectTime = $$0;
        this.isFurnaceMenu = $$1;
    }

    public void init(RecipeCollection $$0, ContextMap $$1, boolean $$2, int $$3, int $$4, int $$5, int $$6, float $$7) {
        float $$19;
        float $$18;
        float $$17;
        float $$16;
        float $$15;
        this.collection = $$0;
        List<RecipeDisplayEntry> $$8 = $$0.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE);
        List $$9 = $$2 ? Collections.emptyList() : $$0.getSelectedRecipes(RecipeCollection.CraftableStatus.NOT_CRAFTABLE);
        int $$10 = $$8.size();
        int $$11 = $$10 + $$9.size();
        int $$12 = $$11 <= 16 ? 4 : 5;
        int $$13 = (int)Math.ceil((float)$$11 / (float)$$12);
        this.x = $$3;
        this.y = $$4;
        float $$14 = this.x + Math.min($$11, $$12) * 25;
        if ($$14 > ($$15 = (float)($$5 + 50))) {
            this.x = (int)((float)this.x - $$7 * (float)((int)(($$14 - $$15) / $$7)));
        }
        if (($$16 = (float)(this.y + $$13 * 25)) > ($$17 = (float)($$6 + 50))) {
            this.y = (int)((float)this.y - $$7 * (float)Mth.ceil(($$16 - $$17) / $$7));
        }
        if (($$18 = (float)this.y) < ($$19 = (float)($$6 - 100))) {
            this.y = (int)((float)this.y - $$7 * (float)Mth.ceil(($$18 - $$19) / $$7));
        }
        this.isVisible = true;
        this.recipeButtons.clear();
        for (int $$20 = 0; $$20 < $$11; ++$$20) {
            boolean $$21 = $$20 < $$10;
            RecipeDisplayEntry $$22 = $$21 ? $$8.get($$20) : (RecipeDisplayEntry)((Object)$$9.get($$20 - $$10));
            int $$23 = this.x + 4 + 25 * ($$20 % $$12);
            int $$24 = this.y + 5 + 25 * ($$20 / $$12);
            if (this.isFurnaceMenu) {
                this.recipeButtons.add(new OverlaySmeltingRecipeButton(this, $$23, $$24, $$22.id(), $$22.display(), $$1, $$21));
                continue;
            }
            this.recipeButtons.add(new OverlayCraftingRecipeButton(this, $$23, $$24, $$22.id(), $$22.display(), $$1, $$21));
        }
        this.lastRecipeClicked = null;
    }

    public RecipeCollection getRecipeCollection() {
        return this.collection;
    }

    @Nullable
    public RecipeDisplayId getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 != 0) {
            return false;
        }
        for (OverlayRecipeButton $$3 : this.recipeButtons) {
            if (!$$3.mouseClicked($$0, $$1, $$2)) continue;
            this.lastRecipeClicked = $$3.recipe;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible) {
            return;
        }
        int $$4 = this.recipeButtons.size() <= 16 ? 4 : 5;
        int $$5 = Math.min(this.recipeButtons.size(), $$4);
        int $$6 = Mth.ceil((float)this.recipeButtons.size() / (float)$$4);
        int $$7 = 4;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, OVERLAY_RECIPE_SPRITE, this.x, this.y, $$5 * 25 + 8, $$6 * 25 + 8);
        for (OverlayRecipeButton $$8 : this.recipeButtons) {
            $$8.render($$0, $$1, $$2, $$3);
        }
    }

    public void setVisible(boolean $$0) {
        this.isVisible = $$0;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setFocused(boolean $$0) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    class OverlaySmeltingRecipeButton
    extends OverlayRecipeButton {
        private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay");
        private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_highlighted");
        private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_disabled");
        private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/furnace_overlay_disabled_highlighted");

        public OverlaySmeltingRecipeButton(OverlayRecipeComponent overlayRecipeComponent, int $$0, int $$1, RecipeDisplayId $$2, RecipeDisplay $$3, ContextMap $$4, boolean $$5) {
            super($$0, $$1, $$2, $$5, OverlaySmeltingRecipeButton.calculateIngredientsPositions($$3, $$4));
        }

        private static List<OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeDisplay $$0, ContextMap $$1) {
            FurnaceRecipeDisplay $$2;
            List<ItemStack> $$3;
            if ($$0 instanceof FurnaceRecipeDisplay && !($$3 = ($$2 = (FurnaceRecipeDisplay)$$0).ingredient().resolveForStacks($$1)).isEmpty()) {
                return List.of((Object)((Object)OverlaySmeltingRecipeButton.createGridPos(1, 1, $$3)));
            }
            return List.of();
        }

        @Override
        protected ResourceLocation getSprite(boolean $$0) {
            if ($$0) {
                return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
            }
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
        }
    }

    class OverlayCraftingRecipeButton
    extends OverlayRecipeButton {
        private static final ResourceLocation ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay");
        private static final ResourceLocation HIGHLIGHTED_ENABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_highlighted");
        private static final ResourceLocation DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_disabled");
        private static final ResourceLocation HIGHLIGHTED_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/crafting_overlay_disabled_highlighted");
        private static final int GRID_WIDTH = 3;
        private static final int GRID_HEIGHT = 3;

        public OverlayCraftingRecipeButton(OverlayRecipeComponent overlayRecipeComponent, int $$0, int $$1, RecipeDisplayId $$2, RecipeDisplay $$3, ContextMap $$4, boolean $$5) {
            super($$0, $$1, $$2, $$5, OverlayCraftingRecipeButton.calculateIngredientsPositions($$3, $$4));
        }

        private static List<OverlayRecipeButton.Pos> calculateIngredientsPositions(RecipeDisplay $$0, ContextMap $$1) {
            ArrayList<OverlayRecipeButton.Pos> $$22 = new ArrayList<OverlayRecipeButton.Pos>();
            RecipeDisplay recipeDisplay = $$0;
            Objects.requireNonNull(recipeDisplay);
            RecipeDisplay recipeDisplay2 = recipeDisplay;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, (int)n)) {
                case 0: {
                    ShapedCraftingRecipeDisplay $$32 = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                    PlaceRecipeHelper.placeRecipe(3, 3, $$32.width(), $$32.height(), $$32.ingredients(), ($$2, $$3, $$4, $$5) -> {
                        List<ItemStack> $$6 = $$2.resolveForStacks($$1);
                        if (!$$6.isEmpty()) {
                            $$22.add(OverlayCraftingRecipeButton.createGridPos($$4, $$5, $$6));
                        }
                    });
                    break;
                }
                case 1: {
                    ShapelessCraftingRecipeDisplay $$42 = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                    List<SlotDisplay> $$52 = $$42.ingredients();
                    for (int $$6 = 0; $$6 < $$52.size(); ++$$6) {
                        List<ItemStack> $$7 = $$52.get($$6).resolveForStacks($$1);
                        if ($$7.isEmpty()) continue;
                        $$22.add(OverlayCraftingRecipeButton.createGridPos($$6 % 3, $$6 / 3, $$7));
                    }
                    break;
                }
            }
            return $$22;
        }

        @Override
        protected ResourceLocation getSprite(boolean $$0) {
            if ($$0) {
                return this.isHoveredOrFocused() ? HIGHLIGHTED_ENABLED_SPRITE : ENABLED_SPRITE;
            }
            return this.isHoveredOrFocused() ? HIGHLIGHTED_DISABLED_SPRITE : DISABLED_SPRITE;
        }
    }

    abstract class OverlayRecipeButton
    extends AbstractWidget {
        final RecipeDisplayId recipe;
        private final boolean isCraftable;
        private final List<Pos> slots;

        public OverlayRecipeButton(int $$0, int $$1, RecipeDisplayId $$2, boolean $$3, List<Pos> $$4) {
            super($$0, $$1, 24, 24, CommonComponents.EMPTY);
            this.slots = $$4;
            this.recipe = $$2;
            this.isCraftable = $$3;
        }

        protected static Pos createGridPos(int $$0, int $$1, List<ItemStack> $$2) {
            return new Pos(3 + $$0 * 7, 3 + $$1 * 7, $$2);
        }

        protected abstract ResourceLocation getSprite(boolean var1);

        @Override
        public void updateWidgetNarration(NarrationElementOutput $$0) {
            this.defaultButtonNarrationText($$0);
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.getSprite(this.isCraftable), this.getX(), this.getY(), this.width, this.height);
            float $$4 = this.getX() + 2;
            float $$5 = this.getY() + 2;
            for (Pos $$6 : this.slots) {
                $$0.pose().pushMatrix();
                $$0.pose().translate($$4 + (float)$$6.x, $$5 + (float)$$6.y);
                $$0.pose().scale(0.375f, 0.375f);
                $$0.pose().translate(-8.0f, -8.0f);
                $$0.renderItem($$6.selectIngredient(OverlayRecipeComponent.this.slotSelectTime.currentIndex()), 0, 0);
                $$0.pose().popMatrix();
            }
        }

        protected static final class Pos
        extends Record {
            final int x;
            final int y;
            private final List<ItemStack> ingredients;

            public Pos(int $$0, int $$1, List<ItemStack> $$2) {
                if ($$2.isEmpty()) {
                    throw new IllegalArgumentException("Ingredient list must be non-empty");
                }
                this.x = $$0;
                this.y = $$1;
                this.ingredients = $$2;
            }

            public ItemStack selectIngredient(int $$0) {
                return this.ingredients.get($$0 % this.ingredients.size());
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Pos.class, "x;y;ingredients", "x", "y", "ingredients"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Pos.class, "x;y;ingredients", "x", "y", "ingredients"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Pos.class, "x;y;ingredients", "x", "y", "ingredients"}, this, $$0);
            }

            public int x() {
                return this.x;
            }

            public int y() {
                return this.y;
            }

            public List<ItemStack> ingredients() {
                return this.ingredients;
            }
        }
    }
}

