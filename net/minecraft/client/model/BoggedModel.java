/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BoggedRenderState;

public class BoggedModel
extends SkeletonModel<BoggedRenderState> {
    private final ModelPart mushrooms;

    public BoggedModel(ModelPart $$0) {
        super($$0);
        this.mushrooms = $$0.getChild("head").getChild("mushrooms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition $$1 = $$0.getRoot();
        SkeletonModel.createDefaultSkeletonMesh($$1);
        PartDefinition $$2 = $$1.getChild("head").addOrReplaceChild("mushrooms", CubeListBuilder.create(), PartPose.ZERO);
        $$2.addOrReplaceChild("red_mushroom_1", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(3.0f, -8.0f, 3.0f, 0.0f, 0.7853982f, 0.0f));
        $$2.addOrReplaceChild("red_mushroom_2", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(3.0f, -8.0f, 3.0f, 0.0f, 2.3561945f, 0.0f));
        $$2.addOrReplaceChild("brown_mushroom_1", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(-3.0f, -8.0f, -3.0f, 0.0f, 0.7853982f, 0.0f));
        $$2.addOrReplaceChild("brown_mushroom_2", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(-3.0f, -8.0f, -3.0f, 0.0f, 2.3561945f, 0.0f));
        $$2.addOrReplaceChild("brown_mushroom_3", CubeListBuilder.create().texOffs(50, 28).addBox(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(-2.0f, -1.0f, 4.0f, -1.5707964f, 0.0f, 0.7853982f));
        $$2.addOrReplaceChild("brown_mushroom_4", CubeListBuilder.create().texOffs(50, 28).addBox(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), PartPose.offsetAndRotation(-2.0f, -1.0f, 4.0f, -1.5707964f, 0.0f, 2.3561945f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(BoggedRenderState $$0) {
        super.setupAnim($$0);
        this.mushrooms.visible = !$$0.isSheared;
    }
}

