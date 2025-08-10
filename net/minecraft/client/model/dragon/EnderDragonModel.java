/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.dragon;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory;

public class EnderDragonModel
extends EntityModel<EnderDragonRenderState> {
    private static final int NECK_PART_COUNT = 5;
    private static final int TAIL_PART_COUNT = 12;
    private final ModelPart head;
    private final ModelPart[] neckParts = new ModelPart[5];
    private final ModelPart[] tailParts = new ModelPart[12];
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftFrontLegTip;
    private final ModelPart leftFrontFoot;
    private final ModelPart leftRearLeg;
    private final ModelPart leftRearLegTip;
    private final ModelPart leftRearFoot;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightFrontLegTip;
    private final ModelPart rightFrontFoot;
    private final ModelPart rightRearLeg;
    private final ModelPart rightRearLegTip;
    private final ModelPart rightRearFoot;

    private static String neckName(int $$0) {
        return "neck" + $$0;
    }

    private static String tailName(int $$0) {
        return "tail" + $$0;
    }

    public EnderDragonModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.jaw = this.head.getChild("jaw");
        for (int $$1 = 0; $$1 < this.neckParts.length; ++$$1) {
            this.neckParts[$$1] = $$0.getChild(EnderDragonModel.neckName($$1));
        }
        for (int $$2 = 0; $$2 < this.tailParts.length; ++$$2) {
            this.tailParts[$$2] = $$0.getChild(EnderDragonModel.tailName($$2));
        }
        this.body = $$0.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
        this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
        this.leftRearLeg = this.body.getChild("left_hind_leg");
        this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
        this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
        this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
        this.rightRearLeg = this.body.getChild("right_hind_leg");
        this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
        this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = -16.0f;
        PartDefinition $$3 = $$1.addOrReplaceChild("head", CubeListBuilder.create().addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirror().addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirror().addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), PartPose.offset(0.0f, 20.0f, -62.0f));
        $$3.addOrReplaceChild("jaw", CubeListBuilder.create().addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 176, 65), PartPose.offset(0.0f, 4.0f, -8.0f));
        CubeListBuilder $$4 = CubeListBuilder.create().addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 192, 104).addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 48, 0);
        for (int $$5 = 0; $$5 < 5; ++$$5) {
            $$1.addOrReplaceChild(EnderDragonModel.neckName($$5), $$4, PartPose.offset(0.0f, 20.0f, -12.0f - (float)$$5 * 10.0f));
        }
        for (int $$6 = 0; $$6 < 12; ++$$6) {
            $$1.addOrReplaceChild(EnderDragonModel.tailName($$6), $$4, PartPose.offset(0.0f, 10.0f, 60.0f + (float)$$6 * 10.0f));
        }
        PartDefinition $$7 = $$1.addOrReplaceChild("body", CubeListBuilder.create().addBox("body", -12.0f, 1.0f, -16.0f, 24, 24, 64, 0, 0).addBox("scale", -1.0f, -5.0f, -10.0f, 2, 6, 12, 220, 53).addBox("scale", -1.0f, -5.0f, 10.0f, 2, 6, 12, 220, 53).addBox("scale", -1.0f, -5.0f, 30.0f, 2, 6, 12, 220, 53), PartPose.offset(0.0f, 3.0f, 8.0f));
        PartDefinition $$8 = $$7.addOrReplaceChild("left_wing", CubeListBuilder.create().mirror().addBox("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), PartPose.offset(12.0f, 2.0f, -6.0f));
        $$8.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().mirror().addBox("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), PartPose.offset(56.0f, 0.0f, 0.0f));
        PartDefinition $$9 = $$7.addOrReplaceChild("left_front_leg", CubeListBuilder.create().addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), PartPose.offsetAndRotation(12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        PartDefinition $$10 = $$9.addOrReplaceChild("left_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), PartPose.offsetAndRotation(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        $$10.addOrReplaceChild("left_front_foot", CubeListBuilder.create().addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), PartPose.offsetAndRotation(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        PartDefinition $$11 = $$7.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), PartPose.offsetAndRotation(16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        PartDefinition $$12 = $$11.addOrReplaceChild("left_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), PartPose.offsetAndRotation(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        $$12.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), PartPose.offsetAndRotation(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        PartDefinition $$13 = $$7.addOrReplaceChild("right_wing", CubeListBuilder.create().addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), PartPose.offset(-12.0f, 2.0f, -6.0f));
        $$13.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), PartPose.offset(-56.0f, 0.0f, 0.0f));
        PartDefinition $$14 = $$7.addOrReplaceChild("right_front_leg", CubeListBuilder.create().addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), PartPose.offsetAndRotation(-12.0f, 17.0f, -6.0f, 1.3f, 0.0f, 0.0f));
        PartDefinition $$15 = $$14.addOrReplaceChild("right_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), PartPose.offsetAndRotation(0.0f, 20.0f, -1.0f, -0.5f, 0.0f, 0.0f));
        $$15.addOrReplaceChild("right_front_foot", CubeListBuilder.create().addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), PartPose.offsetAndRotation(0.0f, 23.0f, 0.0f, 0.75f, 0.0f, 0.0f));
        PartDefinition $$16 = $$7.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), PartPose.offsetAndRotation(-16.0f, 13.0f, 34.0f, 1.0f, 0.0f, 0.0f));
        PartDefinition $$17 = $$16.addOrReplaceChild("right_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), PartPose.offsetAndRotation(0.0f, 32.0f, -4.0f, 0.5f, 0.0f, 0.0f));
        $$17.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), PartPose.offsetAndRotation(0.0f, 31.0f, 4.0f, 0.75f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 256, 256);
    }

    @Override
    public void setupAnim(EnderDragonRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.flapTime * ((float)Math.PI * 2);
        this.jaw.xRot = (Mth.sin($$1) + 1.0f) * 0.2f;
        float $$2 = Mth.sin($$1 - 1.0f) + 1.0f;
        $$2 = ($$2 * $$2 + $$2 * 2.0f) * 0.05f;
        this.root.y = ($$2 - 2.0f) * 16.0f;
        this.root.z = -48.0f;
        this.root.xRot = $$2 * 2.0f * ((float)Math.PI / 180);
        float $$3 = this.neckParts[0].x;
        float $$4 = this.neckParts[0].y;
        float $$5 = this.neckParts[0].z;
        float $$6 = 1.5f;
        DragonFlightHistory.Sample $$7 = $$0.getHistoricalPos(6);
        float $$8 = Mth.wrapDegrees($$0.getHistoricalPos(5).yRot() - $$0.getHistoricalPos(10).yRot());
        float $$9 = Mth.wrapDegrees($$0.getHistoricalPos(5).yRot() + $$8 / 2.0f);
        for (int $$10 = 0; $$10 < 5; ++$$10) {
            ModelPart $$11 = this.neckParts[$$10];
            DragonFlightHistory.Sample $$12 = $$0.getHistoricalPos(5 - $$10);
            float $$13 = Mth.cos((float)$$10 * 0.45f + $$1) * 0.15f;
            $$11.yRot = Mth.wrapDegrees($$12.yRot() - $$7.yRot()) * ((float)Math.PI / 180) * 1.5f;
            $$11.xRot = $$13 + $$0.getHeadPartYOffset($$10, $$7, $$12) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            $$11.zRot = -Mth.wrapDegrees($$12.yRot() - $$9) * ((float)Math.PI / 180) * 1.5f;
            $$11.y = $$4;
            $$11.z = $$5;
            $$11.x = $$3;
            $$3 -= Mth.sin($$11.yRot) * Mth.cos($$11.xRot) * 10.0f;
            $$4 += Mth.sin($$11.xRot) * 10.0f;
            $$5 -= Mth.cos($$11.yRot) * Mth.cos($$11.xRot) * 10.0f;
        }
        this.head.y = $$4;
        this.head.z = $$5;
        this.head.x = $$3;
        DragonFlightHistory.Sample $$14 = $$0.getHistoricalPos(0);
        this.head.yRot = Mth.wrapDegrees($$14.yRot() - $$7.yRot()) * ((float)Math.PI / 180);
        this.head.xRot = Mth.wrapDegrees($$0.getHeadPartYOffset(6, $$7, $$14)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
        this.head.zRot = -Mth.wrapDegrees($$14.yRot() - $$9) * ((float)Math.PI / 180);
        this.body.zRot = -$$8 * 1.5f * ((float)Math.PI / 180);
        this.leftWing.xRot = 0.125f - Mth.cos($$1) * 0.2f;
        this.leftWing.yRot = -0.25f;
        this.leftWing.zRot = -(Mth.sin($$1) + 0.125f) * 0.8f;
        this.leftWingTip.zRot = (Mth.sin($$1 + 2.0f) + 0.5f) * 0.75f;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.zRot = -this.leftWing.zRot;
        this.rightWingTip.zRot = -this.leftWingTip.zRot;
        this.poseLimbs($$2, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot);
        this.poseLimbs($$2, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot);
        float $$15 = 0.0f;
        $$4 = this.tailParts[0].y;
        $$5 = this.tailParts[0].z;
        $$3 = this.tailParts[0].x;
        $$7 = $$0.getHistoricalPos(11);
        for (int $$16 = 0; $$16 < 12; ++$$16) {
            DragonFlightHistory.Sample $$17 = $$0.getHistoricalPos(12 + $$16);
            ModelPart $$18 = this.tailParts[$$16];
            $$18.yRot = (Mth.wrapDegrees($$17.yRot() - $$7.yRot()) * 1.5f + 180.0f) * ((float)Math.PI / 180);
            $$18.xRot = ($$15 += Mth.sin((float)$$16 * 0.45f + $$1) * 0.05f) + (float)($$17.y() - $$7.y()) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            $$18.zRot = Mth.wrapDegrees($$17.yRot() - $$9) * ((float)Math.PI / 180) * 1.5f;
            $$18.y = $$4;
            $$18.z = $$5;
            $$18.x = $$3;
            $$4 += Mth.sin($$18.xRot) * 10.0f;
            $$5 -= Mth.cos($$18.yRot) * Mth.cos($$18.xRot) * 10.0f;
            $$3 -= Mth.sin($$18.yRot) * Mth.cos($$18.xRot) * 10.0f;
        }
    }

    private void poseLimbs(float $$0, ModelPart $$1, ModelPart $$2, ModelPart $$3, ModelPart $$4, ModelPart $$5, ModelPart $$6) {
        $$4.xRot = 1.0f + $$0 * 0.1f;
        $$5.xRot = 0.5f + $$0 * 0.1f;
        $$6.xRot = 0.75f + $$0 * 0.1f;
        $$1.xRot = 1.3f + $$0 * 0.1f;
        $$2.xRot = -0.5f - $$0 * 0.1f;
        $$3.xRot = 0.75f + $$0 * 0.1f;
    }
}

