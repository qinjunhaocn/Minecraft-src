/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeToast
implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
    private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
    private final List<Entry> recipeItems = new ArrayList<Entry>();
    private long lastChanged;
    private boolean changed;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;
    private int displayedRecipeIndex;

    private RecipeToast() {
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager $$0, long $$1) {
        if (this.changed) {
            this.lastChanged = $$1;
            this.changed = false;
        }
        this.wantedVisibility = this.recipeItems.isEmpty() ? Toast.Visibility.HIDE : ((double)($$1 - this.lastChanged) >= 5000.0 * $$0.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW);
        this.displayedRecipeIndex = (int)((double)$$1 / Math.max(1.0, 5000.0 * $$0.getNotificationDisplayTimeMultiplier() / (double)this.recipeItems.size()) % (double)this.recipeItems.size());
    }

    @Override
    public void render(GuiGraphics $$0, Font $$1, long $$2) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        $$0.drawString($$1, TITLE_TEXT, 30, 7, -11534256, false);
        $$0.drawString($$1, DESCRIPTION_TEXT, 30, 18, -16777216, false);
        Entry $$3 = this.recipeItems.get(this.displayedRecipeIndex);
        $$0.pose().pushMatrix();
        $$0.pose().scale(0.6f, 0.6f);
        $$0.renderFakeItem($$3.categoryItem(), 3, 3);
        $$0.pose().popMatrix();
        $$0.renderFakeItem($$3.unlockedItem(), 8, 8);
    }

    private void addItem(ItemStack $$0, ItemStack $$1) {
        this.recipeItems.add(new Entry($$0, $$1));
        this.changed = true;
    }

    public static void addOrUpdate(ToastManager $$0, RecipeDisplay $$1) {
        RecipeToast $$2 = $$0.getToast(RecipeToast.class, NO_TOKEN);
        if ($$2 == null) {
            $$2 = new RecipeToast();
            $$0.addToast($$2);
        }
        ContextMap $$3 = SlotDisplayContext.fromLevel($$0.getMinecraft().level);
        ItemStack $$4 = $$1.craftingStation().resolveForFirstStack($$3);
        ItemStack $$5 = $$1.result().resolveForFirstStack($$3);
        $$2.addItem($$4, $$5);
    }

    record Entry(ItemStack categoryItem, ItemStack unlockedItem) {
    }
}

