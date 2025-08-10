/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class SnowGolemHeadLayer
extends RenderLayer<SnowGolemRenderState, SnowGolemModel> {
    private final BlockRenderDispatcher blockRenderer;

    public SnowGolemHeadLayer(RenderLayerParent<SnowGolemRenderState, SnowGolemModel> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, SnowGolemRenderState $$3, float $$4, float $$5) {
        if (!$$3.hasPumpkin) {
            return;
        }
        if ($$3.isInvisible && !$$3.appearsGlowing) {
            return;
        }
        $$0.pushPose();
        ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate($$0);
        float $$6 = 0.625f;
        $$0.translate(0.0f, -0.34375f, 0.0f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f));
        $$0.scale(0.625f, -0.625f, -0.625f);
        BlockState $$7 = Blocks.CARVED_PUMPKIN.defaultBlockState();
        BlockStateModel $$8 = this.blockRenderer.getBlockModel($$7);
        int $$9 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        VertexConsumer $$10 = $$3.appearsGlowing && $$3.isInvisible ? $$1.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)) : $$1.getBuffer(ItemBlockRenderTypes.getRenderType($$7));
        ModelBlockRenderer.renderModel($$0.last(), $$10, $$8, 0.0f, 0.0f, 0.0f, $$2, $$9);
        $$0.popPose();
    }
}

