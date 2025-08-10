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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.util.Mth;

public class ArrowModel
extends EntityModel<ArrowRenderState> {
    public ArrowModel(ModelPart $$0) {
        super($$0, RenderType::entityCutout);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$02 = new MeshDefinition();
        PartDefinition $$1 = $$02.getRoot();
        $$1.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, -2.5f, -2.5f, 0.0f, 5.0f, 5.0f), PartPose.offsetAndRotation(-11.0f, 0.0f, 0.0f, 0.7853982f, 0.0f, 0.0f).withScale(0.8f));
        CubeListBuilder $$2 = CubeListBuilder.create().texOffs(0, 0).addBox(-12.0f, -2.0f, 0.0f, 16.0f, 4.0f, 0.0f, CubeDeformation.NONE, 1.0f, 0.8f);
        $$1.addOrReplaceChild("cross_1", $$2, PartPose.rotation(0.7853982f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("cross_2", $$2, PartPose.rotation(2.3561945f, 0.0f, 0.0f));
        return LayerDefinition.create($$02.transformed($$0 -> $$0.scaled(0.9f)), 32, 32);
    }

    @Override
    public void setupAnim(ArrowRenderState $$0) {
        super.setupAnim($$0);
        if ($$0.shake > 0.0f) {
            float $$1 = -Mth.sin($$0.shake * 3.0f) * $$0.shake;
            this.root.zRot += $$1 * ((float)Math.PI / 180);
        }
    }
}

