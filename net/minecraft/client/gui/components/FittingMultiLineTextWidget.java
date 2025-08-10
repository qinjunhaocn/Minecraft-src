/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class FittingMultiLineTextWidget
extends AbstractTextAreaWidget {
    private final Font font;
    private final MultiLineTextWidget multilineWidget;

    public FittingMultiLineTextWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, Font $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.font = $$5;
        this.multilineWidget = new MultiLineTextWidget($$4, $$5).setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

    public FittingMultiLineTextWidget setColor(int $$0) {
        this.multilineWidget.setColor($$0);
        return this;
    }

    @Override
    public void setWidth(int $$0) {
        super.setWidth($$0);
        this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
    }

    @Override
    protected int getInnerHeight() {
        return this.multilineWidget.getHeight();
    }

    @Override
    protected double scrollRate() {
        return this.font.lineHeight;
    }

    @Override
    protected void renderBackground(GuiGraphics $$0) {
        super.renderBackground($$0);
    }

    public boolean showingScrollBar() {
        return super.scrollbarVisible();
    }

    @Override
    protected void renderContents(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)this.getInnerLeft(), (float)this.getInnerTop());
        this.multilineWidget.render($$0, $$1, $$2, $$3);
        $$0.pose().popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public void setMessage(Component $$0) {
        super.setMessage($$0);
        this.multilineWidget.setMessage($$0);
    }
}

