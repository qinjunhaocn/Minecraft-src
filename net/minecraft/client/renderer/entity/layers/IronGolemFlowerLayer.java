/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionfc;

public class IronGolemFlowerLayer
extends RenderLayer<IronGolemRenderState, IronGolemModel> {
    private final BlockRenderDispatcher blockRenderer;

    public IronGolemFlowerLayer(RenderLayerParent<IronGolemRenderState, IronGolemModel> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, IronGolemRenderState $$3, float $$4, float $$5) {
        if ($$3.offerFlowerTick == 0) {
            return;
        }
        $$0.pushPose();
        ModelPart $$6 = ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm();
        $$6.translateAndRotate($$0);
        $$0.translate(-1.1875f, 1.0625f, -0.9375f);
        $$0.translate(0.5f, 0.5f, 0.5f);
        float $$7 = 0.5f;
        $$0.scale(0.5f, 0.5f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0f));
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.blockRenderer.renderSingleBlock(Blocks.POPPY.defaultBlockState(), $$0, $$1, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

