/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class TextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static ByteBuffer readResource(InputStream $$0) throws IOException {
        ReadableByteChannel $$1 = Channels.newChannel($$0);
        if ($$1 instanceof SeekableByteChannel) {
            SeekableByteChannel $$2 = (SeekableByteChannel)$$1;
            return TextureUtil.readResource($$1, (int)$$2.size() + 1);
        }
        return TextureUtil.readResource($$1, 8192);
    }

    private static ByteBuffer readResource(ReadableByteChannel $$0, int $$1) throws IOException {
        ByteBuffer $$2 = MemoryUtil.memAlloc((int)$$1);
        try {
            while ($$0.read($$2) != -1) {
                if ($$2.hasRemaining()) continue;
                $$2 = MemoryUtil.memRealloc((ByteBuffer)$$2, (int)($$2.capacity() * 2));
            }
            return $$2;
        } catch (IOException $$3) {
            MemoryUtil.memFree((Buffer)$$2);
            throw $$3;
        }
    }

    public static void writeAsPNG(Path $$0, String $$1, GpuTexture $$2, int $$3, IntUnaryOperator $$4) {
        RenderSystem.assertOnRenderThread();
        int $$5 = 0;
        for (int $$6 = 0; $$6 <= $$3; ++$$6) {
            $$5 += $$2.getFormat().pixelSize() * $$2.getWidth($$6) * $$2.getHeight($$6);
        }
        GpuBuffer $$7 = RenderSystem.getDevice().createBuffer(() -> "Texture output buffer", 9, $$5);
        CommandEncoder $$8 = RenderSystem.getDevice().createCommandEncoder();
        Runnable $$9 = () -> {
            try (GpuBuffer.MappedView $$7 = $$8.mapBuffer($$7, true, false);){
                int $$8 = 0;
                for (int $$9 = 0; $$9 <= $$3; ++$$9) {
                    int $$10 = $$2.getWidth($$9);
                    int $$11 = $$2.getHeight($$9);
                    try (NativeImage $$12 = new NativeImage($$10, $$11, false);){
                        for (int $$13 = 0; $$13 < $$11; ++$$13) {
                            for (int $$14 = 0; $$14 < $$10; ++$$14) {
                                int $$15 = $$7.data().getInt($$8 + ($$14 + $$13 * $$10) * $$2.getFormat().pixelSize());
                                $$12.setPixelABGR($$14, $$13, $$4.applyAsInt($$15));
                            }
                        }
                        Path $$16 = $$0.resolve($$1 + "_" + $$9 + ".png");
                        $$12.writeToFile($$16);
                        LOGGER.debug("Exported png to: {}", (Object)$$16.toAbsolutePath());
                    } catch (IOException $$17) {
                        LOGGER.debug("Unable to write: ", $$17);
                    }
                    $$8 += $$2.getFormat().pixelSize() * $$10 * $$11;
                }
            }
            $$7.close();
        };
        AtomicInteger $$10 = new AtomicInteger();
        int $$11 = 0;
        for (int $$12 = 0; $$12 <= $$3; ++$$12) {
            $$8.copyTextureToBuffer($$2, $$7, $$11, () -> {
                if ($$10.getAndIncrement() == $$3) {
                    $$9.run();
                }
            }, $$12);
            $$11 += $$2.getFormat().pixelSize() * $$2.getWidth($$12) * $$2.getHeight($$12);
        }
    }

    public static Path getDebugTexturePath(Path $$0) {
        return $$0.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath(Path.of((String)".", (String[])new String[0]));
    }
}

