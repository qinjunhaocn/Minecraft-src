/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.util.Mth;

public class ChickenModel
extends EntityModel<ChickenRenderState> {
    public static final String RED_THING = "red_thing";
    public static final float Y_OFFSET = 16.0f;
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 5.0f, 2.0f, 2.0f, 1.99f, 24.0f, Set.of((Object)"head", (Object)"beak", (Object)"red_thing"));
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ChickenModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
        this.rightLeg = $$0.getChild("right_leg");
        this.leftLeg = $$0.getChild("left_leg");
        this.rightWing = $$0.getChild("right_wing");
        this.leftWing = $$0.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = ChickenModel.createBaseChickenModel();
        return LayerDefinition.create($$0, 64, 32);
    }

    protected static MeshDefinition createBaseChickenModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        $$2.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(14, 0).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$2.addOrReplaceChild(RED_THING, CubeListBuilder.create().texOffs(14, 4).addBox(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), PartPose.offsetAndRotation(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(26, 0).addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        $$1.addOrReplaceChild("right_leg", $$3, PartPose.offset(-2.0f, 19.0f, 1.0f));
        $$1.addOrReplaceChild("left_leg", $$3, PartPose.offset(1.0f, 19.0f, 1.0f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 13).addBox(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(-4.0f, 13.0f, 0.0f));
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), PartPose.offset(4.0f, 13.0f, 0.0f));
        return $$0;
    }

    @Override
    public void setupAnim(ChickenRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = (Mth.sin($$0.flap) + 1.0f) * $$0.flapSpeed;
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = $$0.yRot * ((float)Math.PI / 180);
        float $$2 = $$0.walkAnimationSpeed;
        float $$3 = $$0.walkAnimationPos;
        this.rightLeg.xRot = Mth.cos($$3 * 0.6662f) * 1.4f * $$2;
        this.leftLeg.xRot = Mth.cos($$3 * 0.6662f + (float)Math.PI) * 1.4f * $$2;
        this.rightWing.zRot = $$1;
        this.leftWing.zRot = -$$1;
    }
}

