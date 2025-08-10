/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum TextureFormat {
    RGBA8(4),
    RED8(1),
    RED8I(1),
    DEPTH32(4);

    private final int pixelSize;

    private TextureFormat(int $$0) {
        this.pixelSize = $$0;
    }

    public int pixelSize() {
        return this.pixelSize;
    }

    public boolean hasColorAspect() {
        return this == RGBA8 || this == RED8;
    }

    public boolean hasDepthAspect() {
        return this == DEPTH32;
    }
}

