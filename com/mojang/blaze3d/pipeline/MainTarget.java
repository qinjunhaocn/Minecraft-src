/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class MainTarget
extends RenderTarget {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    static final Dimension DEFAULT_DIMENSIONS = new Dimension(854, 480);

    public MainTarget(int $$0, int $$1) {
        super("Main", true);
        this.createFrameBuffer($$0, $$1);
    }

    private void createFrameBuffer(int $$0, int $$1) {
        Dimension $$2 = this.allocateAttachments($$0, $$1);
        if (this.colorTexture == null || this.depthTexture == null) {
            throw new IllegalStateException("Missing color and/or depth textures");
        }
        this.colorTexture.setTextureFilter(FilterMode.NEAREST, false);
        this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.colorTexture.setTextureFilter(FilterMode.NEAREST, false);
        this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.viewWidth = $$2.width;
        this.viewHeight = $$2.height;
        this.width = $$2.width;
        this.height = $$2.height;
    }

    private Dimension allocateAttachments(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        for (Dimension $$2 : Dimension.listWithFallback($$0, $$1)) {
            if (this.colorTexture != null) {
                this.colorTexture.close();
                this.colorTexture = null;
            }
            if (this.colorTextureView != null) {
                this.colorTextureView.close();
                this.colorTextureView = null;
            }
            if (this.depthTexture != null) {
                this.depthTexture.close();
                this.depthTexture = null;
            }
            if (this.depthTextureView != null) {
                this.depthTextureView.close();
                this.depthTextureView = null;
            }
            this.colorTexture = this.allocateColorAttachment($$2);
            this.depthTexture = this.allocateDepthAttachment($$2);
            if (this.colorTexture == null || this.depthTexture == null) continue;
            this.colorTextureView = RenderSystem.getDevice().createTextureView(this.colorTexture);
            this.depthTextureView = RenderSystem.getDevice().createTextureView(this.depthTexture);
            return $$2;
        }
        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + (this.colorTexture == null ? "missing color" : "have color") + ", " + (this.depthTexture == null ? "missing depth" : "have depth") + ")");
    }

    @Nullable
    private GpuTexture allocateColorAttachment(Dimension $$0) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.label + " / Color", 15, TextureFormat.RGBA8, $$0.width, $$0.height, 1, 1);
        } catch (GpuOutOfMemoryException $$1) {
            return null;
        }
    }

    @Nullable
    private GpuTexture allocateDepthAttachment(Dimension $$0) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.label + " / Depth", 15, TextureFormat.DEPTH32, $$0.width, $$0.height, 1, 1);
        } catch (GpuOutOfMemoryException $$1) {
            return null;
        }
    }

    static class Dimension {
        public final int width;
        public final int height;

        Dimension(int $$0, int $$1) {
            this.width = $$0;
            this.height = $$1;
        }

        static List<Dimension> listWithFallback(int $$0, int $$1) {
            RenderSystem.assertOnRenderThread();
            int $$2 = RenderSystem.getDevice().getMaxTextureSize();
            if ($$0 <= 0 || $$0 > $$2 || $$1 <= 0 || $$1 > $$2) {
                return ImmutableList.of(DEFAULT_DIMENSIONS);
            }
            return ImmutableList.of(new Dimension($$0, $$1), DEFAULT_DIMENSIONS);
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            Dimension $$1 = (Dimension)$$0;
            return this.width == $$1.width && this.height == $$1.height;
        }

        public int hashCode() {
            return Objects.hash(this.width, this.height);
        }

        public String toString() {
            return this.width + "x" + this.height;
        }
    }
}

