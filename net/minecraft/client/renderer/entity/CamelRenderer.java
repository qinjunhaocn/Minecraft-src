/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer
extends AgeableMobRenderer<Camel, CamelRenderState, CamelModel> {
    private static final ResourceLocation CAMEL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/camel/camel.png");

    public CamelRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new CamelModel($$02.bakeLayer(ModelLayers.CAMEL)), new CamelModel($$02.bakeLayer(ModelLayers.CAMEL_BABY)), 0.7f);
        this.addLayer(new SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.CAMEL_SADDLE, $$0 -> $$0.saddle, new CamelSaddleModel($$02.bakeLayer(ModelLayers.CAMEL_SADDLE)), new CamelSaddleModel($$02.bakeLayer(ModelLayers.CAMEL_BABY_SADDLE))));
    }

    @Override
    public ResourceLocation getTextureLocation(CamelRenderState $$0) {
        return CAMEL_LOCATION;
    }

    @Override
    public CamelRenderState createRenderState() {
        return new CamelRenderState();
    }

    @Override
    public void extractRenderState(Camel $$0, CamelRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.saddle = $$0.getItemBySlot(EquipmentSlot.SADDLE).copy();
        $$1.isRidden = $$0.isVehicle();
        $$1.jumpCooldown = Math.max((float)$$0.getJumpCooldown() - $$2, 0.0f);
        $$1.sitAnimationState.copyFrom($$0.sitAnimationState);
        $$1.sitPoseAnimationState.copyFrom($$0.sitPoseAnimationState);
        $$1.sitUpAnimationState.copyFrom($$0.sitUpAnimationState);
        $$1.idleAnimationState.copyFrom($$0.idleAnimationState);
        $$1.dashAnimationState.copyFrom($$0.dashAnimationState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((CamelRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

