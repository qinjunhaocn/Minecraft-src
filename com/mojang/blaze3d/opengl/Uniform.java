/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.TextureFormat;

public sealed interface Uniform
extends AutoCloseable {
    @Override
    default public void close() {
    }

    public record Sampler(int location, int samplerIndex) implements Uniform
    {
    }

    public record Utb(int location, int samplerIndex, TextureFormat format, int texture) implements Uniform
    {
        public Utb(int $$0, int $$1, TextureFormat $$2) {
            this($$0, $$1, $$2, GlStateManager._genTexture());
        }

        @Override
        public void close() {
            GlStateManager._deleteTexture(this.texture);
        }
    }

    public record Ubo(int blockBinding) implements Uniform
    {
    }
}

