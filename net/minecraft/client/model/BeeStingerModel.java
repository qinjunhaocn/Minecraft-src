/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class BeeStingerModel
extends Model {
    public BeeStingerModel(ModelPart $$0) {
        super($$0, RenderType::entityCutout);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f);
        $$1.addOrReplaceChild("cross_1", $$2, PartPose.rotation(0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("cross_2", $$2, PartPose.rotation(2.3561945f, 0.0f, 0.0f));
        return LayerDefinition.create($$0, 16, 16);
    }
}

