/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.components;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public interface MultiLineLabel {
    public static final MultiLineLabel EMPTY = new MultiLineLabel(){

        @Override
        public void renderCentered(GuiGraphics $$0, int $$1, int $$2) {
        }

        @Override
        public void renderCentered(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        }

        @Override
        public void renderLeftAligned(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        }

        @Override
        public int renderLeftAlignedNoShadow(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
            return $$2;
        }

        @Override
        @Nullable
        public Style getStyleAtCentered(int $$0, int $$1, int $$2, double $$3, double $$4) {
            return null;
        }

        @Override
        @Nullable
        public Style getStyleAtLeftAligned(int $$0, int $$1, int $$2, double $$3, double $$4) {
            return null;
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }
    };

    public static MultiLineLabel a(Font $$0, Component ... $$1) {
        return MultiLineLabel.a($$0, Integer.MAX_VALUE, Integer.MAX_VALUE, $$1);
    }

    public static MultiLineLabel a(Font $$0, int $$1, Component ... $$2) {
        return MultiLineLabel.a($$0, $$1, Integer.MAX_VALUE, $$2);
    }

    public static MultiLineLabel create(Font $$0, Component $$1, int $$2) {
        return MultiLineLabel.a($$0, $$2, Integer.MAX_VALUE, $$1);
    }

    public static MultiLineLabel a(final Font $$0, final int $$1, final int $$2, final Component ... $$3) {
        if ($$3.length == 0) {
            return EMPTY;
        }
        return new MultiLineLabel(){
            @Nullable
            private List<TextAndWidth> cachedTextAndWidth;
            @Nullable
            private Language splitWithLanguage;

            @Override
            public void renderCentered(GuiGraphics $$02, int $$12, int $$22) {
                this.renderCentered($$02, $$12, $$22, $$0.lineHeight, -1);
            }

            @Override
            public void renderCentered(GuiGraphics $$02, int $$12, int $$22, int $$32, int $$4) {
                int $$5 = $$22;
                for (TextAndWidth $$6 : this.getSplitMessage()) {
                    $$02.drawString($$0, $$6.text, $$12 - $$6.width / 2, $$5, $$4);
                    $$5 += $$32;
                }
            }

            @Override
            public void renderLeftAligned(GuiGraphics $$02, int $$12, int $$22, int $$32, int $$4) {
                int $$5 = $$22;
                for (TextAndWidth $$6 : this.getSplitMessage()) {
                    $$02.drawString($$0, $$6.text, $$12, $$5, $$4);
                    $$5 += $$32;
                }
            }

            @Override
            public int renderLeftAlignedNoShadow(GuiGraphics $$02, int $$12, int $$22, int $$32, int $$4) {
                int $$5 = $$22;
                for (TextAndWidth $$6 : this.getSplitMessage()) {
                    $$02.drawString($$0, $$6.text, $$12, $$5, $$4, false);
                    $$5 += $$32;
                }
                return $$5;
            }

            @Override
            @Nullable
            public Style getStyleAtCentered(int $$02, int $$12, int $$22, double $$32, double $$4) {
                List<TextAndWidth> $$5 = this.getSplitMessage();
                int $$6 = Mth.floor(($$4 - (double)$$12) / (double)$$22);
                if ($$6 < 0 || $$6 >= $$5.size()) {
                    return null;
                }
                TextAndWidth $$7 = $$5.get($$6);
                int $$8 = $$02 - $$7.width / 2;
                if ($$32 < (double)$$8) {
                    return null;
                }
                int $$9 = Mth.floor($$32 - (double)$$8);
                return $$0.getSplitter().componentStyleAtWidth($$7.text, $$9);
            }

            @Override
            @Nullable
            public Style getStyleAtLeftAligned(int $$02, int $$12, int $$22, double $$32, double $$4) {
                if ($$32 < (double)$$02) {
                    return null;
                }
                List<TextAndWidth> $$5 = this.getSplitMessage();
                int $$6 = Mth.floor(($$4 - (double)$$12) / (double)$$22);
                if ($$6 < 0 || $$6 >= $$5.size()) {
                    return null;
                }
                TextAndWidth $$7 = $$5.get($$6);
                int $$8 = Mth.floor($$32 - (double)$$02);
                return $$0.getSplitter().componentStyleAtWidth($$7.text, $$8);
            }

            private List<TextAndWidth> getSplitMessage() {
                Language $$02 = Language.getInstance();
                if (this.cachedTextAndWidth != null && $$02 == this.splitWithLanguage) {
                    return this.cachedTextAndWidth;
                }
                this.splitWithLanguage = $$02;
                ArrayList<FormattedText> $$12 = new ArrayList<FormattedText>();
                for (Component $$22 : $$3) {
                    $$12.addAll($$0.splitIgnoringLanguage($$22, $$1));
                }
                this.cachedTextAndWidth = new ArrayList<TextAndWidth>();
                int $$32 = Math.min($$12.size(), $$2);
                List $$4 = $$12.subList(0, $$32);
                for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                    FormattedText $$6 = (FormattedText)$$4.get($$5);
                    FormattedCharSequence $$7 = Language.getInstance().getVisualOrder($$6);
                    if ($$5 == $$4.size() - 1 && $$32 == $$2 && $$32 != $$12.size()) {
                        FormattedText $$8 = $$0.substrByWidth($$6, $$0.width($$6) - $$0.width(CommonComponents.ELLIPSIS));
                        FormattedText $$9 = FormattedText.a($$8, CommonComponents.ELLIPSIS);
                        this.cachedTextAndWidth.add(new TextAndWidth(Language.getInstance().getVisualOrder($$9), $$0.width($$9)));
                        continue;
                    }
                    this.cachedTextAndWidth.add(new TextAndWidth($$7, $$0.width($$7)));
                }
                return this.cachedTextAndWidth;
            }

            @Override
            public int getLineCount() {
                return this.getSplitMessage().size();
            }

            @Override
            public int getWidth() {
                return Math.min($$1, this.getSplitMessage().stream().mapToInt(TextAndWidth::width).max().orElse(0));
            }
        };
    }

    public void renderCentered(GuiGraphics var1, int var2, int var3);

    public void renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5);

    public void renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5);

    public int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5);

    @Nullable
    public Style getStyleAtCentered(int var1, int var2, int var3, double var4, double var6);

    @Nullable
    public Style getStyleAtLeftAligned(int var1, int var2, int var3, double var4, double var6);

    public int getLineCount();

    public int getWidth();

    public static final class TextAndWidth
    extends Record {
        final FormattedCharSequence text;
        final int width;

        public TextAndWidth(FormattedCharSequence $$0, int $$1) {
            this.text = $$0;
            this.width = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextAndWidth.class, "text;width", "text", "width"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextAndWidth.class, "text;width", "text", "width"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextAndWidth.class, "text;width", "text", "width"}, this, $$0);
        }

        public FormattedCharSequence text() {
            return this.text;
        }

        public int width() {
            return this.width;
        }
    }
}

