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
import net.minecraft.client.renderer.entity.state.GoatRenderState;

public class GoatModel
extends QuadrupedModel<GoatRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 19.0f, 1.0f, 2.5f, 2.0f, 24.0f, Set.of((Object)"head"));

    public GoatModel(ModelPart $$0) {
        super($$0);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 61).addBox("right ear", -6.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).texOffs(2, 61).mirror().addBox("left ear", 2.0f, -11.0f, -10.0f, 3.0f, 2.0f, 1.0f).texOffs(23, 52).addBox("goatee", -0.5f, -3.0f, -14.0f, 0.0f, 7.0f, 5.0f), PartPose.offset(1.0f, 14.0f, 0.0f));
        $$2.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(12, 55).addBox(-0.01f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(12, 55).addBox(-2.99f, -16.0f, -10.0f, 2.0f, 7.0f, 2.0f), PartPose.offset(0.0f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(34, 46).addBox(-3.0f, -4.0f, -8.0f, 5.0f, 7.0f, 10.0f), PartPose.offsetAndRotation(0.0f, -8.0f, -8.0f, 0.9599f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 1).addBox(-4.0f, -17.0f, -7.0f, 9.0f, 11.0f, 16.0f).texOffs(0, 28).addBox(-5.0f, -18.0f, -8.0f, 11.0f, 14.0f, 11.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$1.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(36, 29).addBox(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), PartPose.offset(1.0f, 14.0f, 4.0f));
        $$1.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(49, 29).addBox(0.0f, 4.0f, 0.0f, 3.0f, 6.0f, 3.0f), PartPose.offset(-3.0f, 14.0f, 4.0f));
        $$1.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(49, 2).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), PartPose.offset(1.0f, 14.0f, -6.0f));
        $$1.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(35, 2).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f), PartPose.offset(-3.0f, 14.0f, -6.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(GoatRenderState $$0) {
        super.setupAnim($$0);
        this.head.getChild((String)"left_horn").visible = $$0.hasLeftHorn;
        this.head.getChild((String)"right_horn").visible = $$0.hasRightHorn;
        if ($$0.rammingXHeadRot != 0.0f) {
            this.head.xRot = $$0.rammingXHeadRot;
        }
    }
}

