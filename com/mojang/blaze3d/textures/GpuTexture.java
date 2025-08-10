/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;

@DontObfuscate
public abstract class GpuTexture
implements AutoCloseable {
    public static final int USAGE_COPY_DST = 1;
    public static final int USAGE_COPY_SRC = 2;
    public static final int USAGE_TEXTURE_BINDING = 4;
    public static final int USAGE_RENDER_ATTACHMENT = 8;
    public static final int USAGE_CUBEMAP_COMPATIBLE = 16;
    private final TextureFormat format;
    private final int width;
    private final int height;
    private final int depthOrLayers;
    private final int mipLevels;
    private final int usage;
    private final String label;
    protected AddressMode addressModeU = AddressMode.REPEAT;
    protected AddressMode addressModeV = AddressMode.REPEAT;
    protected FilterMode minFilter = FilterMode.NEAREST;
    protected FilterMode magFilter = FilterMode.LINEAR;
    protected boolean useMipmaps = true;

    public GpuTexture(int $$0, String $$1, TextureFormat $$2, int $$3, int $$4, int $$5, int $$6) {
        this.usage = $$0;
        this.label = $$1;
        this.format = $$2;
        this.width = $$3;
        this.height = $$4;
        this.depthOrLayers = $$5;
        this.mipLevels = $$6;
    }

    public int getWidth(int $$0) {
        return this.width >> $$0;
    }

    public int getHeight(int $$0) {
        return this.height >> $$0;
    }

    public int getDepthOrLayers() {
        return this.depthOrLayers;
    }

    public int getMipLevels() {
        return this.mipLevels;
    }

    public TextureFormat getFormat() {
        return this.format;
    }

    public int usage() {
        return this.usage;
    }

    public void setAddressMode(AddressMode $$0) {
        this.setAddressMode($$0, $$0);
    }

    public void setAddressMode(AddressMode $$0, AddressMode $$1) {
        this.addressModeU = $$0;
        this.addressModeV = $$1;
    }

    public void setTextureFilter(FilterMode $$0, boolean $$1) {
        this.setTextureFilter($$0, $$0, $$1);
    }

    public void setTextureFilter(FilterMode $$0, FilterMode $$1, boolean $$2) {
        this.minFilter = $$0;
        this.magFilter = $$1;
        this.setUseMipmaps($$2);
    }

    public void setUseMipmaps(boolean $$0) {
        this.useMipmaps = $$0;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public abstract void close();

    public abstract boolean isClosed();
}

