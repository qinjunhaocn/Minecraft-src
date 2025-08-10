/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.screens.recipebook;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class RecipeButton
extends AbstractWidget {
    private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
    private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
    private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
    private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
    private static final float ANIMATION_TIME = 15.0f;
    private static final int BACKGROUND_SIZE = 25;
    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    private RecipeCollection collection = RecipeCollection.EMPTY;
    private List<ResolvedEntry> selectedEntries = List.of();
    private boolean allRecipesHaveSameResultDisplay;
    private final SlotSelectTime slotSelectTime;
    private float animationTime;

    public RecipeButton(SlotSelectTime $$0) {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
        this.slotSelectTime = $$0;
    }

    public void init(RecipeCollection $$0, boolean $$12, RecipeBookPage $$2, ContextMap $$3) {
        this.collection = $$0;
        List<RecipeDisplayEntry> $$4 = $$0.getSelectedRecipes($$12 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
        this.selectedEntries = $$4.stream().map($$1 -> new ResolvedEntry($$1.id(), $$1.resultItems($$3))).toList();
        this.allRecipesHaveSameResultDisplay = RecipeButton.allRecipesHaveSameResultDisplay(this.selectedEntries);
        List $$5 = $$4.stream().map(RecipeDisplayEntry::id).filter($$2.getRecipeBook()::willHighlight).toList();
        if (!$$5.isEmpty()) {
            $$5.forEach($$2::recipeShown);
            this.animationTime = 15.0f;
        }
    }

    private static boolean allRecipesHaveSameResultDisplay(List<ResolvedEntry> $$02) {
        Iterator $$1 = $$02.stream().flatMap($$0 -> $$0.displayItems().stream()).iterator();
        if (!$$1.hasNext()) {
            return true;
        }
        ItemStack $$2 = (ItemStack)$$1.next();
        while ($$1.hasNext()) {
            ItemStack $$3 = (ItemStack)$$1.next();
            if (ItemStack.isSameItemSameComponents($$2, $$3)) continue;
            return false;
        }
        return true;
    }

    public RecipeCollection getCollection() {
        return this.collection;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        boolean $$8;
        ResourceLocation $$7;
        if (this.collection.hasCraftable()) {
            if (this.hasMultipleRecipes()) {
                ResourceLocation $$4 = SLOT_MANY_CRAFTABLE_SPRITE;
            } else {
                ResourceLocation $$5 = SLOT_CRAFTABLE_SPRITE;
            }
        } else if (this.hasMultipleRecipes()) {
            ResourceLocation $$6 = SLOT_MANY_UNCRAFTABLE_SPRITE;
        } else {
            $$7 = SLOT_UNCRAFTABLE_SPRITE;
        }
        boolean bl = $$8 = this.animationTime > 0.0f;
        if ($$8) {
            float $$9 = 1.0f + 0.1f * (float)Math.sin(this.animationTime / 15.0f * (float)Math.PI);
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            $$0.pose().scale($$9, $$9);
            $$0.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
            this.animationTime -= $$3;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$7, this.getX(), this.getY(), this.width, this.height);
        ItemStack $$10 = this.getDisplayStack();
        int $$11 = 4;
        if (this.hasMultipleRecipes() && this.allRecipesHaveSameResultDisplay) {
            $$0.renderItem($$10, this.getX() + $$11 + 1, this.getY() + $$11 + 1, 0);
            --$$11;
        }
        $$0.renderFakeItem($$10, this.getX() + $$11, this.getY() + $$11);
        if ($$8) {
            $$0.pose().popMatrix();
        }
    }

    private boolean hasMultipleRecipes() {
        return this.selectedEntries.size() > 1;
    }

    public boolean isOnlyOption() {
        return this.selectedEntries.size() == 1;
    }

    public RecipeDisplayId getCurrentRecipe() {
        int $$0 = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
        return this.selectedEntries.get((int)$$0).id;
    }

    public ItemStack getDisplayStack() {
        int $$0 = this.slotSelectTime.currentIndex();
        int $$1 = this.selectedEntries.size();
        int $$2 = $$0 / $$1;
        int $$3 = $$0 - $$1 * $$2;
        return this.selectedEntries.get($$3).selectItem($$2);
    }

    public List<Component> getTooltipText(ItemStack $$0) {
        ArrayList<Component> $$1 = new ArrayList<Component>(Screen.getTooltipFromItem(Minecraft.getInstance(), $$0));
        if (this.hasMultipleRecipes()) {
            $$1.add(MORE_RECIPES_TOOLTIP);
        }
        return $$1;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, Component.a("narration.recipe", this.getDisplayStack().getHoverName()));
        if (this.hasMultipleRecipes()) {
            $$0.a(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
        } else {
            $$0.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
        }
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int $$0) {
        return $$0 == 0 || $$0 == 1;
    }

    static final class ResolvedEntry
    extends Record {
        final RecipeDisplayId id;
        private final List<ItemStack> displayItems;

        ResolvedEntry(RecipeDisplayId $$0, List<ItemStack> $$1) {
            this.id = $$0;
            this.displayItems = $$1;
        }

        public ItemStack selectItem(int $$0) {
            if (this.displayItems.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int $$1 = $$0 % this.displayItems.size();
            return this.displayItems.get($$1);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ResolvedEntry.class, "id;displayItems", "id", "displayItems"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ResolvedEntry.class, "id;displayItems", "id", "displayItems"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ResolvedEntry.class, "id;displayItems", "id", "displayItems"}, this, $$0);
        }

        public RecipeDisplayId id() {
            return this.id;
        }

        public List<ItemStack> displayItems() {
            return this.displayItems;
        }
    }
}

