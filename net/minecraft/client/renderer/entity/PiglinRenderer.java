/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.CrossbowItem;

public class PiglinRenderer
extends HumanoidMobRenderer<AbstractPiglin, PiglinRenderState, PiglinModel> {
    private static final ResourceLocation PIGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png");
    private static final ResourceLocation PIGLIN_BRUTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
    public static final CustomHeadLayer.Transforms PIGLIN_CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(0.0f, 0.0f, 1.0019531f);

    public PiglinRenderer(EntityRendererProvider.Context $$0, ModelLayerLocation $$1, ModelLayerLocation $$2, ModelLayerLocation $$3, ModelLayerLocation $$4, ModelLayerLocation $$5, ModelLayerLocation $$6) {
        super($$0, new PiglinModel($$0.bakeLayer($$1)), new PiglinModel($$0.bakeLayer($$2)), 0.5f, PIGLIN_CUSTOM_HEAD_TRANSFORMS);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel($$0.bakeLayer($$3)), new HumanoidArmorModel($$0.bakeLayer($$4)), new HumanoidArmorModel($$0.bakeLayer($$5)), new HumanoidArmorModel($$0.bakeLayer($$6)), $$0.getEquipmentRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(PiglinRenderState $$0) {
        return $$0.isBrute ? PIGLIN_BRUTE_LOCATION : PIGLIN_LOCATION;
    }

    @Override
    public PiglinRenderState createRenderState() {
        return new PiglinRenderState();
    }

    @Override
    public void extractRenderState(AbstractPiglin $$0, PiglinRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isBrute = $$0.getType() == EntityType.PIGLIN_BRUTE;
        $$1.armPose = $$0.getArmPose();
        $$1.maxCrossbowChageDuration = CrossbowItem.getChargeDuration($$0.getUseItem(), $$0);
        $$1.isConverting = $$0.isConverting();
    }

    @Override
    protected boolean isShaking(PiglinRenderState $$0) {
        return super.isShaking($$0) || $$0.isConverting;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState livingEntityRenderState) {
        return this.isShaking((PiglinRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PiglinRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

