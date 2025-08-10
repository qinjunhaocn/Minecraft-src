/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractTextAreaWidget
extends AbstractScrollArea {
    private static final WidgetSprites BACKGROUND_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/text_field"), ResourceLocation.withDefaultNamespace("widget/text_field_highlighted"));
    private static final int INNER_PADDING = 4;
    public static final int DEFAULT_TOTAL_PADDING = 8;
    private boolean showBackground = true;
    private boolean showDecorations = true;

    public AbstractTextAreaWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    public AbstractTextAreaWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, boolean $$5, boolean $$6) {
        this($$0, $$1, $$2, $$3, $$4);
        this.showBackground = $$5;
        this.showDecorations = $$6;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        boolean $$3 = this.updateScrolling($$0, $$1, $$2);
        return super.mouseClicked($$0, $$1, $$2) || $$3;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        boolean $$4;
        boolean $$3 = $$0 == 265;
        boolean bl = $$4 = $$0 == 264;
        if ($$3 || $$4) {
            double $$5 = this.scrollAmount();
            this.setScrollAmount(this.scrollAmount() + (double)($$3 ? -1 : 1) * this.scrollRate());
            if ($$5 != this.scrollAmount()) {
                return true;
            }
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.visible) {
            return;
        }
        if (this.showBackground) {
            this.renderBackground($$0);
        }
        $$0.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
        $$0.pose().pushMatrix();
        $$0.pose().translate(0.0f, (float)(-this.scrollAmount()));
        this.renderContents($$0, $$1, $$2, $$3);
        $$0.pose().popMatrix();
        $$0.disableScissor();
        this.renderScrollbar($$0);
        if (this.showDecorations) {
            this.renderDecorations($$0);
        }
    }

    protected void renderDecorations(GuiGraphics $$0) {
    }

    protected int innerPadding() {
        return 4;
    }

    protected int totalInnerPadding() {
        return this.innerPadding() * 2;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return this.active && this.visible && $$0 >= (double)this.getX() && $$1 >= (double)this.getY() && $$0 < (double)(this.getRight() + 6) && $$1 < (double)this.getBottom();
    }

    @Override
    protected int scrollBarX() {
        return this.getRight();
    }

    @Override
    protected int contentHeight() {
        return this.getInnerHeight() + this.totalInnerPadding();
    }

    protected void renderBackground(GuiGraphics $$0) {
        this.renderBorder($$0, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    protected void renderBorder(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        ResourceLocation $$5 = BACKGROUND_SPRITES.get(this.isActive(), this.isFocused());
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5, $$1, $$2, $$3, $$4);
    }

    protected boolean withinContentAreaTopBottom(int $$0, int $$1) {
        return (double)$$1 - this.scrollAmount() >= (double)this.getY() && (double)$$0 - this.scrollAmount() <= (double)(this.getY() + this.height);
    }

    protected abstract int getInnerHeight();

    protected abstract void renderContents(GuiGraphics var1, int var2, int var3, float var4);

    protected int getInnerLeft() {
        return this.getX() + this.innerPadding();
    }

    protected int getInnerTop() {
        return this.getY() + this.innerPadding();
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }
}

