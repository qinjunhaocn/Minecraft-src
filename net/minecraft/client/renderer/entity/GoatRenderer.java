/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;

public class GoatRenderer
extends AgeableMobRenderer<Goat, GoatRenderState, GoatModel> {
    private static final ResourceLocation GOAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/goat/goat.png");

    public GoatRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new GoatModel($$0.bakeLayer(ModelLayers.GOAT)), new GoatModel($$0.bakeLayer(ModelLayers.GOAT_BABY)), 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(GoatRenderState $$0) {
        return GOAT_LOCATION;
    }

    @Override
    public GoatRenderState createRenderState() {
        return new GoatRenderState();
    }

    @Override
    public void extractRenderState(Goat $$0, GoatRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.hasLeftHorn = $$0.hasLeftHorn();
        $$1.hasRightHorn = $$0.hasRightHorn();
        $$1.rammingXHeadRot = $$0.getRammingXHeadRot();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((GoatRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

