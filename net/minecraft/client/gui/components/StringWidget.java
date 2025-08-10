/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class StringWidget
extends AbstractStringWidget {
    private float alignX = 0.5f;

    public StringWidget(Component $$0, Font $$1) {
        this(0, 0, $$1.width($$0.getVisualOrderText()), $$1.lineHeight, $$0, $$1);
    }

    public StringWidget(int $$0, int $$1, Component $$2, Font $$3) {
        this(0, 0, $$0, $$1, $$2, $$3);
    }

    public StringWidget(int $$0, int $$1, int $$2, int $$3, Component $$4, Font $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
        this.active = false;
    }

    @Override
    public StringWidget setColor(int $$0) {
        super.setColor($$0);
        return this;
    }

    private StringWidget horizontalAlignment(float $$0) {
        this.alignX = $$0;
        return this;
    }

    public StringWidget alignLeft() {
        return this.horizontalAlignment(0.0f);
    }

    public StringWidget alignCenter() {
        return this.horizontalAlignment(0.5f);
    }

    public StringWidget alignRight() {
        return this.horizontalAlignment(1.0f);
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        Component $$4 = this.getMessage();
        Font $$5 = this.getFont();
        int $$6 = this.getWidth();
        int $$7 = $$5.width($$4);
        int $$8 = this.getX() + Math.round(this.alignX * (float)($$6 - $$7));
        int $$9 = this.getY() + (this.getHeight() - $$5.lineHeight) / 2;
        FormattedCharSequence $$10 = $$7 > $$6 ? this.clipText($$4, $$6) : $$4.getVisualOrderText();
        $$0.drawString($$5, $$10, $$8, $$9, this.getColor());
    }

    private FormattedCharSequence clipText(Component $$0, int $$1) {
        Font $$2 = this.getFont();
        FormattedText $$3 = $$2.substrByWidth($$0, $$1 - $$2.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.a($$3, CommonComponents.ELLIPSIS));
    }

    @Override
    public /* synthetic */ AbstractStringWidget setColor(int n) {
        return this.setColor(n);
    }
}

