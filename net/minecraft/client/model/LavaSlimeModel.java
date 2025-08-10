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
import net.minecraft.client.renderer.entity.state.SlimeRenderState;

public class LavaSlimeModel
extends EntityModel<SlimeRenderState> {
    private static final int SEGMENT_COUNT = 8;
    private final ModelPart[] bodyCubes = new ModelPart[8];

    public LavaSlimeModel(ModelPart $$0) {
        super($$0);
        Arrays.setAll(this.bodyCubes, $$1 -> $$0.getChild(LavaSlimeModel.getSegmentName($$1)));
    }

    private static String getSegmentName(int $$0) {
        return "cube" + $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        for (int $$2 = 0; $$2 < 8; ++$$2) {
            int $$3 = 0;
            int $$4 = 0;
            if ($$2 > 0 && $$2 < 4) {
                $$4 += 9 * $$2;
            } else if ($$2 > 3) {
                $$3 = 32;
                $$4 += 9 * $$2 - 36;
            }
            $$1.addOrReplaceChild(LavaSlimeModel.getSegmentName($$2), CubeListBuilder.create().texOffs($$3, $$4).addBox(-4.0f, 16 + $$2, -4.0f, 8.0f, 1.0f, 8.0f), PartPose.ZERO);
        }
        $$1.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(24, 40).addBox(-2.0f, 18.0f, -2.0f, 4.0f, 4.0f, 4.0f), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(SlimeRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = Math.max(0.0f, $$0.squish);
        for (int $$2 = 0; $$2 < this.bodyCubes.length; ++$$2) {
            this.bodyCubes[$$2].y = (float)(-(4 - $$2)) * $$1 * 1.7f;
        }
    }
}

