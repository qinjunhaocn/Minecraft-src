/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderPipelines;

public abstract class RenderTarget {
    private static int UNNAMED_RENDER_TARGETS = 0;
    public int width;
    public int height;
    public int viewWidth;
    public int viewHeight;
    protected final String label;
    public final boolean useDepth;
    @Nullable
    protected GpuTexture colorTexture;
    @Nullable
    protected GpuTextureView colorTextureView;
    @Nullable
    protected GpuTexture depthTexture;
    @Nullable
    protected GpuTextureView depthTextureView;
    public FilterMode filterMode;

    public RenderTarget(@Nullable String $$0, boolean $$1) {
        this.label = $$0 == null ? "FBO " + UNNAMED_RENDER_TARGETS++ : $$0;
        this.useDepth = $$1;
    }

    public void resize(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        this.destroyBuffers();
        this.createBuffers($$0, $$1);
    }

    public void destroyBuffers() {
        RenderSystem.assertOnRenderThread();
        if (this.depthTexture != null) {
            this.depthTexture.close();
            this.depthTexture = null;
        }
        if (this.depthTextureView != null) {
            this.depthTextureView.close();
            this.depthTextureView = null;
        }
        if (this.colorTexture != null) {
            this.colorTexture.close();
            this.colorTexture = null;
        }
        if (this.colorTextureView != null) {
            this.colorTextureView.close();
            this.colorTextureView = null;
        }
    }

    public void copyDepthFrom(RenderTarget $$0) {
        RenderSystem.assertOnRenderThread();
        if (this.depthTexture == null) {
            throw new IllegalStateException("Trying to copy depth texture to a RenderTarget without a depth texture");
        }
        if ($$0.depthTexture == null) {
            throw new IllegalStateException("Trying to copy depth texture from a RenderTarget without a depth texture");
        }
        RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture($$0.depthTexture, this.depthTexture, 0, 0, 0, 0, 0, this.width, this.height);
    }

    public void createBuffers(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GpuDevice $$2 = RenderSystem.getDevice();
        int $$3 = $$2.getMaxTextureSize();
        if ($$0 <= 0 || $$0 > $$3 || $$1 <= 0 || $$1 > $$3) {
            throw new IllegalArgumentException("Window " + $$0 + "x" + $$1 + " size out of bounds (max. size: " + $$3 + ")");
        }
        this.viewWidth = $$0;
        this.viewHeight = $$1;
        this.width = $$0;
        this.height = $$1;
        if (this.useDepth) {
            this.depthTexture = $$2.createTexture(() -> this.label + " / Depth", 15, TextureFormat.DEPTH32, $$0, $$1, 1, 1);
            this.depthTextureView = $$2.createTextureView(this.depthTexture);
            this.depthTexture.setTextureFilter(FilterMode.NEAREST, false);
            this.depthTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        }
        this.colorTexture = $$2.createTexture(() -> this.label + " / Color", 15, TextureFormat.RGBA8, $$0, $$1, 1, 1);
        this.colorTextureView = $$2.createTextureView(this.colorTexture);
        this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.setFilterMode(FilterMode.NEAREST, true);
    }

    public void setFilterMode(FilterMode $$0) {
        this.setFilterMode($$0, false);
    }

    private void setFilterMode(FilterMode $$0, boolean $$1) {
        if (this.colorTexture == null) {
            throw new IllegalStateException("Can't change filter mode, color texture doesn't exist yet");
        }
        if ($$1 || $$0 != this.filterMode) {
            this.filterMode = $$0;
            this.colorTexture.setTextureFilter($$0, false);
        }
    }

    public void blitToScreen() {
        if (this.colorTexture == null) {
            throw new IllegalStateException("Can't blit to screen, color texture doesn't exist yet");
        }
        RenderSystem.getDevice().createCommandEncoder().presentTexture(this.colorTextureView);
    }

    public void blitAndBlendToTexture(GpuTextureView $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.AutoStorageIndexBuffer $$1 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$2 = $$1.getBuffer(6);
        GpuBuffer $$3 = RenderSystem.getQuadVertexBuffer();
        try (RenderPass $$4 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Blit render target", $$0, OptionalInt.empty());){
            $$4.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms($$4);
            $$4.setVertexBuffer(0, $$3);
            $$4.setIndexBuffer($$2, $$1.type());
            $$4.bindSampler("InSampler", this.colorTextureView);
            $$4.drawIndexed(0, 0, 6, 1);
        }
    }

    @Nullable
    public GpuTexture getColorTexture() {
        return this.colorTexture;
    }

    @Nullable
    public GpuTextureView getColorTextureView() {
        return this.colorTextureView;
    }

    @Nullable
    public GpuTexture getDepthTexture() {
        return this.depthTexture;
    }

    @Nullable
    public GpuTextureView getDepthTextureView() {
        return this.depthTextureView;
    }
}

