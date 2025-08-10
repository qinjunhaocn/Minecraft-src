/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

public class LevelTargetBundle
implements PostChain.TargetBundle {
    public static final ResourceLocation MAIN_TARGET_ID = PostChain.MAIN_TARGET_ID;
    public static final ResourceLocation TRANSLUCENT_TARGET_ID = ResourceLocation.withDefaultNamespace("translucent");
    public static final ResourceLocation ITEM_ENTITY_TARGET_ID = ResourceLocation.withDefaultNamespace("item_entity");
    public static final ResourceLocation PARTICLES_TARGET_ID = ResourceLocation.withDefaultNamespace("particles");
    public static final ResourceLocation WEATHER_TARGET_ID = ResourceLocation.withDefaultNamespace("weather");
    public static final ResourceLocation CLOUDS_TARGET_ID = ResourceLocation.withDefaultNamespace("clouds");
    public static final ResourceLocation ENTITY_OUTLINE_TARGET_ID = ResourceLocation.withDefaultNamespace("entity_outline");
    public static final Set<ResourceLocation> MAIN_TARGETS = Set.of((Object)MAIN_TARGET_ID);
    public static final Set<ResourceLocation> OUTLINE_TARGETS = Set.of((Object)MAIN_TARGET_ID, (Object)ENTITY_OUTLINE_TARGET_ID);
    public static final Set<ResourceLocation> SORTING_TARGETS = Set.of((Object)MAIN_TARGET_ID, (Object)TRANSLUCENT_TARGET_ID, (Object)ITEM_ENTITY_TARGET_ID, (Object)PARTICLES_TARGET_ID, (Object)WEATHER_TARGET_ID, (Object)CLOUDS_TARGET_ID);
    public ResourceHandle<RenderTarget> main = ResourceHandle.invalid();
    @Nullable
    public ResourceHandle<RenderTarget> translucent;
    @Nullable
    public ResourceHandle<RenderTarget> itemEntity;
    @Nullable
    public ResourceHandle<RenderTarget> particles;
    @Nullable
    public ResourceHandle<RenderTarget> weather;
    @Nullable
    public ResourceHandle<RenderTarget> clouds;
    @Nullable
    public ResourceHandle<RenderTarget> entityOutline;

    @Override
    public void replace(ResourceLocation $$0, ResourceHandle<RenderTarget> $$1) {
        if ($$0.equals(MAIN_TARGET_ID)) {
            this.main = $$1;
        } else if ($$0.equals(TRANSLUCENT_TARGET_ID)) {
            this.translucent = $$1;
        } else if ($$0.equals(ITEM_ENTITY_TARGET_ID)) {
            this.itemEntity = $$1;
        } else if ($$0.equals(PARTICLES_TARGET_ID)) {
            this.particles = $$1;
        } else if ($$0.equals(WEATHER_TARGET_ID)) {
            this.weather = $$1;
        } else if ($$0.equals(CLOUDS_TARGET_ID)) {
            this.clouds = $$1;
        } else if ($$0.equals(ENTITY_OUTLINE_TARGET_ID)) {
            this.entityOutline = $$1;
        } else {
            throw new IllegalArgumentException("No target with id " + String.valueOf($$0));
        }
    }

    @Override
    @Nullable
    public ResourceHandle<RenderTarget> get(ResourceLocation $$0) {
        if ($$0.equals(MAIN_TARGET_ID)) {
            return this.main;
        }
        if ($$0.equals(TRANSLUCENT_TARGET_ID)) {
            return this.translucent;
        }
        if ($$0.equals(ITEM_ENTITY_TARGET_ID)) {
            return this.itemEntity;
        }
        if ($$0.equals(PARTICLES_TARGET_ID)) {
            return this.particles;
        }
        if ($$0.equals(WEATHER_TARGET_ID)) {
            return this.weather;
        }
        if ($$0.equals(CLOUDS_TARGET_ID)) {
            return this.clouds;
        }
        if ($$0.equals(ENTITY_OUTLINE_TARGET_ID)) {
            return this.entityOutline;
        }
        return null;
    }

    public void clear() {
        this.main = ResourceHandle.invalid();
        this.translucent = null;
        this.itemEntity = null;
        this.particles = null;
        this.weather = null;
        this.clouds = null;
        this.entityOutline = null;
    }
}

