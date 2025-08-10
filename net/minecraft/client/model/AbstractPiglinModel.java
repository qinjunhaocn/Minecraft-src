/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

public class AbstractPiglinModel<S extends HumanoidRenderState>
extends HumanoidModel<S> {
    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";
    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    public final ModelPart rightEar;
    public final ModelPart leftEar;

    public AbstractPiglinModel(ModelPart $$0) {
        super($$0, RenderType::entityTranslucent);
        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild("jacket");
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
    }

    public static MeshDefinition createMesh(CubeDeformation $$0) {
        MeshDefinition $$1 = PlayerModel.createMesh($$0, false);
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0), PartPose.ZERO);
        PartDefinition $$3 = AbstractPiglinModel.addHead($$0, $$1);
        $$3.clearChild("hat");
        return $$1;
    }

    public static PartDefinition addHead(CubeDeformation $$0, MeshDefinition $$1) {
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, $$0).texOffs(31, 1).addBox(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, $$0).texOffs(2, 4).addBox(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, $$0).texOffs(2, 0).addBox(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, $$0), PartPose.ZERO);
        $$3.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, $$0), PartPose.offsetAndRotation(4.5f, -6.0f, 0.0f, 0.0f, 0.0f, -0.5235988f));
        $$3.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, $$0), PartPose.offsetAndRotation(-4.5f, -6.0f, 0.0f, 0.0f, 0.0f, 0.5235988f));
        return $$3;
    }

    @Override
    public void setupAnim(S $$0) {
        super.setupAnim($$0);
        float $$1 = ((HumanoidRenderState)$$0).walkAnimationPos;
        float $$2 = ((HumanoidRenderState)$$0).walkAnimationSpeed;
        float $$3 = 0.5235988f;
        float $$4 = ((HumanoidRenderState)$$0).ageInTicks * 0.1f + $$1 * 0.5f;
        float $$5 = 0.08f + $$2 * 0.4f;
        this.leftEar.zRot = -0.5235988f - Mth.cos($$4 * 1.2f) * $$5;
        this.rightEar.zRot = 0.5235988f + Mth.cos($$4) * $$5;
    }

    @Override
    public void setAllVisible(boolean $$0) {
        super.setAllVisible($$0);
        this.leftSleeve.visible = $$0;
        this.rightSleeve.visible = $$0;
        this.leftPants.visible = $$0;
        this.rightPants.visible = $$0;
        this.jacket.visible = $$0;
    }
}

