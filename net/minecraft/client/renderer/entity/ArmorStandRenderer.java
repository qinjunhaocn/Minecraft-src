/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.joml.Quaternionfc;

public class ArmorStandRenderer
extends LivingEntityRenderer<ArmorStand, ArmorStandRenderState, ArmorStandArmorModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armorstand/wood.png");
    private final ArmorStandArmorModel bigModel = (ArmorStandArmorModel)this.getModel();
    private final ArmorStandArmorModel smallModel;

    public ArmorStandRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ArmorStandModel($$0.bakeLayer(ModelLayers.ARMOR_STAND)), 0.0f);
        this.smallModel = new ArmorStandModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_SMALL));
        this.addLayer(new HumanoidArmorLayer<ArmorStandRenderState, ArmorStandArmorModel, ArmorStandArmorModel>(this, new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)), new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_SMALL_INNER_ARMOR)), new ArmorStandArmorModel($$0.bakeLayer(ModelLayers.ARMOR_STAND_SMALL_OUTER_ARMOR)), $$0.getEquipmentRenderer()));
        this.addLayer(new ItemInHandLayer<ArmorStandRenderState, ArmorStandArmorModel>(this));
        this.addLayer(new WingsLayer<ArmorStandRenderState, ArmorStandArmorModel>(this, $$0.getModelSet(), $$0.getEquipmentRenderer()));
        this.addLayer(new CustomHeadLayer<ArmorStandRenderState, ArmorStandArmorModel>(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorStandRenderState $$0) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    public ArmorStandRenderState createRenderState() {
        return new ArmorStandRenderState();
    }

    @Override
    public void extractRenderState(ArmorStand $$0, ArmorStandRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HumanoidMobRenderer.extractHumanoidRenderState($$0, $$1, $$2, this.itemModelResolver);
        $$1.yRot = Mth.rotLerp($$2, $$0.yRotO, $$0.getYRot());
        $$1.isMarker = $$0.isMarker();
        $$1.isSmall = $$0.isSmall();
        $$1.showArms = $$0.showArms();
        $$1.showBasePlate = $$0.showBasePlate();
        $$1.bodyPose = $$0.getBodyPose();
        $$1.headPose = $$0.getHeadPose();
        $$1.leftArmPose = $$0.getLeftArmPose();
        $$1.rightArmPose = $$0.getRightArmPose();
        $$1.leftLegPose = $$0.getLeftLegPose();
        $$1.rightLegPose = $$0.getRightLegPose();
        $$1.wiggle = (float)($$0.level().getGameTime() - $$0.lastHit) + $$2;
    }

    @Override
    public void render(ArmorStandRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        this.model = $$0.isSmall ? this.smallModel : this.bigModel;
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    protected void setupRotations(ArmorStandRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$2));
        if ($$0.wiggle < 5.0f) {
            $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.sin($$0.wiggle / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean shouldShowName(ArmorStand $$0, double $$1) {
        return $$0.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderType getRenderType(ArmorStandRenderState $$0, boolean $$1, boolean $$2, boolean $$3) {
        if (!$$0.isMarker) {
            return super.getRenderType($$0, $$1, $$2, $$3);
        }
        ResourceLocation $$4 = this.getTextureLocation($$0);
        if ($$2) {
            return RenderType.entityTranslucent($$4, false);
        }
        if ($$1) {
            return RenderType.entityCutoutNoCull($$4, false);
        }
        return null;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ArmorStandRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

