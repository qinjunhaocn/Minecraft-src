/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class BakedGlyph {
    public static final float Z_FIGHTER = 0.001f;
    private final GlyphRenderTypes renderTypes;
    @Nullable
    private final GpuTextureView textureView;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;

    public BakedGlyph(GlyphRenderTypes $$0, @Nullable GpuTextureView $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        this.renderTypes = $$0;
        this.textureView = $$1;
        this.u0 = $$2;
        this.u1 = $$3;
        this.v0 = $$4;
        this.v1 = $$5;
        this.left = $$6;
        this.right = $$7;
        this.up = $$8;
        this.down = $$9;
    }

    public float left(GlyphInstance $$0) {
        return $$0.x + this.left + ($$0.style.isItalic() ? Math.min(this.shearTop(), this.shearBottom()) : 0.0f) - BakedGlyph.extraThickness($$0.style.isBold());
    }

    public float top(GlyphInstance $$0) {
        return $$0.y + this.up - BakedGlyph.extraThickness($$0.style.isBold());
    }

    public float right(GlyphInstance $$0) {
        return $$0.x + this.right + ($$0.hasShadow() ? $$0.shadowOffset : 0.0f) + ($$0.style.isItalic() ? Math.max(this.shearTop(), this.shearBottom()) : 0.0f) + BakedGlyph.extraThickness($$0.style.isBold());
    }

    public float bottom(GlyphInstance $$0) {
        return $$0.y + this.down + ($$0.hasShadow() ? $$0.shadowOffset : 0.0f) + BakedGlyph.extraThickness($$0.style.isBold());
    }

    public void renderChar(GlyphInstance $$0, Matrix4f $$1, VertexConsumer $$2, int $$3, boolean $$4) {
        float $$14;
        float $$11;
        Style $$5 = $$0.style();
        boolean $$6 = $$5.isItalic();
        float $$7 = $$0.x();
        float $$8 = $$0.y();
        int $$9 = $$0.color();
        boolean $$10 = $$5.isBold();
        float f = $$11 = $$4 ? 0.0f : 0.001f;
        if ($$0.hasShadow()) {
            int $$12 = $$0.shadowColor();
            this.render($$6, $$7 + $$0.shadowOffset(), $$8 + $$0.shadowOffset(), 0.0f, $$1, $$2, $$12, $$10, $$3);
            if ($$10) {
                this.render($$6, $$7 + $$0.boldOffset() + $$0.shadowOffset(), $$8 + $$0.shadowOffset(), $$11, $$1, $$2, $$12, true, $$3);
            }
            float $$13 = $$4 ? 0.0f : 0.03f;
        } else {
            $$14 = 0.0f;
        }
        this.render($$6, $$7, $$8, $$14, $$1, $$2, $$9, $$10, $$3);
        if ($$10) {
            this.render($$6, $$7 + $$0.boldOffset(), $$8, $$14 + $$11, $$1, $$2, $$9, true, $$3);
        }
    }

    private void render(boolean $$0, float $$1, float $$2, float $$3, Matrix4f $$4, VertexConsumer $$5, int $$6, boolean $$7, int $$8) {
        float $$9 = $$1 + this.left;
        float $$10 = $$1 + this.right;
        float $$11 = $$2 + this.up;
        float $$12 = $$2 + this.down;
        float $$13 = $$0 ? this.shearTop() : 0.0f;
        float $$14 = $$0 ? this.shearBottom() : 0.0f;
        float $$15 = BakedGlyph.extraThickness($$7);
        $$5.addVertex($$4, $$9 + $$13 - $$15, $$11 - $$15, $$3).setColor($$6).setUv(this.u0, this.v0).setLight($$8);
        $$5.addVertex($$4, $$9 + $$14 - $$15, $$12 + $$15, $$3).setColor($$6).setUv(this.u0, this.v1).setLight($$8);
        $$5.addVertex($$4, $$10 + $$14 + $$15, $$12 + $$15, $$3).setColor($$6).setUv(this.u1, this.v1).setLight($$8);
        $$5.addVertex($$4, $$10 + $$13 + $$15, $$11 - $$15, $$3).setColor($$6).setUv(this.u1, this.v0).setLight($$8);
    }

    private static float extraThickness(boolean $$0) {
        return $$0 ? 0.1f : 0.0f;
    }

    private float shearBottom() {
        return 1.0f - 0.25f * this.down;
    }

    private float shearTop() {
        return 1.0f - 0.25f * this.up;
    }

    public void renderEffect(Effect $$0, Matrix4f $$1, VertexConsumer $$2, int $$3, boolean $$4) {
        float $$5;
        float f = $$5 = $$4 ? 0.0f : $$0.depth;
        if ($$0.hasShadow()) {
            this.buildEffect($$0, $$0.shadowOffset(), $$5, $$0.shadowColor(), $$2, $$3, $$1);
            $$5 += $$4 ? 0.0f : 0.03f;
        }
        this.buildEffect($$0, 0.0f, $$5, $$0.color, $$2, $$3, $$1);
    }

    private void buildEffect(Effect $$0, float $$1, float $$2, int $$3, VertexConsumer $$4, int $$5, Matrix4f $$6) {
        $$4.addVertex($$6, $$0.x0 + $$1, $$0.y1 + $$1, $$2).setColor($$3).setUv(this.u0, this.v0).setLight($$5);
        $$4.addVertex($$6, $$0.x1 + $$1, $$0.y1 + $$1, $$2).setColor($$3).setUv(this.u0, this.v1).setLight($$5);
        $$4.addVertex($$6, $$0.x1 + $$1, $$0.y0 + $$1, $$2).setColor($$3).setUv(this.u1, this.v1).setLight($$5);
        $$4.addVertex($$6, $$0.x0 + $$1, $$0.y0 + $$1, $$2).setColor($$3).setUv(this.u1, this.v0).setLight($$5);
    }

    @Nullable
    public GpuTextureView textureView() {
        return this.textureView;
    }

    public RenderPipeline guiPipeline() {
        return this.renderTypes.guiPipeline();
    }

    public RenderType renderType(Font.DisplayMode $$0) {
        return this.renderTypes.select($$0);
    }

    public static final class GlyphInstance
    extends Record {
        final float x;
        final float y;
        private final int color;
        private final int shadowColor;
        private final BakedGlyph glyph;
        final Style style;
        private final float boldOffset;
        final float shadowOffset;

        public GlyphInstance(float $$0, float $$1, int $$2, int $$3, BakedGlyph $$4, Style $$5, float $$6, float $$7) {
            this.x = $$0;
            this.y = $$1;
            this.color = $$2;
            this.shadowColor = $$3;
            this.glyph = $$4;
            this.style = $$5;
            this.boldOffset = $$6;
            this.shadowOffset = $$7;
        }

        public float left() {
            return this.glyph.left(this);
        }

        public float top() {
            return this.glyph.top(this);
        }

        public float right() {
            return this.glyph.right(this);
        }

        public float bottom() {
            return this.glyph.bottom(this);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GlyphInstance.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GlyphInstance.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GlyphInstance.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this, $$0);
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public BakedGlyph glyph() {
            return this.glyph;
        }

        public Style style() {
            return this.style;
        }

        public float boldOffset() {
            return this.boldOffset;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }

    public static final class Effect
    extends Record {
        final float x0;
        final float y0;
        final float x1;
        final float y1;
        final float depth;
        final int color;
        private final int shadowColor;
        private final float shadowOffset;

        public Effect(float $$0, float $$1, float $$2, float $$3, float $$4, int $$5) {
            this($$0, $$1, $$2, $$3, $$4, $$5, 0, 0.0f);
        }

        public Effect(float $$0, float $$1, float $$2, float $$3, float $$4, int $$5, int $$6, float $$7) {
            this.x0 = $$0;
            this.y0 = $$1;
            this.x1 = $$2;
            this.y1 = $$3;
            this.depth = $$4;
            this.color = $$5;
            this.shadowColor = $$6;
            this.shadowOffset = $$7;
        }

        public float left() {
            return this.x0;
        }

        public float top() {
            return this.y0;
        }

        public float right() {
            return this.x1 + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        public float bottom() {
            return this.y1 + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Effect.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "x0", "y0", "x1", "y1", "depth", "color", "shadowColor", "shadowOffset"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Effect.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "x0", "y0", "x1", "y1", "depth", "color", "shadowColor", "shadowOffset"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Effect.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "x0", "y0", "x1", "y1", "depth", "color", "shadowColor", "shadowOffset"}, this, $$0);
        }

        public float x0() {
            return this.x0;
        }

        public float y0() {
            return this.y0;
        }

        public float x1() {
            return this.x1;
        }

        public float y1() {
            return this.y1;
        }

        public float depth() {
            return this.depth;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }
}

