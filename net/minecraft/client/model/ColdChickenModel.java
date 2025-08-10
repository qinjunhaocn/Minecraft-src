/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class ColdChickenModel
extends ChickenModel {
    public ColdChickenModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = ColdChickenModel.createBaseChickenModel();
        $$0.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f).texOffs(38, 9).addBox(0.0f, 3.0f, -1.0f, 0.0f, 3.0f, 5.0f), PartPose.offsetAndRotation(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        $$0.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f).texOffs(44, 0).addBox(-3.0f, -7.0f, -2.015f, 6.0f, 3.0f, 4.0f), PartPose.offset(0.0f, 15.0f, -4.0f));
        return LayerDefinition.create($$0, 64, 32);
    }
}

