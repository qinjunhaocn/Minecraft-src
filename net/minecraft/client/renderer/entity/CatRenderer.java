/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import org.joml.Quaternionfc;

public class CatRenderer
extends AgeableMobRenderer<Cat, CatRenderState, CatModel> {
    public CatRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CatModel($$0.bakeLayer(ModelLayers.CAT)), new CatModel($$0.bakeLayer(ModelLayers.CAT_BABY)), 0.4f);
        this.addLayer(new CatCollarLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CatRenderState $$0) {
        return $$0.texture;
    }

    @Override
    public CatRenderState createRenderState() {
        return new CatRenderState();
    }

    @Override
    public void extractRenderState(Cat $$0, CatRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.texture = $$0.getVariant().value().assetInfo().texturePath();
        $$1.isCrouching = $$0.isCrouching();
        $$1.isSprinting = $$0.isSprinting();
        $$1.isSitting = $$0.isInSittingPose();
        $$1.lieDownAmount = $$0.getLieDownAmount($$2);
        $$1.lieDownAmountTail = $$0.getLieDownAmountTail($$2);
        $$1.relaxStateOneAmount = $$0.getRelaxStateOneAmount($$2);
        $$1.isLyingOnTopOfSleepingPlayer = $$0.isLyingOnTopOfSleepingPlayer();
        $$1.collarColor = $$0.isTame() ? $$0.getCollarColor() : null;
    }

    @Override
    protected void setupRotations(CatRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        float $$4 = $$0.lieDownAmount;
        if ($$4 > 0.0f) {
            $$1.translate(0.4f * $$4, 0.15f * $$4, 0.1f * $$4);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.rotLerp($$4, 0.0f, 90.0f)));
            if ($$0.isLyingOnTopOfSleepingPlayer) {
                $$1.translate(0.15f * $$4, 0.0f, 0.0f);
            }
        }
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((CatRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

