/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogRenderer
extends MobRenderer<Frog, FrogRenderState, FrogModel> {
    public FrogRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new FrogModel($$0.bakeLayer(ModelLayers.FROG)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(FrogRenderState $$0) {
        return $$0.texture;
    }

    @Override
    public FrogRenderState createRenderState() {
        return new FrogRenderState();
    }

    @Override
    public void extractRenderState(Frog $$0, FrogRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isSwimming = $$0.isInWater();
        $$1.jumpAnimationState.copyFrom($$0.jumpAnimationState);
        $$1.croakAnimationState.copyFrom($$0.croakAnimationState);
        $$1.tongueAnimationState.copyFrom($$0.tongueAnimationState);
        $$1.swimIdleAnimationState.copyFrom($$0.swimIdleAnimationState);
        $$1.texture = $$0.getVariant().value().assetInfo().texturePath();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((FrogRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

