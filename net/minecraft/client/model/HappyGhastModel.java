/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;

public class HappyGhastModel
extends EntityModel<HappyGhastRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = MeshTransformer.scaling(0.2375f);
    private static final float BODY_SQUEEZE = 0.9375f;
    private final ModelPart[] tentacles = new ModelPart[9];
    private final ModelPart body;

    public HappyGhastModel(ModelPart $$0) {
        super($$0);
        this.body = $$0.getChild("body");
        for (int $$1 = 0; $$1 < this.tentacles.length; ++$$1) {
            this.tentacles[$$1] = this.body.getChild(PartNames.tentacle($$1));
        }
    }

    public static LayerDefinition createBodyLayer(boolean $$0, CubeDeformation $$1) {
        MeshDefinition $$2 = new MeshDefinition();
        PartDefinition $$3 = $$2.getRoot();
        PartDefinition $$4 = $$3.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f, $$1), PartPose.offset(0.0f, 16.0f, 0.0f));
        if ($$0) {
            $$4.addOrReplaceChild("inner_body", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f, $$1.extend(-0.5f)), PartPose.offset(0.0f, 8.0f, 0.0f));
        }
        $$4.addOrReplaceChild(PartNames.tentacle(0), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, $$1), PartPose.offset(-3.75f, 7.0f, -5.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(1), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, $$1), PartPose.offset(1.25f, 7.0f, -5.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(2), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f, $$1), PartPose.offset(6.25f, 7.0f, -5.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(3), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, $$1), PartPose.offset(-6.25f, 7.0f, 0.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(4), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, $$1), PartPose.offset(-1.25f, 7.0f, 0.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(5), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, $$1), PartPose.offset(3.75f, 7.0f, 0.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(6), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, $$1), PartPose.offset(-3.75f, 7.0f, 5.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(7), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, $$1), PartPose.offset(1.25f, 7.0f, 5.0f));
        $$4.addOrReplaceChild(PartNames.tentacle(8), CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, $$1), PartPose.offset(6.25f, 7.0f, 5.0f));
        return LayerDefinition.create($$2, 64, 64).apply(MeshTransformer.scaling(4.0f));
    }

    @Override
    public void setupAnim(HappyGhastRenderState $$0) {
        super.setupAnim($$0);
        if (!$$0.bodyItem.isEmpty()) {
            this.body.xScale = 0.9375f;
            this.body.yScale = 0.9375f;
            this.body.zScale = 0.9375f;
        }
        GhastModel.a($$0, this.tentacles);
    }
}

