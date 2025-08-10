/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class FaviconTexture
implements AutoCloseable {
    private static final ResourceLocation MISSING_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");
    private static final int WIDTH = 64;
    private static final int HEIGHT = 64;
    private final TextureManager textureManager;
    private final ResourceLocation textureLocation;
    @Nullable
    private DynamicTexture texture;
    private boolean closed;

    private FaviconTexture(TextureManager $$0, ResourceLocation $$1) {
        this.textureManager = $$0;
        this.textureLocation = $$1;
    }

    public static FaviconTexture forWorld(TextureManager $$0, String $$1) {
        return new FaviconTexture($$0, ResourceLocation.withDefaultNamespace("worlds/" + Util.sanitizeName($$1, ResourceLocation::b) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars($$1)) + "/icon"));
    }

    public static FaviconTexture forServer(TextureManager $$0, String $$1) {
        return new FaviconTexture($$0, ResourceLocation.withDefaultNamespace("servers/" + String.valueOf(Hashing.sha1().hashUnencodedChars($$1)) + "/icon"));
    }

    public void upload(NativeImage $$0) {
        if ($$0.getWidth() != 64 || $$0.getHeight() != 64) {
            $$0.close();
            throw new IllegalArgumentException("Icon must be 64x64, but was " + $$0.getWidth() + "x" + $$0.getHeight());
        }
        try {
            this.checkOpen();
            if (this.texture == null) {
                this.texture = new DynamicTexture(() -> "Favicon " + String.valueOf(this.textureLocation), $$0);
            } else {
                this.texture.setPixels($$0);
                this.texture.upload();
            }
            this.textureManager.register(this.textureLocation, this.texture);
        } catch (Throwable $$1) {
            $$0.close();
            this.clear();
            throw $$1;
        }
    }

    public void clear() {
        this.checkOpen();
        if (this.texture != null) {
            this.textureManager.release(this.textureLocation);
            this.texture.close();
            this.texture = null;
        }
    }

    public ResourceLocation textureLocation() {
        return this.texture != null ? this.textureLocation : MISSING_LOCATION;
    }

    @Override
    public void close() {
        this.clear();
        this.closed = true;
    }

    private void checkOpen() {
        if (this.closed) {
            throw new IllegalStateException("Icon already closed");
        }
    }
}

