/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class SnowGolemModel
extends EntityModel<LivingEntityRenderState> {
    private static final String UPPER_BODY = "upper_body";
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public SnowGolemModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.leftArm = $$0.getChild("left_arm");
        this.rightArm = $$0.getChild("right_arm");
        this.upperBody = $$0.getChild(UPPER_BODY);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float $$2 = 4.0f;
        CubeDeformation $$3 = new CubeDeformation(-0.5f);
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$3), PartPose.offset(0.0f, 4.0f, 0.0f));
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(32, 0).addBox(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, $$3);
        $$1.addOrReplaceChild("left_arm", $$4, PartPose.offsetAndRotation(5.0f, 6.0f, 1.0f, 0.0f, 0.0f, 1.0f));
        $$1.addOrReplaceChild("right_arm", $$4, PartPose.offsetAndRotation(-5.0f, 6.0f, -1.0f, 0.0f, (float)Math.PI, -1.0f));
        $$1.addOrReplaceChild(UPPER_BODY, CubeListBuilder.create().texOffs(0, 16).addBox(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, $$3), PartPose.offset(0.0f, 13.0f, 0.0f));
        $$1.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(0, 36).addBox(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, $$3), PartPose.offset(0.0f, 24.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState $$0) {
        super.setupAnim($$0);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.upperBody.yRot = $$0.yRot * ((float)Math.PI / 180) * 0.25f;
        float $$1 = Mth.sin(this.upperBody.yRot);
        float $$2 = Mth.cos(this.upperBody.yRot);
        this.leftArm.yRot = this.upperBody.yRot;
        this.rightArm.yRot = this.upperBody.yRot + (float)Math.PI;
        this.leftArm.x = $$2 * 5.0f;
        this.leftArm.z = -$$1 * 5.0f;
        this.rightArm.x = -$$2 * 5.0f;
        this.rightArm.z = $$1 * 5.0f;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

