/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionfc;

public class ItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    public ItemInHandLayer(RenderLayerParent<S, M> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        this.renderArmWithItem($$3, ((ArmedEntityRenderState)$$3).rightHandItem, HumanoidArm.RIGHT, $$0, $$1, $$2);
        this.renderArmWithItem($$3, ((ArmedEntityRenderState)$$3).leftHandItem, HumanoidArm.LEFT, $$0, $$1, $$2);
    }

    protected void renderArmWithItem(S $$0, ItemStackRenderState $$1, HumanoidArm $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if ($$1.isEmpty()) {
            return;
        }
        $$3.pushPose();
        ((ArmedModel)this.getParentModel()).translateToHand($$2, $$3);
        $$3.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0f));
        $$3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        boolean $$6 = $$2 == HumanoidArm.LEFT;
        $$3.translate((float)($$6 ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        $$1.render($$3, $$4, $$5, OverlayTexture.NO_OVERLAY);
        $$3.popPose();
    }
}

