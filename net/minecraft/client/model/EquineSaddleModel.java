/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;

public class EquineSaddleModel
extends AbstractEquineModel<EquineRenderState> {
    private static final String SADDLE = "saddle";
    private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    private static final String LEFT_SADDLE_LINE = "left_saddle_line";
    private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
    private static final String HEAD_SADDLE = "head_saddle";
    private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    private final ModelPart[] ridingParts;

    public EquineSaddleModel(ModelPart $$0) {
        super($$0);
        ModelPart $$1 = this.headParts.getChild(LEFT_SADDLE_LINE);
        ModelPart $$2 = this.headParts.getChild(RIGHT_SADDLE_LINE);
        this.ridingParts = new ModelPart[]{$$1, $$2};
    }

    public static LayerDefinition createSaddleLayer(boolean $$0) {
        return EquineSaddleModel.createFullScaleSaddleLayer($$0).apply($$0 ? BABY_TRANSFORMER : MeshTransformer.IDENTITY);
    }

    public static LayerDefinition createFullScaleSaddleLayer(boolean $$0) {
        MeshDefinition $$1 = $$0 ? EquineSaddleModel.createFullScaleBabyMesh(CubeDeformation.NONE) : EquineSaddleModel.createBodyMesh(CubeDeformation.NONE);
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.getChild("body");
        PartDefinition $$4 = $$2.getChild("head_parts");
        $$3.addOrReplaceChild(SADDLE, CubeListBuilder.create().texOffs(26, 0).addBox(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new CubeDeformation(0.5f)), PartPose.ZERO);
        $$4.addOrReplaceChild(LEFT_SADDLE_MOUTH, CubeListBuilder.create().texOffs(29, 5).addBox(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$4.addOrReplaceChild(RIGHT_SADDLE_MOUTH, CubeListBuilder.create().texOffs(29, 5).addBox(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), PartPose.ZERO);
        $$4.addOrReplaceChild(LEFT_SADDLE_LINE, CubeListBuilder.create().texOffs(32, 2).addBox(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), PartPose.rotation(-0.5235988f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(RIGHT_SADDLE_LINE, CubeListBuilder.create().texOffs(32, 2).addBox(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), PartPose.rotation(-0.5235988f, 0.0f, 0.0f));
        $$4.addOrReplaceChild(HEAD_SADDLE, CubeListBuilder.create().texOffs(1, 1).addBox(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new CubeDeformation(0.22f)), PartPose.ZERO);
        $$4.addOrReplaceChild(MOUTH_SADDLE_WRAP, CubeListBuilder.create().texOffs(19, 0).addBox(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new CubeDeformation(0.2f)), PartPose.ZERO);
        return LayerDefinition.create($$1, 64, 64);
    }

    @Override
    public void setupAnim(EquineRenderState $$0) {
        super.setupAnim($$0);
        for (ModelPart $$1 : this.ridingParts) {
            $$1.visible = $$0.isRidden;
        }
    }
}

