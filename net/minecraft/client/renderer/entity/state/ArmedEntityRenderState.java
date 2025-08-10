/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class ArmedEntityRenderState
extends LivingEntityRenderState {
    public HumanoidArm mainArm = HumanoidArm.RIGHT;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public final ItemStackRenderState rightHandItem = new ItemStackRenderState();
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public final ItemStackRenderState leftHandItem = new ItemStackRenderState();

    public ItemStackRenderState getMainHandItem() {
        return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItem : this.leftHandItem;
    }

    public static void extractArmedEntityRenderState(LivingEntity $$0, ArmedEntityRenderState $$1, ItemModelResolver $$2) {
        $$1.mainArm = $$0.getMainArm();
        $$2.updateForLiving($$1.rightHandItem, $$0.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, $$0);
        $$2.updateForLiving($$1.leftHandItem, $$0.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, $$0);
    }
}

