/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractScrollArea
extends AbstractWidget {
    public static final int SCROLLBAR_WIDTH = 6;
    private double scrollAmount;
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
    private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
    private boolean scrolling;

    public AbstractScrollArea(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (!this.visible) {
            return false;
        }
        this.setScrollAmount(this.scrollAmount() - $$3 * this.scrollRate());
        return true;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling) {
            if ($$1 < (double)this.getY()) {
                this.setScrollAmount(0.0);
            } else if ($$1 > (double)this.getBottom()) {
                this.setScrollAmount(this.maxScrollAmount());
            } else {
                double $$5 = Math.max(1, this.maxScrollAmount());
                int $$6 = this.scrollerHeight();
                double $$7 = Math.max(1.0, $$5 / (double)(this.height - $$6));
                this.setScrollAmount(this.scrollAmount() + $$4 * $$7);
            }
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void onRelease(double $$0, double $$1) {
        this.scrolling = false;
    }

    public double scrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double $$0) {
        this.scrollAmount = Mth.clamp($$0, 0.0, (double)this.maxScrollAmount());
    }

    public boolean updateScrolling(double $$0, double $$1, int $$2) {
        this.scrolling = this.scrollbarVisible() && this.isValidClickButton($$2) && $$0 >= (double)this.scrollBarX() && $$0 <= (double)(this.scrollBarX() + 6) && $$1 >= (double)this.getY() && $$1 < (double)this.getBottom();
        return this.scrolling;
    }

    public void refreshScrollAmount() {
        this.setScrollAmount(this.scrollAmount);
    }

    public int maxScrollAmount() {
        return Math.max(0, this.contentHeight() - this.height);
    }

    protected boolean scrollbarVisible() {
        return this.maxScrollAmount() > 0;
    }

    protected int scrollerHeight() {
        return Mth.clamp((int)((float)(this.height * this.height) / (float)this.contentHeight()), 32, this.height - 8);
    }

    protected int scrollBarX() {
        return this.getRight() - 6;
    }

    protected int scrollBarY() {
        return Math.max(this.getY(), (int)this.scrollAmount * (this.height - this.scrollerHeight()) / this.maxScrollAmount() + this.getY());
    }

    protected void renderScrollbar(GuiGraphics $$0) {
        if (this.scrollbarVisible()) {
            int $$1 = this.scrollBarX();
            int $$2 = this.scrollerHeight();
            int $$3 = this.scrollBarY();
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_BACKGROUND_SPRITE, $$1, this.getY(), 6, this.getHeight());
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_SPRITE, $$1, $$3, 6, $$2);
        }
    }

    protected abstract int contentHeight();

    protected abstract double scrollRate();
}

