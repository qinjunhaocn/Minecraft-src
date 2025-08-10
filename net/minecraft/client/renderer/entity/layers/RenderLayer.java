/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderLayer<S extends EntityRenderState, M extends EntityModel<? super S>> {
    private final RenderLayerParent<S, M> renderer;

    public RenderLayer(RenderLayerParent<S, M> $$0) {
        this.renderer = $$0;
    }

    protected static <S extends LivingEntityRenderState> void coloredCutoutModelCopyLayerRender(EntityModel<S> $$0, ResourceLocation $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, S $$5, int $$6) {
        if (!$$5.isInvisible) {
            $$0.setupAnim($$5);
            RenderLayer.renderColoredCutoutModel($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
    }

    protected static void renderColoredCutoutModel(EntityModel<?> $$0, ResourceLocation $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, LivingEntityRenderState $$5, int $$6) {
        VertexConsumer $$7 = $$3.getBuffer(RenderType.entityCutoutNoCull($$1));
        $$0.renderToBuffer($$2, $$7, $$4, LivingEntityRenderer.getOverlayCoords($$5, 0.0f), $$6);
    }

    public M getParentModel() {
        return this.renderer.getModel();
    }

    public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6);
}

