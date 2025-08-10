/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArmadilloRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public class ArmadilloRenderer
extends AgeableMobRenderer<Armadillo, ArmadilloRenderState, ArmadilloModel> {
    private static final ResourceLocation ARMADILLO_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armadillo.png");

    public ArmadilloRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ArmadilloModel($$0.bakeLayer(ModelLayers.ARMADILLO)), new ArmadilloModel($$0.bakeLayer(ModelLayers.ARMADILLO_BABY)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(ArmadilloRenderState $$0) {
        return ARMADILLO_LOCATION;
    }

    @Override
    public ArmadilloRenderState createRenderState() {
        return new ArmadilloRenderState();
    }

    @Override
    public void extractRenderState(Armadillo $$0, ArmadilloRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isHidingInShell = $$0.shouldHideInShell();
        $$1.peekAnimationState.copyFrom($$0.peekAnimationState);
        $$1.rollOutAnimationState.copyFrom($$0.rollOutAnimationState);
        $$1.rollUpAnimationState.copyFrom($$0.rollUpAnimationState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ArmadilloRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

