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
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.gui.font.providers;

import com.google.common.annotations.VisibleForTesting;
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
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastBufferedInputStream;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class UnihexProvider
implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int GLYPH_HEIGHT = 16;
    private static final int DIGITS_PER_BYTE = 2;
    private static final int DIGITS_FOR_WIDTH_8 = 32;
    private static final int DIGITS_FOR_WIDTH_16 = 64;
    private static final int DIGITS_FOR_WIDTH_24 = 96;
    private static final int DIGITS_FOR_WIDTH_32 = 128;
    private final CodepointMap<Glyph> glyphs;

    UnihexProvider(CodepointMap<Glyph> $$0) {
        this.glyphs = $$0;
    }

    @Override
    @Nullable
    public GlyphInfo getGlyph(int $$0) {
        return this.glyphs.get($$0);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return this.glyphs.keySet();
    }

    @VisibleForTesting
    static void unpackBitsToBytes(IntBuffer $$0, int $$1, int $$2, int $$3) {
        int $$4 = 32 - $$2 - 1;
        int $$5 = 32 - $$3 - 1;
        for (int $$6 = $$4; $$6 >= $$5; --$$6) {
            if ($$6 >= 32 || $$6 < 0) {
                $$0.put(0);
                continue;
            }
            boolean $$7 = ($$1 >> $$6 & 1) != 0;
            $$0.put($$7 ? -1 : 0);
        }
    }

    static void unpackBitsToBytes(IntBuffer $$0, LineData $$1, int $$2, int $$3) {
        for (int $$4 = 0; $$4 < 16; ++$$4) {
            int $$5 = $$1.line($$4);
            UnihexProvider.unpackBitsToBytes($$0, $$5, $$2, $$3);
        }
    }

    @VisibleForTesting
    static void readFromStream(InputStream $$0, ReaderOutput $$1) throws IOException {
        int $$2 = 0;
        ByteArrayList $$3 = new ByteArrayList(128);
        while (true) {
            boolean $$4 = UnihexProvider.copyUntil($$0, (ByteList)$$3, 58);
            int $$5 = $$3.size();
            if ($$5 == 0 && !$$4) break;
            if (!$$4 || $$5 != 4 && $$5 != 5 && $$5 != 6) {
                throw new IllegalArgumentException("Invalid entry at line " + $$2 + ": expected 4, 5 or 6 hex digits followed by a colon");
            }
            int $$6 = 0;
            for (int $$7 = 0; $$7 < $$5; ++$$7) {
                $$6 = $$6 << 4 | UnihexProvider.decodeHex($$2, $$3.getByte($$7));
            }
            $$3.clear();
            UnihexProvider.copyUntil($$0, (ByteList)$$3, 10);
            int $$8 = $$3.size();
            LineData $$9 = switch ($$8) {
                case 32 -> ByteContents.read($$2, (ByteList)$$3);
                case 64 -> ShortContents.read($$2, (ByteList)$$3);
                case 96 -> IntContents.read24($$2, (ByteList)$$3);
                case 128 -> IntContents.read32($$2, (ByteList)$$3);
                default -> throw new IllegalArgumentException("Invalid entry at line " + $$2 + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
            };
            $$1.accept($$6, $$9);
            ++$$2;
            $$3.clear();
        }
    }

    static int decodeHex(int $$0, ByteList $$1, int $$2) {
        return UnihexProvider.decodeHex($$0, $$1.getByte($$2));
    }

    private static int decodeHex(int $$0, byte $$1) {
        return switch ($$1) {
            case 48 -> 0;
            case 49 -> 1;
            case 50 -> 2;
            case 51 -> 3;
            case 52 -> 4;
            case 53 -> 5;
            case 54 -> 6;
            case 55 -> 7;
            case 56 -> 8;
            case 57 -> 9;
            case 65 -> 10;
            case 66 -> 11;
            case 67 -> 12;
            case 68 -> 13;
            case 69 -> 14;
            case 70 -> 15;
            default -> throw new IllegalArgumentException("Invalid entry at line " + $$0 + ": expected hex digit, got " + (char)$$1);
        };
    }

    private static boolean copyUntil(InputStream $$0, ByteList $$1, int $$2) throws IOException {
        int $$3;
        while (($$3 = $$0.read()) != -1) {
            if ($$3 == $$2) {
                return true;
            }
            $$1.add((byte)$$3);
        }
        return false;
    }

    public static interface LineData {
        public int line(int var1);

        public int bitWidth();

        default public int mask() {
            int $$0 = 0;
            for (int $$1 = 0; $$1 < 16; ++$$1) {
                $$0 |= this.line($$1);
            }
            return $$0;
        }

        default public int calculateWidth() {
            int $$5;
            int $$4;
            int $$0 = this.mask();
            int $$1 = this.bitWidth();
            if ($$0 == 0) {
                boolean $$2 = false;
                int $$3 = $$1;
            } else {
                $$4 = Integer.numberOfLeadingZeros($$0);
                $$5 = 32 - Integer.numberOfTrailingZeros($$0) - 1;
            }
            return Dimensions.pack($$4, $$5);
        }
    }

    static final class ByteContents
    extends Record
    implements LineData {
        private final byte[] contents;

        private ByteContents(byte[] $$0) {
            this.contents = $$0;
        }

        @Override
        public int line(int $$0) {
            return this.contents[$$0] << 24;
        }

        static LineData read(int $$0, ByteList $$1) {
            byte[] $$2 = new byte[16];
            int $$3 = 0;
            for (int $$4 = 0; $$4 < 16; ++$$4) {
                byte $$7;
                int $$5 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                int $$6 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                $$2[$$4] = $$7 = (byte)($$5 << 4 | $$6);
            }
            return new ByteContents($$2);
        }

        @Override
        public int bitWidth() {
            return 8;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ByteContents.class, "contents", "contents"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ByteContents.class, "contents", "contents"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ByteContents.class, "contents", "contents"}, this, $$0);
        }

        public byte[] b() {
            return this.contents;
        }
    }

    static final class ShortContents
    extends Record
    implements LineData {
        private final short[] contents;

        private ShortContents(short[] $$0) {
            this.contents = $$0;
        }

        @Override
        public int line(int $$0) {
            return this.contents[$$0] << 16;
        }

        static LineData read(int $$0, ByteList $$1) {
            short[] $$2 = new short[16];
            int $$3 = 0;
            for (int $$4 = 0; $$4 < 16; ++$$4) {
                short $$9;
                int $$5 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                int $$6 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                int $$7 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                int $$8 = UnihexProvider.decodeHex($$0, $$1, $$3++);
                $$2[$$4] = $$9 = (short)($$5 << 12 | $$6 << 8 | $$7 << 4 | $$8);
            }
            return new ShortContents($$2);
        }

        @Override
        public int bitWidth() {
            return 16;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ShortContents.class, "contents", "contents"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShortContents.class, "contents", "contents"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShortContents.class, "contents", "contents"}, this, $$0);
        }

        public short[] b() {
            return this.contents;
        }
    }

    static final class IntContents
    extends Record
    implements LineData {
        private final int[] contents;
        private final int bitWidth;
        private static final int SIZE_24 = 24;

        private IntContents(int[] $$0, int $$1) {
            this.contents = $$0;
            this.bitWidth = $$1;
        }

        @Override
        public int line(int $$0) {
            return this.contents[$$0];
        }

        static LineData read24(int $$0, ByteList $$1) {
            int[] $$2 = new int[16];
            int $$3 = 0;
            int $$4 = 0;
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                int $$6 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$7 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$8 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$9 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$10 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$11 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$12 = $$6 << 20 | $$7 << 16 | $$8 << 12 | $$9 << 8 | $$10 << 4 | $$11;
                $$2[$$5] = $$12 << 8;
                $$3 |= $$12;
            }
            return new IntContents($$2, 24);
        }

        public static LineData read32(int $$0, ByteList $$1) {
            int[] $$2 = new int[16];
            int $$3 = 0;
            int $$4 = 0;
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                int $$14;
                int $$6 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$7 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$8 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$9 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$10 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$11 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$12 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                int $$13 = UnihexProvider.decodeHex($$0, $$1, $$4++);
                $$2[$$5] = $$14 = $$6 << 28 | $$7 << 24 | $$8 << 20 | $$9 << 16 | $$10 << 12 | $$11 << 8 | $$12 << 4 | $$13;
                $$3 |= $$14;
            }
            return new IntContents($$2, 32);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{IntContents.class, "contents;bitWidth", "contents", "bitWidth"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IntContents.class, "contents;bitWidth", "contents", "bitWidth"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IntContents.class, "contents;bitWidth", "contents", "bitWidth"}, this, $$0);
        }

        public int[] b() {
            return this.contents;
        }

        @Override
        public int bitWidth() {
            return this.bitWidth;
        }
    }

    @FunctionalInterface
    public static interface ReaderOutput {
        public void accept(int var1, LineData var2);
    }

    static final class Glyph
    extends Record
    implements GlyphInfo {
        final LineData contents;
        final int left;
        final int right;

        Glyph(LineData $$0, int $$1, int $$2) {
            this.contents = $$0;
            this.left = $$1;
            this.right = $$2;
        }

        public int width() {
            return this.right - this.left + 1;
        }

        @Override
        public float getAdvance() {
            return this.width() / 2 + 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> $$0) {
            return $$0.apply(new SheetGlyphInfo(){

                @Override
                public float getOversample() {
                    return 2.0f;
                }

                @Override
                public int getPixelWidth() {
                    return this.width();
                }

                @Override
                public int getPixelHeight() {
                    return 16;
                }

                @Override
                public void upload(int $$0, int $$1, GpuTexture $$2) {
                    IntBuffer $$3 = MemoryUtil.memAllocInt((int)(this.width() * 16));
                    UnihexProvider.unpackBitsToBytes($$3, contents, left, right);
                    $$3.rewind();
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture($$2, $$3, NativeImage.Format.RGBA, 0, 0, $$0, $$1, this.width(), 16);
                    MemoryUtil.memFree((Buffer)$$3);
                }

                @Override
                public boolean isColored() {
                    return true;
                }
            });
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Glyph.class, "contents;left;right", "contents", "left", "right"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Glyph.class, "contents;left;right", "contents", "left", "right"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Glyph.class, "contents;left;right", "contents", "left", "right"}, this, $$0);
        }

        public LineData contents() {
            return this.contents;
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }
    }

    public static class Definition
    implements GlyphProviderDefinition {
        public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ResourceLocation.CODEC.fieldOf("hex_file").forGetter($$0 -> $$0.hexFile), (App)OverrideRange.CODEC.listOf().optionalFieldOf("size_overrides", (Object)List.of()).forGetter($$0 -> $$0.sizeOverrides)).apply((Applicative)$$02, Definition::new));
        private final ResourceLocation hexFile;
        private final List<OverrideRange> sizeOverrides;

        private Definition(ResourceLocation $$0, List<OverrideRange> $$1) {
            this.hexFile = $$0;
            this.sizeOverrides = $$1;
        }

        @Override
        public GlyphProviderType type() {
            return GlyphProviderType.UNIHEX;
        }

        @Override
        public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
            return Either.left(this::load);
        }

        private GlyphProvider load(ResourceManager $$0) throws IOException {
            try (InputStream $$1 = $$0.open(this.hexFile);){
                UnihexProvider unihexProvider = this.loadData($$1);
                return unihexProvider;
            }
        }

        private UnihexProvider loadData(InputStream $$02) throws IOException {
            CodepointMap<LineData> $$12 = new CodepointMap<LineData>(LineData[]::new, $$0 -> new LineData[$$0][]);
            ReaderOutput $$22 = $$12::put;
            try (ZipInputStream $$3 = new ZipInputStream($$02);){
                ZipEntry $$4;
                while (($$4 = $$3.getNextEntry()) != null) {
                    String $$5 = $$4.getName();
                    if (!$$5.endsWith(".hex")) continue;
                    LOGGER.info("Found {}, loading", (Object)$$5);
                    UnihexProvider.readFromStream(new FastBufferedInputStream($$3), $$22);
                }
                CodepointMap<Glyph> $$6 = new CodepointMap<Glyph>(Glyph[]::new, $$0 -> new Glyph[$$0][]);
                for (OverrideRange $$7 : this.sizeOverrides) {
                    int $$8 = $$7.from;
                    int $$9 = $$7.to;
                    Dimensions $$10 = $$7.dimensions;
                    for (int $$11 = $$8; $$11 <= $$9; ++$$11) {
                        LineData $$122 = (LineData)$$12.remove($$11);
                        if ($$122 == null) continue;
                        $$6.put($$11, new Glyph($$122, $$10.left, $$10.right));
                    }
                }
                $$12.forEach(($$1, $$2) -> {
                    int $$3 = $$2.calculateWidth();
                    int $$4 = Dimensions.left($$3);
                    int $$5 = Dimensions.right($$3);
                    $$6.put($$1, new Glyph((LineData)$$2, $$4, $$5));
                });
                UnihexProvider unihexProvider = new UnihexProvider($$6);
                return unihexProvider;
            }
        }
    }

    public static final class Dimensions
    extends Record {
        final int left;
        final int right;
        public static final MapCodec<Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.INT.fieldOf("left").forGetter(Dimensions::left), (App)Codec.INT.fieldOf("right").forGetter(Dimensions::right)).apply((Applicative)$$0, Dimensions::new));
        public static final Codec<Dimensions> CODEC = MAP_CODEC.codec();

        public Dimensions(int $$0, int $$1) {
            this.left = $$0;
            this.right = $$1;
        }

        public int pack() {
            return Dimensions.pack(this.left, this.right);
        }

        public static int pack(int $$0, int $$1) {
            return ($$0 & 0xFF) << 8 | $$1 & 0xFF;
        }

        public static int left(int $$0) {
            return (byte)($$0 >> 8);
        }

        public static int right(int $$0) {
            return (byte)$$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this, $$0);
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }
    }

    static final class OverrideRange
    extends Record {
        final int from;
        final int to;
        final Dimensions dimensions;
        private static final Codec<OverrideRange> RAW_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.CODEPOINT.fieldOf("from").forGetter(OverrideRange::from), (App)ExtraCodecs.CODEPOINT.fieldOf("to").forGetter(OverrideRange::to), (App)Dimensions.MAP_CODEC.forGetter(OverrideRange::dimensions)).apply((Applicative)$$0, OverrideRange::new));
        public static final Codec<OverrideRange> CODEC = RAW_CODEC.validate($$0 -> {
            if ($$0.from >= $$0.to) {
                return DataResult.error(() -> "Invalid range: [" + $$0.from + ";" + $$0.to + "]");
            }
            return DataResult.success((Object)$$0);
        });

        private OverrideRange(int $$0, int $$1, Dimensions $$2) {
            this.from = $$0;
            this.to = $$1;
            this.dimensions = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{OverrideRange.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OverrideRange.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OverrideRange.class, "from;to;dimensions", "from", "to", "dimensions"}, this, $$0);
        }

        public int from() {
            return this.from;
        }

        public int to() {
            return this.to;
        }

        public Dimensions dimensions() {
            return this.dimensions;
        }
    }
}

