/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.textures.GpuTexture;

public interface SheetGlyphInfo {
    public int getPixelWidth();

    public int getPixelHeight();

    public void upload(int var1, int var2, GpuTexture var3);

    public boolean isColored();

    public float getOversample();

    default public float getLeft() {
        return this.getBearingLeft();
    }

    default public float getRight() {
        return this.getLeft() + (float)this.getPixelWidth() / this.getOversample();
    }

    default public float getTop() {
        return 7.0f - this.getBearingTop();
    }

    default public float getBottom() {
        return this.getTop() + (float)this.getPixelHeight() / this.getOversample();
    }

    default public float getBearingLeft() {
        return 0.0f;
    }

    default public float getBearingTop() {
        return 7.0f;
    }
}

