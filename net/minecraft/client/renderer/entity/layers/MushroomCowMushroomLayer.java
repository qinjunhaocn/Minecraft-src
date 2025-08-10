/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.MushroomCowRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class MushroomCowMushroomLayer
extends RenderLayer<MushroomCowRenderState, CowModel> {
    private final BlockRenderDispatcher blockRenderer;

    public MushroomCowMushroomLayer(RenderLayerParent<MushroomCowRenderState, CowModel> $$0, BlockRenderDispatcher $$1) {
        super($$0);
        this.blockRenderer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, MushroomCowRenderState $$3, float $$4, float $$5) {
        boolean $$6;
        if ($$3.isBaby) {
            return;
        }
        boolean bl = $$6 = $$3.appearsGlowing && $$3.isInvisible;
        if ($$3.isInvisible && !$$6) {
            return;
        }
        BlockState $$7 = $$3.variant.getBlockState();
        int $$8 = LivingEntityRenderer.getOverlayCoords($$3, 0.0f);
        BlockStateModel $$9 = this.blockRenderer.getBlockModel($$7);
        $$0.pushPose();
        $$0.translate(0.2f, -0.35f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$6, $$7, $$8, $$9);
        $$0.popPose();
        $$0.pushPose();
        $$0.translate(0.2f, -0.35f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(42.0f));
        $$0.translate(0.1f, 0.0f, -0.6f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$6, $$7, $$8, $$9);
        $$0.popPose();
        $$0.pushPose();
        ((CowModel)this.getParentModel()).getHead().translateAndRotate($$0);
        $$0.translate(0.0f, -0.7f, -0.2f);
        $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-78.0f));
        $$0.scale(-1.0f, -1.0f, 1.0f);
        $$0.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroomBlock($$0, $$1, $$2, $$6, $$7, $$8, $$9);
        $$0.popPose();
    }

    private void renderMushroomBlock(PoseStack $$0, MultiBufferSource $$1, int $$2, boolean $$3, BlockState $$4, int $$5, BlockStateModel $$6) {
        if ($$3) {
            ModelBlockRenderer.renderModel($$0.last(), $$1.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), $$6, 0.0f, 0.0f, 0.0f, $$2, $$5);
        } else {
            this.blockRenderer.renderSingleBlock($$4, $$0, $$1, $$2, $$5);
        }
    }
}

