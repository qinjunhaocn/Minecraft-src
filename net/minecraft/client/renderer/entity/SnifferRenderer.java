/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SnifferRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.phys.AABB;

public class SnifferRenderer
extends AgeableMobRenderer<Sniffer, SnifferRenderState, SnifferModel> {
    private static final ResourceLocation SNIFFER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sniffer/sniffer.png");

    public SnifferRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new SnifferModel($$0.bakeLayer(ModelLayers.SNIFFER)), new SnifferModel($$0.bakeLayer(ModelLayers.SNIFFER_BABY)), 1.1f);
    }

    @Override
    public ResourceLocation getTextureLocation(SnifferRenderState $$0) {
        return SNIFFER_LOCATION;
    }

    @Override
    public SnifferRenderState createRenderState() {
        return new SnifferRenderState();
    }

    @Override
    public void extractRenderState(Sniffer $$0, SnifferRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isSearching = $$0.isSearching();
        $$1.diggingAnimationState.copyFrom($$0.diggingAnimationState);
        $$1.sniffingAnimationState.copyFrom($$0.sniffingAnimationState);
        $$1.risingAnimationState.copyFrom($$0.risingAnimationState);
        $$1.feelingHappyAnimationState.copyFrom($$0.feelingHappyAnimationState);
        $$1.scentingAnimationState.copyFrom($$0.scentingAnimationState);
    }

    @Override
    protected AABB getBoundingBoxForCulling(Sniffer $$0) {
        return super.getBoundingBoxForCulling($$0).inflate(0.6f);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((SnifferRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

