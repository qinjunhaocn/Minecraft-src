/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public abstract class HumanoidMobRenderer<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>>
extends AgeableMobRenderer<T, S, M> {
    public HumanoidMobRenderer(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        this($$0, $$1, $$1, $$2);
    }

    public HumanoidMobRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2, float $$3) {
        this($$0, $$1, $$2, $$3, CustomHeadLayer.Transforms.DEFAULT);
    }

    public HumanoidMobRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2, float $$3, CustomHeadLayer.Transforms $$4) {
        super($$0, $$1, $$2, $$3);
        this.addLayer(new CustomHeadLayer(this, $$0.getModelSet(), $$4));
        this.addLayer(new WingsLayer(this, $$0.getModelSet(), $$0.getEquipmentRenderer()));
        this.addLayer(new ItemInHandLayer(this));
    }

    protected HumanoidModel.ArmPose getArmPose(T $$0, HumanoidArm $$1) {
        return HumanoidModel.ArmPose.EMPTY;
    }

    @Override
    public void extractRenderState(T $$0, S $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        HumanoidMobRenderer.extractHumanoidRenderState($$0, $$1, $$2, this.itemModelResolver);
        ((HumanoidRenderState)$$1).leftArmPose = this.getArmPose($$0, HumanoidArm.LEFT);
        ((HumanoidRenderState)$$1).rightArmPose = this.getArmPose($$0, HumanoidArm.RIGHT);
    }

    public static void extractHumanoidRenderState(LivingEntity $$0, HumanoidRenderState $$1, float $$2, ItemModelResolver $$3) {
        ArmedEntityRenderState.extractArmedEntityRenderState($$0, $$1, $$3);
        $$1.isCrouching = $$0.isCrouching();
        $$1.isFallFlying = $$0.isFallFlying();
        $$1.isVisuallySwimming = $$0.isVisuallySwimming();
        $$1.isPassenger = $$0.isPassenger();
        $$1.speedValue = 1.0f;
        if ($$1.isFallFlying) {
            $$1.speedValue = (float)$$0.getDeltaMovement().lengthSqr();
            $$1.speedValue /= 0.2f;
            $$1.speedValue *= $$1.speedValue * $$1.speedValue;
        }
        if ($$1.speedValue < 1.0f) {
            $$1.speedValue = 1.0f;
        }
        $$1.attackTime = $$0.getAttackAnim($$2);
        $$1.swimAmount = $$0.getSwimAmount($$2);
        $$1.attackArm = HumanoidMobRenderer.getAttackArm($$0);
        $$1.useItemHand = $$0.getUsedItemHand();
        $$1.maxCrossbowChargeDuration = CrossbowItem.getChargeDuration($$0.getUseItem(), $$0);
        $$1.ticksUsingItem = $$0.getTicksUsingItem();
        $$1.isUsingItem = $$0.isUsingItem();
        $$1.elytraRotX = $$0.elytraAnimationState.getRotX($$2);
        $$1.elytraRotY = $$0.elytraAnimationState.getRotY($$2);
        $$1.elytraRotZ = $$0.elytraAnimationState.getRotZ($$2);
        $$1.headEquipment = HumanoidMobRenderer.getEquipmentIfRenderable($$0, EquipmentSlot.HEAD);
        $$1.chestEquipment = HumanoidMobRenderer.getEquipmentIfRenderable($$0, EquipmentSlot.CHEST);
        $$1.legsEquipment = HumanoidMobRenderer.getEquipmentIfRenderable($$0, EquipmentSlot.LEGS);
        $$1.feetEquipment = HumanoidMobRenderer.getEquipmentIfRenderable($$0, EquipmentSlot.FEET);
    }

    private static ItemStack getEquipmentIfRenderable(LivingEntity $$0, EquipmentSlot $$1) {
        ItemStack $$2 = $$0.getItemBySlot($$1);
        return HumanoidArmorLayer.shouldRender($$2, $$1) ? $$2.copy() : ItemStack.EMPTY;
    }

    private static HumanoidArm getAttackArm(LivingEntity $$0) {
        HumanoidArm $$1 = $$0.getMainArm();
        return $$0.swingingArm == InteractionHand.MAIN_HAND ? $$1 : $$1.getOpposite();
    }
}

