/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;

public class PlayerModel
extends HumanoidModel<PlayerRenderState> {
    private static final String LEFT_SLEEVE = "left_sleeve";
    private static final String RIGHT_SLEEVE = "right_sleeve";
    private static final String LEFT_PANTS = "left_pants";
    private static final String RIGHT_PANTS = "right_pants";
    private final List<ModelPart> bodyParts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final boolean slim;

    public PlayerModel(ModelPart $$0, boolean $$1) {
        super($$0, RenderType::entityTranslucent);
        this.slim = $$1;
        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild("jacket");
        this.bodyParts = List.of((Object)this.head, (Object)this.body, (Object)this.leftArm, (Object)this.rightArm, (Object)this.leftLeg, (Object)this.rightLeg);
    }

    public static MeshDefinition createMesh(CubeDeformation $$0, boolean $$1) {
        MeshDefinition $$2 = HumanoidModel.createMesh($$0, 0.0f);
        PartDefinition $$3 = $$2.getRoot();
        float $$4 = 0.25f;
        if ($$1) {
            PartDefinition $$5 = $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.0f, 0.0f));
            PartDefinition $$6 = $$3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0), PartPose.offset(-5.0f, 2.0f, 0.0f));
            $$5.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
            $$6.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        } else {
            PartDefinition $$7 = $$3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(5.0f, 2.0f, 0.0f));
            PartDefinition $$8 = $$3.getChild("right_arm");
            $$7.addOrReplaceChild(LEFT_SLEEVE, CubeListBuilder.create().texOffs(48, 48).addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
            $$8.addOrReplaceChild(RIGHT_SLEEVE, CubeListBuilder.create().texOffs(40, 32).addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        }
        PartDefinition $$9 = $$3.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0), PartPose.offset(1.9f, 12.0f, 0.0f));
        PartDefinition $$10 = $$3.getChild("right_leg");
        $$9.addOrReplaceChild(LEFT_PANTS, CubeListBuilder.create().texOffs(0, 48).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        $$10.addOrReplaceChild(RIGHT_PANTS, CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        PartDefinition $$11 = $$3.getChild("body");
        $$11.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, $$0.extend(0.25f)), PartPose.ZERO);
        return $$2;
    }

    @Override
    public void setupAnim(PlayerRenderState $$0) {
        boolean $$1;
        this.body.visible = $$1 = !$$0.isSpectator;
        this.rightArm.visible = $$1;
        this.leftArm.visible = $$1;
        this.rightLeg.visible = $$1;
        this.leftLeg.visible = $$1;
        this.hat.visible = $$0.showHat;
        this.jacket.visible = $$0.showJacket;
        this.leftPants.visible = $$0.showLeftPants;
        this.rightPants.visible = $$0.showRightPants;
        this.leftSleeve.visible = $$0.showLeftSleeve;
        this.rightSleeve.visible = $$0.showRightSleeve;
        super.setupAnim($$0);
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

    @Override
    public void translateToHand(HumanoidArm $$0, PoseStack $$1) {
        this.root().translateAndRotate($$1);
        ModelPart $$2 = this.getArm($$0);
        if (this.slim) {
            float $$3 = 0.5f * (float)($$0 == HumanoidArm.RIGHT ? 1 : -1);
            $$2.x += $$3;
            $$2.translateAndRotate($$1);
            $$2.x -= $$3;
        } else {
            $$2.translateAndRotate($$1);
        }
    }

    public ModelPart getRandomBodyPart(RandomSource $$0) {
        return Util.getRandom(this.bodyParts, $$0);
    }
}

