/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.util.Mth;

public class WitchModel
extends EntityModel<WitchRenderState>
implements HeadedModel,
VillagerLikeModel {
    protected final ModelPart nose;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart hatRim;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart arms;

    public WitchModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.nose = this.head.getChild("nose");
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
        this.arms = $$0.getChild("arms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = VillagerModel.createBodyModel();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), PartPose.ZERO);
        PartDefinition $$3 = $$2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 64).addBox(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f), PartPose.offset(-5.0f, -10.03125f, -5.0f));
        PartDefinition $$4 = $$3.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(0, 76).addBox(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f), PartPose.offsetAndRotation(1.75f, -4.0f, 2.0f, -0.05235988f, 0.0f, 0.02617994f));
        PartDefinition $$5 = $$4.addOrReplaceChild("hat3", CubeListBuilder.create().texOffs(0, 87).addBox(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f), PartPose.offsetAndRotation(1.75f, -4.0f, 2.0f, -0.10471976f, 0.0f, 0.05235988f));
        $$5.addOrReplaceChild("hat4", CubeListBuilder.create().texOffs(0, 95).addBox(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, new CubeDeformation(0.25f)), PartPose.offsetAndRotation(1.75f, -2.0f, 2.0f, -0.20943952f, 0.0f, 0.10471976f));
        PartDefinition $$6 = $$2.getChild("nose");
        $$6.addOrReplaceChild("mole", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, new CubeDeformation(-0.25f)), PartPose.offset(0.0f, -2.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 128);
    }

    @Override
    public void setupAnim(WitchRenderState $$0) {
        super.setupAnim($$0);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.rightLeg.xRot = Mth.cos($$0.walkAnimationPos * 0.6662f) * 1.4f * $$0.walkAnimationSpeed * 0.5f;
        this.leftLeg.xRot = Mth.cos($$0.walkAnimationPos * 0.6662f + (float)Math.PI) * 1.4f * $$0.walkAnimationSpeed * 0.5f;
        float $$1 = 0.01f * (float)($$0.entityId % 10);
        this.nose.xRot = Mth.sin($$0.ageInTicks * $$1) * 4.5f * ((float)Math.PI / 180);
        this.nose.zRot = Mth.cos($$0.ageInTicks * $$1) * 2.5f * ((float)Math.PI / 180);
        if ($$0.isHoldingItem) {
            this.nose.setPos(0.0f, 1.0f, -1.5f);
            this.nose.xRot = -0.9f;
        }
    }

    public ModelPart getNose() {
        return this.nose;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void hatVisible(boolean $$0) {
        this.head.visible = $$0;
        this.hat.visible = $$0;
        this.hatRim.visible = $$0;
    }

    @Override
    public void translateToArms(PoseStack $$0) {
        this.root.translateAndRotate($$0);
        this.arms.translateAndRotate($$0);
    }
}

