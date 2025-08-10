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
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class CodModel
extends EntityModel<LivingEntityRenderState> {
    private final ModelPart tailFin;

    public CodModel(ModelPart $$0) {
        super($$0);
        this.tailFin = $$0.getChild("tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        int $$2 = 22;
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), PartPose.offset(0.0f, 22.0f, 0.0f));
        $$1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f), PartPose.offset(0.0f, 22.0f, -3.0f));
        $$1.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(22, 1).addBox(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), PartPose.offsetAndRotation(-1.0f, 23.0f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        $$1.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(22, 4).addBox(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), PartPose.offsetAndRotation(1.0f, 23.0f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        $$1.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(22, 3).addBox(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f), PartPose.offset(0.0f, 22.0f, 7.0f));
        $$1.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(20, -6).addBox(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f), PartPose.offset(0.0f, 20.0f, 0.0f));
        return LayerDefinition.create($$0, 32, 32);
    }

    @Override
    public void setupAnim(LivingEntityRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.isInWater ? 1.0f : 1.5f;
        this.tailFin.yRot = -$$1 * 0.45f * Mth.sin(0.6f * $$0.ageInTicks);
    }
}

