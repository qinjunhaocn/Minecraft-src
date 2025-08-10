/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;

public class HappyGhastHarnessModel
extends EntityModel<HappyGhastRenderState> {
    private static final float GOGGLES_Y_OFFSET = 14.0f;
    private final ModelPart goggles;

    public HappyGhastHarnessModel(ModelPart $$0) {
        super($$0);
        this.goggles = $$0.getChild("goggles");
    }

    public static LayerDefinition createHarnessLayer(boolean $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("harness", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$2.addOrReplaceChild("goggles", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0f, -2.5f, -2.5f, 16.0f, 5.0f, 5.0f, new CubeDeformation(0.15f)), PartPose.offset(0.0f, 14.0f, -5.5f));
        return LayerDefinition.create($$1, 64, 64).apply(MeshTransformer.scaling(4.0f)).apply($$0 ? HappyGhastModel.BABY_TRANSFORMER : MeshTransformer.IDENTITY);
    }

    @Override
    public void setupAnim(HappyGhastRenderState $$0) {
        super.setupAnim($$0);
        if ($$0.isRidden) {
            this.goggles.xRot = 0.0f;
            this.goggles.y = 14.0f;
        } else {
            this.goggles.xRot = -0.7854f;
            this.goggles.y = 9.0f;
        }
    }
}

