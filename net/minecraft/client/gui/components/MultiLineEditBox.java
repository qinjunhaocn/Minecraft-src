/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;

public class MultiLineEditBox
extends AbstractTextAreaWidget {
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int TEXT_COLOR = -2039584;
    private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
    private static final int CURSOR_BLINK_INTERVAL_MS = 300;
    private final Font font;
    private final Component placeholder;
    private final MultilineTextField textField;
    private final int textColor;
    private final boolean textShadow;
    private final int cursorColor;
    private long focusedTime = Util.getMillis();

    MultiLineEditBox(Font $$0, int $$1, int $$2, int $$3, int $$4, Component $$5, Component $$6, int $$7, boolean $$8, int $$9, boolean $$10, boolean $$11) {
        super($$1, $$2, $$3, $$4, $$6, $$10, $$11);
        this.font = $$0;
        this.textShadow = $$8;
        this.textColor = $$7;
        this.cursorColor = $$9;
        this.placeholder = $$5;
        this.textField = new MultilineTextField($$0, $$3 - this.totalInnerPadding());
        this.textField.setCursorListener(this::scrollToCursor);
    }

    public void setCharacterLimit(int $$0) {
        this.textField.setCharacterLimit($$0);
    }

    public void setLineLimit(int $$0) {
        this.textField.setLineLimit($$0);
    }

    public void setValueListener(Consumer<String> $$0) {
        this.textField.setValueListener($$0);
    }

    public void setValue(String $$0) {
        this.setValue($$0, false);
    }

    public void setValue(String $$0, boolean $$1) {
        this.textField.setValue($$0, $$1);
    }

    public String getValue() {
        return this.textField.value();
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, Component.a("gui.narrate.editBox", this.getMessage(), this.getValue()));
    }

    @Override
    public void onClick(double $$0, double $$1) {
        this.textField.setSelecting(Screen.hasShiftDown());
        this.seekCursorScreen($$0, $$1);
    }

    @Override
    protected void onDrag(double $$0, double $$1, double $$2, double $$3) {
        this.textField.setSelecting(true);
        this.seekCursorScreen($$0, $$1);
        this.textField.setSelecting(Screen.hasShiftDown());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        return this.textField.keyPressed($$0);
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (!(this.visible && this.isFocused() && StringUtil.a($$0))) {
            return false;
        }
        this.textField.insertText(Character.toString($$0));
        return true;
    }

    @Override
    protected void renderContents(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        String $$4 = this.textField.value();
        if ($$4.isEmpty() && !this.isFocused()) {
            $$0.drawWordWrap(this.font, this.placeholder, this.getInnerLeft(), this.getInnerTop(), this.width - this.totalInnerPadding(), -857677600);
            return;
        }
        int $$5 = this.textField.cursor();
        boolean $$6 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L;
        boolean $$7 = $$5 < $$4.length();
        int $$8 = 0;
        int $$9 = 0;
        int $$10 = this.getInnerTop();
        for (MultilineTextField.StringView $$11 : this.textField.iterateLines()) {
            boolean $$12 = this.withinContentAreaTopBottom($$10, $$10 + this.font.lineHeight);
            int $$13 = this.getInnerLeft();
            if ($$6 && $$7 && $$5 >= $$11.beginIndex() && $$5 < $$11.endIndex()) {
                if ($$12) {
                    String $$14 = $$4.substring($$11.beginIndex(), $$5);
                    $$0.drawString(this.font, $$14, $$13, $$10, this.textColor, this.textShadow);
                    $$8 = $$13 + this.font.width($$14);
                    $$0.fill($$8, $$10 - 1, $$8 + 1, $$10 + 1 + this.font.lineHeight, this.cursorColor);
                    $$0.drawString(this.font, $$4.substring($$5, $$11.endIndex()), $$8, $$10, this.textColor, this.textShadow);
                }
            } else {
                if ($$12) {
                    String $$15 = $$4.substring($$11.beginIndex(), $$11.endIndex());
                    $$0.drawString(this.font, $$15, $$13, $$10, this.textColor, this.textShadow);
                    $$8 = $$13 + this.font.width($$15) - 1;
                }
                $$9 = $$10;
            }
            $$10 += this.font.lineHeight;
        }
        if ($$6 && !$$7 && this.withinContentAreaTopBottom($$9, $$9 + this.font.lineHeight)) {
            $$0.drawString(this.font, CURSOR_APPEND_CHARACTER, $$8, $$9, this.cursorColor, this.textShadow);
        }
        if (this.textField.hasSelection()) {
            MultilineTextField.StringView $$16 = this.textField.getSelected();
            int $$17 = this.getInnerLeft();
            $$10 = this.getInnerTop();
            for (MultilineTextField.StringView $$18 : this.textField.iterateLines()) {
                if ($$16.beginIndex() > $$18.endIndex()) {
                    $$10 += this.font.lineHeight;
                    continue;
                }
                if ($$18.beginIndex() > $$16.endIndex()) break;
                if (this.withinContentAreaTopBottom($$10, $$10 + this.font.lineHeight)) {
                    int $$21;
                    int $$19 = this.font.width($$4.substring($$18.beginIndex(), Math.max($$16.beginIndex(), $$18.beginIndex())));
                    if ($$16.endIndex() > $$18.endIndex()) {
                        int $$20 = this.width - this.innerPadding();
                    } else {
                        $$21 = this.font.width($$4.substring($$18.beginIndex(), $$16.endIndex()));
                    }
                    $$0.textHighlight($$17 + $$19, $$10, $$17 + $$21, $$10 + this.font.lineHeight);
                }
                $$10 += this.font.lineHeight;
            }
        }
    }

    @Override
    protected void renderDecorations(GuiGraphics $$0) {
        super.renderDecorations($$0);
        if (this.textField.hasCharacterLimit()) {
            int $$1 = this.textField.characterLimit();
            MutableComponent $$2 = Component.a("gui.multiLineEditBox.character_limit", this.textField.value().length(), $$1);
            $$0.drawString(this.font, $$2, this.getX() + this.width - this.font.width($$2), this.getY() + this.height + 4, -6250336);
        }
    }

    @Override
    public int getInnerHeight() {
        return this.font.lineHeight * this.textField.getLineCount();
    }

    @Override
    protected double scrollRate() {
        return (double)this.font.lineHeight / 2.0;
    }

    private void scrollToCursor() {
        double $$0 = this.scrollAmount();
        MultilineTextField.StringView $$1 = this.textField.getLineView((int)($$0 / (double)this.font.lineHeight));
        if (this.textField.cursor() <= $$1.beginIndex()) {
            $$0 = this.textField.getLineAtCursor() * this.font.lineHeight;
        } else {
            MultilineTextField.StringView $$2 = this.textField.getLineView((int)(($$0 + (double)this.height) / (double)this.font.lineHeight) - 1);
            if (this.textField.cursor() > $$2.endIndex()) {
                $$0 = this.textField.getLineAtCursor() * this.font.lineHeight - this.height + this.font.lineHeight + this.totalInnerPadding();
            }
        }
        this.setScrollAmount($$0);
    }

    private void seekCursorScreen(double $$0, double $$1) {
        double $$2 = $$0 - (double)this.getX() - (double)this.innerPadding();
        double $$3 = $$1 - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
        this.textField.seekCursorToPoint($$2, $$3);
    }

    @Override
    public void setFocused(boolean $$0) {
        super.setFocused($$0);
        if ($$0) {
            this.focusedTime = Util.getMillis();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int x;
        private int y;
        private Component placeholder = CommonComponents.EMPTY;
        private int textColor = -2039584;
        private boolean textShadow = true;
        private int cursorColor = -3092272;
        private boolean showBackground = true;
        private boolean showDecorations = true;

        public Builder setX(int $$0) {
            this.x = $$0;
            return this;
        }

        public Builder setY(int $$0) {
            this.y = $$0;
            return this;
        }

        public Builder setPlaceholder(Component $$0) {
            this.placeholder = $$0;
            return this;
        }

        public Builder setTextColor(int $$0) {
            this.textColor = $$0;
            return this;
        }

        public Builder setTextShadow(boolean $$0) {
            this.textShadow = $$0;
            return this;
        }

        public Builder setCursorColor(int $$0) {
            this.cursorColor = $$0;
            return this;
        }

        public Builder setShowBackground(boolean $$0) {
            this.showBackground = $$0;
            return this;
        }

        public Builder setShowDecorations(boolean $$0) {
            this.showDecorations = $$0;
            return this;
        }

        public MultiLineEditBox build(Font $$0, int $$1, int $$2, Component $$3) {
            return new MultiLineEditBox($$0, this.x, this.y, $$1, $$2, this.placeholder, $$3, this.textColor, this.textShadow, this.cursorColor, this.showBackground, this.showDecorations);
        }
    }
}

