/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class PlayerItemInHandLayer<S extends PlayerRenderState, M extends EntityModel<S> & HeadedModel>
extends ItemInHandLayer<S, M> {
    private static final float X_ROT_MIN = -0.5235988f;
    private static final float X_ROT_MAX = 1.5707964f;

    public PlayerItemInHandLayer(RenderLayerParent<S, M> $$0) {
        super($$0);
    }

    @Override
    protected void renderArmWithItem(S $$0, ItemStackRenderState $$1, HumanoidArm $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        InteractionHand $$6;
        if ($$1.isEmpty()) {
            return;
        }
        InteractionHand interactionHand = $$6 = $$2 == ((PlayerRenderState)$$0).mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (((PlayerRenderState)$$0).isUsingItem && ((PlayerRenderState)$$0).useItemHand == $$6 && ((PlayerRenderState)$$0).attackTime < 1.0E-5f && !((PlayerRenderState)$$0).heldOnHead.isEmpty()) {
            this.renderItemHeldToEye(((PlayerRenderState)$$0).heldOnHead, $$2, $$3, $$4, $$5);
        } else {
            super.renderArmWithItem($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    private void renderItemHeldToEye(ItemStackRenderState $$0, HumanoidArm $$1, PoseStack $$2, MultiBufferSource $$3, int $$4) {
        $$2.pushPose();
        ((Model)this.getParentModel()).root().translateAndRotate($$2);
        ModelPart $$5 = ((HeadedModel)this.getParentModel()).getHead();
        float $$6 = $$5.xRot;
        $$5.xRot = Mth.clamp($$5.xRot, -0.5235988f, 1.5707964f);
        $$5.translateAndRotate($$2);
        $$5.xRot = $$6;
        CustomHeadLayer.translateToHead($$2, CustomHeadLayer.Transforms.DEFAULT);
        boolean $$7 = $$1 == HumanoidArm.LEFT;
        $$2.translate(($$7 ? -2.5f : 2.5f) / 16.0f, -0.0625f, 0.0f);
        $$0.render($$2, $$3, $$4, OverlayTexture.NO_OVERLAY);
        $$2.popPose();
    }
}

