/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;

public class ArmorStandArmorModel
extends HumanoidModel<ArmorStandRenderState> {
    public ArmorStandArmorModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = HumanoidModel.createMesh($$0, 0.0f);
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.offset(0.0f, 1.0f, 0.0f));
        $$3.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0.extend(0.5f)), PartPose.ZERO);
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(-0.1f)), PartPose.offset(-1.9f, 11.0f, 0.0f));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(-0.1f)), PartPose.offset(1.9f, 11.0f, 0.0f));
        return LayerDefinition.create($$1, 64, 32);
    }

    @Override
    public void setupAnim(ArmorStandRenderState $$0) {
        super.setupAnim($$0);
        this.head.xRot = (float)Math.PI / 180 * $$0.headPose.x();
        this.head.yRot = (float)Math.PI / 180 * $$0.headPose.y();
        this.head.zRot = (float)Math.PI / 180 * $$0.headPose.z();
        this.body.xRot = (float)Math.PI / 180 * $$0.bodyPose.x();
        this.body.yRot = (float)Math.PI / 180 * $$0.bodyPose.y();
        this.body.zRot = (float)Math.PI / 180 * $$0.bodyPose.z();
        this.leftArm.xRot = (float)Math.PI / 180 * $$0.leftArmPose.x();
        this.leftArm.yRot = (float)Math.PI / 180 * $$0.leftArmPose.y();
        this.leftArm.zRot = (float)Math.PI / 180 * $$0.leftArmPose.z();
        this.rightArm.xRot = (float)Math.PI / 180 * $$0.rightArmPose.x();
        this.rightArm.yRot = (float)Math.PI / 180 * $$0.rightArmPose.y();
        this.rightArm.zRot = (float)Math.PI / 180 * $$0.rightArmPose.z();
        this.leftLeg.xRot = (float)Math.PI / 180 * $$0.leftLegPose.x();
        this.leftLeg.yRot = (float)Math.PI / 180 * $$0.leftLegPose.y();
        this.leftLeg.zRot = (float)Math.PI / 180 * $$0.leftLegPose.z();
        this.rightLeg.xRot = (float)Math.PI / 180 * $$0.rightLegPose.x();
        this.rightLeg.yRot = (float)Math.PI / 180 * $$0.rightLegPose.y();
        this.rightLeg.zRot = (float)Math.PI / 180 * $$0.rightLegPose.z();
    }
}

