/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ElytraModel
extends EntityModel<HumanoidRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5f);
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ElytraModel(ModelPart $$0) {
        super($$0);
        this.leftWing = $$0.getChild("left_wing");
        this.rightWing = $$0.getChild("right_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(1.0f);
        $$1.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(22, 0).addBox(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, $$2), PartPose.offsetAndRotation(5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, -0.2617994f));
        $$1.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, $$2), PartPose.offsetAndRotation(-5.0f, 0.0f, 0.0f, 0.2617994f, 0.0f, 0.2617994f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(HumanoidRenderState $$0) {
        super.setupAnim($$0);
        this.leftWing.y = $$0.isCrouching ? 3.0f : 0.0f;
        this.leftWing.xRot = $$0.elytraRotX;
        this.leftWing.zRot = $$0.elytraRotZ;
        this.leftWing.yRot = $$0.elytraRotY;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}

