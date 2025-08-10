/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.ZombifiedPiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombifiedPiglinRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombifiedPiglin;

public class ZombifiedPiglinRenderer
extends HumanoidMobRenderer<ZombifiedPiglin, ZombifiedPiglinRenderState, ZombifiedPiglinModel> {
    private static final ResourceLocation ZOMBIFIED_PIGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

    public ZombifiedPiglinRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, ModelLayerLocation $$3, ModelLayerLocation $$4, ModelLayerLocation $$5, ModelLayerLocation $$6) {
        super($$0, new ZombifiedPiglinModel($$0.bakeLayer($$1)), new ZombifiedPiglinModel($$0.bakeLayer($$2)), 0.5f, PiglinRenderer.PIGLIN_CUSTOM_HEAD_TRANSFORMS);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel($$0.bakeLayer($$3)), new HumanoidArmorModel($$0.bakeLayer($$4)), new HumanoidArmorModel($$0.bakeLayer($$5)), new HumanoidArmorModel($$0.bakeLayer($$6)), $$0.getEquipmentRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(ZombifiedPiglinRenderState $$0) {
        return ZOMBIFIED_PIGLIN_LOCATION;
    }

    @Override
    public ZombifiedPiglinRenderState createRenderState() {
        return new ZombifiedPiglinRenderState();
    }

    @Override
    public void extractRenderState(ZombifiedPiglin $$0, ZombifiedPiglinRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isAggressive = $$0.isAggressive();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ZombifiedPiglinRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

