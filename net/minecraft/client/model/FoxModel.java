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
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.util.Mth;

public class FoxModel
extends EntityModel<FoxRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 8.0f, 3.35f, Set.of((Object)"head"));
    public final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private static final int LEG_SIZE = 6;
    private static final float HEAD_HEIGHT = 16.5f;
    private static final float LEG_POS = 17.5f;
    private float legMotionPos;

    public FoxModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.body = $$0.getChild("body");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), PartPose.offset(-1.0f, 16.5f, -3.0f));
        $$2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), PartPose.ZERO);
        $$2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), PartPose.ZERO);
        PartDefinition $$3 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        CubeDeformation $$4 = new CubeDeformation(0.001f);
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(4, 24).addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, $$4);
        CubeListBuilder $$6 = CubeListBuilder.create().texOffs(13, 24).addBox(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, $$4);
        $$1.addOrReplaceChild("right_hind_leg", $$6, PartPose.offset(-5.0f, 17.5f, 7.0f));
        $$1.addOrReplaceChild("left_hind_leg", $$5, PartPose.offset(-1.0f, 17.5f, 7.0f));
        $$1.addOrReplaceChild("right_front_leg", $$6, PartPose.offset(-5.0f, 17.5f, 0.0f));
        $$1.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(-1.0f, 17.5f, 0.0f));
        $$3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), PartPose.offsetAndRotation(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 48, 32);
    }

    @Override
    public void setupAnim(FoxRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.walkAnimationSpeed;
        float $$2 = $$0.walkAnimationPos;
        this.rightHindLeg.xRot = Mth.cos($$2 * 0.6662f) * 1.4f * $$1;
        this.leftHindLeg.xRot = Mth.cos($$2 * 0.6662f + (float)Math.PI) * 1.4f * $$1;
        this.rightFrontLeg.xRot = Mth.cos($$2 * 0.6662f + (float)Math.PI) * 1.4f * $$1;
        this.leftFrontLeg.xRot = Mth.cos($$2 * 0.6662f) * 1.4f * $$1;
        this.head.zRot = $$0.headRollAngle;
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        float $$3 = $$0.ageScale;
        if ($$0.isCrouching) {
            this.body.xRot += 0.10471976f;
            float $$4 = $$0.crouchAmount;
            this.body.y += $$4 * $$3;
            this.head.y += $$4 * $$3;
        } else if ($$0.isSleeping) {
            this.body.zRot = -1.5707964f;
            this.body.y += 5.0f * $$3;
            this.tail.xRot = -2.6179938f;
            if ($$0.isBaby) {
                this.tail.xRot = -2.1816616f;
                this.body.z += 2.0f;
            }
            this.head.x += 2.0f * $$3;
            this.head.y += 2.99f * $$3;
            this.head.yRot = -2.0943952f;
            this.head.zRot = 0.0f;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if ($$0.isSitting) {
            this.body.xRot = 0.5235988f;
            this.body.y -= 7.0f * $$3;
            this.body.z += 3.0f * $$3;
            this.tail.xRot = 0.7853982f;
            this.tail.z -= 1.0f * $$3;
            this.head.xRot = 0.0f;
            this.head.yRot = 0.0f;
            if ($$0.isBaby) {
                this.head.y -= 1.75f;
                this.head.z -= 0.375f;
            } else {
                this.head.y -= 6.5f;
                this.head.z += 2.75f;
            }
            this.rightHindLeg.xRot = -1.3089969f;
            this.rightHindLeg.y += 4.0f * $$3;
            this.rightHindLeg.z -= 0.25f * $$3;
            this.leftHindLeg.xRot = -1.3089969f;
            this.leftHindLeg.y += 4.0f * $$3;
            this.leftHindLeg.z -= 0.25f * $$3;
            this.rightFrontLeg.xRot = -0.2617994f;
            this.leftFrontLeg.xRot = -0.2617994f;
        }
        if (!($$0.isSleeping || $$0.isFaceplanted || $$0.isCrouching)) {
            this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
            this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        }
        if ($$0.isSleeping) {
            this.head.xRot = 0.0f;
            this.head.yRot = -2.0943952f;
            this.head.zRot = Mth.cos($$0.ageInTicks * 0.027f) / 22.0f;
        }
        if ($$0.isCrouching) {
            float $$5;
            this.body.yRot = $$5 = Mth.cos($$0.ageInTicks) * 0.01f;
            this.rightHindLeg.zRot = $$5;
            this.leftHindLeg.zRot = $$5;
            this.rightFrontLeg.zRot = $$5 / 2.0f;
            this.leftFrontLeg.zRot = $$5 / 2.0f;
        }
        if ($$0.isFaceplanted) {
            float $$6 = 0.1f;
            this.legMotionPos += 0.67f;
            this.rightHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
            this.leftHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662f) * 0.1f;
        }
    }
}

