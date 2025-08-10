/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public abstract class AbstractStringWidget
extends AbstractWidget {
    private final Font font;
    private int color = -1;

    public AbstractStringWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, Font $$5) {
        super($$0, $$1, $$2, $$3, $$4);
        this.font = $$5;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
    }

    public AbstractStringWidget setColor(int $$0) {
        this.color = $$0;
        return this;
    }

    protected final Font getFont() {
        return this.font;
    }

    protected final int getColor() {
        return ARGB.color(this.alpha, this.color);
    }
}

