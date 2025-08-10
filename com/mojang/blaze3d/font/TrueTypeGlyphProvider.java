/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Bitmap
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FT_GlyphSlot
 *  org.lwjgl.util.freetype.FT_Vector
 *  org.lwjgl.util.freetype.FreeType
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

public class TrueTypeGlyphProvider
implements GlyphProvider {
    @Nullable
    private ByteBuffer fontMemory;
    @Nullable
    private FT_Face face;
    final float oversample;
    private final CodepointMap<GlyphEntry> glyphs = new CodepointMap(GlyphEntry[]::new, $$0 -> new GlyphEntry[$$0][]);

    public TrueTypeGlyphProvider(ByteBuffer $$02, FT_Face $$1, float $$2, float $$3, float $$4, float $$5, String $$6) {
        this.fontMemory = $$02;
        this.face = $$1;
        this.oversample = $$3;
        IntArraySet $$7 = new IntArraySet();
        $$6.codePoints().forEach(arg_0 -> ((IntSet)$$7).add(arg_0));
        int $$8 = Math.round($$2 * $$3);
        FreeType.FT_Set_Pixel_Sizes((FT_Face)$$1, (int)$$8, (int)$$8);
        float $$9 = $$4 * $$3;
        float $$10 = -$$5 * $$3;
        try (MemoryStack $$11 = MemoryStack.stackPush();){
            int $$15;
            FT_Vector $$12 = FreeTypeUtil.setVector(FT_Vector.malloc((MemoryStack)$$11), $$9, $$10);
            FreeType.FT_Set_Transform((FT_Face)$$1, null, (FT_Vector)$$12);
            IntBuffer $$13 = $$11.mallocInt(1);
            int $$14 = (int)FreeType.FT_Get_First_Char((FT_Face)$$1, (IntBuffer)$$13);
            while (($$15 = $$13.get(0)) != 0) {
                if (!$$7.contains($$14)) {
                    this.glyphs.put($$14, new GlyphEntry($$15));
                }
                $$14 = (int)FreeType.FT_Get_Next_Char((FT_Face)$$1, (long)$$14, (IntBuffer)$$13);
            }
        }
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        GlyphEntry $$1 = this.glyphs.get($$0);
        return $$1 != null ? this.getOrLoadGlyphInfo($$0, $$1) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private GlyphInfo getOrLoadGlyphInfo(int $$0, GlyphEntry $$1) {
        GlyphInfo $$2 = $$1.glyph;
        if ($$2 == null) {
            FT_Face $$3;
            FT_Face fT_Face = $$3 = this.validateFontOpen();
            synchronized (fT_Face) {
                $$2 = $$1.glyph;
                if ($$2 == null) {
                    $$1.glyph = $$2 = this.loadGlyph($$0, $$3, $$1.index);
                }
            }
        }
        return $$2;
    }

    private GlyphInfo loadGlyph(int $$0, FT_Face $$1, int $$2) {
        FT_GlyphSlot $$4;
        int $$3 = FreeType.FT_Load_Glyph((FT_Face)$$1, (int)$$2, (int)0x400008);
        if ($$3 != 0) {
            FreeTypeUtil.assertError($$3, String.format(Locale.ROOT, "Loading glyph U+%06X", $$0));
        }
        if (($$4 = $$1.glyph()) == null) {
            throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", $$0));
        }
        float $$5 = FreeTypeUtil.x($$4.advance());
        FT_Bitmap $$6 = $$4.bitmap();
        int $$7 = $$4.bitmap_left();
        int $$8 = $$4.bitmap_top();
        int $$9 = $$6.width();
        int $$10 = $$6.rows();
        if ($$9 <= 0 || $$10 <= 0) {
            return () -> $$5 / this.oversample;
        }
        return new Glyph($$7, $$8, $$9, $$10, $$5, $$2);
    }

    FT_Face validateFontOpen() {
        if (this.fontMemory == null || this.face == null) {
            throw new IllegalStateException("Provider already closed");
        }
        return this.face;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.face != null) {
            Object object = FreeTypeUtil.LIBRARY_LOCK;
            synchronized (object) {
                FreeTypeUtil.checkError(FreeType.FT_Done_Face((FT_Face)this.face), "Deleting face");
            }
            this.face = null;
        }
        MemoryUtil.memFree((Buffer)this.fontMemory);
        this.fontMemory = null;
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return this.glyphs.keySet();
    }

    static class GlyphEntry {
        final int index;
        @Nullable
        volatile GlyphInfo glyph;

        GlyphEntry(int $$0) {
            this.index = $$0;
        }
    }

    class Glyph
    implements GlyphInfo {
        final int width;
        final int height;
        final float bearingX;
        final float bearingY;
        private final float advance;
        final int index;

        Glyph(float $$0, float $$1, int $$2, int $$3, float $$4, int $$5) {
            this.width = $$2;
            this.height = $$3;
            this.advance = $$4 / TrueTypeGlyphProvider.this.oversample;
            this.bearingX = $$0 / TrueTypeGlyphProvider.this.oversample;
            this.bearingY = $$1 / TrueTypeGlyphProvider.this.oversample;
            this.index = $$5;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return $$0.apply(new SheetGlyphInfo(){

                @Override
                public int getPixelWidth() {
                    return Glyph.this.width;
                }

                @Override
                public int getPixelHeight() {
                    return Glyph.this.height;
                }

                @Override
                public float getOversample() {
                    return TrueTypeGlyphProvider.this.oversample;
                }

                @Override
                public float getBearingLeft() {
                    return Glyph.this.bearingX;
                }

                @Override
                public float getBearingTop() {
                    return Glyph.this.bearingY;
                }

                @Override
                public void upload(int $$0, int $$1, GpuTexture $$2) {
                    FT_Face $$3 = TrueTypeGlyphProvider.this.validateFontOpen();
                    try (NativeImage $$4 = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);){
                        if ($$4.copyFromFont($$3, Glyph.this.index)) {
                            RenderSystem.getDevice().createCommandEncoder().writeToTexture($$2, $$4, 0, 0, $$0, $$1, Glyph.this.width, Glyph.this.height, 0, 0);
                        }
                    }
                }

                @Override
                public boolean isColored() {
                    return false;
                }
            });
        }
    }
}

