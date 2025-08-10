/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public abstract class EnergySwirlLayer<S extends EntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    public EnergySwirlLayer(RenderLayerParent<S, M> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        if (!this.isPowered($$3)) {
            return;
        }
        float $$6 = ((EntityRenderState)$$3).ageInTicks;
        M $$7 = this.model();
        VertexConsumer $$8 = $$1.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset($$6) % 1.0f, $$6 * 0.01f % 1.0f));
        ((EntityModel)$$7).setupAnim($$3);
        ((Model)$$7).renderToBuffer($$0, $$8, $$2, OverlayTexture.NO_OVERLAY, -8355712);
    }

    protected abstract boolean isPowered(S var1);

    protected abstract float xOffset(float var1);

    protected abstract ResourceLocation getTextureLocation();

    protected abstract M model();
}

