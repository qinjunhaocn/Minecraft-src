/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public interface SpriteSet {
    public TextureAtlasSprite get(int var1, int var2);

    public TextureAtlasSprite get(RandomSource var1);
}

