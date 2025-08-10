/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringDecomposer;
import org.joml.Matrix4f;

public class Font {
    private static final float EFFECT_DEPTH = 0.01f;
    private static final float OVER_EFFECT_DEPTH = 0.01f;
    private static final float UNDER_EFFECT_DEPTH = -0.01f;
    public static final float SHADOW_DEPTH = 0.03f;
    public static final int NO_SHADOW = 0;
    public final int lineHeight = 9;
    public final RandomSource random = RandomSource.create();
    private final Function<ResourceLocation, FontSet> fonts;
    final boolean filterFishyGlyphs;
    private final StringSplitter splitter;

    public Font(Function<ResourceLocation, FontSet> $$02, boolean $$12) {
        this.fonts = $$02;
        this.filterFishyGlyphs = $$12;
        this.splitter = new StringSplitter(($$0, $$1) -> this.getFontSet($$1.getFont()).getGlyphInfo($$0, this.filterFishyGlyphs).getAdvance($$1.isBold()));
    }

    FontSet getFontSet(ResourceLocation $$0) {
        return this.fonts.apply($$0);
    }

    public String bidirectionalShaping(String $$0) {
        try {
            Bidi $$1 = new Bidi(new ArabicShaping(8).shape($$0), 127);
            $$1.setReorderingMode(0);
            return $$1.writeReordered(2);
        } catch (ArabicShapingException arabicShapingException) {
            return $$0;
        }
    }

    public void drawInBatch(String $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, DisplayMode $$7, int $$8, int $$9) {
        PreparedText $$10 = this.prepareText($$0, $$1, $$2, $$3, $$4, $$8);
        $$10.visit(GlyphVisitor.forMultiBufferSource($$6, $$5, $$7, $$9));
    }

    public void drawInBatch(Component $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, DisplayMode $$7, int $$8, int $$9) {
        PreparedText $$10 = this.prepareText($$0.getVisualOrderText(), $$1, $$2, $$3, $$4, $$8);
        $$10.visit(GlyphVisitor.forMultiBufferSource($$6, $$5, $$7, $$9));
    }

    public void drawInBatch(FormattedCharSequence $$0, float $$1, float $$2, int $$3, boolean $$4, Matrix4f $$5, MultiBufferSource $$6, DisplayMode $$7, int $$8, int $$9) {
        PreparedText $$10 = this.prepareText($$0, $$1, $$2, $$3, $$4, $$8);
        $$10.visit(GlyphVisitor.forMultiBufferSource($$6, $$5, $$7, $$9));
    }

    public void drawInBatch8xOutline(FormattedCharSequence $$0, float $$1, float $$2, int $$3, int $$4, Matrix4f $$5, MultiBufferSource $$62, int $$72) {
        PreparedTextBuilder $$82 = new PreparedTextBuilder(0.0f, 0.0f, $$4, false);
        for (int $$9 = -1; $$9 <= 1; ++$$9) {
            for (int $$10 = -1; $$10 <= 1; ++$$10) {
                if ($$9 == 0 && $$10 == 0) continue;
                float[] $$11 = new float[]{$$1};
                int $$12 = $$9;
                int $$13 = $$10;
                $$0.accept(($$6, $$7, $$8) -> {
                    boolean $$9 = $$7.isBold();
                    FontSet $$10 = this.getFontSet($$7.getFont());
                    GlyphInfo $$11 = $$10.getGlyphInfo($$8, this.filterFishyGlyphs);
                    $$0.x = $$11[0] + (float)$$12 * $$11.getShadowOffset();
                    $$0.y = $$2 + (float)$$13 * $$11.getShadowOffset();
                    $$1[0] = $$11[0] + $$11.getAdvance($$9);
                    return $$82.accept($$6, $$7.withColor($$4), $$8);
                });
            }
        }
        GlyphVisitor $$14 = GlyphVisitor.forMultiBufferSource($$62, $$5, DisplayMode.NORMAL, $$72);
        for (BakedGlyph.GlyphInstance $$15 : $$82.glyphs) {
            $$14.acceptGlyph($$15);
        }
        PreparedTextBuilder $$16 = new PreparedTextBuilder($$1, $$2, $$3, false);
        $$0.accept($$16);
        $$16.visit(GlyphVisitor.forMultiBufferSource($$62, $$5, DisplayMode.POLYGON_OFFSET, $$72));
    }

    public PreparedText prepareText(String $$0, float $$1, float $$2, int $$3, boolean $$4, int $$5) {
        if (this.isBidirectional()) {
            $$0 = this.bidirectionalShaping($$0);
        }
        PreparedTextBuilder $$6 = new PreparedTextBuilder($$1, $$2, $$3, $$5, $$4);
        StringDecomposer.iterateFormatted($$0, Style.EMPTY, (FormattedCharSink)$$6);
        return $$6;
    }

    public PreparedText prepareText(FormattedCharSequence $$0, float $$1, float $$2, int $$3, boolean $$4, int $$5) {
        PreparedTextBuilder $$6 = new PreparedTextBuilder($$1, $$2, $$3, $$5, $$4);
        $$0.accept($$6);
        return $$6;
    }

    public int width(String $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public int width(FormattedText $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public int width(FormattedCharSequence $$0) {
        return Mth.ceil(this.splitter.stringWidth($$0));
    }

    public String plainSubstrByWidth(String $$0, int $$1, boolean $$2) {
        return $$2 ? this.splitter.plainTailByWidth($$0, $$1, Style.EMPTY) : this.splitter.plainHeadByWidth($$0, $$1, Style.EMPTY);
    }

    public String plainSubstrByWidth(String $$0, int $$1) {
        return this.splitter.plainHeadByWidth($$0, $$1, Style.EMPTY);
    }

    public FormattedText substrByWidth(FormattedText $$0, int $$1) {
        return this.splitter.headByWidth($$0, $$1, Style.EMPTY);
    }

    public int wordWrapHeight(String $$0, int $$1) {
        return 9 * this.splitter.splitLines($$0, $$1, Style.EMPTY).size();
    }

    public int wordWrapHeight(FormattedText $$0, int $$1) {
        return 9 * this.splitter.splitLines($$0, $$1, Style.EMPTY).size();
    }

    public List<FormattedCharSequence> split(FormattedText $$0, int $$1) {
        return Language.getInstance().getVisualOrder(this.splitter.splitLines($$0, $$1, Style.EMPTY));
    }

    public List<FormattedText> splitIgnoringLanguage(FormattedText $$0, int $$1) {
        return this.splitter.splitLines($$0, $$1, Style.EMPTY);
    }

    public boolean isBidirectional() {
        return Language.getInstance().isDefaultRightToLeft();
    }

    public StringSplitter getSplitter() {
        return this.splitter;
    }

    public static interface PreparedText {
        public void visit(GlyphVisitor var1);

        @Nullable
        public ScreenRectangle bounds();
    }

    public static interface GlyphVisitor {
        public static GlyphVisitor forMultiBufferSource(final MultiBufferSource $$0, final Matrix4f $$1, final DisplayMode $$2, final int $$3) {
            return new GlyphVisitor(){

                @Override
                public void acceptGlyph(BakedGlyph.GlyphInstance $$02) {
                    BakedGlyph $$12 = $$02.glyph();
                    VertexConsumer $$22 = $$0.getBuffer($$12.renderType($$2));
                    $$12.renderChar($$02, $$1, $$22, $$3, false);
                }

                @Override
                public void acceptEffect(BakedGlyph $$02, BakedGlyph.Effect $$12) {
                    VertexConsumer $$22 = $$0.getBuffer($$02.renderType($$2));
                    $$02.renderEffect($$12, $$1, $$22, $$3, false);
                }
            };
        }

        public void acceptGlyph(BakedGlyph.GlyphInstance var1);

        public void acceptEffect(BakedGlyph var1, BakedGlyph.Effect var2);
    }

    public static final class DisplayMode
    extends Enum<DisplayMode> {
        public static final /* enum */ DisplayMode NORMAL = new DisplayMode();
        public static final /* enum */ DisplayMode SEE_THROUGH = new DisplayMode();
        public static final /* enum */ DisplayMode POLYGON_OFFSET = new DisplayMode();
        private static final /* synthetic */ DisplayMode[] $VALUES;

        public static DisplayMode[] values() {
            return (DisplayMode[])$VALUES.clone();
        }

        public static DisplayMode valueOf(String $$0) {
            return Enum.valueOf(DisplayMode.class, $$0);
        }

        private static /* synthetic */ DisplayMode[] a() {
            return new DisplayMode[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
        }

        static {
            $VALUES = DisplayMode.a();
        }
    }

    class PreparedTextBuilder
    implements FormattedCharSink,
    PreparedText {
        private final boolean drawShadow;
        private final int color;
        private final int backgroundColor;
        float x;
        float y;
        private float left = Float.MAX_VALUE;
        private float top = Float.MAX_VALUE;
        private float right = -3.4028235E38f;
        private float bottom = -3.4028235E38f;
        private float backgroundLeft = Float.MAX_VALUE;
        private float backgroundTop = Float.MAX_VALUE;
        private float backgroundRight = -3.4028235E38f;
        private float backgroundBottom = -3.4028235E38f;
        final List<BakedGlyph.GlyphInstance> glyphs = new ArrayList<BakedGlyph.GlyphInstance>();
        @Nullable
        private List<BakedGlyph.Effect> effects;

        public PreparedTextBuilder(float $$0, float $$1, int $$2, boolean $$3) {
            this($$0, $$1, $$2, 0, $$3);
        }

        public PreparedTextBuilder(float $$0, float $$1, int $$2, int $$3, boolean $$4) {
            this.x = $$0;
            this.y = $$1;
            this.drawShadow = $$4;
            this.color = $$2;
            this.backgroundColor = $$3;
            this.markBackground($$0, $$1, 0.0f);
        }

        private void markSize(float $$0, float $$1, float $$2, float $$3) {
            this.left = Math.min(this.left, $$0);
            this.top = Math.min(this.top, $$1);
            this.right = Math.max(this.right, $$2);
            this.bottom = Math.max(this.bottom, $$3);
        }

        private void markBackground(float $$0, float $$1, float $$2) {
            if (ARGB.alpha(this.backgroundColor) == 0) {
                return;
            }
            this.backgroundLeft = Math.min(this.backgroundLeft, $$0 - 1.0f);
            this.backgroundTop = Math.min(this.backgroundTop, $$1 - 1.0f);
            this.backgroundRight = Math.max(this.backgroundRight, $$0 + $$2);
            this.backgroundBottom = Math.max(this.backgroundBottom, $$1 + 9.0f);
            this.markSize(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom);
        }

        private void addGlyph(BakedGlyph.GlyphInstance $$0) {
            this.glyphs.add($$0);
            this.markSize($$0.left(), $$0.top(), $$0.right(), $$0.bottom());
        }

        private void addEffect(BakedGlyph.Effect $$0) {
            if (this.effects == null) {
                this.effects = new ArrayList<BakedGlyph.Effect>();
            }
            this.effects.add($$0);
            this.markSize($$0.left(), $$0.top(), $$0.right(), $$0.bottom());
        }

        @Override
        public boolean accept(int $$0, Style $$1, int $$2) {
            FontSet $$3 = Font.this.getFontSet($$1.getFont());
            GlyphInfo $$4 = $$3.getGlyphInfo($$2, Font.this.filterFishyGlyphs);
            BakedGlyph $$5 = $$1.isObfuscated() && $$2 != 32 ? $$3.getRandomGlyph($$4) : $$3.getGlyph($$2);
            boolean $$6 = $$1.isBold();
            TextColor $$7 = $$1.getColor();
            int $$8 = this.getTextColor($$7);
            int $$9 = this.getShadowColor($$1, $$8);
            float $$10 = $$4.getAdvance($$6);
            float $$11 = $$0 == 0 ? this.x - 1.0f : this.x;
            float $$12 = $$4.getShadowOffset();
            if (!($$5 instanceof EmptyGlyph)) {
                float $$13 = $$6 ? $$4.getBoldOffset() : 0.0f;
                this.addGlyph(new BakedGlyph.GlyphInstance(this.x, this.y, $$8, $$9, $$5, $$1, $$13, $$12));
            }
            this.markBackground(this.x, this.y, $$10);
            if ($$1.isStrikethrough()) {
                this.addEffect(new BakedGlyph.Effect($$11, this.y + 4.5f - 1.0f, this.x + $$10, this.y + 4.5f, 0.01f, $$8, $$9, $$12));
            }
            if ($$1.isUnderlined()) {
                this.addEffect(new BakedGlyph.Effect($$11, this.y + 9.0f - 1.0f, this.x + $$10, this.y + 9.0f, 0.01f, $$8, $$9, $$12));
            }
            this.x += $$10;
            return true;
        }

        @Override
        public void visit(GlyphVisitor $$0) {
            BakedGlyph $$1 = null;
            if (ARGB.alpha(this.backgroundColor) != 0) {
                BakedGlyph.Effect $$2 = new BakedGlyph.Effect(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom, -0.01f, this.backgroundColor);
                $$1 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
                $$0.acceptEffect($$1, $$2);
            }
            for (BakedGlyph.GlyphInstance $$3 : this.glyphs) {
                $$0.acceptGlyph($$3);
            }
            if (this.effects != null) {
                if ($$1 == null) {
                    $$1 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
                }
                for (BakedGlyph.Effect $$4 : this.effects) {
                    $$0.acceptEffect($$1, $$4);
                }
            }
        }

        private int getTextColor(@Nullable TextColor $$0) {
            if ($$0 != null) {
                int $$1 = ARGB.alpha(this.color);
                int $$2 = $$0.getValue();
                return ARGB.color($$1, $$2);
            }
            return this.color;
        }

        private int getShadowColor(Style $$0, int $$1) {
            Integer $$2 = $$0.getShadowColor();
            if ($$2 != null) {
                float $$3 = ARGB.alphaFloat($$1);
                float $$4 = ARGB.alphaFloat($$2);
                if ($$3 != 1.0f) {
                    return ARGB.color(ARGB.as8BitChannel($$3 * $$4), (int)$$2);
                }
                return $$2;
            }
            if (this.drawShadow) {
                return ARGB.scaleRGB($$1, 0.25f);
            }
            return 0;
        }

        @Override
        @Nullable
        public ScreenRectangle bounds() {
            if (this.left >= this.right || this.top >= this.bottom) {
                return null;
            }
            int $$0 = Mth.floor(this.left);
            int $$1 = Mth.floor(this.top);
            int $$2 = Mth.ceil(this.right);
            int $$3 = Mth.ceil(this.bottom);
            return new ScreenRectangle($$0, $$1, $$2 - $$0, $$3 - $$1);
        }
    }
}

