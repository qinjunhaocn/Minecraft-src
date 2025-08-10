/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PolarBearRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearRenderer
extends AgeableMobRenderer<PolarBear, PolarBearRenderState, PolarBearModel> {
    private static final ResourceLocation BEAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/bear/polarbear.png");

    public PolarBearRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PolarBearModel($$0.bakeLayer(ModelLayers.POLAR_BEAR)), new PolarBearModel($$0.bakeLayer(ModelLayers.POLAR_BEAR_BABY)), 0.9f);
    }

    @Override
    public ResourceLocation getTextureLocation(PolarBearRenderState $$0) {
        return BEAR_LOCATION;
    }

    @Override
    public PolarBearRenderState createRenderState() {
        return new PolarBearRenderState();
    }

    @Override
    public void extractRenderState(PolarBear $$0, PolarBearRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.standScale = $$0.getStandingAnimationScale($$2);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PolarBearRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

