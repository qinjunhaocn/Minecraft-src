/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class SimpleTexture
extends ReloadableTexture {
    public SimpleTexture(ResourceLocation $$0) {
        super($$0);
    }

    @Override
    public TextureContents loadContents(ResourceManager $$0) throws IOException {
        return TextureContents.load($$0, this.resourceId());
    }
}

