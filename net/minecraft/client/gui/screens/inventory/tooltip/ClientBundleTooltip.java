/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

public class ClientBundleTooltip
implements ClientTooltipComponent {
    private static final ResourceLocation PROGRESSBAR_BORDER_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_border");
    private static final ResourceLocation PROGRESSBAR_FILL_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_fill");
    private static final ResourceLocation PROGRESSBAR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_full");
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_front");
    private static final ResourceLocation SLOT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_background");
    private static final int SLOT_MARGIN = 4;
    private static final int SLOT_SIZE = 24;
    private static final int GRID_WIDTH = 96;
    private static final int PROGRESSBAR_HEIGHT = 13;
    private static final int PROGRESSBAR_WIDTH = 96;
    private static final int PROGRESSBAR_BORDER = 1;
    private static final int PROGRESSBAR_FILL_MAX = 94;
    private static final int PROGRESSBAR_MARGIN_Y = 4;
    private static final Component BUNDLE_FULL_TEXT = Component.translatable("item.minecraft.bundle.full");
    private static final Component BUNDLE_EMPTY_TEXT = Component.translatable("item.minecraft.bundle.empty");
    private static final Component BUNDLE_EMPTY_DESCRIPTION = Component.translatable("item.minecraft.bundle.empty.description");
    private final BundleContents contents;

    public ClientBundleTooltip(BundleContents $$0) {
        this.contents = $$0;
    }

    @Override
    public int getHeight(Font $$0) {
        return this.contents.isEmpty() ? ClientBundleTooltip.getEmptyBundleBackgroundHeight($$0) : this.backgroundHeight();
    }

    @Override
    public int getWidth(Font $$0) {
        return 96;
    }

    @Override
    public boolean showTooltipWithItemInHand() {
        return true;
    }

    private static int getEmptyBundleBackgroundHeight(Font $$0) {
        return ClientBundleTooltip.getEmptyBundleDescriptionTextHeight($$0) + 13 + 8;
    }

    private int backgroundHeight() {
        return this.itemGridHeight() + 13 + 8;
    }

    private int itemGridHeight() {
        return this.gridSizeY() * 24;
    }

    private int getContentXOffset(int $$0) {
        return ($$0 - 96) / 2;
    }

    private int gridSizeY() {
        return Mth.positiveCeilDiv(this.slotCount(), 4);
    }

    private int slotCount() {
        return Math.min(12, this.contents.size());
    }

    @Override
    public void renderImage(Font $$0, int $$1, int $$2, int $$3, int $$4, GuiGraphics $$5) {
        if (this.contents.isEmpty()) {
            this.renderEmptyBundleTooltip($$0, $$1, $$2, $$3, $$4, $$5);
        } else {
            this.renderBundleWithItemsTooltip($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    private void renderEmptyBundleTooltip(Font $$0, int $$1, int $$2, int $$3, int $$4, GuiGraphics $$5) {
        ClientBundleTooltip.drawEmptyBundleDescriptionText($$1 + this.getContentXOffset($$3), $$2, $$0, $$5);
        this.drawProgressbar($$1 + this.getContentXOffset($$3), $$2 + ClientBundleTooltip.getEmptyBundleDescriptionTextHeight($$0) + 4, $$0, $$5);
    }

    private void renderBundleWithItemsTooltip(Font $$0, int $$1, int $$2, int $$3, int $$4, GuiGraphics $$5) {
        boolean $$6 = this.contents.size() > 12;
        List<ItemStack> $$7 = this.getShownItems(this.contents.getNumberOfItemsToShow());
        int $$8 = $$1 + this.getContentXOffset($$3) + 96;
        int $$9 = $$2 + this.gridSizeY() * 24;
        int $$10 = 1;
        for (int $$11 = 1; $$11 <= this.gridSizeY(); ++$$11) {
            for (int $$12 = 1; $$12 <= 4; ++$$12) {
                int $$13 = $$8 - $$12 * 24;
                int $$14 = $$9 - $$11 * 24;
                if (ClientBundleTooltip.shouldRenderSurplusText($$6, $$12, $$11)) {
                    ClientBundleTooltip.renderCount($$13, $$14, this.getAmountOfHiddenItems($$7), $$0, $$5);
                    continue;
                }
                if (!ClientBundleTooltip.shouldRenderItemSlot($$7, $$10)) continue;
                this.renderSlot($$10, $$13, $$14, $$7, $$10, $$0, $$5);
                ++$$10;
            }
        }
        this.drawSelectedItemTooltip($$0, $$5, $$1, $$2, $$3);
        this.drawProgressbar($$1 + this.getContentXOffset($$3), $$2 + this.itemGridHeight() + 4, $$0, $$5);
    }

    private List<ItemStack> getShownItems(int $$0) {
        int $$1 = Math.min(this.contents.size(), $$0);
        return this.contents.itemCopyStream().toList().subList(0, $$1);
    }

    private static boolean shouldRenderSurplusText(boolean $$0, int $$1, int $$2) {
        return $$0 && $$1 * $$2 == 1;
    }

    private static boolean shouldRenderItemSlot(List<ItemStack> $$0, int $$1) {
        return $$0.size() >= $$1;
    }

    private int getAmountOfHiddenItems(List<ItemStack> $$0) {
        return this.contents.itemCopyStream().skip($$0.size()).mapToInt(ItemStack::getCount).sum();
    }

    private void renderSlot(int $$0, int $$1, int $$2, List<ItemStack> $$3, int $$4, Font $$5, GuiGraphics $$6) {
        int $$7 = $$3.size() - $$0;
        boolean $$8 = $$7 == this.contents.getSelectedItem();
        ItemStack $$9 = $$3.get($$7);
        if ($$8) {
            $$6.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, $$1, $$2, 24, 24);
        } else {
            $$6.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, $$1, $$2, 24, 24);
        }
        $$6.renderItem($$9, $$1 + 4, $$2 + 4, $$4);
        $$6.renderItemDecorations($$5, $$9, $$1 + 4, $$2 + 4);
        if ($$8) {
            $$6.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, $$1, $$2, 24, 24);
        }
    }

    private static void renderCount(int $$0, int $$1, int $$2, Font $$3, GuiGraphics $$4) {
        $$4.drawCenteredString($$3, "+" + $$2, $$0 + 12, $$1 + 10, -1);
    }

    private void drawSelectedItemTooltip(Font $$0, GuiGraphics $$1, int $$2, int $$3, int $$4) {
        if (this.contents.hasSelectedItem()) {
            ItemStack $$5 = this.contents.getItemUnsafe(this.contents.getSelectedItem());
            Component $$6 = $$5.getStyledHoverName();
            int $$7 = $$0.width($$6.getVisualOrderText());
            int $$8 = $$2 + $$4 / 2 - 12;
            ClientTooltipComponent $$9 = ClientTooltipComponent.create($$6.getVisualOrderText());
            $$1.renderTooltip($$0, List.of((Object)$$9), $$8 - $$7 / 2, $$3 - 15, DefaultTooltipPositioner.INSTANCE, $$5.get(DataComponents.TOOLTIP_STYLE));
        }
    }

    private void drawProgressbar(int $$0, int $$1, Font $$2, GuiGraphics $$3) {
        $$3.blitSprite(RenderPipelines.GUI_TEXTURED, this.getProgressBarTexture(), $$0 + 1, $$1, this.getProgressBarFill(), 13);
        $$3.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESSBAR_BORDER_SPRITE, $$0, $$1, 96, 13);
        Component $$4 = this.getProgressBarFillText();
        if ($$4 != null) {
            $$3.drawCenteredString($$2, $$4, $$0 + 48, $$1 + 3, -1);
        }
    }

    private static void drawEmptyBundleDescriptionText(int $$0, int $$1, Font $$2, GuiGraphics $$3) {
        $$3.drawWordWrap($$2, BUNDLE_EMPTY_DESCRIPTION, $$0, $$1, 96, -5592406);
    }

    private static int getEmptyBundleDescriptionTextHeight(Font $$0) {
        return $$0.split(BUNDLE_EMPTY_DESCRIPTION, 96).size() * $$0.lineHeight;
    }

    private int getProgressBarFill() {
        return Mth.clamp(Mth.mulAndTruncate(this.contents.weight(), 94), 0, 94);
    }

    private ResourceLocation getProgressBarTexture() {
        return this.contents.weight().compareTo(Fraction.ONE) >= 0 ? PROGRESSBAR_FULL_SPRITE : PROGRESSBAR_FILL_SPRITE;
    }

    @Nullable
    private Component getProgressBarFillText() {
        if (this.contents.isEmpty()) {
            return BUNDLE_EMPTY_TEXT;
        }
        if (this.contents.weight().compareTo(Fraction.ONE) >= 0) {
            return BUNDLE_FULL_TEXT;
        }
        return null;
    }
}

