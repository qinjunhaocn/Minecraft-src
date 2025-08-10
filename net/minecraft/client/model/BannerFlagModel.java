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
import net.minecraft.util.Mth;

public class BannerFlagModel
extends Model {
    private final ModelPart flag;

    public BannerFlagModel(ModelPart $$0) {
        super($$0, RenderType::entitySolid);
        this.flag = $$0.getChild("flag");
    }

    public static LayerDefinition createFlagLayer(boolean $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f), PartPose.offset(0.0f, $$0 ? -44.0f : -20.5f, $$0 ? 0.0f : 10.5f));
        return LayerDefinition.create($$1, 64, 64);
    }

    public void setupAnim(float $$0) {
        this.flag.xRot = (-0.0125f + 0.01f * Mth.cos((float)Math.PI * 2 * $$0)) * (float)Math.PI;
    }
}

