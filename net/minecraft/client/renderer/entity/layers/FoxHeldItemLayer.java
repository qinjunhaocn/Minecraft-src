/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionfc;

public class FoxHeldItemLayer
extends RenderLayer<FoxRenderState, FoxModel> {
    public FoxHeldItemLayer(RenderLayerParent<FoxRenderState, FoxModel> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, FoxRenderState $$3, float $$4, float $$5) {
        ItemStackRenderState $$6 = $$3.heldItem;
        if ($$6.isEmpty()) {
            return;
        }
        boolean $$7 = $$3.isSleeping;
        boolean $$8 = $$3.isBaby;
        $$0.pushPose();
        $$0.translate(((FoxModel)this.getParentModel()).head.x / 16.0f, ((FoxModel)this.getParentModel()).head.y / 16.0f, ((FoxModel)this.getParentModel()).head.z / 16.0f);
        if ($$8) {
            float $$9 = 0.75f;
            $$0.scale(0.75f, 0.75f, 0.75f);
        }
        $$0.mulPose((Quaternionfc)Axis.ZP.rotation($$3.headRollAngle));
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$4));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees($$5));
        if ($$3.isBaby) {
            if ($$7) {
                $$0.translate(0.4f, 0.26f, 0.15f);
            } else {
                $$0.translate(0.06f, 0.26f, -0.5f);
            }
        } else if ($$7) {
            $$0.translate(0.46f, 0.26f, 0.22f);
        } else {
            $$0.translate(0.06f, 0.27f, -0.5f);
        }
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
        if ($$7) {
            $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(90.0f));
        }
        $$6.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

