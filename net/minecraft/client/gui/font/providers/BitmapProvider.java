/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class BitmapProvider
implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private final NativeImage image;
    private final CodepointMap<Glyph> glyphs;

    BitmapProvider(NativeImage $$0, CodepointMap<Glyph> $$1) {
        this.image = $$0;
        this.glyphs = $$1;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        return this.glyphs.get($$0);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.keySet());
    }

    static final class Glyph
    extends Record
    implements GlyphInfo {
        final float scale;
        final NativeImage image;
        final int offsetX;
        final int offsetY;
        final int width;
        final int height;
        private final int advance;
        final int ascent;

        Glyph(float $$0, NativeImage $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
            this.scale = $$0;
            this.image = $$1;
            this.offsetX = $$2;
            this.offsetY = $$3;
            this.width = $$4;
            this.height = $$5;
            this.advance = $$6;
            this.ascent = $$7;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return $$0.apply(new SheetGlyphInfo(){

                @Override
                public float getOversample() {
                    return 1.0f / scale;
                }

                @Override
                public int getPixelWidth() {
                    return width;
                }

                @Override
                public int getPixelHeight() {
                    return height;
                }

                @Override
                public float getBearingTop() {
                    return ascent;
                }

                @Override
                public void upload(int $$0, int $$1, GpuTexture $$2) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture($$2, image, 0, 0, $$0, $$1, width, height, offsetX, offsetY);
                }

                @Override
                public boolean isColored() {
                    return image.format().components() > 1;
                }
            });
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Glyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scale", "image", "offsetX", "offsetY", "width", "height", "advance", "ascent"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Glyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scale", "image", "offsetX", "offsetY", "width", "height", "advance", "ascent"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Glyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scale", "image", "offsetX", "offsetY", "width", "height", "advance", "ascent"}, this, $$0);
        }

        public float scale() {
            return this.scale;
        }

        public NativeImage image() {
            return this.image;
        }

        public int offsetX() {
            return this.offsetX;
        }

        public int offsetY() {
            return this.offsetY;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public int advance() {
            return this.advance;
        }

        public int ascent() {
            return this.ascent;
        }
    }

    public static final class Definition
    extends Record
    implements GlyphProviderDefinition {
        private final ResourceLocation file;
        private final int height;
        private final int ascent;
        private final int[][] codepointGrid;
        private static final Codec<int[][]> CODEPOINT_GRID_CODEC = Codec.STRING.listOf().xmap($$0 -> {
            int $$1 = $$0.size();
            int[][] $$2 = new int[$$1][];
            for (int $$3 = 0; $$3 < $$1; ++$$3) {
                $$2[$$3] = ((String)$$0.get($$3)).codePoints().toArray();
            }
            return $$2;
        }, $$0 -> {
            ArrayList<String> $$1 = new ArrayList<String>(((int[][])$$0).length);
            for (int[] $$2 : $$0) {
                $$1.add(new String($$2, 0, $$2.length));
            }
            return $$1;
        }).validate(Definition::a);
        public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("file").forGetter(Definition::file), (App)Codec.INT.optionalFieldOf("height", (Object)8).forGetter(Definition::height), (App)Codec.INT.fieldOf("ascent").forGetter(Definition::ascent), (App)CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition::f)).apply((Applicative)$$0, Definition::new)).validate(Definition::validate);

        public Definition(ResourceLocation $$0, int $$1, int $$2, int[][] $$3) {
            this.file = $$0;
            this.height = $$1;
            this.ascent = $$2;
            this.codepointGrid = $$3;
        }

        private static DataResult<int[][]> a(int[][] $$0) {
            int $$1 = $$0.length;
            if ($$1 == 0) {
                return DataResult.error(() -> "Expected to find data in codepoint grid");
            }
            int[] $$2 = $$0[0];
            int $$3 = $$2.length;
            if ($$3 == 0) {
                return DataResult.error(() -> "Expected to find data in codepoint grid");
            }
            for (int $$4 = 1; $$4 < $$1; ++$$4) {
                int[] $$5 = $$0[$$4];
                if ($$5.length == $$3) continue;
                return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + $$5.length + " codepoints, expected: " + $$3 + "), pad with \\u0000");
            }
            return DataResult.success((Object)$$0);
        }

        private static DataResult<Definition> validate(Definition $$0) {
            if ($$0.ascent > $$0.height) {
                return DataResult.error(() -> "Ascent " + $$0.ascent + " higher than height " + $$0.height);
            }
            return DataResult.success((Object)$$0);
        }

        @Override
        public GlyphProviderType type() {
            return GlyphProviderType.BITMAP;
        }

        @Override
        public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
            return Either.left(this::load);
        }

        private GlyphProvider load(ResourceManager $$02) throws IOException {
            ResourceLocation $$1 = this.file.withPrefix("textures/");
            try (InputStream $$2 = $$02.open($$1);){
                NativeImage $$3 = NativeImage.read(NativeImage.Format.RGBA, $$2);
                int $$4 = $$3.getWidth();
                int $$5 = $$3.getHeight();
                int $$6 = $$4 / this.codepointGrid[0].length;
                int $$7 = $$5 / this.codepointGrid.length;
                float $$8 = (float)this.height / (float)$$7;
                CodepointMap<Glyph> $$9 = new CodepointMap<Glyph>(Glyph[]::new, $$0 -> new Glyph[$$0][]);
                for (int $$10 = 0; $$10 < this.codepointGrid.length; ++$$10) {
                    int $$11 = 0;
                    for (int $$12 : this.codepointGrid[$$10]) {
                        int $$14;
                        Glyph $$15;
                        int $$13 = $$11++;
                        if ($$12 == 0 || ($$15 = $$9.put($$12, new Glyph($$8, $$3, $$13 * $$6, $$10 * $$7, $$6, $$7, (int)(0.5 + (double)((float)($$14 = this.getActualGlyphWidth($$3, $$6, $$7, $$13, $$10)) * $$8)) + 1, this.ascent))) == null) continue;
                        LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString($$12), (Object)$$1);
                    }
                }
                BitmapProvider bitmapProvider = new BitmapProvider($$3, $$9);
                return bitmapProvider;
            }
        }

        private int getActualGlyphWidth(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
            int $$5;
            for ($$5 = $$1 - 1; $$5 >= 0; --$$5) {
                int $$6 = $$3 * $$1 + $$5;
                for (int $$7 = 0; $$7 < $$2; ++$$7) {
                    int $$8 = $$4 * $$2 + $$7;
                    if ($$0.getLuminanceOrAlpha($$6, $$8) == 0) continue;
                    return $$5 + 1;
                }
            }
            return $$5 + 1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Definition.class, "file;height;ascent;codepointGrid", "file", "height", "ascent", "codepointGrid"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Definition.class, "file;height;ascent;codepointGrid", "file", "height", "ascent", "codepointGrid"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Definition.class, "file;height;ascent;codepointGrid", "file", "height", "ascent", "codepointGrid"}, this, $$0);
        }

        public ResourceLocation file() {
            return this.file;
        }

        public int height() {
            return this.height;
        }

        public int ascent() {
            return this.ascent;
        }

        public int[][] f() {
            return this.codepointGrid;
        }
    }
}

