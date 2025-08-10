/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public final class MissingTextureAtlasSprite {
    private static final int MISSING_IMAGE_WIDTH = 16;
    private static final int MISSING_IMAGE_HEIGHT = 16;
    private static final String MISSING_TEXTURE_NAME = "missingno";
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("missingno");

    public static NativeImage generateMissingImage() {
        return MissingTextureAtlasSprite.generateMissingImage(16, 16);
    }

    public static NativeImage generateMissingImage(int $$0, int $$1) {
        NativeImage $$2 = new NativeImage($$0, $$1, false);
        int $$3 = -524040;
        for (int $$4 = 0; $$4 < $$1; ++$$4) {
            for (int $$5 = 0; $$5 < $$0; ++$$5) {
                if ($$4 < $$1 / 2 ^ $$5 < $$0 / 2) {
                    $$2.setPixel($$5, $$4, -524040);
                    continue;
                }
                $$2.setPixel($$5, $$4, -16777216);
            }
        }
        return $$2;
    }

    public static SpriteContents create() {
        NativeImage $$0 = MissingTextureAtlasSprite.generateMissingImage(16, 16);
        return new SpriteContents(MISSING_TEXTURE_LOCATION, new FrameSize(16, 16), $$0, ResourceMetadata.EMPTY);
    }

    public static ResourceLocation getLocation() {
        return MISSING_TEXTURE_LOCATION;
    }
}

