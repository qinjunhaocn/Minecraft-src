/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.util.Mth;

public class WitherBossModel
extends EntityModel<WitherRenderState> {
    private static final String RIBCAGE = "ribcage";
    private static final String CENTER_HEAD = "center_head";
    private static final String RIGHT_HEAD = "right_head";
    private static final String LEFT_HEAD = "left_head";
    private static final float RIBCAGE_X_ROT_OFFSET = 0.065f;
    private static final float TAIL_X_ROT_OFFSET = 0.265f;
    private final ModelPart centerHead;
    private final ModelPart rightHead;
    private final ModelPart leftHead;
    private final ModelPart ribcage;
    private final ModelPart tail;

    public WitherBossModel(ModelPart $$0) {
        super($$0);
        this.ribcage = $$0.getChild(RIBCAGE);
        this.tail = $$0.getChild("tail");
        this.centerHead = $$0.getChild(CENTER_HEAD);
        this.rightHead = $$0.getChild(RIGHT_HEAD);
        this.leftHead = $$0.getChild(LEFT_HEAD);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        $$2.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 16).addBox(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, $$0), PartPose.ZERO);
        float $$3 = 0.20420352f;
        $$2.addOrReplaceChild(RIBCAGE, CubeListBuilder.create().texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, $$0).texOffs(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, $$0).texOffs(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, $$0).texOffs(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, $$0), PartPose.offsetAndRotation(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        $$2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, $$0), PartPose.offsetAndRotation(-2.0f, 6.9f + Mth.cos(0.20420352f) * 10.0f, -0.5f + Mth.sin(0.20420352f) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        $$2.addOrReplaceChild(CENTER_HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, $$0), PartPose.ZERO);
        CubeListBuilder $$4 = CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, $$0);
        $$2.addOrReplaceChild(RIGHT_HEAD, $$4, PartPose.offset(-8.0f, 4.0f, 0.0f));
        $$2.addOrReplaceChild(LEFT_HEAD, $$4, PartPose.offset(10.0f, 4.0f, 0.0f));
        return LayerDefinition.create($$1, 64, 64);
    }

    @Override
    public void setupAnim(WitherRenderState $$0) {
        super.setupAnim($$0);
        WitherBossModel.setupHeadRotation($$0, this.rightHead, 0);
        WitherBossModel.setupHeadRotation($$0, this.leftHead, 1);
        float $$1 = Mth.cos($$0.ageInTicks * 0.1f);
        this.ribcage.xRot = (0.065f + 0.05f * $$1) * (float)Math.PI;
        this.tail.setPos(-2.0f, 6.9f + Mth.cos(this.ribcage.xRot) * 10.0f, -0.5f + Mth.sin(this.ribcage.xRot) * 10.0f);
        this.tail.xRot = (0.265f + 0.1f * $$1) * (float)Math.PI;
        this.centerHead.yRot = $$0.yRot * ((float)Math.PI / 180);
        this.centerHead.xRot = $$0.xRot * ((float)Math.PI / 180);
    }

    private static void setupHeadRotation(WitherRenderState $$0, ModelPart $$1, int $$2) {
        $$1.yRot = ($$0.yHeadRots[$$2] - $$0.bodyRot) * ((float)Math.PI / 180);
        $$1.xRot = $$0.xHeadRots[$$2] * ((float)Math.PI / 180);
    }
}

