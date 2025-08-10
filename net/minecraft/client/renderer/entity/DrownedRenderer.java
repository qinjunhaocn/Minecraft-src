/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionfc;

public class DrownedRenderer
extends AbstractZombieRenderer<Drowned, ZombieRenderState, DrownedModel> {
    private static final ResourceLocation DROWNED_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned.png");

    public DrownedRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_BABY)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_BABY_INNER_ARMOR)), new DrownedModel($$0.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_ARMOR)));
        this.addLayer(new DrownedOuterLayer(this, $$0.getModelSet()));
    }

    @Override
    public ZombieRenderState createRenderState() {
        return new ZombieRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieRenderState $$0) {
        return DROWNED_LOCATION;
    }

    @Override
    protected void setupRotations(ZombieRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2, $$3);
        float $$4 = $$0.swimAmount;
        if ($$4 > 0.0f) {
            float $$5 = -10.0f - $$0.xRot;
            float $$6 = Mth.lerp($$4, 0.0f, $$5);
            $$1.rotateAround((Quaternionfc)Axis.XP.rotationDegrees($$6), 0.0f, $$0.boundingBoxHeight / 2.0f / $$3, 0.0f);
        }
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(Drowned $$0, HumanoidArm $$1) {
        ItemStack $$2 = $$0.getItemHeldByArm($$1);
        if ($$0.getMainArm() == $$1 && $$0.isAggressive() && $$2.is(Items.TRIDENT)) {
            return HumanoidModel.ArmPose.THROW_SPEAR;
        }
        return HumanoidModel.ArmPose.EMPTY;
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ZombieRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

