/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;

public class BreezeModel
extends EntityModel<BreezeRenderState> {
    private static final float WIND_TOP_SPEED = 0.6f;
    private static final float WIND_MIDDLE_SPEED = 0.8f;
    private static final float WIND_BOTTOM_SPEED = 1.0f;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart wind;
    private final ModelPart windTop;
    private final ModelPart windMid;
    private final ModelPart windBottom;
    private final ModelPart rods;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation shootAnimation;
    private final KeyframeAnimation slideAnimation;
    private final KeyframeAnimation slideBackAnimation;
    private final KeyframeAnimation inhaleAnimation;
    private final KeyframeAnimation jumpAnimation;

    public BreezeModel(ModelPart $$0) {
        super($$0, RenderType::entityTranslucent);
        this.wind = $$0.getChild("wind_body");
        this.windBottom = this.wind.getChild("wind_bottom");
        this.windMid = this.windBottom.getChild("wind_mid");
        this.windTop = this.windMid.getChild("wind_top");
        this.head = $$0.getChild("body").getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.rods = $$0.getChild("body").getChild("rods");
        this.idleAnimation = BreezeAnimation.IDLE.bake($$0);
        this.shootAnimation = BreezeAnimation.SHOOT.bake($$0);
        this.slideAnimation = BreezeAnimation.SLIDE.bake($$0);
        this.slideBackAnimation = BreezeAnimation.SLIDE_BACK.bake($$0);
        this.inhaleAnimation = BreezeAnimation.INHALE.bake($$0);
        this.jumpAnimation = BreezeAnimation.JUMP.bake($$0);
    }

    public static LayerDefinition createBodyLayer(int $$0, int $$1) {
        MeshDefinition $$2 = new MeshDefinition();
        PartDefinition $$3 = $$2.getRoot();
        PartDefinition $$4 = $$3.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f));
        PartDefinition $$5 = $$4.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0f, 8.0f, 0.0f));
        $$5.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation(2.5981f, -3.0f, 1.5f, -2.7489f, -1.0472f, 3.1416f));
        $$5.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation(-2.5981f, -3.0f, 1.5f, -2.7489f, 1.0472f, 3.1416f));
        $$5.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, -3.0f, -3.0f, 0.3927f, 0.0f, 0.0f));
        PartDefinition $$6 = $$4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new CubeDeformation(0.0f)).texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 4.0f, 0.0f));
        $$6.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new CubeDeformation(0.0f)).texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.0f, 0.0f));
        PartDefinition $$7 = $$3.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f));
        PartDefinition $$8 = $$7.addOrReplaceChild("wind_bottom", CubeListBuilder.create().texOffs(1, 83).addBox(-2.5f, -7.0f, -2.5f, 5.0f, 7.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition $$9 = $$8.addOrReplaceChild("wind_mid", CubeListBuilder.create().texOffs(74, 28).addBox(-6.0f, -6.0f, -6.0f, 12.0f, 6.0f, 12.0f, new CubeDeformation(0.0f)).texOffs(78, 32).addBox(-4.0f, -6.0f, -4.0f, 8.0f, 6.0f, 8.0f, new CubeDeformation(0.0f)).texOffs(49, 71).addBox(-2.5f, -6.0f, -2.5f, 5.0f, 6.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -7.0f, 0.0f));
        $$9.addOrReplaceChild("wind_top", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0f, -8.0f, -9.0f, 18.0f, 8.0f, 18.0f, new CubeDeformation(0.0f)).texOffs(6, 6).addBox(-6.0f, -8.0f, -6.0f, 12.0f, 8.0f, 12.0f, new CubeDeformation(0.0f)).texOffs(105, 57).addBox(-2.5f, -8.0f, -2.5f, 5.0f, 8.0f, 5.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, -6.0f, 0.0f));
        return LayerDefinition.create($$2, $$0, $$1);
    }

    @Override
    public void setupAnim(BreezeRenderState $$0) {
        super.setupAnim($$0);
        this.idleAnimation.apply($$0.idle, $$0.ageInTicks);
        this.shootAnimation.apply($$0.shoot, $$0.ageInTicks);
        this.slideAnimation.apply($$0.slide, $$0.ageInTicks);
        this.slideBackAnimation.apply($$0.slideBack, $$0.ageInTicks);
        this.inhaleAnimation.apply($$0.inhale, $$0.ageInTicks);
        this.jumpAnimation.apply($$0.longJump, $$0.ageInTicks);
    }

    public ModelPart head() {
        return this.head;
    }

    public ModelPart eyes() {
        return this.eyes;
    }

    public ModelPart rods() {
        return this.rods;
    }

    public ModelPart wind() {
        return this.wind;
    }
}

