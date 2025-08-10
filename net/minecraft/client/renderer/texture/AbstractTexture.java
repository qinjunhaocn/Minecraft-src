/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import javax.annotation.Nullable;

public abstract class AbstractTexture
implements AutoCloseable {
    @Nullable
    protected GpuTexture texture;
    @Nullable
    protected GpuTextureView textureView;

    public void setClamp(boolean $$0) {
        if (this.texture == null) {
            throw new IllegalStateException("Texture does not exist, can't change its clamp before something initializes it");
        }
        this.texture.setAddressMode($$0 ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT);
    }

    public void setFilter(boolean $$0, boolean $$1) {
        if (this.texture == null) {
            throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
        }
        this.texture.setTextureFilter($$0 ? FilterMode.LINEAR : FilterMode.NEAREST, $$1);
    }

    public void setUseMipmaps(boolean $$0) {
        if (this.texture == null) {
            throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
        }
        this.texture.setUseMipmaps($$0);
    }

    @Override
    public void close() {
        if (this.texture != null) {
            this.texture.close();
            this.texture = null;
        }
        if (this.textureView != null) {
            this.textureView.close();
            this.textureView = null;
        }
    }

    public GpuTexture getTexture() {
        if (this.texture == null) {
            throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
        }
        return this.texture;
    }

    public GpuTextureView getTextureView() {
        if (this.textureView == null) {
            throw new IllegalStateException("Texture view does not exist, can't get it before something initializes it");
        }
        return this.textureView;
    }
}

