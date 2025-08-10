/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;

public class SlimeOuterLayer
extends RenderLayer<SlimeRenderState, SlimeModel> {
    private final SlimeModel model;

    public SlimeOuterLayer(RenderLayerParent<SlimeRenderState, SlimeModel> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new SlimeModel($$1.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, SlimeRenderState $$3, float $$4, float $$5) {
        VertexConsumer $$8;
        boolean $$6;
        boolean bl = $$6 = $$3.appearsGlowing && $$3.isInvisible;
        if ($$3.isInvisible && !$$6) {
            return;
        }
        if ($$6) {
            VertexConsumer $$7 = $$1.getBuffer(RenderType.outline(SlimeRenderer.SLIME_LOCATION));
        } else {
            $$8 = $$1.getBuffer(RenderType.entityTranslucent(SlimeRenderer.SLIME_LOCATION));
        }
        this.model.setupAnim($$3);
        this.model.renderToBuffer($$0, $$8, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f));
    }
}

