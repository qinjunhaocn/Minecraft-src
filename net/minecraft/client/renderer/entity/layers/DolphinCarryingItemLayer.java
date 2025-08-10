/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class DolphinCarryingItemLayer
extends RenderLayer<DolphinRenderState, DolphinModel> {
    public DolphinCarryingItemLayer(RenderLayerParent<DolphinRenderState, DolphinModel> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, DolphinRenderState $$3, float $$4, float $$5) {
        ItemStackRenderState $$6 = $$3.heldItem;
        if ($$6.isEmpty()) {
            return;
        }
        $$0.pushPose();
        float $$7 = 1.0f;
        float $$8 = -1.0f;
        float $$9 = Mth.abs($$3.xRot) / 60.0f;
        if ($$3.xRot < 0.0f) {
            $$0.translate(0.0f, 1.0f - $$9 * 0.5f, -1.0f + $$9 * 0.5f);
        } else {
            $$0.translate(0.0f, 1.0f + $$9 * 0.8f, -1.0f + $$9 * 0.2f);
        }
        $$6.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

