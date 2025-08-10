/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class CubeMapTexture
extends ReloadableTexture {
    private static final String[] SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

    public CubeMapTexture(ResourceLocation $$0) {
        super($$0);
    }

    @Override
    public TextureContents loadContents(ResourceManager $$0) throws IOException {
        ResourceLocation $$1 = this.resourceId();
        try (TextureContents $$2 = TextureContents.load($$0, $$1.withSuffix(SUFFIXES[0]));){
            int $$3 = $$2.image().getWidth();
            int $$4 = $$2.image().getHeight();
            NativeImage $$5 = new NativeImage($$3, $$4 * 6, false);
            $$2.image().copyRect($$5, 0, 0, 0, 0, $$3, $$4, false, true);
            for (int $$6 = 1; $$6 < 6; ++$$6) {
                try (TextureContents $$7 = TextureContents.load($$0, $$1.withSuffix(SUFFIXES[$$6]));){
                    if ($$7.image().getWidth() != $$3 || $$7.image().getHeight() != $$4) {
                        throw new IOException("Image dimensions of cubemap '" + String.valueOf($$1) + "' sides do not match: part 0 is " + $$3 + "x" + $$4 + ", but part " + $$6 + " is " + $$7.image().getWidth() + "x" + $$7.image().getHeight());
                    }
                    $$7.image().copyRect($$5, 0, 0, 0, $$6 * $$4, $$3, $$4, false, true);
                    continue;
                }
            }
            TextureContents textureContents = new TextureContents($$5, new TextureMetadataSection(true, false));
            return textureContents;
        }
    }

    @Override
    protected void doLoad(NativeImage $$0, boolean $$1, boolean $$2) {
        GpuDevice $$3 = RenderSystem.getDevice();
        int $$4 = $$0.getWidth();
        int $$5 = $$0.getHeight() / 6;
        this.close();
        this.texture = $$3.createTexture(this.resourceId()::toString, 21, TextureFormat.RGBA8, $$4, $$5, 6, 1);
        this.textureView = $$3.createTextureView(this.texture);
        this.setFilter($$1, false);
        this.setClamp($$2);
        for (int $$6 = 0; $$6 < 6; ++$$6) {
            $$3.createCommandEncoder().writeToTexture(this.texture, $$0, 0, $$6, 0, 0, $$4, $$5, 0, $$5 * $$6);
        }
    }
}

