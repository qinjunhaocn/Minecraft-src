/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class HumanoidArmorModel<S extends HumanoidRenderState>
extends HumanoidModel<S> {
    public HumanoidArmorModel(ModelPart $$0) {
        super($$0);
    }

    public static MeshDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = HumanoidModel.createMesh($$0, 0.0f);
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(-0.1f)), PartPose.offset(-1.9f, 12.0f, 0.0f));
        $$2.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, $$0.extend(-0.1f)), PartPose.offset(1.9f, 12.0f, 0.0f));
        return $$1;
    }
}

