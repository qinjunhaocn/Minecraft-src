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
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.util.Mth;

public class StriderModel
extends EntityModel<StriderRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5f);
    private static final String RIGHT_BOTTOM_BRISTLE = "right_bottom_bristle";
    private static final String RIGHT_MIDDLE_BRISTLE = "right_middle_bristle";
    private static final String RIGHT_TOP_BRISTLE = "right_top_bristle";
    private static final String LEFT_TOP_BRISTLE = "left_top_bristle";
    private static final String LEFT_MIDDLE_BRISTLE = "left_middle_bristle";
    private static final String LEFT_BOTTOM_BRISTLE = "left_bottom_bristle";
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart body;
    private final ModelPart rightBottomBristle;
    private final ModelPart rightMiddleBristle;
    private final ModelPart rightTopBristle;
    private final ModelPart leftTopBristle;
    private final ModelPart leftMiddleBristle;
    private final ModelPart leftBottomBristle;

    public StriderModel(ModelPart $$0) {
        super($$0);
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
        this.body = $$0.getChild("body");
        this.rightBottomBristle = this.body.getChild(RIGHT_BOTTOM_BRISTLE);
        this.rightMiddleBristle = this.body.getChild(RIGHT_MIDDLE_BRISTLE);
        this.rightTopBristle = this.body.getChild(RIGHT_TOP_BRISTLE);
        this.leftTopBristle = this.body.getChild(LEFT_TOP_BRISTLE);
        this.leftMiddleBristle = this.body.getChild(LEFT_MIDDLE_BRISTLE);
        this.leftBottomBristle = this.body.getChild(LEFT_BOTTOM_BRISTLE);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), PartPose.offset(-4.0f, 8.0f, 0.0f));
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), PartPose.offset(4.0f, 8.0f, 0.0f));
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f), PartPose.offset(0.0f, 1.0f, 0.0f));
        $$2.addOrReplaceChild(RIGHT_BOTTOM_BRISTLE, CubeListBuilder.create().texOffs(16, 65).addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), PartPose.offsetAndRotation(-8.0f, 4.0f, -8.0f, 0.0f, 0.0f, -1.2217305f));
        $$2.addOrReplaceChild(RIGHT_MIDDLE_BRISTLE, CubeListBuilder.create().texOffs(16, 49).addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), PartPose.offsetAndRotation(-8.0f, -1.0f, -8.0f, 0.0f, 0.0f, -1.134464f));
        $$2.addOrReplaceChild(RIGHT_TOP_BRISTLE, CubeListBuilder.create().texOffs(16, 33).addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), PartPose.offsetAndRotation(-8.0f, -5.0f, -8.0f, 0.0f, 0.0f, -0.87266463f));
        $$2.addOrReplaceChild(LEFT_TOP_BRISTLE, CubeListBuilder.create().texOffs(16, 33).addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), PartPose.offsetAndRotation(8.0f, -6.0f, -8.0f, 0.0f, 0.0f, 0.87266463f));
        $$2.addOrReplaceChild(LEFT_MIDDLE_BRISTLE, CubeListBuilder.create().texOffs(16, 49).addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), PartPose.offsetAndRotation(8.0f, -2.0f, -8.0f, 0.0f, 0.0f, 1.134464f));
        $$2.addOrReplaceChild(LEFT_BOTTOM_BRISTLE, CubeListBuilder.create().texOffs(16, 65).addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), PartPose.offsetAndRotation(8.0f, 3.0f, -8.0f, 0.0f, 0.0f, 1.2217305f));
        return LayerDefinition.create($$0, 64, 128);
    }

    @Override
    public void setupAnim(StriderRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.walkAnimationPos;
        float $$2 = Math.min($$0.walkAnimationSpeed, 0.25f);
        if (!$$0.isRidden) {
            this.body.xRot = $$0.xRot * ((float)Math.PI / 180);
            this.body.yRot = $$0.yRot * ((float)Math.PI / 180);
        } else {
            this.body.xRot = 0.0f;
            this.body.yRot = 0.0f;
        }
        float $$3 = 1.5f;
        this.body.zRot = 0.1f * Mth.sin($$1 * 1.5f) * 4.0f * $$2;
        this.body.y = 2.0f;
        this.body.y -= 2.0f * Mth.cos($$1 * 1.5f) * 2.0f * $$2;
        this.leftLeg.xRot = Mth.sin($$1 * 1.5f * 0.5f) * 2.0f * $$2;
        this.rightLeg.xRot = Mth.sin($$1 * 1.5f * 0.5f + (float)Math.PI) * 2.0f * $$2;
        this.leftLeg.zRot = 0.17453292f * Mth.cos($$1 * 1.5f * 0.5f) * $$2;
        this.rightLeg.zRot = 0.17453292f * Mth.cos($$1 * 1.5f * 0.5f + (float)Math.PI) * $$2;
        this.leftLeg.y = 8.0f + 2.0f * Mth.sin($$1 * 1.5f * 0.5f + (float)Math.PI) * 2.0f * $$2;
        this.rightLeg.y = 8.0f + 2.0f * Mth.sin($$1 * 1.5f * 0.5f) * 2.0f * $$2;
        this.rightBottomBristle.zRot = -1.2217305f;
        this.rightMiddleBristle.zRot = -1.134464f;
        this.rightTopBristle.zRot = -0.87266463f;
        this.leftTopBristle.zRot = 0.87266463f;
        this.leftMiddleBristle.zRot = 1.134464f;
        this.leftBottomBristle.zRot = 1.2217305f;
        float $$4 = Mth.cos($$1 * 1.5f + (float)Math.PI) * $$2;
        this.rightBottomBristle.zRot += $$4 * 1.3f;
        this.rightMiddleBristle.zRot += $$4 * 1.2f;
        this.rightTopBristle.zRot += $$4 * 0.6f;
        this.leftTopBristle.zRot += $$4 * 0.6f;
        this.leftMiddleBristle.zRot += $$4 * 1.2f;
        this.leftBottomBristle.zRot += $$4 * 1.3f;
        float $$5 = 1.0f;
        float $$6 = 1.0f;
        this.rightBottomBristle.zRot += 0.05f * Mth.sin($$0.ageInTicks * 1.0f * -0.4f);
        this.rightMiddleBristle.zRot += 0.1f * Mth.sin($$0.ageInTicks * 1.0f * 0.2f);
        this.rightTopBristle.zRot += 0.1f * Mth.sin($$0.ageInTicks * 1.0f * 0.4f);
        this.leftTopBristle.zRot += 0.1f * Mth.sin($$0.ageInTicks * 1.0f * 0.4f);
        this.leftMiddleBristle.zRot += 0.1f * Mth.sin($$0.ageInTicks * 1.0f * 0.2f);
        this.leftBottomBristle.zRot += 0.05f * Mth.sin($$0.ageInTicks * 1.0f * -0.4f);
    }
}

