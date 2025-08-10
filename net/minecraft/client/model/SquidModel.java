/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.SquidRenderState;

public class SquidModel
extends EntityModel<SquidRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.5f);
    private final ModelPart[] tentacles = new ModelPart[8];

    public SquidModel(ModelPart $$0) {
        super($$0);
        Arrays.setAll(this.tentacles, $$1 -> $$0.getChild(SquidModel.createTentacleName($$1)));
    }

    private static String createTentacleName(int $$0) {
        return "tentacle" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        CubeDeformation $$2 = new CubeDeformation(0.02f);
        int $$3 = -16;
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f, $$2), PartPose.offset(0.0f, 8.0f, 0.0f));
        int $$4 = 8;
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
        for (int $$6 = 0; $$6 < 8; ++$$6) {
            double $$7 = (double)$$6 * Math.PI * 2.0 / 8.0;
            float $$8 = (float)Math.cos($$7) * 5.0f;
            float $$9 = 15.0f;
            float $$10 = (float)Math.sin($$7) * 5.0f;
            $$7 = (double)$$6 * Math.PI * -2.0 / 8.0 + 1.5707963267948966;
            float $$11 = (float)$$7;
            $$1.addOrReplaceChild(SquidModel.createTentacleName($$6), $$5, PartPose.offsetAndRotation($$8, 15.0f, $$10, 0.0f, $$11, 0.0f));
        }
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(SquidRenderState $$0) {
        super.setupAnim($$0);
        for (ModelPart $$1 : this.tentacles) {
            $$1.xRot = $$0.tentacleAngle;
        }
    }
}

