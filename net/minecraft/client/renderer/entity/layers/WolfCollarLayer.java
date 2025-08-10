/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class WolfCollarLayer
extends RenderLayer<WolfRenderState, WolfModel> {
    private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

    public WolfCollarLayer(RenderLayerParent<WolfRenderState, WolfModel> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, WolfRenderState $$3, float $$4, float $$5) {
        DyeColor $$6 = $$3.collarColor;
        if ($$6 == null || $$3.isInvisible) {
            return;
        }
        int $$7 = $$6.getTextureDiffuseColor();
        VertexConsumer $$8 = $$1.getBuffer(RenderType.entityCutoutNoCull(WOLF_COLLAR_LOCATION));
        ((WolfModel)this.getParentModel()).renderToBuffer($$0, $$8, $$2, OverlayTexture.NO_OVERLAY, $$7);
    }
}

