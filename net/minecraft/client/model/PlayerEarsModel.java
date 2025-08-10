/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class PlayerEarsModel
extends HumanoidModel<PlayerRenderState> {
    public PlayerEarsModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createEarsLayer() {
        MeshDefinition $$0 = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.clearChild("head");
        $$2.clearChild("hat");
        $$1.clearChild("body");
        $$1.clearChild("left_arm");
        $$1.clearChild("right_arm");
        $$1.clearChild("left_leg");
        $$1.clearChild("right_leg");
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(24, 0).addBox(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, new CubeDeformation(1.0f));
        $$2.addOrReplaceChild("left_ear", $$3, PartPose.offset(-6.0f, -6.0f, 0.0f));
        $$2.addOrReplaceChild("right_ear", $$3, PartPose.offset(6.0f, -6.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }
}

