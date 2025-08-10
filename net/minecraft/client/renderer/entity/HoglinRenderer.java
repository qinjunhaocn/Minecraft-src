/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHoglinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class HoglinRenderer
extends AbstractHoglinRenderer<Hoglin> {
    private static final ResourceLocation HOGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/hoglin/hoglin.png");

    public HoglinRenderer(EntityRendererProvider.Context $$0) {
        super($$0, ModelLayers.HOGLIN, ModelLayers.HOGLIN_BABY, 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(HoglinRenderState $$0) {
        return HOGLIN_LOCATION;
    }

    @Override
    public void extractRenderState(Hoglin $$0, HoglinRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isConverting = $$0.isConverting();
    }

    @Override
    protected boolean isShaking(HoglinRenderState $$0) {
        return super.isShaking($$0) || $$0.isConverting;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState livingEntityRenderState) {
        return this.isShaking((HoglinRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((HoglinRenderState)livingEntityRenderState);
    }
}

