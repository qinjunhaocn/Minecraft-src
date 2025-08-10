/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.util.Mth;

public class WolfModel
extends EntityModel<WolfRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(Set.of((Object)"head"));
    private static final String REAL_HEAD = "real_head";
    private static final String UPPER_BODY = "upper_body";
    private static final String REAL_TAIL = "real_tail";
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart upperBody;
    private static final int LEG_SIZE = 8;

    public WolfModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.realHead = this.head.getChild(REAL_HEAD);
        this.body = $$0.getChild("body");
        this.upperBody = $$0.getChild(UPPER_BODY);
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.tail = $$0.getChild("tail");
        this.realTail = this.tail.getChild(REAL_TAIL);
    }

    public static MeshDefinition createMeshDefinition(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        float $$3 = 13.5f;
        PartDefinition $$4 = $$2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1.0f, 13.5f, -7.0f));
        $$4.addOrReplaceChild(REAL_HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, $$0).texOffs(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, $$0).texOffs(16, 14).addBox(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, $$0).texOffs(0, 10).addBox(-0.5f, -0.001f, -5.0f, 3.0f, 3.0f, 4.0f, $$0), PartPose.ZERO);
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f, $$0), PartPose.offsetAndRotation(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$2.addOrReplaceChild(UPPER_BODY, CubeListBuilder.create().texOffs(21, 0).addBox(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f, $$0), PartPose.offsetAndRotation(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, $$0);
        CubeListBuilder $$6 = CubeListBuilder.create().mirror().texOffs(0, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, $$0);
        $$2.addOrReplaceChild("right_hind_leg", $$6, PartPose.offset(-2.5f, 16.0f, 7.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$5, PartPose.offset(0.5f, 16.0f, 7.0f));
        $$2.addOrReplaceChild("right_front_leg", $$6, PartPose.offset(-2.5f, 16.0f, -4.0f));
        $$2.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(0.5f, 16.0f, -4.0f));
        PartDefinition $$7 = $$2.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        $$7.addOrReplaceChild(REAL_TAIL, CubeListBuilder.create().texOffs(9, 18).addBox(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, $$0), PartPose.ZERO);
        return $$1;
    }

    @Override
    public void setupAnim(WolfRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.walkAnimationPos;
        float $$2 = $$0.walkAnimationSpeed;
        this.tail.yRot = $$0.isAngry ? 0.0f : Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        if ($$0.isSitting) {
            float $$3 = $$0.ageScale;
            this.upperBody.y += 2.0f * $$3;
            this.upperBody.xRot = 1.2566371f;
            this.upperBody.yRot = 0.0f;
            this.body.y += 4.0f * $$3;
            this.body.z -= 2.0f * $$3;
            this.body.xRot = 0.7853982f;
            this.tail.y += 9.0f * $$3;
            this.tail.z -= 2.0f * $$3;
            this.rightHindLeg.y += 6.7f * $$3;
            this.rightHindLeg.z -= 5.0f * $$3;
            this.rightHindLeg.xRot = 4.712389f;
            this.leftHindLeg.y += 6.7f * $$3;
            this.leftHindLeg.z -= 5.0f * $$3;
            this.leftHindLeg.xRot = 4.712389f;
            this.rightFrontLeg.xRot = 5.811947f;
            this.rightFrontLeg.x += 0.01f * $$3;
            this.rightFrontLeg.y += 1.0f * $$3;
            this.leftFrontLeg.xRot = 5.811947f;
            this.leftFrontLeg.x -= 0.01f * $$3;
            this.leftFrontLeg.y += 1.0f * $$3;
        } else {
            this.rightHindLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
            this.leftHindLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
            this.rightFrontLeg.xRot = Mth.cos($$1 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
            this.leftFrontLeg.xRot = Mth.cos($$1 * 0.6662f) * 1.4f * $$2;
        }
        this.realHead.zRot = $$0.headRollAngle + $$0.getBodyRollAngle(0.0f);
        this.upperBody.zRot = $$0.getBodyRollAngle(-0.08f);
        this.body.zRot = $$0.getBodyRollAngle(-0.16f);
        this.realTail.zRot = $$0.getBodyRollAngle(-0.2f);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        this.tail.xRot = $$0.tailAngle;
    }
}

