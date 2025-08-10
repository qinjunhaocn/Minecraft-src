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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.util.Mth;

public class ShulkerModel
extends EntityModel<ShulkerRenderState> {
    public static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerModel(ModelPart $$0) {
        super($$0, RenderType::entityCutoutNoCullZOffset);
        this.lid = $$0.getChild(LID);
        this.head = $$0.getChild("head");
    }

    private static MeshDefinition createShellMesh() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        $$1.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 28).addBox(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), PartPose.offset(0.0f, 24.0f, 0.0f));
        return $$0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = ShulkerModel.createShellMesh();
        $$0.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), PartPose.offset(0.0f, 12.0f, 0.0f));
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createBoxLayer() {
        MeshDefinition $$0 = ShulkerModel.createShellMesh();
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void setupAnim(ShulkerRenderState $$0) {
        super.setupAnim($$0);
        float $$1 = (0.5f + $$0.peekAmount) * (float)Math.PI;
        float $$2 = -1.0f + Mth.sin($$1);
        float $$3 = 0.0f;
        if ($$1 > (float)Math.PI) {
            $$3 = Mth.sin($$0.ageInTicks * 0.1f) * 0.7f;
        }
        this.lid.setPos(0.0f, 16.0f + Mth.sin($$1) * 8.0f + $$3, 0.0f);
        this.lid.yRot = $$0.peekAmount > 0.3f ? $$2 * $$2 * $$2 * $$2 * (float)Math.PI * 0.125f : 0.0f;
        this.head.xRot = $$0.xRot * ((float)Math.PI / 180);
        this.head.yRot = ($$0.yHeadRot - 180.0f - $$0.yBodyRot) * ((float)Math.PI / 180);
    }
}

