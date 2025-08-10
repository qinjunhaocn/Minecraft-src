/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import com.mojang.blaze3d.systems.RenderSystem;

public record RenderTargetDescriptor(int width, int height, boolean useDepth, int clearColor) implements ResourceDescriptor<RenderTarget>
{
    @Override
    public RenderTarget allocate() {
        return new TextureTarget(null, this.width, this.height, this.useDepth);
    }

    @Override
    public void prepare(RenderTarget $$0) {
        if (this.useDepth) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures($$0.getColorTexture(), this.clearColor, $$0.getDepthTexture(), 1.0);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture($$0.getColorTexture(), this.clearColor);
        }
    }

    @Override
    public void free(RenderTarget $$0) {
        $$0.destroyBuffers();
    }

    @Override
    public boolean canUsePhysicalResource(ResourceDescriptor<?> $$0) {
        if ($$0 instanceof RenderTargetDescriptor) {
            RenderTargetDescriptor $$1 = (RenderTargetDescriptor)$$0;
            return this.width == $$1.width && this.height == $$1.height && this.useDepth == $$1.useDepth;
        }
        return false;
    }

    @Override
    public /* synthetic */ void free(Object object) {
        this.free((RenderTarget)object);
    }

    @Override
    public /* synthetic */ void prepare(Object object) {
        this.prepare((RenderTarget)object);
    }

    @Override
    public /* synthetic */ Object allocate() {
        return this.allocate();
    }
}

