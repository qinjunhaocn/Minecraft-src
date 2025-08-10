/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.util.Mth;

public class TropicalFishModelB
extends EntityModel<TropicalFishRenderState> {
    private final ModelPart tail;

    public TropicalFishModelB(ModelPart $$0) {
        super($$0);
        this.tail = $$0.getChild("tail");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        int $$3 = 19;
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0f, -3.0f, -3.0f, 2.0f, 6.0f, 6.0f, $$0), PartPose.offset(0.0f, 19.0f, 0.0f));
        $$2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(21, 16).addBox(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 5.0f, $$0), PartPose.offset(0.0f, 19.0f, 3.0f));
        $$2.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(2, 16).addBox(-2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, $$0), PartPose.offsetAndRotation(-1.0f, 20.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        $$2.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(2, 12).addBox(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, $$0), PartPose.offsetAndRotation(1.0f, 20.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        $$2.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(20, 11).addBox(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f, $$0), PartPose.offset(0.0f, 16.0f, -3.0f));
        $$2.addOrReplaceChild("bottom_fin", CubeListBuilder.create().texOffs(20, 21).addBox(0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 6.0f, $$0), PartPose.offset(0.0f, 22.0f, -3.0f));
        return LayerDefinition.create($$1, 32, 32);
    }

    @Override
    public void setupAnim(TropicalFishRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.isInWater ? 1.0f : 1.5f;
        this.tail.yRot = -$$1 * 0.45f * Mth.sin(0.6f * $$0.ageInTicks);
    }
}

