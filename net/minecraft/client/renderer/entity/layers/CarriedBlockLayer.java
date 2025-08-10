/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class CarriedBlockLayer
extends RenderLayer<EndermanRenderState, EndermanModel<EndermanRenderState>> {
    private final BlockRenderDispatcher blockRenderer;

    public CarriedBlockLayer(RenderLayerParent<EndermanRenderState, EndermanModel<EndermanRenderState>> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, EndermanRenderState $$3, float $$4, float $$5) {
        BlockState $$6 = $$3.carriedBlock;
        if ($$6 == null) {
            return;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.6875f, -0.75f);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(20.0f));
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(45.0f));
        $$0.translate(0.25f, 0.1875f, 0.25f);
        float $$7 = 0.5f;
        $$0.scale(-0.5f, -0.5f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
        this.blockRenderer.renderSingleBlock($$6, $$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

