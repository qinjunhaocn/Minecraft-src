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
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotModel
extends EntityModel<ParrotRenderState> {
    private static final String FEATHER = "feather";
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ParrotModel(ModelPart $$0) {
        super($$0);
        this.body = $$0.getChild("body");
        this.tail = $$0.getChild("tail");
        this.leftWing = $$0.getChild("left_wing");
        this.rightWing = $$0.getChild("right_wing");
        this.head = $$0.getChild("head");
        this.leftLeg = $$0.getChild("left_leg");
        this.rightLeg = $$0.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(2, 8).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), PartPose.offsetAndRotation(0.0f, 16.5f, -3.0f, 0.4937f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 1).addBox(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 21.07f, 1.16f, 1.015f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), PartPose.offsetAndRotation(1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(19, 8).addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), PartPose.offsetAndRotation(-1.5f, 16.94f, -2.76f, -0.6981f, (float)(-Math.PI), 0.0f));
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), PartPose.offset(0.0f, 15.69f, -2.76f));
        $$2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), PartPose.offset(0.0f, -2.0f, -1.0f));
        $$2.addOrReplaceChild("beak1", CubeListBuilder.create().texOffs(11, 7).addBox(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), PartPose.offset(0.0f, -0.5f, -1.5f));
        $$2.addOrReplaceChild("beak2", CubeListBuilder.create().texOffs(16, 7).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), PartPose.offset(0.0f, -1.75f, -2.45f));
        $$2.addOrReplaceChild(FEATHER, CubeListBuilder.create().texOffs(2, 18).addBox(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), PartPose.offsetAndRotation(0.0f, -2.15f, 0.15f, -0.2214f, 0.0f, 0.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(14, 18).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        $$1.addOrReplaceChild("left_leg", $$3, PartPose.offsetAndRotation(1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_leg", $$3, PartPose.offsetAndRotation(-1.0f, 22.0f, -1.05f, -0.0299f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(ParrotRenderState $$0) {
        super.setupAnim($$0);
        this.prepare($$0.pose);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        switch ($$0.pose.ordinal()) {
            case 2: {
                break;
            }
            case 3: {
                float $$1 = Mth.cos($$0.ageInTicks);
                float $$2 = Mth.sin($$0.ageInTicks);
                this.head.x += $$1;
                this.head.y += $$2;
                this.head.xRot = 0.0f;
                this.head.yRot = 0.0f;
                this.head.zRot = Mth.sin($$0.ageInTicks) * 0.4f;
                this.body.x += $$1;
                this.body.y += $$2;
                this.leftWing.zRot = -0.0873f - $$0.flapAngle;
                this.leftWing.x += $$1;
                this.leftWing.y += $$2;
                this.rightWing.zRot = 0.0873f + $$0.flapAngle;
                this.rightWing.x += $$1;
                this.rightWing.y += $$2;
                this.tail.x += $$1;
                this.tail.y += $$2;
                break;
            }
            case 1: {
                this.leftLeg.xRot += Mth.cos($$0.walkAnimationPos * 0.6662f) * 1.4f * $$0.walkAnimationSpeed;
                this.rightLeg.xRot += Mth.cos($$0.walkAnimationPos * 0.6662f + (float)Math.PI) * 1.4f * $$0.walkAnimationSpeed;
            }
            default: {
                float $$3 = $$0.flapAngle * 0.3f;
                this.head.y += $$3;
                this.tail.xRot += Mth.cos($$0.walkAnimationPos * 0.6662f) * 0.3f * $$0.walkAnimationSpeed;
                this.tail.y += $$3;
                this.body.y += $$3;
                this.leftWing.zRot = -0.0873f - $$0.flapAngle;
                this.leftWing.y += $$3;
                this.rightWing.zRot = 0.0873f + $$0.flapAngle;
                this.rightWing.y += $$3;
                this.leftLeg.y += $$3;
                this.rightLeg.y += $$3;
            }
        }
    }

    private void prepare(Pose $$0) {
        switch ($$0.ordinal()) {
            case 0: {
                this.leftLeg.xRot += 0.6981317f;
                this.rightLeg.xRot += 0.6981317f;
                break;
            }
            case 2: {
                float $$1 = 1.9f;
                this.head.y += 1.9f;
                this.tail.xRot += 0.5235988f;
                this.tail.y += 1.9f;
                this.body.y += 1.9f;
                this.leftWing.zRot = -0.0873f;
                this.leftWing.y += 1.9f;
                this.rightWing.zRot = 0.0873f;
                this.rightWing.y += 1.9f;
                this.leftLeg.y += 1.9f;
                this.rightLeg.y += 1.9f;
                this.leftLeg.xRot += 1.5707964f;
                this.rightLeg.xRot += 1.5707964f;
                break;
            }
            case 3: {
                this.leftLeg.zRot = -0.34906584f;
                this.rightLeg.zRot = 0.34906584f;
                break;
            }
        }
    }

    public static Pose getPose(Parrot $$0) {
        if ($$0.isPartyParrot()) {
            return Pose.PARTY;
        }
        if ($$0.isInSittingPose()) {
            return Pose.SITTING;
        }
        if ($$0.isFlying()) {
            return Pose.FLYING;
        }
        return Pose.STANDING;
    }

    public static final class Pose
    extends Enum<Pose> {
        public static final /* enum */ Pose FLYING = new Pose();
        public static final /* enum */ Pose STANDING = new Pose();
        public static final /* enum */ Pose SITTING = new Pose();
        public static final /* enum */ Pose PARTY = new Pose();
        public static final /* enum */ Pose ON_SHOULDER = new Pose();
        private static final /* synthetic */ Pose[] $VALUES;

        public static Pose[] values() {
            return (Pose[])$VALUES.clone();
        }

        public static Pose valueOf(String $$0) {
            return Enum.valueOf(Pose.class, $$0);
        }

        private static /* synthetic */ Pose[] a() {
            return new Pose[]{FLYING, STANDING, SITTING, PARTY, ON_SHOULDER};
        }

        static {
            $VALUES = Pose.a();
        }
    }
}

