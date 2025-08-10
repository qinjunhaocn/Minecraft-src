/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Set;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.util.Mth;

public abstract class AbstractEquineModel<T extends EquineRenderState>
extends EntityModel<T> {
    private static final float DEG_125 = 2.1816616f;
    private static final float DEG_60 = 1.0471976f;
    private static final float DEG_45 = 0.7853982f;
    private static final float DEG_30 = 0.5235988f;
    private static final float DEG_15 = 0.2617994f;
    protected static final String HEAD_PARTS = "head_parts";
    protected static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f, Set.of((Object)"head_parts"));
    protected final ModelPart body;
    protected final ModelPart headParts;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;

    public AbstractEquineModel(ModelPart $$0) {
        super($$0);
        this.body = $$0.getChild("body");
        this.headParts = $$0.getChild(HEAD_PARTS);
        this.rightHindLeg = $$0.getChild("right_hind_leg");
        this.leftHindLeg = $$0.getChild("left_hind_leg");
        this.rightFrontLeg = $$0.getChild("right_front_leg");
        this.leftFrontLeg = $$0.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static MeshDefinition createBodyMesh(CubeDeformation $$0) {
        MeshDefinition $$1 = new MeshDefinition();
        PartDefinition $$2 = $$1.getRoot();
        PartDefinition $$3 = $$2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, new CubeDeformation(0.05f)), PartPose.offset(0.0f, 11.0f, 5.0f));
        PartDefinition $$4 = $$2.addOrReplaceChild(HEAD_PARTS, CubeListBuilder.create().texOffs(0, 35).addBox(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f), PartPose.offsetAndRotation(0.0f, 4.0f, -12.0f, 0.5235988f, 0.0f, 0.0f));
        PartDefinition $$5 = $$4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, $$0), PartPose.ZERO);
        $$4.addOrReplaceChild("upper_mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, $$0), PartPose.ZERO);
        $$2.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(-4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(4.0f, 14.0f, -10.0f));
        $$2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$0), PartPose.offset(-4.0f, 14.0f, -10.0f));
        $$3.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, $$0), PartPose.offsetAndRotation(0.0f, -5.0f, 2.0f, 0.5235988f, 0.0f, 0.0f));
        $$5.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new CubeDeformation(-0.001f)), PartPose.ZERO);
        $$5.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new CubeDeformation(-0.001f)), PartPose.ZERO);
        return $$1;
    }

    public static MeshDefinition createBabyMesh(CubeDeformation $$0) {
        return BABY_TRANSFORMER.apply(AbstractEquineModel.createFullScaleBabyMesh($$0));
    }

    protected static MeshDefinition createFullScaleBabyMesh(CubeDeformation $$0) {
        MeshDefinition $$1 = AbstractEquineModel.createBodyMesh($$0);
        PartDefinition $$2 = $$1.getRoot();
        CubeDeformation $$3 = $$0.extend(0.0f, 5.5f, 0.0f);
        $$2.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$3), PartPose.offset(4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, $$3), PartPose.offset(-4.0f, 14.0f, 7.0f));
        $$2.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$3), PartPose.offset(4.0f, 14.0f, -10.0f));
        $$2.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, $$3), PartPose.offset(-4.0f, 14.0f, -10.0f));
        return $$1;
    }

    @Override
    public void setupAnim(T $$0) {
        super.setupAnim($$0);
        float $$1 = Mth.clamp(((EquineRenderState)$$0).yRot, -20.0f, 20.0f);
        float $$2 = ((EquineRenderState)$$0).xRot * ((float)Math.PI / 180);
        float $$3 = ((EquineRenderState)$$0).walkAnimationSpeed;
        float $$4 = ((EquineRenderState)$$0).walkAnimationPos;
        if ($$3 > 0.2f) {
            $$2 += Mth.cos($$4 * 0.8f) * 0.15f * $$3;
        }
        float $$5 = ((EquineRenderState)$$0).eatAnimation;
        float $$6 = ((EquineRenderState)$$0).standAnimation;
        float $$7 = 1.0f - $$6;
        float $$8 = ((EquineRenderState)$$0).feedingAnimation;
        boolean $$9 = ((EquineRenderState)$$0).animateTail;
        this.headParts.xRot = 0.5235988f + $$2;
        this.headParts.yRot = $$1 * ((float)Math.PI / 180);
        float $$10 = ((EquineRenderState)$$0).isInWater ? 0.2f : 1.0f;
        float $$11 = Mth.cos($$10 * $$4 * 0.6662f + (float)Math.PI);
        float $$12 = $$11 * 0.8f * $$3;
        float $$13 = (1.0f - Math.max($$6, $$5)) * (0.5235988f + $$2 + $$8 * Mth.sin(((EquineRenderState)$$0).ageInTicks) * 0.05f);
        this.headParts.xRot = $$6 * (0.2617994f + $$2) + $$5 * (2.1816616f + Mth.sin(((EquineRenderState)$$0).ageInTicks) * 0.05f) + $$13;
        this.headParts.yRot = $$6 * $$1 * ((float)Math.PI / 180) + (1.0f - Math.max($$6, $$5)) * this.headParts.yRot;
        float $$14 = ((EquineRenderState)$$0).ageScale;
        this.headParts.y += Mth.lerp($$5, Mth.lerp($$6, 0.0f, -8.0f * $$14), 7.0f * $$14);
        this.headParts.z = Mth.lerp($$6, this.headParts.z, -4.0f * $$14);
        this.body.xRot = $$6 * -0.7853982f + $$7 * this.body.xRot;
        float $$15 = 0.2617994f * $$6;
        float $$16 = Mth.cos(((EquineRenderState)$$0).ageInTicks * 0.6f + (float)Math.PI);
        this.leftFrontLeg.y -= 12.0f * $$14 * $$6;
        this.leftFrontLeg.z += 4.0f * $$14 * $$6;
        this.rightFrontLeg.y = this.leftFrontLeg.y;
        this.rightFrontLeg.z = this.leftFrontLeg.z;
        float $$17 = (-1.0471976f + $$16) * $$6 + $$12 * $$7;
        float $$18 = (-1.0471976f - $$16) * $$6 - $$12 * $$7;
        this.leftHindLeg.xRot = $$15 - $$11 * 0.5f * $$3 * $$7;
        this.rightHindLeg.xRot = $$15 + $$11 * 0.5f * $$3 * $$7;
        this.leftFrontLeg.xRot = $$17;
        this.rightFrontLeg.xRot = $$18;
        this.tail.xRot = 0.5235988f + $$3 * 0.75f;
        this.tail.y += $$3 * $$14;
        this.tail.z += $$3 * 2.0f * $$14;
        this.tail.yRot = $$9 ? Mth.cos(((EquineRenderState)$$0).ageInTicks * 0.7f) : 0.0f;
    }
}

