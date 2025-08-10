/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class WolfRenderer
extends AgeableMobRenderer<Wolf, WolfRenderState, WolfModel> {
    public WolfRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new WolfModel($$0.bakeLayer(ModelLayers.WOLF)), new WolfModel($$0.bakeLayer(ModelLayers.WOLF_BABY)), 0.5f);
        this.addLayer(new WolfArmorLayer(this, $$0.getModelSet(), $$0.getEquipmentRenderer()));
        this.addLayer(new WolfCollarLayer(this));
    }

    @Override
    protected int getModelTint(WolfRenderState $$0) {
        float $$1 = $$0.wetShade;
        if ($$1 == 1.0f) {
            return -1;
        }
        return ARGB.colorFromFloat(1.0f, $$1, $$1, $$1);
    }

    @Override
    public ResourceLocation getTextureLocation(WolfRenderState $$0) {
        return $$0.texture;
    }

    @Override
    public WolfRenderState createRenderState() {
        return new WolfRenderState();
    }

    @Override
    public void extractRenderState(Wolf $$0, WolfRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.isAngry = $$0.isAngry();
        $$1.isSitting = $$0.isInSittingPose();
        $$1.tailAngle = $$0.getTailAngle();
        $$1.headRollAngle = $$0.getHeadRollAngle($$2);
        $$1.shakeAnim = $$0.getShakeAnim($$2);
        $$1.texture = $$0.getTexture();
        $$1.wetShade = $$0.getWetShade($$2);
        $$1.collarColor = $$0.isTame() ? $$0.getCollarColor() : null;
        $$1.bodyArmorItem = $$0.getBodyArmorItem().copy();
    }

    @Override
    protected /* synthetic */ int getModelTint(LivingEntityRenderState livingEntityRenderState) {
        return this.getModelTint((WolfRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

