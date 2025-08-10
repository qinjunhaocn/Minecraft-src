/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.util.Mth;

public class RavagerModel
extends EntityModel<RavagerRenderState> {
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    public RavagerModel(ModelPart $$0) {
        super($$0);
        this.neck = $$0.getChild("neck");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("mouth");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 16;
        PartDefinition $$3 = $$1.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f), PartPose.offset(0.0f, -7.0f, 5.5f));
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).texOffs(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), PartPose.offset(0.0f, 16.0f, -17.0f));
        $$4.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), PartPose.offsetAndRotation(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(74, 55).mirror().addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), PartPose.offsetAndRotation(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), PartPose.offset(0.0f, -2.0f, 2.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f).texOffs(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f), PartPose.offsetAndRotation(0.0f, 1.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(-8.0f, -13.0f, 18.0f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(8.0f, -13.0f, 18.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(-8.0f, -13.0f, -5.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), PartPose.offset(8.0f, -13.0f, -5.0f));
        return LayerDefinition.create($$0, 128, 128);
    }

    @Override
    public void setupAnim(RavagerRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.stunnedTicksRemaining;
        float $$2 = $$0.attackTicksRemaining;
        int $$3 = 10;
        if ($$2 > 0.0f) {
            float $$4 = Mth.triangleWave($$2, 10.0f);
            float $$5 = (1.0f + $$4) * 0.5f;
            float $$6 = $$5 * $$5 * $$5 * 12.0f;
            float $$7 = $$6 * Mth.sin(this.neck.xRot);
            this.neck.z = -6.5f + $$6;
            this.neck.y = -7.0f - $$7;
            this.mouth.xRot = $$2 > 5.0f ? Mth.sin((-4.0f + $$2) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * Mth.sin((float)Math.PI * $$2 / 10.0f);
        } else {
            float $$8 = -1.0f;
            float $$9 = -1.0f * Mth.sin(this.neck.xRot);
            this.neck.x = 0.0f;
            this.neck.y = -7.0f - $$9;
            this.neck.z = 5.5f;
            boolean $$10 = $$1 > 0.0f;
            this.neck.xRot = $$10 ? 0.21991149f : 0.0f;
            this.mouth.xRot = (float)Math.PI * ($$10 ? 0.05f : 0.01f);
            if ($$10) {
                double $$11 = (double)$$1 / 40.0;
                this.neck.x = (float)Math.sin($$11 * 10.0) * 3.0f;
            } else if ((double)$$0.roarAnimation > 0.0) {
                float $$12 = Mth.sin($$0.roarAnimation * (float)Math.PI * 0.25f);
                this.mouth.xRot = 1.5707964f * $$12;
            }
        }
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        float $$13 = $$0.walkAnimationPos;
        float $$14 = 0.4f * $$0.walkAnimationSpeed;
        this.rightHindLeg.xRot = Mth.cos($$13 * 0.6662f) * $$14;
        this.leftHindLeg.xRot = Mth.cos($$13 * 0.6662f + (float)Math.PI) * $$14;
        this.rightFrontLeg.xRot = Mth.cos($$13 * 0.6662f + (float)Math.PI) * $$14;
        this.leftFrontLeg.xRot = Mth.cos($$13 * 0.6662f) * $$14;
    }
}

