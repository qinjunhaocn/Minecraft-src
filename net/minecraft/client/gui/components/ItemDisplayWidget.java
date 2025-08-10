/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayWidget
extends AbstractWidget {
    private final Minecraft minecraft;
    private final int offsetX;
    private final int offsetY;
    private final ItemStack itemStack;
    private final boolean decorations;
    private final boolean tooltip;

    public ItemDisplayWidget(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, Component $$5, ItemStack $$6, boolean $$7, boolean $$8) {
        super(0, 0, $$3, $$4, $$5);
        this.minecraft = $$0;
        this.offsetX = $$1;
        this.offsetY = $$2;
        this.itemStack = $$6;
        this.decorations = $$7;
        this.tooltip = $$8;
    }

    @Override
    protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.renderItem(this.itemStack, this.getX() + this.offsetX, this.getY() + this.offsetY, 0);
        if (this.decorations) {
            $$0.renderItemDecorations(this.minecraft.font, this.itemStack, this.getX() + this.offsetX, this.getY() + this.offsetY, null);
        }
        if (this.isFocused()) {
            $$0.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1);
        }
        if (this.tooltip && this.isHoveredOrFocused()) {
            $$0.setTooltipForNextFrame(this.minecraft.font, this.itemStack, $$1, $$2);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, Component.a("narration.item", this.itemStack.getHoverName()));
    }
}

