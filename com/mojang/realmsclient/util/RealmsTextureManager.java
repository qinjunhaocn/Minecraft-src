/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TEMPLATE_ICON_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/presets/isles.png");

    public static ResourceLocation worldTemplate(String $$0, @Nullable String $$1) {
        if ($$1 == null) {
            return TEMPLATE_ICON_LOCATION;
        }
        return RealmsTextureManager.getTexture($$0, $$1);
    }

    private static ResourceLocation getTexture(String $$0, String $$1) {
        RealmsTexture $$2 = TEXTURES.get($$0);
        if ($$2 != null && $$2.image().equals($$1)) {
            return $$2.textureId;
        }
        NativeImage $$3 = RealmsTextureManager.loadImage($$1);
        if ($$3 == null) {
            ResourceLocation $$4 = MissingTextureAtlasSprite.getLocation();
            TEXTURES.put($$0, new RealmsTexture($$1, $$4));
            return $$4;
        }
        ResourceLocation $$5 = ResourceLocation.fromNamespaceAndPath("realms", "dynamic/" + $$0);
        Minecraft.getInstance().getTextureManager().register($$5, new DynamicTexture($$5::toString, $$3));
        TEXTURES.put($$0, new RealmsTexture($$1, $$5));
        return $$5;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private static NativeImage loadImage(String $$0) {
        byte[] $$1 = Base64.getDecoder().decode($$0);
        ByteBuffer $$2 = MemoryUtil.memAlloc((int)$$1.length);
        try {
            NativeImage nativeImage = NativeImage.read((ByteBuffer)$$2.put($$1).flip());
            return nativeImage;
        } catch (IOException $$3) {
            LOGGER.warn("Failed to load world image: {}", (Object)$$0, (Object)$$3);
        } finally {
            MemoryUtil.memFree((Buffer)$$2);
        }
        return null;
    }

    public static final class RealmsTexture
    extends Record {
        private final String image;
        final ResourceLocation textureId;

        public RealmsTexture(String $$0, ResourceLocation $$1) {
            this.image = $$0;
            this.textureId = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsTexture.class, "image;textureId", "image", "textureId"}, this, $$0);
        }

        public String image() {
            return this.image;
        }

        public ResourceLocation textureId() {
            return this.textureId;
        }
    }
}

