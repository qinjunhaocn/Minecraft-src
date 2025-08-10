/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinRenderer
extends AgeableMobRenderer<Dolphin, DolphinRenderState, DolphinModel> {
    private static final ResourceLocation DOLPHIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/dolphin.png");

    public DolphinRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new DolphinModel($$0.bakeLayer(ModelLayers.DOLPHIN)), new DolphinModel($$0.bakeLayer(ModelLayers.DOLPHIN_BABY)), 0.7f);
        this.addLayer(new DolphinCarryingItemLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(DolphinRenderState $$0) {
        return DOLPHIN_LOCATION;
    }

    @Override
    public DolphinRenderState createRenderState() {
        return new DolphinRenderState();
    }

    @Override
    public void extractRenderState(Dolphin $$0, DolphinRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HoldingEntityRenderState.extractHoldingEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.isMoving = $$0.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((DolphinRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

