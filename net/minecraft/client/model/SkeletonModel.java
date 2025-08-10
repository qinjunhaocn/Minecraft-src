/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class SkeletonModel<S extends SkeletonRenderState>
extends HumanoidModel<S> {
    public SkeletonModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition $$1 = $$0.getRoot();
        SkeletonModel.createDefaultSkeletonMesh($$1);
        return LayerDefinition.create($$0, 64, 32);
    }

    protected static void createDefaultSkeletonMesh(PartDefinition $$0) {
        $$0.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(-5.0f, 2.0f, 0.0f));
        $$0.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(5.0f, 2.0f, 0.0f));
        $$0.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(-2.0f, 12.0f, 0.0f));
        $$0.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), PartPose.offset(2.0f, 12.0f, 0.0f));
    }

    @Override
    public void setupAnim(S $$0) {
        super.setupAnim($$0);
        if (((SkeletonRenderState)$$0).isAggressive && !((SkeletonRenderState)$$0).isHoldingBow) {
            float $$1 = ((SkeletonRenderState)$$0).attackTime;
            float $$2 = Mth.sin($$1 * (float)Math.PI);
            float $$3 = Mth.sin((1.0f - (1.0f - $$1) * (1.0f - $$1)) * (float)Math.PI);
            this.rightArm.zRot = 0.0f;
            this.leftArm.zRot = 0.0f;
            this.rightArm.yRot = -(0.1f - $$2 * 0.6f);
            this.leftArm.yRot = 0.1f - $$2 * 0.6f;
            this.rightArm.xRot = -1.5707964f;
            this.leftArm.xRot = -1.5707964f;
            this.rightArm.xRot -= $$2 * 1.2f - $$3 * 0.4f;
            this.leftArm.xRot -= $$2 * 1.2f - $$3 * 0.4f;
            AnimationUtils.bobArms(this.rightArm, this.leftArm, ((SkeletonRenderState)$$0).ageInTicks);
        }
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        this.root().translateAndRotate($$1);
        float $$2 = $$0 == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        ModelPart $$3 = this.getArm($$0);
        $$3.x += $$2;
        $$3.translateAndRotate($$1);
        $$3.x -= $$2;
    }
}

