/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class ReloadableTexture
extends AbstractTexture {
    private final ResourceLocation resourceId;

    public ReloadableTexture(ResourceLocation $$0) {
        this.resourceId = $$0;
    }

    public ResourceLocation resourceId() {
        return this.resourceId;
    }

    public void apply(TextureContents $$0) {
        boolean $$1 = $$0.clamp();
        boolean $$2 = $$0.blur();
        try (NativeImage $$3 = $$0.image();){
            this.doLoad($$3, $$2, $$1);
        }
    }

    protected void doLoad(NativeImage $$0, boolean $$1, boolean $$2) {
        GpuDevice $$3 = RenderSystem.getDevice();
        this.close();
        this.texture = $$3.createTexture(this.resourceId::toString, 5, TextureFormat.RGBA8, $$0.getWidth(), $$0.getHeight(), 1, 1);
        this.textureView = $$3.createTextureView(this.texture);
        this.setFilter($$1, false);
        this.setClamp($$2);
        $$3.createCommandEncoder().writeToTexture(this.texture, $$0);
    }

    public abstract TextureContents loadContents(ResourceManager var1) throws IOException;
}

