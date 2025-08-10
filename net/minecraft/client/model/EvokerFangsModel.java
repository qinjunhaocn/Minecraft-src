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
import net.minecraft.client.renderer.entity.state.EvokerFangsRenderState;
import net.minecraft.util.Mth;

public class EvokerFangsModel
extends EntityModel<EvokerFangsRenderState> {
    private static final String BASE = "base";
    private static final String UPPER_JAW = "upper_jaw";
    private static final String LOWER_JAW = "lower_jaw";
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public EvokerFangsModel(ModelPart $$0) {
        super($$0);
        this.base = $$0.getChild(BASE);
        this.upperJaw = this.base.getChild(UPPER_JAW);
        this.lowerJaw = this.base.getChild(LOWER_JAW);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), PartPose.offset(-5.0f, 24.0f, -5.0f));
        CubeListBuilder $$3 = CubeListBuilder.create().texOffs(40, 0).addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        $$2.addOrReplaceChild(UPPER_JAW, $$3, PartPose.offsetAndRotation(6.5f, 0.0f, 1.0f, 0.0f, 0.0f, 2.042035f));
        $$2.addOrReplaceChild(LOWER_JAW, $$3, PartPose.offsetAndRotation(3.5f, 0.0f, 9.0f, 0.0f, (float)Math.PI, 4.2411504f));
        return LayerDefinition.create($$0, 64, 32);
    }

    @Override
    public void setupAnim(EvokerFangsRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.biteProgress;
        float $$2 = Math.min($$1 * 2.0f, 1.0f);
        $$2 = 1.0f - $$2 * $$2 * $$2;
        this.upperJaw.zRot = (float)Math.PI - $$2 * 0.35f * (float)Math.PI;
        this.lowerJaw.zRot = (float)Math.PI + $$2 * 0.35f * (float)Math.PI;
        this.base.y -= ($$1 + Mth.sin($$1 * 2.7f)) * 7.2f;
        float $$3 = 1.0f;
        if ($$1 > 0.9f) {
            $$3 *= (1.0f - $$1) / 0.1f;
        }
        this.root.y = 24.0f - 20.0f * $$3;
        this.root.xScale = $$3;
        this.root.yScale = $$3;
        this.root.zScale = $$3;
    }
}

