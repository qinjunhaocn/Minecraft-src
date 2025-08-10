/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class QuadrupedModel<T extends LivingEntityRenderState>
extends EntityModel<T> {
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart leftFrontLeg;

    protected QuadrupedModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.body = $$0.getChild("body");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
    }

    public static MeshDefinition createBodyMesh(int $$0, boolean $$1, boolean $$2, CubeDeformation $$3) {
        MeshDefinition $$4 = new MeshDefinition();
        PartDefinition $$5 = $$4.getRoot();
        $$5.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, $$3), PartPose.offset(0.0f, 18 - $$0, -6.0f));
        $$5.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, $$3), PartPose.offsetAndRotation(0.0f, 17 - $$0, 2.0f, 1.5707964f, 0.0f, 0.0f));
        QuadrupedModel.createLegs($$5, $$1, $$2, $$0, $$3);
        return $$4;
    }

    static void createLegs(PartDefinition $$0, boolean $$1, boolean $$2, int $$3, CubeDeformation $$4) {
        CubeListBuilder $$5 = CubeListBuilder.create().mirror($$2).texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)$$3, 4.0f, $$4);
        CubeListBuilder $$6 = CubeListBuilder.create().mirror($$1).texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)$$3, 4.0f, $$4);
        $$0.addOrReplaceChild("right_hind_leg", $$5, PartPose.offset(-3.0f, 24 - $$3, 7.0f));
        $$0.addOrReplaceChild("left_hind_leg", $$6, PartPose.offset(3.0f, 24 - $$3, 7.0f));
        $$0.addOrReplaceChild("right_front_leg", $$5, PartPose.offset(-3.0f, 24 - $$3, -5.0f));
        $$0.addOrReplaceChild("left_front_leg", $$6, PartPose.offset(3.0f, 24 - $$3, -5.0f));
    }

    @Override
    public void setupAnim(T $$0) {
        super.setupAnim($$0);
        this.head.xRot = ((LivingEntityRenderState)$$0).xRot * ((float)Math.PI / 180);
        this.head.yRot = ((LivingEntityRenderState)$$0).yRot * ((float)Math.PI / 180);
        float $$1 = ((LivingEntityRenderState)$$0).walkAnimationPos;
        float $$2 = ((LivingEntityRenderState)$$0).walkAnimationSpeed;
        this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
    }
}

