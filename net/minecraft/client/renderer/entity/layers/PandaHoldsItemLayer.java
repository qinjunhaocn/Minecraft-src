/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class PandaHoldsItemLayer
extends RenderLayer<PandaRenderState, PandaModel> {
    public PandaHoldsItemLayer(RenderLayerParent<PandaRenderState, PandaModel> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PandaRenderState $$3, float $$4, float $$5) {
        ItemStackRenderState $$6 = $$3.heldItem;
        if ($$6.isEmpty() || !$$3.isSitting || $$3.isScared) {
            return;
        }
        float $$7 = -0.6f;
        float $$8 = 1.4f;
        if ($$3.isEating) {
            $$7 -= 0.2f * Mth.sin($$3.ageInTicks * 0.6f) + 0.2f;
            $$8 -= 0.09f * Mth.sin($$3.ageInTicks * 0.6f);
        }
        $$0.pushPose();
        $$0.translate(0.1f, $$8, $$7);
        $$6.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

