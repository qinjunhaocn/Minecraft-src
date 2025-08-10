/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class DynamicTexture
extends AbstractTexture
implements Dumpable {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private NativeImage pixels;

    public DynamicTexture(Supplier<String> $$0, NativeImage $$1) {
        this.pixels = $$1;
        this.createTexture($$0);
        this.upload();
    }

    public DynamicTexture(String $$0, int $$1, int $$2, boolean $$3) {
        this.pixels = new NativeImage($$1, $$2, $$3);
        this.createTexture($$0);
    }

    public DynamicTexture(Supplier<String> $$0, int $$1, int $$2, boolean $$3) {
        this.pixels = new NativeImage($$1, $$2, $$3);
        this.createTexture($$0);
    }

    private void createTexture(Supplier<String> $$0) {
        GpuDevice $$1 = RenderSystem.getDevice();
        this.texture = $$1.createTexture($$0, 5, TextureFormat.RGBA8, this.pixels.getWidth(), this.pixels.getHeight(), 1, 1);
        this.texture.setTextureFilter(FilterMode.NEAREST, false);
        this.textureView = $$1.createTextureView(this.texture);
    }

    private void createTexture(String $$0) {
        GpuDevice $$1 = RenderSystem.getDevice();
        this.texture = $$1.createTexture($$0, 5, TextureFormat.RGBA8, this.pixels.getWidth(), this.pixels.getHeight(), 1, 1);
        this.texture.setTextureFilter(FilterMode.NEAREST, false);
        this.textureView = $$1.createTextureView(this.texture);
    }

    public void upload() {
        if (this.pixels != null && this.texture != null) {
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.texture, this.pixels);
        } else {
            LOGGER.warn("Trying to upload disposed texture {}", (Object)this.getTexture().getLabel());
        }
    }

    @Nullable
    public NativeImage getPixels() {
        return this.pixels;
    }

    public void setPixels(NativeImage $$0) {
        if (this.pixels != null) {
            this.pixels.close();
        }
        this.pixels = $$0;
    }

    @Override
    public void close() {
        if (this.pixels != null) {
            this.pixels.close();
            this.pixels = null;
        }
        super.close();
    }

    @Override
    public void dumpContents(ResourceLocation $$0, Path $$1) throws IOException {
        if (this.pixels != null) {
            String $$2 = $$0.toDebugFileName() + ".png";
            Path $$3 = $$1.resolve($$2);
            this.pixels.writeToFile($$3);
        }
    }
}

