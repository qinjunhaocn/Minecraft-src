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
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;

public class PufferfishMidModel
extends EntityModel<EntityRenderState> {
    private final ModelPart leftBlueFin;
    private final ModelPart rightBlueFin;

    public PufferfishMidModel(ModelPart $$0) {
        super($$0);
        this.leftBlueFin = $$0.getChild("left_blue_fin");
        this.rightBlueFin = $$0.getChild("right_blue_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 22;
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 22).addBox(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$1.addOrReplaceChild("right_blue_fin", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), PartPose.offset(-2.5f, 18.0f, -1.5f));
        $$1.addOrReplaceChild("left_blue_fin", CubeListBuilder.create().texOffs(24, 3).addBox(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), PartPose.offset(2.5f, 18.0f, -1.5f));
        $$1.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(19, 17).addBox(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 17.0f, -2.5f, 0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(11, 17).addBox(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(0.0f, 17.0f, 2.5f, -0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_front_fin", CubeListBuilder.create().texOffs(5, 17).addBox(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), PartPose.offsetAndRotation(-2.5f, 22.0f, -2.5f, 0.0f, -0.7853982f, 0.0f));
        $$1.addOrReplaceChild("right_back_fin", CubeListBuilder.create().texOffs(9, 17).addBox(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), PartPose.offsetAndRotation(-2.5f, 22.0f, 2.5f, 0.0f, 0.7853982f, 0.0f));
        $$1.addOrReplaceChild("left_back_fin", CubeListBuilder.create().texOffs(1, 17).addBox(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), PartPose.offsetAndRotation(2.5f, 22.0f, 2.5f, 0.0f, -0.7853982f, 0.0f));
        $$1.addOrReplaceChild("left_front_fin", CubeListBuilder.create().texOffs(1, 17).addBox(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f), PartPose.offsetAndRotation(2.5f, 22.0f, -2.5f, 0.0f, 0.7853982f, 0.0f));
        $$1.addOrReplaceChild("bottom_back_fin", CubeListBuilder.create().texOffs(18, 20).addBox(0.0f, 0.0f, 0.0f, 5.0f, 1.0f, 0.0f), PartPose.offsetAndRotation(-2.5f, 22.0f, 2.5f, 0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("bottom_front_fin", CubeListBuilder.create().texOffs(17, 19).addBox(-2.5f, 0.0f, 0.0f, 5.0f, 1.0f, 1.0f), PartPose.offsetAndRotation(0.0f, 22.0f, -2.5f, -0.7853982f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(EntityRenderState $$0) {
        super.setupAnim($$0);
        this.rightBlueFin.zRot = -0.2f + 0.4f * Mth.sin($$0.ageInTicks * 0.2f);
        this.leftBlueFin.zRot = 0.2f - 0.4f * Mth.sin($$0.ageInTicks * 0.2f);
    }
}

