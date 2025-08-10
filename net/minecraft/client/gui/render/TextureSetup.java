/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record TextureSetup(@Nullable GpuTextureView texure0, @Nullable GpuTextureView texure1, @Nullable GpuTextureView texure2) {
    private static final TextureSetup NO_TEXTURE_SETUP = new TextureSetup(null, null, null);
    private static int sortKeySeed;

    public static TextureSetup singleTexture(GpuTextureView $$0) {
        return new TextureSetup($$0, null, null);
    }

    public static TextureSetup singleTextureWithLightmap(GpuTextureView $$0) {
        return new TextureSetup($$0, null, Minecraft.getInstance().gameRenderer.lightTexture().getTextureView());
    }

    public static TextureSetup doubleTexture(GpuTextureView $$0, GpuTextureView $$1) {
        return new TextureSetup($$0, $$1, null);
    }

    public static TextureSetup noTexture() {
        return NO_TEXTURE_SETUP;
    }

    public int getSortKey() {
        return this.hashCode();
    }

    public static void updateSortKeySeed() {
        sortKeySeed = Math.round(100000.0f * (float)Math.random());
    }

    @Nullable
    public GpuTextureView texure0() {
        return this.texure0;
    }

    @Nullable
    public GpuTextureView texure1() {
        return this.texure1;
    }

    @Nullable
    public GpuTextureView texure2() {
        return this.texure2;
    }
}

