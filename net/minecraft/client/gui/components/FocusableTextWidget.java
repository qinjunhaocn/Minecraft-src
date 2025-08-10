/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class FocusableTextWidget
extends MultiLineTextWidget {
    public static final int DEFAULT_PADDING = 4;
    private final boolean alwaysShowBorder;
    private final boolean fillBackground;
    private final int padding;

    public FocusableTextWidget(int $$0, Component $$1, Font $$2) {
        this($$0, $$1, $$2, 4);
    }

    public FocusableTextWidget(int $$0, Component $$1, Font $$2, int $$3) {
        this($$0, $$1, $$2, true, true, $$3);
    }

    public FocusableTextWidget(int $$0, Component $$1, Font $$2, boolean $$3, boolean $$4, int $$5) {
        super($$1, $$2);
        this.setMaxWidth($$0);
        this.setCentered(true);
        this.active = true;
        this.alwaysShowBorder = $$3;
        this.fillBackground = $$4;
        this.padding = $$5;
    }

    public void containWithin(int $$0) {
        this.setMaxWidth($$0 - this.padding * 4);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.getX() - this.padding;
        int $$5 = this.getY() - this.padding;
        int $$6 = this.getWidth() + this.padding * 2;
        int $$7 = this.getHeight() + this.padding * 2;
        int $$8 = ARGB.color(this.alpha, this.alwaysShowBorder ? (this.isFocused() ? -1 : -6250336) : -1);
        if (this.fillBackground) {
            $$0.fill($$4 + 1, $$5, $$4 + $$6, $$5 + $$7, ARGB.color(this.alpha, -16777216));
        }
        if (this.isFocused() || this.alwaysShowBorder) {
            $$0.renderOutline($$4, $$5, $$6, $$7, $$8);
        }
        super.renderWidget($$0, $$1, $$2, $$3);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }
}

