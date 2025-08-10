/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class GhastModel
extends EntityModel<GhastRenderState> {
    private final ModelPart[] tentacles = new ModelPart[9];

    public GhastModel(ModelPart $$0) {
        super($$0);
        for (int $$1 = 0; $$1 < this.tentacles.length; ++$$1) {
            this.tentacles[$$1] = $$0.getChild(PartNames.tentacle($$1));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), PartPose.offset(0.0f, 17.6f, 0.0f));
        RandomSource $$2 = RandomSource.create(1660L);
        for (int $$3 = 0; $$3 < 9; ++$$3) {
            float $$4 = (((float)($$3 % 3) - (float)($$3 / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float $$5 = ((float)($$3 / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int $$6 = $$2.nextInt(7) + 8;
            $$1.addOrReplaceChild(PartNames.tentacle($$3), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, $$6, 2.0f), PartPose.offset($$4, 24.6f, $$5));
        }
        return LayerDefinition.create($$0, 64, 32).apply(MeshTransformer.scaling(4.5f));
    }

    @Override
    public void setupAnim(GhastRenderState $$0) {
        super.setupAnim($$0);
        GhastModel.a($$0, this.tentacles);
    }

    public static void a(EntityRenderState $$0, ModelPart[] $$1) {
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            $$1[$$2].xRot = 0.2f * Mth.sin($$0.ageInTicks * 0.3f + (float)$$2) + 0.4f;
        }
    }
}

