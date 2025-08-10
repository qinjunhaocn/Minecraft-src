/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Bitmap
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FT_GlyphSlot
 *  org.lwjgl.util.freetype.FreeType
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.util.ARGB;
import net.minecraft.util.PngInfo;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

public final class NativeImage
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MemoryPool MEMORY_POOL = TracyClient.createMemoryPool((String)"NativeImage");
    private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean useStbFree;
    private long pixels;
    private final long size;

    public NativeImage(int $$0, int $$1, boolean $$2) {
        this(Format.RGBA, $$0, $$1, $$2);
    }

    public NativeImage(Format $$0, int $$1, int $$2, boolean $$3) {
        if ($$1 <= 0 || $$2 <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + $$1 + "x" + $$2);
        }
        this.format = $$0;
        this.width = $$1;
        this.height = $$2;
        this.size = (long)$$1 * (long)$$2 * (long)$$0.components();
        this.useStbFree = false;
        this.pixels = $$3 ? MemoryUtil.nmemCalloc((long)1L, (long)this.size) : MemoryUtil.nmemAlloc((long)this.size);
        MEMORY_POOL.malloc(this.pixels, (int)this.size);
        if (this.pixels == 0L) {
            throw new IllegalStateException("Unable to allocate texture of size " + $$1 + "x" + $$2 + " (" + $$0.components() + " channels)");
        }
    }

    public NativeImage(Format $$0, int $$1, int $$2, boolean $$3, long $$4) {
        if ($$1 <= 0 || $$2 <= 0) {
            throw new IllegalArgumentException("Invalid texture size: " + $$1 + "x" + $$2);
        }
        this.format = $$0;
        this.width = $$1;
        this.height = $$2;
        this.useStbFree = $$3;
        this.pixels = $$4;
        this.size = (long)$$1 * (long)$$2 * (long)$$0.components();
    }

    public String toString() {
        return "NativeImage[" + String.valueOf((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
    }

    private boolean isOutsideBounds(int $$0, int $$1) {
        return $$0 < 0 || $$0 >= this.width || $$1 < 0 || $$1 >= this.height;
    }

    public static NativeImage read(InputStream $$0) throws IOException {
        return NativeImage.read(Format.RGBA, $$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format $$0, InputStream $$1) throws IOException {
        ByteBuffer $$2 = null;
        try {
            $$2 = TextureUtil.readResource($$1);
            $$2.rewind();
            NativeImage nativeImage = NativeImage.read($$0, $$2);
            return nativeImage;
        } finally {
            MemoryUtil.memFree((Buffer)$$2);
            IOUtils.closeQuietly((InputStream)$$1);
        }
    }

    public static NativeImage read(ByteBuffer $$0) throws IOException {
        return NativeImage.read(Format.RGBA, $$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage a(byte[] $$0) throws IOException {
        MemoryStack $$1 = MemoryStack.stackGet();
        int $$2 = $$1.getPointer();
        if ($$2 < $$0.length) {
            ByteBuffer $$3 = MemoryUtil.memAlloc((int)$$0.length);
            try {
                NativeImage nativeImage = NativeImage.a($$3, $$0);
                return nativeImage;
            } finally {
                MemoryUtil.memFree((Buffer)$$3);
            }
        }
        try (MemoryStack $$4 = MemoryStack.stackPush();){
            ByteBuffer $$5 = $$4.malloc($$0.length);
            NativeImage nativeImage = NativeImage.a($$5, $$0);
            return nativeImage;
        }
    }

    private static NativeImage a(ByteBuffer $$0, byte[] $$1) throws IOException {
        $$0.put($$1);
        $$0.rewind();
        return NativeImage.read($$0);
    }

    public static NativeImage read(@Nullable Format $$0, ByteBuffer $$1) throws IOException {
        if ($$0 != null && !$$0.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to read format " + String.valueOf((Object)$$0));
        }
        if (MemoryUtil.memAddress((ByteBuffer)$$1) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        PngInfo.validateHeader($$1);
        try (MemoryStack $$2 = MemoryStack.stackPush();){
            IntBuffer $$3 = $$2.mallocInt(1);
            IntBuffer $$4 = $$2.mallocInt(1);
            IntBuffer $$5 = $$2.mallocInt(1);
            ByteBuffer $$6 = STBImage.stbi_load_from_memory((ByteBuffer)$$1, (IntBuffer)$$3, (IntBuffer)$$4, (IntBuffer)$$5, (int)($$0 == null ? 0 : $$0.components));
            if ($$6 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            long $$7 = MemoryUtil.memAddress((ByteBuffer)$$6);
            MEMORY_POOL.malloc($$7, $$6.limit());
            NativeImage nativeImage = new NativeImage($$0 == null ? Format.getStbFormat($$5.get(0)) : $$0, $$3.get(0), $$4.get(0), true, $$7);
            return nativeImage;
        }
    }

    private void checkAllocated() {
        if (this.pixels == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pixels != 0L) {
            if (this.useStbFree) {
                STBImage.nstbi_image_free((long)this.pixels);
            } else {
                MemoryUtil.nmemFree((long)this.pixels);
            }
            MEMORY_POOL.free(this.pixels);
        }
        this.pixels = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format format() {
        return this.format;
    }

    private int getPixelABGR(int $$0, int $$1) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", $$0, $$1, this.width, this.height));
        }
        this.checkAllocated();
        long $$2 = ((long)$$0 + (long)$$1 * (long)this.width) * 4L;
        return MemoryUtil.memGetInt((long)(this.pixels + $$2));
    }

    public int getPixel(int $$0, int $$1) {
        return ARGB.fromABGR(this.getPixelABGR($$0, $$1));
    }

    public void setPixelABGR(int $$0, int $$1, int $$2) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "setPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", $$0, $$1, this.width, this.height));
        }
        this.checkAllocated();
        long $$3 = ((long)$$0 + (long)$$1 * (long)this.width) * 4L;
        MemoryUtil.memPutInt((long)(this.pixels + $$3), (int)$$2);
    }

    public void setPixel(int $$0, int $$1, int $$2) {
        this.setPixelABGR($$0, $$1, ARGB.toABGR($$2));
    }

    public NativeImage mappedCopy(IntUnaryOperator $$0) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "function application only works on RGBA images; have %s", new Object[]{this.format}));
        }
        this.checkAllocated();
        NativeImage $$1 = new NativeImage(this.width, this.height, false);
        int $$2 = this.width * this.height;
        IntBuffer $$3 = MemoryUtil.memIntBuffer((long)this.pixels, (int)$$2);
        IntBuffer $$4 = MemoryUtil.memIntBuffer((long)$$1.pixels, (int)$$2);
        for (int $$5 = 0; $$5 < $$2; ++$$5) {
            int $$6 = ARGB.fromABGR($$3.get($$5));
            int $$7 = $$0.applyAsInt($$6);
            $$4.put($$5, ARGB.toABGR($$7));
        }
        return $$1;
    }

    public int[] d() {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixels only works on RGBA images; have %s", new Object[]{this.format}));
        }
        this.checkAllocated();
        int[] $$0 = new int[this.width * this.height];
        MemoryUtil.memIntBuffer((long)this.pixels, (int)(this.width * this.height)).get($$0);
        return $$0;
    }

    public int[] e() {
        int[] $$0 = this.d();
        for (int $$1 = 0; $$1 < $$0.length; ++$$1) {
            $$0[$$1] = ARGB.fromABGR($$0[$$1]);
        }
        return $$0;
    }

    public byte getLuminanceOrAlpha(int $$0, int $$1) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (this.isOutsideBounds($$0, $$1)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", $$0, $$1, this.width, this.height));
        }
        int $$2 = ($$0 + $$1 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pixels + (long)$$2));
    }

    @Deprecated
    public int[] f() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] $$0 = new int[this.getWidth() * this.getHeight()];
        for (int $$1 = 0; $$1 < this.getHeight(); ++$$1) {
            for (int $$2 = 0; $$2 < this.getWidth(); ++$$2) {
                $$0[$$2 + $$1 * this.getWidth()] = this.getPixel($$2, $$1);
            }
        }
        return $$0;
    }

    public void writeToFile(File $$0) throws IOException {
        this.writeToFile($$0.toPath());
    }

    public boolean copyFromFont(FT_Face $$0, int $$1) {
        if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        if (FreeTypeUtil.checkError(FreeType.FT_Load_Glyph((FT_Face)$$0, (int)$$1, (int)4), "Loading glyph")) {
            return false;
        }
        FT_GlyphSlot $$2 = Objects.requireNonNull($$0.glyph(), "Glyph not initialized");
        FT_Bitmap $$3 = $$2.bitmap();
        if ($$3.pixel_mode() != 2) {
            throw new IllegalStateException("Rendered glyph was not 8-bit grayscale");
        }
        if ($$3.width() != this.getWidth() || $$3.rows() != this.getHeight()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Glyph bitmap of size %sx%s does not match image of size: %sx%s", $$3.width(), $$3.rows(), this.getWidth(), this.getHeight()));
        }
        int $$4 = $$3.width() * $$3.rows();
        ByteBuffer $$5 = Objects.requireNonNull($$3.buffer($$4), "Glyph has no bitmap");
        MemoryUtil.memCopy((long)MemoryUtil.memAddress((ByteBuffer)$$5), (long)this.pixels, (long)$$4);
        return true;
    }

    public void writeToFile(Path $$0) throws IOException {
        if (!this.format.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to write format " + String.valueOf((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel $$1 = Files.newByteChannel($$0, OPEN_OPTIONS, new FileAttribute[0]);){
            if (!this.writeToChannel($$1)) {
                throw new IOException("Could not write image to the PNG file \"" + String.valueOf($$0.toAbsolutePath()) + "\": " + STBImage.stbi_failure_reason());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean writeToChannel(WritableByteChannel $$0) throws IOException {
        WriteCallback $$1 = new WriteCallback($$0);
        try {
            int $$2 = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.components());
            if ($$2 < this.getHeight()) {
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)$$2);
            }
            if (STBImageWrite.nstbi_write_png_to_func((long)$$1.address(), (long)0L, (int)this.getWidth(), (int)$$2, (int)this.format.components(), (long)this.pixels, (int)0) == 0) {
                boolean bl = false;
                return bl;
            }
            $$1.throwIfException();
            boolean bl = true;
            return bl;
        } finally {
            $$1.free();
        }
    }

    public void copyFrom(NativeImage $$0) {
        if ($$0.format() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int $$1 = this.format.components();
        this.checkAllocated();
        $$0.checkAllocated();
        if (this.width == $$0.width) {
            MemoryUtil.memCopy((long)$$0.pixels, (long)this.pixels, (long)Math.min(this.size, $$0.size));
        } else {
            int $$2 = Math.min(this.getWidth(), $$0.getWidth());
            int $$3 = Math.min(this.getHeight(), $$0.getHeight());
            for (int $$4 = 0; $$4 < $$3; ++$$4) {
                int $$5 = $$4 * $$0.getWidth() * $$1;
                int $$6 = $$4 * this.getWidth() * $$1;
                MemoryUtil.memCopy((long)($$0.pixels + (long)$$5), (long)(this.pixels + (long)$$6), (long)$$2);
            }
        }
    }

    public void fillRect(int $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$1 + $$3; ++$$5) {
            for (int $$6 = $$0; $$6 < $$0 + $$2; ++$$6) {
                this.setPixel($$6, $$5, $$4);
            }
        }
    }

    public void copyRect(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, boolean $$6, boolean $$7) {
        this.copyRect(this, $$0, $$1, $$0 + $$2, $$1 + $$3, $$4, $$5, $$6, $$7);
    }

    public void copyRect(NativeImage $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, boolean $$8) {
        for (int $$9 = 0; $$9 < $$6; ++$$9) {
            for (int $$10 = 0; $$10 < $$5; ++$$10) {
                int $$11 = $$7 ? $$5 - 1 - $$10 : $$10;
                int $$12 = $$8 ? $$6 - 1 - $$9 : $$9;
                int $$13 = this.getPixelABGR($$1 + $$10, $$2 + $$9);
                $$0.setPixelABGR($$3 + $$11, $$4 + $$12, $$13);
            }
        }
    }

    public void resizeSubRectTo(int $$0, int $$1, int $$2, int $$3, NativeImage $$4) {
        this.checkAllocated();
        if ($$4.format() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int $$5 = this.format.components();
        STBImageResize.nstbir_resize_uint8((long)(this.pixels + (long)(($$0 + $$1 * this.getWidth()) * $$5)), (int)$$2, (int)$$3, (int)(this.getWidth() * $$5), (long)$$4.pixels, (int)$$4.getWidth(), (int)$$4.getHeight(), (int)0, (int)$$5);
    }

    public void untrack() {
        DebugMemoryUntracker.untrack(this.pixels);
    }

    public long getPointer() {
        return this.pixels;
    }

    public static final class Format
    extends Enum<Format> {
        public static final /* enum */ Format RGBA = new Format(4, true, true, true, false, true, 0, 8, 16, 255, 24, true);
        public static final /* enum */ Format RGB = new Format(3, true, true, true, false, false, 0, 8, 16, 255, 255, true);
        public static final /* enum */ Format LUMINANCE_ALPHA = new Format(2, false, false, false, true, true, 255, 255, 255, 0, 8, true);
        public static final /* enum */ Format LUMINANCE = new Format(1, false, false, false, true, false, 0, 0, 0, 0, 255, true);
        final int components;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceOffset;
        private final int alphaOffset;
        private final boolean supportedByStb;
        private static final /* synthetic */ Format[] $VALUES;

        public static Format[] values() {
            return (Format[])$VALUES.clone();
        }

        public static Format valueOf(String $$0) {
            return Enum.valueOf(Format.class, $$0);
        }

        private Format(int $$0, boolean $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5, int $$6, int $$7, int $$8, int $$9, int $$10, boolean $$11) {
            this.components = $$0;
            this.hasRed = $$1;
            this.hasGreen = $$2;
            this.hasBlue = $$3;
            this.hasLuminance = $$4;
            this.hasAlpha = $$5;
            this.redOffset = $$6;
            this.greenOffset = $$7;
            this.blueOffset = $$8;
            this.luminanceOffset = $$9;
            this.alphaOffset = $$10;
            this.supportedByStb = $$11;
        }

        public int components() {
            return this.components;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlpha() {
            return this.hasAlpha;
        }

        public int redOffset() {
            return this.redOffset;
        }

        public int greenOffset() {
            return this.greenOffset;
        }

        public int blueOffset() {
            return this.blueOffset;
        }

        public int luminanceOffset() {
            return this.luminanceOffset;
        }

        public int alphaOffset() {
            return this.alphaOffset;
        }

        public boolean hasLuminanceOrRed() {
            return this.hasLuminance || this.hasRed;
        }

        public boolean hasLuminanceOrGreen() {
            return this.hasLuminance || this.hasGreen;
        }

        public boolean hasLuminanceOrBlue() {
            return this.hasLuminance || this.hasBlue;
        }

        public boolean hasLuminanceOrAlpha() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int luminanceOrRedOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.redOffset;
        }

        public int luminanceOrGreenOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
        }

        public int luminanceOrBlueOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
        }

        public int luminanceOrAlphaOffset() {
            return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
        }

        public boolean supportedByStb() {
            return this.supportedByStb;
        }

        static Format getStbFormat(int $$0) {
            switch ($$0) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }

        private static /* synthetic */ Format[] u() {
            return new Format[]{RGBA, RGB, LUMINANCE_ALPHA, LUMINANCE};
        }

        static {
            $VALUES = Format.u();
        }
    }

    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel output;
        @Nullable
        private IOException exception;

        WriteCallback(WritableByteChannel $$0) {
            this.output = $$0;
        }

        public void invoke(long $$0, long $$1, int $$2) {
            ByteBuffer $$3 = WriteCallback.getData((long)$$1, (int)$$2);
            try {
                this.output.write($$3);
            } catch (IOException $$4) {
                this.exception = $$4;
            }
        }

        public void throwIfException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

