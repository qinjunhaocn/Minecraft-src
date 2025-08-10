/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer
extends MobRenderer<Bat, BatRenderState, BatModel> {
    private static final ResourceLocation BAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/bat.png");

    public BatRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new BatModel($$0.bakeLayer(ModelLayers.BAT)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(BatRenderState $$0) {
        return BAT_LOCATION;
    }

    @Override
    public BatRenderState createRenderState() {
        return new BatRenderState();
    }

    @Override
    public void extractRenderState(Bat $$0, BatRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isResting = $$0.isResting();
        $$1.flyAnimationState.copyFrom($$0.flyAnimationState);
        $$1.restAnimationState.copyFrom($$0.restAnimationState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((BatRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

