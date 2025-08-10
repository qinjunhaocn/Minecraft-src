/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public record TextureContents(NativeImage image, @Nullable TextureMetadataSection metadata) implements Closeable
{
    /*
     * WARNING - void declaration
     */
    public static TextureContents load(ResourceManager $$0, ResourceLocation $$1) throws IOException {
        void $$5;
        Resource $$2 = $$0.getResourceOrThrow($$1);
        try (InputStream $$3 = $$2.open();){
            NativeImage $$4 = NativeImage.read($$3);
        }
        TextureMetadataSection $$6 = $$2.metadata().getSection(TextureMetadataSection.TYPE).orElse(null);
        return new TextureContents((NativeImage)$$5, $$6);
    }

    public static TextureContents createMissing() {
        return new TextureContents(MissingTextureAtlasSprite.generateMissingImage(), null);
    }

    public boolean blur() {
        return this.metadata != null ? this.metadata.blur() : false;
    }

    public boolean clamp() {
        return this.metadata != null ? this.metadata.clamp() : false;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Nullable
    public TextureMetadataSection metadata() {
        return this.metadata;
    }
}

