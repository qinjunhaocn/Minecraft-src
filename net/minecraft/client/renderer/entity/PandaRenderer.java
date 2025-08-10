/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;
import org.joml.Quaternionfc;

public class PandaRenderer
extends AgeableMobRenderer<Panda, PandaRenderState, PandaModel> {
    private static final Map<Panda.Gene, ResourceLocation> TEXTURES = Maps.newEnumMap(Map.of((Object)Panda.Gene.NORMAL, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/panda.png"), (Object)Panda.Gene.LAZY, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/lazy_panda.png"), (Object)Panda.Gene.WORRIED, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/worried_panda.png"), (Object)Panda.Gene.PLAYFUL, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/playful_panda.png"), (Object)Panda.Gene.BROWN, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/brown_panda.png"), (Object)Panda.Gene.WEAK, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/weak_panda.png"), (Object)Panda.Gene.AGGRESSIVE, (Object)ResourceLocation.withDefaultNamespace("textures/entity/panda/aggressive_panda.png")));

    public PandaRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new PandaModel($$0.bakeLayer(ModelLayers.PANDA)), new PandaModel($$0.bakeLayer(ModelLayers.PANDA_BABY)), 0.9f);
        this.addLayer(new PandaHoldsItemLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PandaRenderState $$0) {
        return TEXTURES.getOrDefault($$0.variant, TEXTURES.get(Panda.Gene.NORMAL));
    }

    @Override
    public PandaRenderState createRenderState() {
        return new PandaRenderState();
    }

    @Override
    public void extractRenderState(Panda $$0, PandaRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HoldingEntityRenderState.extractHoldingEntityRenderState($$0, $$1, this.itemModelResolver);
        $$1.variant = $$0.getVariant();
        $$1.isUnhappy = $$0.getUnhappyCounter() > 0;
        $$1.isSneezing = $$0.isSneezing();
        $$1.sneezeTime = $$0.getSneezeCounter();
        $$1.isEating = $$0.isEating();
        $$1.isScared = $$0.isScared();
        $$1.isSitting = $$0.isSitting();
        $$1.sitAmount = $$0.getSitAmount($$2);
        $$1.lieOnBackAmount = $$0.getLieOnBackAmount($$2);
        $$1.rollAmount = $$0.isBaby() ? 0.0f : $$0.getRollAmount($$2);
        $$1.rollTime = $$0.rollCounter > 0 ? (float)$$0.rollCounter + $$2 : 0.0f;
    }

    @Override
    protected void setupRotations(PandaRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        float $$26;
        float $$24;
        super.setupRotations($$0, $$1, $$2, $$3);
        if ($$0.rollTime > 0.0f) {
            float $$8;
            float $$4 = Mth.frac($$0.rollTime);
            int $$5 = Mth.floor($$0.rollTime);
            int $$6 = $$5 + 1;
            float $$7 = 7.0f;
            float f = $$8 = $$0.isBaby ? 0.3f : 0.8f;
            if ((float)$$5 < 8.0f) {
                float $$9 = 90.0f * (float)$$5 / 7.0f;
                float $$10 = 90.0f * (float)$$6 / 7.0f;
                float $$11 = this.getAngle($$9, $$10, $$6, $$4, 8.0f);
                $$1.translate(0.0f, ($$8 + 0.2f) * ($$11 / 90.0f), 0.0f);
                $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-$$11));
            } else if ((float)$$5 < 16.0f) {
                float $$12 = ((float)$$5 - 8.0f) / 7.0f;
                float $$13 = 90.0f + 90.0f * $$12;
                float $$14 = 90.0f + 90.0f * ((float)$$6 - 8.0f) / 7.0f;
                float $$15 = this.getAngle($$13, $$14, $$6, $$4, 16.0f);
                $$1.translate(0.0f, $$8 + 0.2f + ($$8 - 0.2f) * ($$15 - 90.0f) / 90.0f, 0.0f);
                $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-$$15));
            } else if ((float)$$5 < 24.0f) {
                float $$16 = ((float)$$5 - 16.0f) / 7.0f;
                float $$17 = 180.0f + 90.0f * $$16;
                float $$18 = 180.0f + 90.0f * ((float)$$6 - 16.0f) / 7.0f;
                float $$19 = this.getAngle($$17, $$18, $$6, $$4, 24.0f);
                $$1.translate(0.0f, $$8 + $$8 * (270.0f - $$19) / 90.0f, 0.0f);
                $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-$$19));
            } else if ($$5 < 32) {
                float $$20 = ((float)$$5 - 24.0f) / 7.0f;
                float $$21 = 270.0f + 90.0f * $$20;
                float $$22 = 270.0f + 90.0f * ((float)$$6 - 24.0f) / 7.0f;
                float $$23 = this.getAngle($$21, $$22, $$6, $$4, 32.0f);
                $$1.translate(0.0f, $$8 * ((360.0f - $$23) / 90.0f), 0.0f);
                $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-$$23));
            }
        }
        if (($$24 = $$0.sitAmount) > 0.0f) {
            $$1.translate(0.0f, 0.8f * $$24, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.lerp($$24, $$0.xRot, $$0.xRot + 90.0f)));
            $$1.translate(0.0f, -1.0f * $$24, 0.0f);
            if ($$0.isScared) {
                float $$25 = (float)(Math.cos($$0.ageInTicks * 1.25f) * Math.PI * (double)0.05f);
                $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$25));
                if ($$0.isBaby) {
                    $$1.translate(0.0f, 0.8f, 0.55f);
                }
            }
        }
        if (($$26 = $$0.lieOnBackAmount) > 0.0f) {
            float $$27 = $$0.isBaby ? 0.5f : 1.3f;
            $$1.translate(0.0f, $$27 * $$26, 0.0f);
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.lerp($$26, $$0.xRot, $$0.xRot + 180.0f)));
        }
    }

    private float getAngle(float $$0, float $$1, int $$2, float $$3, float $$4) {
        if ((float)$$2 < $$4) {
            return Mth.lerp($$3, $$0, $$1);
        }
        return $$0;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((PandaRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

