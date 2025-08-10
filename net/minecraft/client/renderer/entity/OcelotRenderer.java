/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class OcelotRenderer
extends AgeableMobRenderer<Ocelot, FelineRenderState, OcelotModel> {
    private static final ResourceLocation CAT_OCELOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/ocelot.png");

    public OcelotRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new OcelotModel($$0.bakeLayer(ModelLayers.OCELOT)), new OcelotModel($$0.bakeLayer(ModelLayers.OCELOT_BABY)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(FelineRenderState $$0) {
        return CAT_OCELOT_LOCATION;
    }

    @Override
    public FelineRenderState createRenderState() {
        return new FelineRenderState();
    }

    @Override
    public void extractRenderState(Ocelot $$0, FelineRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isCrouching = $$0.isCrouching();
        $$1.isSprinting = $$0.isSprinting();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((FelineRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

