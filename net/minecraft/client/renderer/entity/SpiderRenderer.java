/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer<T extends Spider>
extends MobRenderer<T, LivingEntityRenderState, SpiderModel> {
    private static final ResourceLocation SPIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/spider/spider.png");

    public SpiderRenderer(EntityRendererProvider.Context $$0) {
        this($$0, ModelLayers.SPIDER);
    }

    public SpiderRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1) {
        super($$0, new SpiderModel($$0.bakeLayer($$1)), 0.8f);
        this.addLayer(new SpiderEyesLayer<SpiderModel>(this));
    }

    @Override
    protected float getFlipDegrees() {
        return 180.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntityRenderState $$0) {
        return SPIDER_LOCATION;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void extractRenderState(T $$0, LivingEntityRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

