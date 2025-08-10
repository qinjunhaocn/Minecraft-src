/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.List;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CreakingAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CreakingRenderState;

public class CreakingModel
extends EntityModel<CreakingRenderState> {
    public static final List<ModelPart> NO_PARTS = List.of();
    private final ModelPart head;
    private final List<ModelPart> headParts;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;
    private final KeyframeAnimation invulnerableAnimation;
    private final KeyframeAnimation deathAnimation;

    public CreakingModel(ModelPart $$0) {
        super($$0);
        ModelPart $$1 = $$0.getChild("root");
        ModelPart $$2 = $$1.getChild("upper_body");
        this.head = $$2.getChild("head");
        this.headParts = List.of((Object)this.head);
        this.walkAnimation = CreakingAnimation.CREAKING_WALK.bake($$1);
        this.attackAnimation = CreakingAnimation.CREAKING_ATTACK.bake($$1);
        this.invulnerableAnimation = CreakingAnimation.CREAKING_INVULNERABLE.bake($$1);
        this.deathAnimation = CreakingAnimation.CREAKING_DEATH.bake($$1);
    }

    private static MeshDefinition createMesh() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition $$3 = $$2.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(-1.0f, -19.0f, 0.0f));
        $$3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -10.0f, -3.0f, 6.0f, 10.0f, 6.0f).texOffs(28, 31).addBox(-3.0f, -13.0f, -3.0f, 6.0f, 3.0f, 6.0f).texOffs(12, 40).addBox(3.0f, -13.0f, 0.0f, 9.0f, 14.0f, 0.0f).texOffs(34, 12).addBox(-12.0f, -14.0f, 0.0f, 9.0f, 14.0f, 0.0f), PartPose.offset(-3.0f, -11.0f, 0.0f));
        $$3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(0.0f, -3.0f, -3.0f, 6.0f, 13.0f, 5.0f).texOffs(24, 0).addBox(-6.0f, -4.0f, -3.0f, 6.0f, 7.0f, 5.0f), PartPose.offset(0.0f, -7.0f, 1.0f));
        $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(22, 13).addBox(-2.0f, -1.5f, -1.5f, 3.0f, 21.0f, 3.0f).texOffs(46, 0).addBox(-2.0f, 19.5f, -1.5f, 3.0f, 4.0f, 3.0f), PartPose.offset(-7.0f, -9.5f, 1.5f));
        $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(30, 40).addBox(0.0f, -1.0f, -1.5f, 3.0f, 16.0f, 3.0f).texOffs(52, 12).addBox(0.0f, -5.0f, -1.5f, 3.0f, 4.0f, 3.0f).texOffs(52, 19).addBox(0.0f, 15.0f, -1.5f, 3.0f, 4.0f, 3.0f), PartPose.offset(6.0f, -9.0f, 0.5f));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(42, 40).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 16.0f, 3.0f).texOffs(45, 55).addBox(-1.5f, 15.7f, -4.5f, 5.0f, 0.0f, 9.0f), PartPose.offset(1.5f, -16.0f, 0.5f));
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-3.0f, -1.5f, -1.5f, 3.0f, 19.0f, 3.0f).texOffs(45, 46).addBox(-5.0f, 17.2f, -4.5f, 5.0f, 0.0f, 9.0f).texOffs(12, 34).addBox(-3.0f, -4.5f, -1.5f, 3.0f, 3.0f, 3.0f), PartPose.offset(-1.0f, -17.5f, 0.5f));
        return $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = CreakingModel.createMesh();
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(CreakingRenderState $$0) {
        super.setupAnim($$0);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        if ($$0.canMove) {
            this.walkAnimation.applyWalk($$0.walkAnimationPos, $$0.walkAnimationSpeed, 1.0f, 1.0f);
        }
        this.attackAnimation.apply($$0.attackAnimationState, $$0.ageInTicks);
        this.invulnerableAnimation.apply($$0.invulnerabilityAnimationState, $$0.ageInTicks);
        this.deathAnimation.apply($$0.deathAnimationState, $$0.ageInTicks);
    }

    public List<ModelPart> getHeadModelParts(CreakingRenderState $$0) {
        if (!$$0.eyesGlowing) {
            return NO_PARTS;
        }
        return this.headParts;
    }
}

