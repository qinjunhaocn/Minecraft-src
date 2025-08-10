/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.util.Mth;

public class PandaModel
extends QuadrupedModel<PandaRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 23.0f, 4.8f, 2.7f, 3.0f, 49.0f, Set.of((Object)"head"));

    public PandaModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 6).addBox(-6.5f, -5.0f, -4.0f, 13.0f, 10.0f, 9.0f).texOffs(45, 16).addBox("nose", -3.5f, 0.0f, -6.0f, 7.0f, 5.0f, 2.0f).texOffs(52, 25).addBox("left_ear", 3.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f).texOffs(52, 25).addBox("right_ear", -8.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f), PartPose.offset(0.0f, 11.5f, -17.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-9.5f, -13.0f, -6.5f, 19.0f, 26.0f, 13.0f), PartPose.offsetAndRotation(0.0f, 10.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        int $$2 = 9;
        int $$3 = 6;
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(40, 0).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 9.0f, 6.0f);
        $$1.addOrReplaceChild("right_hind_leg", $$4, PartPose.offset(-5.5f, 15.0f, 9.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$4, PartPose.offset(5.5f, 15.0f, 9.0f));
        $$1.addOrReplaceChild("right_front_leg", $$4, PartPose.offset(-5.5f, 15.0f, -9.0f));
        $$1.addOrReplaceChild("left_front_leg", $$4, PartPose.offset(5.5f, 15.0f, -9.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(PandaRenderState $$0) {
        super.setupAnim($$0);
        if ($$0.isUnhappy) {
            this.head.yRot = 0.35f * Mth.sin(0.6f * $$0.ageInTicks);
            this.head.zRot = 0.35f * Mth.sin(0.6f * $$0.ageInTicks);
            this.rightFrontLeg.xRot = -0.75f * Mth.sin(0.3f * $$0.ageInTicks);
            this.leftFrontLeg.xRot = 0.75f * Mth.sin(0.3f * $$0.ageInTicks);
        } else {
            this.head.zRot = 0.0f;
        }
        if ($$0.isSneezing) {
            if ($$0.sneezeTime < 15) {
                this.head.xRot = -0.7853982f * (float)$$0.sneezeTime / 14.0f;
            } else if ($$0.sneezeTime < 20) {
                float $$1 = ($$0.sneezeTime - 15) / 5;
                this.head.xRot = -0.7853982f + 0.7853982f * $$1;
            }
        }
        if ($$0.sitAmount > 0.0f) {
            this.body.xRot = Mth.rotLerpRad($$0.sitAmount, this.body.xRot, 1.7407963f);
            this.head.xRot = Mth.rotLerpRad($$0.sitAmount, this.head.xRot, 1.5707964f);
            this.rightFrontLeg.zRot = -0.27079642f;
            this.leftFrontLeg.zRot = 0.27079642f;
            this.rightHindLeg.zRot = 0.5707964f;
            this.leftHindLeg.zRot = -0.5707964f;
            if ($$0.isEating) {
                this.head.xRot = 1.5707964f + 0.2f * Mth.sin($$0.ageInTicks * 0.6f);
                this.rightFrontLeg.xRot = -0.4f - 0.2f * Mth.sin($$0.ageInTicks * 0.6f);
                this.leftFrontLeg.xRot = -0.4f - 0.2f * Mth.sin($$0.ageInTicks * 0.6f);
            }
            if ($$0.isScared) {
                this.head.xRot = 2.1707964f;
                this.rightFrontLeg.xRot = -0.9f;
                this.leftFrontLeg.xRot = -0.9f;
            }
        } else {
            this.rightHindLeg.zRot = 0.0f;
            this.leftHindLeg.zRot = 0.0f;
            this.rightFrontLeg.zRot = 0.0f;
            this.leftFrontLeg.zRot = 0.0f;
        }
        if ($$0.lieOnBackAmount > 0.0f) {
            this.rightHindLeg.xRot = -0.6f * Mth.sin($$0.ageInTicks * 0.15f);
            this.leftHindLeg.xRot = 0.6f * Mth.sin($$0.ageInTicks * 0.15f);
            this.rightFrontLeg.xRot = 0.3f * Mth.sin($$0.ageInTicks * 0.25f);
            this.leftFrontLeg.xRot = -0.3f * Mth.sin($$0.ageInTicks * 0.25f);
            this.head.xRot = Mth.rotLerpRad($$0.lieOnBackAmount, this.head.xRot, 1.5707964f);
        }
        if ($$0.rollAmount > 0.0f) {
            this.head.xRot = Mth.rotLerpRad($$0.rollAmount, this.head.xRot, 2.0561945f);
            this.rightHindLeg.xRot = -0.5f * Mth.sin($$0.ageInTicks * 0.5f);
            this.leftHindLeg.xRot = 0.5f * Mth.sin($$0.ageInTicks * 0.5f);
            this.rightFrontLeg.xRot = 0.5f * Mth.sin($$0.ageInTicks * 0.5f);
            this.leftFrontLeg.xRot = -0.5f * Mth.sin($$0.ageInTicks * 0.5f);
        }
    }
}

