/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HappyGhastHarnessModel;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RopesLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.phys.AABB;

public class HappyGhastRenderer
extends AgeableMobRenderer<HappyGhast, HappyGhastRenderState, HappyGhastModel> {
    private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast.png");
    private static final ResourceLocation GHAST_BABY_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_baby.png");
    private static final ResourceLocation GHAST_ROPES = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_ropes.png");

    public HappyGhastRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new HappyGhastModel($$02.bakeLayer(ModelLayers.HAPPY_GHAST)), new HappyGhastModel($$02.bakeLayer(ModelLayers.HAPPY_GHAST_BABY)), 2.0f);
        this.addLayer(new SimpleEquipmentLayer<HappyGhastRenderState, HappyGhastModel, HappyGhastHarnessModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, $$0 -> $$0.bodyItem, new HappyGhastHarnessModel($$02.bakeLayer(ModelLayers.HAPPY_GHAST_HARNESS)), new HappyGhastHarnessModel($$02.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_HARNESS))));
        this.addLayer(new RopesLayer<HappyGhastModel>(this, $$02.getModelSet(), GHAST_ROPES));
    }

    @Override
    public ResourceLocation getTextureLocation(HappyGhastRenderState $$0) {
        if ($$0.isBaby) {
            return GHAST_BABY_LOCATION;
        }
        return GHAST_LOCATION;
    }

    @Override
    public HappyGhastRenderState createRenderState() {
        return new HappyGhastRenderState();
    }

    @Override
    protected AABB getBoundingBoxForCulling(HappyGhast $$0) {
        AABB $$1 = super.getBoundingBoxForCulling($$0);
        float $$2 = $$0.getBbHeight();
        return $$1.setMinY($$1.minY - (double)($$2 / 2.0f));
    }

    @Override
    public void extractRenderState(HappyGhast $$0, HappyGhastRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.bodyItem = $$0.getItemBySlot(EquipmentSlot.BODY).copy();
        $$1.isRidden = $$0.isVehicle();
        $$1.isLeashHolder = $$0.isLeashHolder();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((HappyGhastRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

