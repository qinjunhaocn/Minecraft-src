/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;

public class SilverfishModel
extends EntityModel<EntityRenderState> {
    private static final int BODY_COUNT = 7;
    private final ModelPart[] bodyParts = new ModelPart[7];
    private final ModelPart[] bodyLayers = new ModelPart[3];
    private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishModel(ModelPart $$0) {
        super($$0);
        Arrays.setAll(this.bodyParts, $$1 -> $$0.getChild(SilverfishModel.getSegmentName($$1)));
        Arrays.setAll(this.bodyLayers, $$1 -> $$0.getChild(SilverfishModel.getLayerName($$1)));
    }

    private static String getLayerName(int $$0) {
        return "layer" + $$0;
    }

    private static String getSegmentName(int $$0) {
        return "segment" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        float[] $$2 = new float[7];
        float $$3 = -3.5f;
        for (int $$4 = 0; $$4 < 7; ++$$4) {
            $$1.addOrReplaceChild(SilverfishModel.getSegmentName($$4), CubeListBuilder.create().texOffs(BODY_TEXS[$$4][0], BODY_TEXS[$$4][1]).addBox((float)BODY_SIZES[$$4][0] * -0.5f, 0.0f, (float)BODY_SIZES[$$4][2] * -0.5f, BODY_SIZES[$$4][0], BODY_SIZES[$$4][1], BODY_SIZES[$$4][2]), PartPose.offset(0.0f, 24 - BODY_SIZES[$$4][1], $$3));
            $$2[$$4] = $$3;
            if ($$4 >= 6) continue;
            $$3 += (float)(BODY_SIZES[$$4][2] + BODY_SIZES[$$4 + 1][2]) * 0.5f;
        }
        $$1.addOrReplaceChild(SilverfishModel.getLayerName(0), CubeListBuilder.create().texOffs(20, 0).addBox(-5.0f, 0.0f, (float)BODY_SIZES[2][2] * -0.5f, 10.0f, 8.0f, BODY_SIZES[2][2]), PartPose.offset(0.0f, 16.0f, $$2[2]));
        $$1.addOrReplaceChild(SilverfishModel.getLayerName(1), CubeListBuilder.create().texOffs(20, 11).addBox(-3.0f, 0.0f, (float)BODY_SIZES[4][2] * -0.5f, 6.0f, 4.0f, BODY_SIZES[4][2]), PartPose.offset(0.0f, 20.0f, $$2[4]));
        $$1.addOrReplaceChild(SilverfishModel.getLayerName(2), CubeListBuilder.create().texOffs(20, 18).addBox(-3.0f, 0.0f, (float)BODY_SIZES[4][2] * -0.5f, 6.0f, 5.0f, BODY_SIZES[1][2]), PartPose.offset(0.0f, 19.0f, $$2[1]));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(EntityRenderState $$0) {
        super.setupAnim($$0);
        for (int $$1 = 0; $$1 < this.bodyParts.length; ++$$1) {
            this.bodyParts[$$1].yRot = Mth.cos($$0.ageInTicks * 0.9f + (float)$$1 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.05f * (float)(1 + Math.abs($$1 - 2));
            this.bodyParts[$$1].x = Mth.sin($$0.ageInTicks * 0.9f + (float)$$1 * 0.15f * (float)Math.PI) * (float)Math.PI * 0.2f * (float)Math.abs($$1 - 2);
        }
        this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
        this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
        this.bodyLayers[1].x = this.bodyParts[4].x;
        this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
        this.bodyLayers[2].x = this.bodyParts[1].x;
    }
}

