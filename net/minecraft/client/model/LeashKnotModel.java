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

public class LeashKnotModel
extends EntityModel<EntityRenderState> {
    private static final String KNOT = "knot";
    private final ModelPart knot;

    public LeashKnotModel(ModelPart $$0) {
        super($$0);
        this.knot = $$0.getChild(KNOT);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(KNOT, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -8.0f, -3.0f, 6.0f, 8.0f, 6.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 32, 32);
    }
}

