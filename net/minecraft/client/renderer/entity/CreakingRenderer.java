/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CreakingModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.creaking.Creaking;

public class CreakingRenderer<T extends Creaking>
extends MobRenderer<T, CreakingRenderState, CreakingModel> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking.png");
    private static final ResourceLocation EYES_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creaking/creaking_eyes.png");

    public CreakingRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new CreakingModel($$02.bakeLayer(ModelLayers.CREAKING)), 0.6f);
        this.addLayer(new LivingEntityEmissiveLayer<CreakingRenderState, CreakingModel>(this, EYES_TEXTURE_LOCATION, ($$0, $$1) -> 1.0f, CreakingModel::getHeadModelParts, RenderType::eyes, true));
    }

    @Override
    public ResourceLocation getTextureLocation(CreakingRenderState $$0) {
        return TEXTURE_LOCATION;
    }

    @Override
    public CreakingRenderState createRenderState() {
        return new CreakingRenderState();
    }

    @Override
    public void extractRenderState(T $$0, CreakingRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.attackAnimationState.copyFrom(((Creaking)$$0).attackAnimationState);
        $$1.invulnerabilityAnimationState.copyFrom(((Creaking)$$0).invulnerabilityAnimationState);
        $$1.deathAnimationState.copyFrom(((Creaking)$$0).deathAnimationState);
        if (((Creaking)$$0).isTearingDown()) {
            $$1.deathTime = 0.0f;
            $$1.hasRedOverlay = false;
            $$1.eyesGlowing = ((Creaking)$$0).hasGlowingEyes();
        } else {
            $$1.eyesGlowing = ((Creaking)$$0).isActive();
        }
        $$1.canMove = ((Creaking)$$0).canMove();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((CreakingRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

