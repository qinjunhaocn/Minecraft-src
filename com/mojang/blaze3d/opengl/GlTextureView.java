/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

public class GlTextureView
extends GpuTextureView {
    private boolean closed;

    protected GlTextureView(GlTexture $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
        $$0.addViews();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.texture().removeViews();
        }
    }

    @Override
    public GlTexture texture() {
        return (GlTexture)super.texture();
    }

    @Override
    public /* synthetic */ GpuTexture texture() {
        return this.texture();
    }
}

