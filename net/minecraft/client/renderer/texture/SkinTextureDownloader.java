/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SkinTextureDownloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SKIN_WIDTH = 64;
    private static final int SKIN_HEIGHT = 64;
    private static final int LEGACY_SKIN_HEIGHT = 32;

    public static CompletableFuture<ResourceLocation> downloadAndRegisterSkin(ResourceLocation $$0, Path $$12, String $$2, boolean $$3) {
        return CompletableFuture.supplyAsync(() -> {
            void $$5;
            try {
                NativeImage $$3 = SkinTextureDownloader.downloadSkin($$12, $$2);
            } catch (IOException $$4) {
                throw new UncheckedIOException($$4);
            }
            return $$3 ? SkinTextureDownloader.processLegacySkin((NativeImage)$$5, $$2) : $$5;
        }, Util.nonCriticalIoPool().forName("downloadTexture")).thenCompose($$1 -> SkinTextureDownloader.registerTextureInManager($$0, $$1));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static NativeImage downloadSkin(Path $$0, String $$1) throws IOException {
        if (Files.isRegularFile($$0, new LinkOption[0])) {
            LOGGER.debug("Loading HTTP texture from local cache ({})", (Object)$$0);
            try (InputStream $$2 = Files.newInputStream($$0, new OpenOption[0]);){
                NativeImage nativeImage = NativeImage.read($$2);
                return nativeImage;
            }
        }
        HttpURLConnection $$3 = null;
        LOGGER.debug("Downloading HTTP texture from {} to {}", (Object)$$1, (Object)$$0);
        URI $$4 = URI.create($$1);
        try {
            $$3 = (HttpURLConnection)$$4.toURL().openConnection(Minecraft.getInstance().getProxy());
            $$3.setDoInput(true);
            $$3.setDoOutput(false);
            $$3.connect();
            int $$5 = $$3.getResponseCode();
            if ($$5 / 100 != 2) {
                throw new IOException("Failed to open " + String.valueOf($$4) + ", HTTP error code: " + $$5);
            }
            byte[] $$6 = $$3.getInputStream().readAllBytes();
            try {
                FileUtil.createDirectoriesSafe($$0.getParent());
                Files.write($$0, $$6, new OpenOption[0]);
            } catch (IOException $$7) {
                LOGGER.warn("Failed to cache texture {} in {}", (Object)$$1, (Object)$$0);
            }
            NativeImage nativeImage = NativeImage.a($$6);
            return nativeImage;
        } finally {
            if ($$3 != null) {
                $$3.disconnect();
            }
        }
    }

    private static CompletableFuture<ResourceLocation> registerTextureInManager(ResourceLocation $$0, NativeImage $$1) {
        Minecraft $$2 = Minecraft.getInstance();
        return CompletableFuture.supplyAsync(() -> {
            DynamicTexture $$3 = new DynamicTexture($$0::toString, $$1);
            $$2.getTextureManager().register($$0, $$3);
            return $$0;
        }, $$2);
    }

    private static NativeImage processLegacySkin(NativeImage $$0, String $$1) {
        boolean $$4;
        int $$2 = $$0.getHeight();
        int $$3 = $$0.getWidth();
        if ($$3 != 64 || $$2 != 32 && $$2 != 64) {
            $$0.close();
            throw new IllegalStateException("Discarding incorrectly sized (" + $$3 + "x" + $$2 + ") skin texture from " + $$1);
        }
        boolean bl = $$4 = $$2 == 32;
        if ($$4) {
            NativeImage $$5 = new NativeImage(64, 64, true);
            $$5.copyFrom($$0);
            $$0.close();
            $$0 = $$5;
            $$0.fillRect(0, 32, 64, 32, 0);
            $$0.copyRect(4, 16, 16, 32, 4, 4, true, false);
            $$0.copyRect(8, 16, 16, 32, 4, 4, true, false);
            $$0.copyRect(0, 20, 24, 32, 4, 12, true, false);
            $$0.copyRect(4, 20, 16, 32, 4, 12, true, false);
            $$0.copyRect(8, 20, 8, 32, 4, 12, true, false);
            $$0.copyRect(12, 20, 16, 32, 4, 12, true, false);
            $$0.copyRect(44, 16, -8, 32, 4, 4, true, false);
            $$0.copyRect(48, 16, -8, 32, 4, 4, true, false);
            $$0.copyRect(40, 20, 0, 32, 4, 12, true, false);
            $$0.copyRect(44, 20, -8, 32, 4, 12, true, false);
            $$0.copyRect(48, 20, -16, 32, 4, 12, true, false);
            $$0.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        SkinTextureDownloader.setNoAlpha($$0, 0, 0, 32, 16);
        if ($$4) {
            SkinTextureDownloader.doNotchTransparencyHack($$0, 32, 0, 64, 32);
        }
        SkinTextureDownloader.setNoAlpha($$0, 0, 16, 64, 32);
        SkinTextureDownloader.setNoAlpha($$0, 16, 48, 48, 64);
        return $$0;
    }

    private static void doNotchTransparencyHack(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$3; ++$$5) {
            for (int $$6 = $$2; $$6 < $$4; ++$$6) {
                int $$7 = $$0.getPixel($$5, $$6);
                if (ARGB.alpha($$7) >= 128) continue;
                return;
            }
        }
        for (int $$8 = $$1; $$8 < $$3; ++$$8) {
            for (int $$9 = $$2; $$9 < $$4; ++$$9) {
                $$0.setPixel($$8, $$9, $$0.getPixel($$8, $$9) & 0xFFFFFF);
            }
        }
    }

    private static void setNoAlpha(NativeImage $$0, int $$1, int $$2, int $$3, int $$4) {
        for (int $$5 = $$1; $$5 < $$3; ++$$5) {
            for (int $$6 = $$2; $$6 < $$4; ++$$6) {
                $$0.setPixel($$5, $$6, ARGB.opaque($$0.getPixel($$5, $$6)));
            }
        }
    }
}

