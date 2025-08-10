/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Strider;

public class StriderRenderer
extends AgeableMobRenderer<Strider, StriderRenderState, StriderModel> {
    private static final ResourceLocation STRIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider.png");
    private static final ResourceLocation COLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider_cold.png");
    private static final float SHADOW_RADIUS = 0.5f;

    public StriderRenderer(EntityRendererProvider.Context $$02) {
        super($$02, new StriderModel($$02.bakeLayer(ModelLayers.STRIDER)), new StriderModel($$02.bakeLayer(ModelLayers.STRIDER_BABY)), 0.5f);
        this.addLayer(new SimpleEquipmentLayer<StriderRenderState, StriderModel, StriderModel>(this, $$02.getEquipmentRenderer(), EquipmentClientInfo.LayerType.STRIDER_SADDLE, $$0 -> $$0.saddle, new StriderModel($$02.bakeLayer(ModelLayers.STRIDER_SADDLE)), new StriderModel($$02.bakeLayer(ModelLayers.STRIDER_BABY_SADDLE))));
    }

    @Override
    public ResourceLocation getTextureLocation(StriderRenderState $$0) {
        return $$0.isSuffocating ? COLD_LOCATION : STRIDER_LOCATION;
    }

    @Override
    protected float getShadowRadius(StriderRenderState $$0) {
        float $$1 = super.getShadowRadius($$0);
        if ($$0.isBaby) {
            return $$1 * 0.5f;
        }
        return $$1;
    }

    @Override
    public StriderRenderState createRenderState() {
        return new StriderRenderState();
    }

    @Override
    public void extractRenderState(Strider $$0, StriderRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.saddle = $$0.getItemBySlot(EquipmentSlot.SADDLE).copy();
        $$1.isSuffocating = $$0.isSuffocating();
        $$1.isRidden = $$0.isVehicle();
    }

    @Override
    protected boolean isShaking(StriderRenderState $$0) {
        return super.isShaking($$0) || $$0.isSuffocating;
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((StriderRenderState)livingEntityRenderState);
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState livingEntityRenderState) {
        return this.isShaking((StriderRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((StriderRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
        return this.getShadowRadius((StriderRenderState)entityRenderState);
    }
}

