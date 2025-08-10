/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class HumanoidModel<T extends HumanoidRenderState>
extends EntityModel<T>
implements ArmedModel,
HeadedModel {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f, Set.of((Object)"head"));
    public static final float OVERLAY_SCALE = 0.25f;
    public static final float HAT_OVERLAY_SCALE = 0.5f;
    public static final float LEGGINGS_OVERLAY_SCALE = -0.1f;
    private static final float DUCK_WALK_ROTATION = 0.005f;
    private static final float SPYGLASS_ARM_ROT_Y = 0.2617994f;
    private static final float SPYGLASS_ARM_ROT_X = 1.9198622f;
    private static final float SPYGLASS_ARM_CROUCH_ROT_X = 0.2617994f;
    private static final float HIGHEST_SHIELD_BLOCKING_ANGLE = -1.3962634f;
    private static final float LOWEST_SHIELD_BLOCKING_ANGLE = 0.43633232f;
    private static final float HORIZONTAL_SHIELD_MOVEMENT_LIMIT = 0.5235988f;
    public static final float TOOT_HORN_XROT_BASE = 1.4835298f;
    public static final float TOOT_HORN_YROT_BASE = 0.5235988f;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public HumanoidModel(ModelPart $$0) {
        this($$0, RenderType::entityCutoutNoCull);
    }

    public HumanoidModel(ModelPart $$0, Function<ResourceLocation, RenderType> $$1) {
        super($$0, $$1);
        this.head = $$0.getChild("head");
        this.hat = this.head.getChild("hat");
        this.body = $$0.getChild("body");
        this.rightArm = $$0.getChild("right_arm");
        this.leftArm = $$0.getChild("left_arm");
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
    }

    public static MeshDefinition createMesh(CubeDeformation $$0, float $$1) {
        MeshDefinition $$2 = new MeshDefinition();
        PartDefinition $$3 = $$2.getRoot();
        PartDefinition $$4 = $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.offset(0.0f, 0.0f + $$1, 0.0f));
        $$4.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0.extend(0.5f)), PartPose.ZERO);
        $$3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0), PartPose.offset(0.0f, 0.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(-5.0f, 2.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(-1.9f, 12.0f + $$1, 0.0f));
        $$3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(1.9f, 12.0f + $$1, 0.0f));
        return $$2;
    }

    @Override
    public void setupAnim(T $$0) {
        boolean $$7;
        super.setupAnim($$0);
        ArmPose $$1 = ((HumanoidRenderState)$$0).leftArmPose;
        ArmPose $$2 = ((HumanoidRenderState)$$0).rightArmPose;
        float $$3 = ((HumanoidRenderState)$$0).swimAmount;
        boolean $$4 = ((HumanoidRenderState)$$0).isFallFlying;
        this.head.xRot = ((HumanoidRenderState)$$0).xRot * ((float)Math.PI / 180);
        this.head.yRot = ((HumanoidRenderState)$$0).yRot * ((float)Math.PI / 180);
        if ($$4) {
            this.head.xRot = -0.7853982f;
        } else if ($$3 > 0.0f) {
            this.head.xRot = Mth.rotLerpRad($$3, this.head.xRot, -0.7853982f);
        }
        float $$5 = ((HumanoidRenderState)$$0).walkAnimationPos;
        float $$6 = ((HumanoidRenderState)$$0).walkAnimationSpeed;
        this.rightArm.xRot = Mth.cos($$5 * 0.6662f + (float)Math.PI) * 2.0f * $$6 * 0.5f / ((HumanoidRenderState)$$0).speedValue;
        this.leftArm.xRot = Mth.cos($$5 * 0.6662f) * 2.0f * $$6 * 0.5f / ((HumanoidRenderState)$$0).speedValue;
        this.rightLeg.xRot = Mth.cos($$5 * 0.6662f) * 1.4f * $$6 / ((HumanoidRenderState)$$0).speedValue;
        this.leftLeg.xRot = Mth.cos($$5 * 0.6662f + (float)Math.PI) * 1.4f * $$6 / ((HumanoidRenderState)$$0).speedValue;
        this.rightLeg.yRot = 0.005f;
        this.leftLeg.yRot = -0.005f;
        this.rightLeg.zRot = 0.005f;
        this.leftLeg.zRot = -0.005f;
        if (((HumanoidRenderState)$$0).isPassenger) {
            this.rightArm.xRot += -0.62831855f;
            this.leftArm.xRot += -0.62831855f;
            this.rightLeg.xRot = -1.4137167f;
            this.rightLeg.yRot = 0.31415927f;
            this.rightLeg.zRot = 0.07853982f;
            this.leftLeg.xRot = -1.4137167f;
            this.leftLeg.yRot = -0.31415927f;
            this.leftLeg.zRot = -0.07853982f;
        }
        boolean bl = $$7 = ((HumanoidRenderState)$$0).mainArm == HumanoidArm.RIGHT;
        if (((HumanoidRenderState)$$0).isUsingItem) {
            boolean $$8;
            boolean bl2 = $$8 = ((HumanoidRenderState)$$0).useItemHand == InteractionHand.MAIN_HAND;
            if ($$8 == $$7) {
                this.poseRightArm($$0, $$2);
            } else {
                this.poseLeftArm($$0, $$1);
            }
        } else {
            boolean $$9;
            boolean bl3 = $$9 = $$7 ? $$1.isTwoHanded() : $$2.isTwoHanded();
            if ($$7 != $$9) {
                this.poseLeftArm($$0, $$1);
                this.poseRightArm($$0, $$2);
            } else {
                this.poseRightArm($$0, $$2);
                this.poseLeftArm($$0, $$1);
            }
        }
        this.setupAttackAnimation($$0, ((HumanoidRenderState)$$0).ageInTicks);
        if (((HumanoidRenderState)$$0).isCrouching) {
            this.body.xRot = 0.5f;
            this.rightArm.xRot += 0.4f;
            this.leftArm.xRot += 0.4f;
            this.rightLeg.z += 4.0f;
            this.leftLeg.z += 4.0f;
            this.head.y += 4.2f;
            this.body.y += 3.2f;
            this.leftArm.y += 3.2f;
            this.rightArm.y += 3.2f;
        }
        if ($$2 != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.rightArm, ((HumanoidRenderState)$$0).ageInTicks, 1.0f);
        }
        if ($$1 != ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.leftArm, ((HumanoidRenderState)$$0).ageInTicks, -1.0f);
        }
        if ($$3 > 0.0f) {
            float $$13;
            float $$10 = $$5 % 26.0f;
            HumanoidArm $$11 = ((HumanoidRenderState)$$0).attackArm;
            float $$12 = $$11 == HumanoidArm.RIGHT && ((HumanoidRenderState)$$0).attackTime > 0.0f ? 0.0f : $$3;
            float f = $$13 = $$11 == HumanoidArm.LEFT && ((HumanoidRenderState)$$0).attackTime > 0.0f ? 0.0f : $$3;
            if (!((HumanoidRenderState)$$0).isUsingItem) {
                if ($$10 < 14.0f) {
                    this.leftArm.xRot = Mth.rotLerpRad($$13, this.leftArm.xRot, 0.0f);
                    this.rightArm.xRot = Mth.lerp($$12, this.rightArm.xRot, 0.0f);
                    this.leftArm.yRot = Mth.rotLerpRad($$13, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$12, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad($$13, this.leftArm.zRot, (float)Math.PI + 1.8707964f * this.quadraticArmUpdate($$10) / this.quadraticArmUpdate(14.0f));
                    this.rightArm.zRot = Mth.lerp($$12, this.rightArm.zRot, (float)Math.PI - 1.8707964f * this.quadraticArmUpdate($$10) / this.quadraticArmUpdate(14.0f));
                } else if ($$10 >= 14.0f && $$10 < 22.0f) {
                    float $$14 = ($$10 - 14.0f) / 8.0f;
                    this.leftArm.xRot = Mth.rotLerpRad($$13, this.leftArm.xRot, 1.5707964f * $$14);
                    this.rightArm.xRot = Mth.lerp($$12, this.rightArm.xRot, 1.5707964f * $$14);
                    this.leftArm.yRot = Mth.rotLerpRad($$13, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$12, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad($$13, this.leftArm.zRot, 5.012389f - 1.8707964f * $$14);
                    this.rightArm.zRot = Mth.lerp($$12, this.rightArm.zRot, 1.2707963f + 1.8707964f * $$14);
                } else if ($$10 >= 22.0f && $$10 < 26.0f) {
                    float $$15 = ($$10 - 22.0f) / 4.0f;
                    this.leftArm.xRot = Mth.rotLerpRad($$13, this.leftArm.xRot, 1.5707964f - 1.5707964f * $$15);
                    this.rightArm.xRot = Mth.lerp($$12, this.rightArm.xRot, 1.5707964f - 1.5707964f * $$15);
                    this.leftArm.yRot = Mth.rotLerpRad($$13, this.leftArm.yRot, (float)Math.PI);
                    this.rightArm.yRot = Mth.lerp($$12, this.rightArm.yRot, (float)Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad($$13, this.leftArm.zRot, (float)Math.PI);
                    this.rightArm.zRot = Mth.lerp($$12, this.rightArm.zRot, (float)Math.PI);
                }
            }
            float $$16 = 0.3f;
            float $$17 = 0.33333334f;
            this.leftLeg.xRot = Mth.lerp($$3, this.leftLeg.xRot, 0.3f * Mth.cos($$5 * 0.33333334f + (float)Math.PI));
            this.rightLeg.xRot = Mth.lerp($$3, this.rightLeg.xRot, 0.3f * Mth.cos($$5 * 0.33333334f));
        }
    }

    private void poseRightArm(T $$0, ArmPose $$1) {
        switch ($$1.ordinal()) {
            case 0: {
                this.rightArm.yRot = 0.0f;
                break;
            }
            case 2: {
                this.poseBlockingArm(this.rightArm, true);
                break;
            }
            case 1: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.31415927f;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case 4: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - (float)Math.PI;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yRot = -0.1f + this.head.yRot;
                this.leftArm.yRot = 0.1f + this.head.yRot + 0.4f;
                this.rightArm.xRot = -1.5707964f + this.head.xRot;
                this.leftArm.xRot = -1.5707964f + this.head.xRot;
                break;
            }
            case 5: {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, ((HumanoidRenderState)$$0).maxCrossbowChargeDuration, ((HumanoidRenderState)$$0).ticksUsingItem, true);
                break;
            }
            case 6: {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            }
            case 9: {
                this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.62831855f;
                this.rightArm.yRot = 0.0f;
                break;
            }
            case 7: {
                this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622f - (((HumanoidRenderState)$$0).isCrouching ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.rightArm.yRot = this.head.yRot - 0.2617994f;
                break;
            }
            case 8: {
                this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2f, 1.2f) - 1.4835298f;
                this.rightArm.yRot = this.head.yRot - 0.5235988f;
            }
        }
    }

    private void poseLeftArm(T $$0, ArmPose $$1) {
        switch ($$1.ordinal()) {
            case 0: {
                this.leftArm.yRot = 0.0f;
                break;
            }
            case 2: {
                this.poseBlockingArm(this.leftArm, false);
                break;
            }
            case 1: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.31415927f;
                this.leftArm.yRot = 0.0f;
                break;
            }
            case 4: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - (float)Math.PI;
                this.leftArm.yRot = 0.0f;
                break;
            }
            case 3: {
                this.rightArm.yRot = -0.1f + this.head.yRot - 0.4f;
                this.leftArm.yRot = 0.1f + this.head.yRot;
                this.rightArm.xRot = -1.5707964f + this.head.xRot;
                this.leftArm.xRot = -1.5707964f + this.head.xRot;
                break;
            }
            case 5: {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, ((HumanoidRenderState)$$0).maxCrossbowChargeDuration, ((HumanoidRenderState)$$0).ticksUsingItem, false);
                break;
            }
            case 6: {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
                break;
            }
            case 9: {
                this.leftArm.xRot = this.leftArm.xRot * 0.5f - 0.62831855f;
                this.leftArm.yRot = 0.0f;
                break;
            }
            case 7: {
                this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622f - (((HumanoidRenderState)$$0).isCrouching ? 0.2617994f : 0.0f), -2.4f, 3.3f);
                this.leftArm.yRot = this.head.yRot + 0.2617994f;
                break;
            }
            case 8: {
                this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2f, 1.2f) - 1.4835298f;
                this.leftArm.yRot = this.head.yRot + 0.5235988f;
            }
        }
    }

    private void poseBlockingArm(ModelPart $$0, boolean $$1) {
        $$0.xRot = $$0.xRot * 0.5f - 0.9424779f + Mth.clamp(this.head.xRot, -1.3962634f, 0.43633232f);
        $$0.yRot = ($$1 ? -30.0f : 30.0f) * ((float)Math.PI / 180) + Mth.clamp(this.head.yRot, -0.5235988f, 0.5235988f);
    }

    protected void setupAttackAnimation(T $$0, float $$1) {
        float $$2 = ((HumanoidRenderState)$$0).attackTime;
        if ($$2 <= 0.0f) {
            return;
        }
        HumanoidArm $$3 = ((HumanoidRenderState)$$0).attackArm;
        ModelPart $$4 = this.getArm($$3);
        float $$5 = $$2;
        this.body.yRot = Mth.sin(Mth.sqrt($$5) * ((float)Math.PI * 2)) * 0.2f;
        if ($$3 == HumanoidArm.LEFT) {
            this.body.yRot *= -1.0f;
        }
        float $$6 = ((HumanoidRenderState)$$0).ageScale;
        this.rightArm.z = Mth.sin(this.body.yRot) * 5.0f * $$6;
        this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0f * $$6;
        this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0f * $$6;
        this.leftArm.x = Mth.cos(this.body.yRot) * 5.0f * $$6;
        this.rightArm.yRot += this.body.yRot;
        this.leftArm.yRot += this.body.yRot;
        this.leftArm.xRot += this.body.yRot;
        $$5 = 1.0f - $$2;
        $$5 *= $$5;
        $$5 *= $$5;
        $$5 = 1.0f - $$5;
        float $$7 = Mth.sin($$5 * (float)Math.PI);
        float $$8 = Mth.sin($$2 * (float)Math.PI) * -(this.head.xRot - 0.7f) * 0.75f;
        $$4.xRot -= $$7 * 1.2f + $$8;
        $$4.yRot += this.body.yRot * 2.0f;
        $$4.zRot += Mth.sin($$2 * (float)Math.PI) * -0.4f;
    }

    private float quadraticArmUpdate(float $$0) {
        return -65.0f * $$0 + $$0 * $$0;
    }

    public void copyPropertiesTo(HumanoidModel<T> $$0) {
        $$0.head.copyFrom(this.head);
        $$0.body.copyFrom(this.body);
        $$0.rightArm.copyFrom(this.rightArm);
        $$0.leftArm.copyFrom(this.leftArm);
        $$0.rightLeg.copyFrom(this.rightLeg);
        $$0.leftLeg.copyFrom(this.leftLeg);
    }

    public void setAllVisible(boolean $$0) {
        this.head.visible = $$0;
        this.hat.visible = $$0;
        this.body.visible = $$0;
        this.rightArm.visible = $$0;
        this.leftArm.visible = $$0;
        this.rightLeg.visible = $$0;
        this.leftLeg.visible = $$0;
    }

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        this.root.translateAndRotate($$1);
        this.getArm($$0).translateAndRotate($$1);
    }

    protected ModelPart getArm(HumanoidArm $$0) {
        if ($$0 == HumanoidArm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    public static final class ArmPose
    extends Enum<ArmPose> {
        public static final /* enum */ ArmPose EMPTY = new ArmPose(false);
        public static final /* enum */ ArmPose ITEM = new ArmPose(false);
        public static final /* enum */ ArmPose BLOCK = new ArmPose(false);
        public static final /* enum */ ArmPose BOW_AND_ARROW = new ArmPose(true);
        public static final /* enum */ ArmPose THROW_SPEAR = new ArmPose(false);
        public static final /* enum */ ArmPose CROSSBOW_CHARGE = new ArmPose(true);
        public static final /* enum */ ArmPose CROSSBOW_HOLD = new ArmPose(true);
        public static final /* enum */ ArmPose SPYGLASS = new ArmPose(false);
        public static final /* enum */ ArmPose TOOT_HORN = new ArmPose(false);
        public static final /* enum */ ArmPose BRUSH = new ArmPose(false);
        private final boolean twoHanded;
        private static final /* synthetic */ ArmPose[] $VALUES;

        public static ArmPose[] values() {
            return (ArmPose[])$VALUES.clone();
        }

        public static ArmPose valueOf(String $$0) {
            return Enum.valueOf(ArmPose.class, $$0);
        }

        private ArmPose(boolean $$0) {
            this.twoHanded = $$0;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }

        private static /* synthetic */ ArmPose[] b() {
            return new ArmPose[]{EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_SPEAR, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH};
        }

        static {
            $VALUES = ArmPose.b();
        }
    }
}

