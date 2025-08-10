/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenRenderer
extends MobRenderer<Warden, WardenRenderState, WardenModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png");
    private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_bioluminescent_layer.png");
    private static final ResourceLocation HEART_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_heart.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_1.png");
    private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new WardenModel($$02.bakeLayer(ModelLayers.WARDEN)), 0.9f);
        this.addLayer(new LivingEntityEmissiveLayer<WardenRenderState, WardenModel>(this, BIOLUMINESCENT_LAYER_TEXTURE, ($$0, $$1) -> 1.0f, WardenModel::getBioluminescentLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<WardenRenderState, WardenModel>(this, PULSATING_SPOTS_TEXTURE_1, ($$0, $$1) -> Math.max(0.0f, Mth.cos($$1 * 0.045f) * 0.25f), WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<WardenRenderState, WardenModel>(this, PULSATING_SPOTS_TEXTURE_2, ($$0, $$1) -> Math.max(0.0f, Mth.cos($$1 * 0.045f + (float)Math.PI) * 0.25f), WardenModel::getPulsatingSpotsLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<WardenRenderState, WardenModel>(this, TEXTURE, ($$0, $$1) -> $$0.tendrilAnimation, WardenModel::getTendrilsLayerModelParts, RenderType::entityTranslucentEmissive, false));
        this.addLayer(new LivingEntityEmissiveLayer<WardenRenderState, WardenModel>(this, HEART_TEXTURE, ($$0, $$1) -> $$0.heartAnimation, WardenModel::getHeartLayerModelParts, RenderType::entityTranslucentEmissive, false));
    }

    @Override
    public ResourceLocation getTextureLocation(WardenRenderState $$0) {
        return TEXTURE;
    }

    @Override
    public WardenRenderState createRenderState() {
        return new WardenRenderState();
    }

    @Override
    public void extractRenderState(Warden $$0, WardenRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.tendrilAnimation = $$0.getTendrilAnimation($$2);
        $$1.heartAnimation = $$0.getHeartAnimation($$2);
        $$1.roarAnimationState.copyFrom($$0.roarAnimationState);
        $$1.sniffAnimationState.copyFrom($$0.sniffAnimationState);
        $$1.emergeAnimationState.copyFrom($$0.emergeAnimationState);
        $$1.diggingAnimationState.copyFrom($$0.diggingAnimationState);
        $$1.attackAnimationState.copyFrom($$0.attackAnimationState);
        $$1.sonicBoomAnimationState.copyFrom($$0.sonicBoomAnimationState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((WardenRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

