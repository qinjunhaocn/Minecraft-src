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
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

public class SpinAttackEffectModel
extends EntityModel<PlayerRenderState> {
    private static final int BOX_COUNT = 2;
    private final ModelPart[] boxes = new ModelPart[2];

    public SpinAttackEffectModel(ModelPart $$0) {
        super($$0);
        for (int $$1 = 0; $$1 < 2; ++$$1) {
            this.boxes[$$1] = $$0.getChild(SpinAttackEffectModel.boxName($$1));
        }
    }

    private static String boxName(int $$0) {
        return "box" + $$0;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        for (int $$2 = 0; $$2 < 2; ++$$2) {
            float $$3 = -3.2f + 9.6f * (float)($$2 + 1);
            float $$4 = 0.75f * (float)($$2 + 1);
            $$1.addOrReplaceChild(SpinAttackEffectModel.boxName($$2), CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -16.0f + $$3, -8.0f, 16.0f, 32.0f, 16.0f), PartPose.ZERO.withScale($$4));
        }
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(PlayerRenderState $$0) {
        super.setupAnim($$0);
        for (int $$1 = 0; $$1 < this.boxes.length; ++$$1) {
            float $$2 = $$0.ageInTicks * (float)(-(45 + ($$1 + 1) * 5));
            this.boxes[$$1].yRot = Mth.wrapDegrees($$2) * ((float)Math.PI / 180);
        }
    }
}

