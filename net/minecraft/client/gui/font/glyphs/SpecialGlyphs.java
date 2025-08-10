/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;

public final class SpecialGlyphs
extends Enum<SpecialGlyphs>
implements GlyphInfo {
    public static final /* enum */ SpecialGlyphs WHITE = new SpecialGlyphs(() -> SpecialGlyphs.generate(5, 8, ($$0, $$1) -> -1));
    public static final /* enum */ SpecialGlyphs MISSING = new SpecialGlyphs(() -> {
        int $$02 = 5;
        int $$12 = 8;
        return SpecialGlyphs.generate(5, 8, ($$0, $$1) -> {
            boolean $$2 = $$0 == 0 || $$0 + 1 == 5 || $$1 == 0 || $$1 + 1 == 8;
            return $$2 ? -1 : 0;
        });
    });
    final NativeImage image;
    private static final /* synthetic */ SpecialGlyphs[] $VALUES;

    public static SpecialGlyphs[] values() {
        return (SpecialGlyphs[])$VALUES.clone();
    }

    public static SpecialGlyphs valueOf(String $$0) {
        return Enum.valueOf(SpecialGlyphs.class, $$0);
    }

    private static NativeImage generate(int $$0, int $$1, PixelProvider $$2) {
        NativeImage $$3 = new NativeImage(NativeImage.Format.RGBA, $$0, $$1, false);
        for (int $$4 = 0; $$4 < $$1; ++$$4) {
            for (int $$5 = 0; $$5 < $$0; ++$$5) {
                $$3.setPixel($$5, $$4, $$2.getColor($$5, $$4));
            }
        }
        $$3.untrack();
        return $$3;
    }

    private SpecialGlyphs(Supplier<NativeImage> $$0) {
        this.image = $$0.get();
    }

    @Override
    public float getAdvance() {
        return this.image.getWidth() + 1;
    }

    @Override
    public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
        return $$0.apply(new SheetGlyphInfo(){

            @Override
            public int getPixelWidth() {
                return SpecialGlyphs.this.image.getWidth();
            }

            @Override
            public int getPixelHeight() {
                return SpecialGlyphs.this.image.getHeight();
            }

            @Override
            public float getOversample() {
                return 1.0f;
            }

            @Override
            public void upload(int $$0, int $$1, GpuTexture $$2) {
                RenderSystem.getDevice().createCommandEncoder().writeToTexture($$2, SpecialGlyphs.this.image, 0, 0, $$0, $$1, SpecialGlyphs.this.image.getWidth(), SpecialGlyphs.this.image.getHeight(), 0, 0);
            }

            @Override
            public boolean isColored() {
                return true;
            }
        });
    }

    private static /* synthetic */ SpecialGlyphs[] e() {
        return new SpecialGlyphs[]{WHITE, MISSING};
    }

    static {
        $VALUES = SpecialGlyphs.e();
    }

    @FunctionalInterface
    static interface PixelProvider {
        public int getColor(int var1, int var2);
    }
}

