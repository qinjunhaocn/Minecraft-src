/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.PolarBearRenderState;

public class PolarBearModel
extends QuadrupedModel<PolarBearRenderState> {
    private static final float BABY_HEAD_SCALE = 2.25f;
    private static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.0f, 4.0f, 2.25f, 2.0f, 24.0f, Set.of((Object)"head"));

    public PolarBearModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer(boolean $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3.0f, -3.0f, 7.0f, 7.0f, 7.0f).texOffs(0, 44).addBox("mouth", -2.5f, 1.0f, -6.0f, 5.0f, 3.0f, 3.0f).texOffs(26, 0).addBox("right_ear", -4.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f).texOffs(26, 0).mirror().addBox("left_ear", 2.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f), PartPose.offset(0.0f, 10.0f, -16.0f));
        $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f).texOffs(39, 0).addBox(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f), PartPose.offsetAndRotation(-2.0f, 9.0f, 12.0f, 1.5707964f, 0.0f, 0.0f));
        int $$3 = 10;
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(50, 22).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f);
        $$2.addOrReplaceChild("right_hind_leg", $$4, PartPose.offset(-4.5f, 14.0f, 6.0f));
        $$2.addOrReplaceChild("left_hind_leg", $$4, PartPose.offset(4.5f, 14.0f, 6.0f));
        CubeListBuilder $$5 = CubeListBuilder.create().texOffs(50, 40).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f);
        $$2.addOrReplaceChild("right_front_leg", $$5, PartPose.offset(-3.5f, 14.0f, -8.0f));
        $$2.addOrReplaceChild("left_front_leg", $$5, PartPose.offset(3.5f, 14.0f, -8.0f));
        return LayerDefinition.create($$1, 128, 64).apply($$0 ? BABY_TRANSFORMER : MeshTransformer.IDENTITY).apply(MeshTransformer.scaling(1.2f));
    }

    @Override
    public void setupAnim(PolarBearRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = $$0.standScale * $$0.standScale;
        float $$2 = $$0.ageScale;
        float $$3 = $$0.isBaby ? 0.44444445f : 1.0f;
        this.body.xRot -= $$1 * (float)Math.PI * 0.35f;
        this.body.y += $$1 * $$2 * 2.0f;
        this.rightFrontLeg.y -= $$1 * $$2 * 20.0f;
        this.rightFrontLeg.z += $$1 * $$2 * 4.0f;
        this.rightFrontLeg.xRot -= $$1 * (float)Math.PI * 0.45f;
        this.leftFrontLeg.y = this.rightFrontLeg.y;
        this.leftFrontLeg.z = this.rightFrontLeg.z;
        this.leftFrontLeg.xRot -= $$1 * (float)Math.PI * 0.45f;
        this.head.y -= $$1 * $$3 * 24.0f;
        this.head.z += $$1 * $$3 * 13.0f;
        this.head.xRot += $$1 * (float)Math.PI * 0.15f;
    }
}

