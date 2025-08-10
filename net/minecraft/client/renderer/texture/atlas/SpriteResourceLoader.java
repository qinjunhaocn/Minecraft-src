/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.texture.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

@FunctionalInterface
public interface SpriteResourceLoader {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static SpriteResourceLoader create(Collection<MetadataSectionType<?>> $$0) {
        return ($$1, $$2) -> {
            FrameSize $$13;
            void $$10;
            void $$5;
            try {
                ResourceMetadata $$3 = $$2.metadata().copySections($$0);
            } catch (Exception $$4) {
                LOGGER.error("Unable to parse metadata from {}", (Object)$$1, (Object)$$4);
                return null;
            }
            try (InputStream $$6 = $$2.open();){
                NativeImage $$7 = NativeImage.read($$6);
            } catch (IOException $$9) {
                LOGGER.error("Using missing texture, unable to load {}", (Object)$$1, (Object)$$9);
                return null;
            }
            Optional<AnimationMetadataSection> $$11 = $$5.getSection(AnimationMetadataSection.TYPE);
            if ($$11.isPresent()) {
                FrameSize $$12 = $$11.get().calculateFrameSize($$10.getWidth(), $$10.getHeight());
                if (!Mth.isMultipleOf($$10.getWidth(), $$12.width()) || !Mth.isMultipleOf($$10.getHeight(), $$12.height())) {
                    LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", $$1, $$10.getWidth(), $$10.getHeight(), $$12.width(), $$12.height());
                    $$10.close();
                    return null;
                }
            } else {
                $$13 = new FrameSize($$10.getWidth(), $$10.getHeight());
            }
            return new SpriteContents($$1, $$13, (NativeImage)$$10, (ResourceMetadata)$$5);
        };
    }

    @Nullable
    public SpriteContents loadSprite(ResourceLocation var1, Resource var2);
}

