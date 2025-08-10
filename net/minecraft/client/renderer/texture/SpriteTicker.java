/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.textures.GpuTexture;

public interface SpriteTicker
extends AutoCloseable {
    public void tickAndUpload(int var1, int var2, GpuTexture var3);

    @Override
    public void close();
}

