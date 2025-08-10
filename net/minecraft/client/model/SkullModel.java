/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SkullModel
extends SkullModelBase {
    protected final ModelPart head;

    public SkullModel(ModelPart $$0) {
        super($$0);
        this.head = $$0.getChild("head");
    }

    public static MeshDefinition createHeadModel() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), PartPose.ZERO);
        return $$0;
    }

    public static LayerDefinition createHumanoidHeadLayer() {
        MeshDefinition $$0 = SkullModel.createHeadModel();
        PartDefinition $$1 = $$0.getRoot();
        $$1.getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.25f)), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createMobHeadLayer() {
        MeshDefinition $$0 = SkullModel.createHeadModel();
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(float $$0, float $$1, float $$2) {
        this.head.yRot = $$1 * ((float)Math.PI / 180);
        this.head.xRot = $$2 * ((float)Math.PI / 180);
    }
}

