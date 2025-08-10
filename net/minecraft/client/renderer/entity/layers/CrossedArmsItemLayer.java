/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionfc;

public class CrossedArmsItemLayer<S extends HoldingEntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    public CrossedArmsItemLayer(RenderLayerParent<S, M> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        ItemStackRenderState $$6 = ((HoldingEntityRenderState)$$3).heldItem;
        if ($$6.isEmpty()) {
            return;
        }
        $$0.pushPose();
        this.applyTranslation($$3, $$0);
        $$6.render($$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }

    protected void applyTranslation(S $$0, PoseStack $$1) {
        ((VillagerLikeModel)this.getParentModel()).translateToArms($$1);
        $$1.mulPose((Quaternionfc)Axis.XP.rotation(0.75f));
        $$1.scale(1.07f, 1.07f, 1.07f);
        $$1.translate(0.0f, 0.13f, -0.34f);
        $$1.mulPose((Quaternionfc)Axis.XP.rotation((float)Math.PI));
    }
}

