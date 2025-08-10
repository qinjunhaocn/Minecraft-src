/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BatRenderState;

public class BatModel
extends EntityModel<BatRenderState> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;
    private final ModelPart feet;
    private final KeyframeAnimation flyingAnimation;
    private final KeyframeAnimation restingAnimation;

    public BatModel(ModelPart $$0) {
        super($$0, RenderType::entityCutout);
        this.body = $$0.getChild("body");
        this.head = $$0.getChild("head");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.feet = this.body.getChild("feet");
        this.flyingAnimation = BatAnimation.BAT_FLYING.bake($$0);
        this.restingAnimation = BatAnimation.BAT_RESTING.bake($$0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 5.0f, 2.0f), PartPose.offset(0.0f, 17.0f, 0.0f));
        PartDefinition $$3 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0f, -3.0f, -1.0f, 4.0f, 3.0f, 2.0f), PartPose.offset(0.0f, 17.0f, 0.0f));
        $$3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(1, 15).addBox(-2.5f, -4.0f, 0.0f, 3.0f, 5.0f, 0.0f), PartPose.offset(-1.5f, -2.0f, 0.0f));
        $$3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(8, 15).addBox(-0.1f, -3.0f, 0.0f, 3.0f, 5.0f, 0.0f), PartPose.offset(1.1f, -3.0f, 0.0f));
        PartDefinition $$4 = $$2.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(12, 0).addBox(-2.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), PartPose.offset(-1.5f, 0.0f, 0.0f));
        $$4.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(16, 0).addBox(-6.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), PartPose.offset(-2.0f, 0.0f, 0.0f));
        PartDefinition $$5 = $$2.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(12, 7).addBox(0.0f, -2.0f, 0.0f, 2.0f, 7.0f, 0.0f), PartPose.offset(1.5f, 0.0f, 0.0f));
        $$5.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(16, 8).addBox(0.0f, -2.0f, 0.0f, 6.0f, 8.0f, 0.0f), PartPose.offset(2.0f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 2.0f, 0.0f), PartPose.offset(0.0f, 5.0f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(BatRenderState $$0) {
        super.setupAnim($$0);
        if ($$0.isResting) {
            this.applyHeadRotation($$0.yRot);
        }
        this.flyingAnimation.apply($$0.flyAnimationState, $$0.ageInTicks);
        this.restingAnimation.apply($$0.restAnimationState, $$0.ageInTicks);
    }

    private void applyHeadRotation(float $$0) {
        this.head.yRot = $$0 * ((float)Math.PI / 180);
    }
}

