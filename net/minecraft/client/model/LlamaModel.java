/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.util.Mth;

public class LlamaModel
extends EntityModel<LlamaRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = LlamaModel::transformToBaby;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightChest;
    private final ModelPart leftChest;

    public LlamaModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.rightChest = $$0.getChild("right_chest");
        this.leftChest = $$0.getChild("left_chest");
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, $$0).texOffs(0, 14).addBox("neck", -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, $$0).texOffs(17, 0).addBox("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, $$0).texOffs(17, 0).addBox("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, $$0), PartPose.offset(0.0f, 7.0f, -6.0f));
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(29, 0).addBox(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, $$0), PartPose.offsetAndRotation(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("right_chest", CubeListBuilder.create().texOffs(45, 28).addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, $$0), PartPose.offsetAndRotation(-8.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        $$2.addOrReplaceChild("left_chest", CubeListBuilder.create().texOffs(45, 41).addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, $$0), PartPose.offsetAndRotation(5.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        int $$3 = 4;
        int $$4 = 14;
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(29, 29).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, $$0);
        $$2.addOrReplaceChild("right_hind_leg", $$5, PartPose.offset(-3.5f, 10.0f, 6.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$5, PartPose.offset(3.5f, 10.0f, 6.0f));
        $$2.addOrReplaceChild("right_front_leg", $$5, PartPose.offset(-3.5f, 10.0f, -5.0f));
        $$2.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(3.5f, 10.0f, -5.0f));
        return LayerDefinition.create($$1, 128, 64);
    }

    private static MeshDefinition transformToBaby(MeshDefinition $$02) {
        float $$1 = 2.0f;
        float $$2 = 0.7f;
        float $$3 = 1.1f;
        UnaryOperator $$4 = $$0 -> $$0.translated(0.0f, 21.0f, 3.52f).scaled(0.71428573f, 0.64935064f, 0.7936508f);
        UnaryOperator $$5 = $$0 -> $$0.translated(0.0f, 33.0f, 0.0f).scaled(0.625f, 0.45454544f, 0.45454544f);
        UnaryOperator $$6 = $$0 -> $$0.translated(0.0f, 33.0f, 0.0f).scaled(0.45454544f, 0.41322312f, 0.45454544f);
        MeshDefinition $$7 = new MeshDefinition();
        for (Map.Entry<String, PartDefinition> $$8 : $$02.getRoot().getChildren()) {
            String $$9 = $$8.getKey();
            PartDefinition $$10 = $$8.getValue();
            UnaryOperator $$11 = switch ($$9) {
                case "head" -> $$4;
                case "body" -> $$5;
                default -> $$6;
            };
            $$7.getRoot().addOrReplaceChild($$9, $$10.transformed($$11));
        }
        return $$7;
    }

    @Override
    public void setupAnim(LlamaRenderState $$0) {
        super.setupAnim($$0);
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        float $$1 = $$0.walkAnimationSpeed;
        float $$2 = $$0.walkAnimationPos;
        this.rightHindLeg.xRot = Mth.cos($$2 * 0.6662f) * 1.4f * $$1;
        this.leftHindLeg.xRot = Mth.cos($$2 * 0.6662f + (float)Math.PI) * 1.4f * $$1;
        this.rightFrontLeg.xRot = Mth.cos($$2 * 0.6662f + (float)Math.PI) * 1.4f * $$1;
        this.leftFrontLeg.xRot = Mth.cos($$2 * 0.6662f) * 1.4f * $$1;
        this.rightChest.visible = $$0.hasChest;
        this.leftChest.visible = $$0.hasChest;
    }
}

