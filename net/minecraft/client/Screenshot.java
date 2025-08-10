/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class Screenshot {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SCREENSHOT_DIR = "screenshots";

    public static void grab(File $$0, RenderTarget $$1, Consumer<Component> $$2) {
        Screenshot.grab($$0, null, $$1, 1, $$2);
    }

    public static void grab(File $$0, @Nullable String $$1, RenderTarget $$2, int $$32, Consumer<Component> $$4) {
        Screenshot.takeScreenshot($$2, $$32, $$3 -> {
            File $$6;
            File $$4 = new File($$0, SCREENSHOT_DIR);
            $$4.mkdir();
            if ($$1 == null) {
                File $$5 = Screenshot.getFile($$4);
            } else {
                $$6 = new File($$4, $$1);
            }
            Util.ioPool().execute(() -> {
                try (NativeImage nativeImage = $$3;){
                    $$3.writeToFile($$6);
                    MutableComponent $$3 = Component.literal($$6.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle($$1 -> $$1.withClickEvent(new ClickEvent.OpenFile($$6.getAbsoluteFile())));
                    $$4.accept(Component.a("screenshot.success", $$3));
                } catch (Exception $$4) {
                    LOGGER.warn("Couldn't save screenshot", $$4);
                    $$4.accept(Component.a("screenshot.failure", $$4.getMessage()));
                }
            });
        });
    }

    public static void takeScreenshot(RenderTarget $$0, Consumer<NativeImage> $$1) {
        Screenshot.takeScreenshot($$0, 1, $$1);
    }

    public static void takeScreenshot(RenderTarget $$0, int $$1, Consumer<NativeImage> $$2) {
        int $$3 = $$0.width;
        int $$4 = $$0.height;
        GpuTexture $$5 = $$0.getColorTexture();
        if ($$5 == null) {
            throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
        }
        if ($$3 % $$1 != 0 || $$4 % $$1 != 0) {
            throw new IllegalArgumentException("Image size is not divisible by downscale factor");
        }
        GpuBuffer $$6 = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", 9, $$3 * $$4 * $$5.getFormat().pixelSize());
        CommandEncoder $$7 = RenderSystem.getDevice().createCommandEncoder();
        RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer($$5, $$6, 0, () -> {
            try (GpuBuffer.MappedView $$7 = $$7.mapBuffer($$6, true, false);){
                int $$8 = $$4 / $$1;
                int $$9 = $$3 / $$1;
                NativeImage $$10 = new NativeImage($$9, $$8, false);
                for (int $$11 = 0; $$11 < $$8; ++$$11) {
                    for (int $$12 = 0; $$12 < $$9; ++$$12) {
                        if ($$1 == 1) {
                            int $$13 = $$7.data().getInt(($$12 + $$11 * $$3) * $$5.getFormat().pixelSize());
                            $$10.setPixelABGR($$12, $$4 - $$11 - 1, $$13 | 0xFF000000);
                            continue;
                        }
                        int $$14 = 0;
                        int $$15 = 0;
                        int $$16 = 0;
                        for (int $$17 = 0; $$17 < $$1; ++$$17) {
                            for (int $$18 = 0; $$18 < $$1; ++$$18) {
                                int $$19 = $$7.data().getInt(($$12 * $$1 + $$17 + ($$11 * $$1 + $$18) * $$3) * $$5.getFormat().pixelSize());
                                $$14 += ARGB.red($$19);
                                $$15 += ARGB.green($$19);
                                $$16 += ARGB.blue($$19);
                            }
                        }
                        int $$20 = $$1 * $$1;
                        $$10.setPixelABGR($$12, $$8 - $$11 - 1, ARGB.color(255, $$14 / $$20, $$15 / $$20, $$16 / $$20));
                    }
                }
                $$2.accept($$10);
            }
            $$6.close();
        }, 0);
    }

    private static File getFile(File $$0) {
        String $$1 = Util.getFilenameFormattedDateTime();
        int $$2 = 1;
        File $$3;
        while (($$3 = new File($$0, $$1 + (String)($$2 == 1 ? "" : "_" + $$2) + ".png")).exists()) {
            ++$$2;
        }
        return $$3;
    }
}

