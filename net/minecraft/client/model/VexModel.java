/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class VexModel
extends EntityModel<VexRenderState>
implements ArmedModel {
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public VexModel(ModelPart $$0) {
        super($$0.getChild("root"), RenderType::entityTranslucent);
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.head = this.root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, -2.5f, 0.0f));
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 20.0f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 4.0f, 2.0f, new CubeDeformation(0.0f)).texOffs(0, 16).addBox(-1.5f, 1.0f, -1.0f, 3.0f, 5.0f, 2.0f, new CubeDeformation(-0.2f)), PartPose.offset(0.0f, 20.0f, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-1.25f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new CubeDeformation(-0.1f)), PartPose.offset(-1.75f, 0.25f, 0.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.75f, -0.5f, -1.0f, 2.0f, 4.0f, 2.0f, new CubeDeformation(-0.1f)), PartPose.offset(1.75f, 0.25f, 0.0f));
        $$3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).mirror().addBox(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)).mirror(false), PartPose.offset(0.5f, 1.0f, 1.0f));
        $$3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0f, 0.0f, 0.0f, 0.0f, 5.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(-0.5f, 1.0f, 1.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(VexRenderState $$0) {
        super.setupAnim($$0);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        float $$1 = Mth.cos($$0.ageInTicks * 5.5f * ((float)Math.PI / 180)) * 0.1f;
        this.rightArm.zRot = 0.62831855f + $$1;
        this.leftArm.zRot = -(0.62831855f + $$1);
        if ($$0.isCharging) {
            this.body.xRot = 0.0f;
            this.setArmsCharging(!$$0.rightHandItem.isEmpty(), !$$0.leftHandItem.isEmpty(), $$1);
        } else {
            this.body.xRot = 0.15707964f;
        }
        this.leftWing.yRot = 1.0995574f + Mth.cos($$0.ageInTicks * 45.836624f * ((float)Math.PI / 180)) * ((float)Math.PI / 180) * 16.2f;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.leftWing.xRot = 0.47123888f;
        this.leftWing.zRot = -0.47123888f;
        this.rightWing.xRot = 0.47123888f;
        this.rightWing.zRot = 0.47123888f;
    }

    private void setArmsCharging(boolean $$0, boolean $$1, float $$2) {
        if (!$$0 && !$$1) {
            this.rightArm.xRot = -1.2217305f;
            this.rightArm.yRot = 0.2617994f;
            this.rightArm.zRot = -0.47123888f - $$2;
            this.leftArm.xRot = -1.2217305f;
            this.leftArm.yRot = -0.2617994f;
            this.leftArm.zRot = 0.47123888f + $$2;
            return;
        }
        if ($$0) {
            this.rightArm.xRot = 3.6651914f;
            this.rightArm.yRot = 0.2617994f;
            this.rightArm.zRot = -0.47123888f - $$2;
        }
        if ($$1) {
            this.leftArm.xRot = 3.6651914f;
            this.leftArm.yRot = -0.2617994f;
            this.leftArm.zRot = 0.47123888f + $$2;
        }
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        boolean $$2 = $$0 == HumanoidArm.RIGHT;
        ModelPart $$3 = $$2 ? this.rightArm : this.leftArm;
        this.root.translateAndRotate($$1);
        this.body.translateAndRotate($$1);
        $$3.translateAndRotate($$1);
        $$1.scale(0.55f, 0.55f, 0.55f);
        this.offsetStackPosition($$1, $$2);
    }

    private void offsetStackPosition(PoseStack $$0, boolean $$1) {
        if ($$1) {
            $$0.translate(0.046875, -0.15625, 0.078125);
        } else {
            $$0.translate(-0.046875, -0.15625, 0.078125);
        }
    }
}

