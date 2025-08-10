/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;

public class SnowGolemRenderer
extends MobRenderer<SnowGolem, SnowGolemRenderState, SnowGolemModel> {
    private static final ResourceLocation SNOW_GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/snow_golem.png");

    public SnowGolemRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SnowGolemModel($$0.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5f);
        this.addLayer(new SnowGolemHeadLayer(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(SnowGolemRenderState $$0) {
        return SNOW_GOLEM_LOCATION;
    }

    @Override
    public SnowGolemRenderState createRenderState() {
        return new SnowGolemRenderState();
    }

    @Override
    public void extractRenderState(SnowGolem $$0, SnowGolemRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.hasPumpkin = $$0.hasPumpkin();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SnowGolemRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

