/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockRenderer
extends EntityRenderer<FallingBlockEntity, FallingBlockRenderState> {
    private final BlockRenderDispatcher dispatcher;

    public FallingBlockRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5f;
        this.dispatcher = $$0.getBlockRenderDispatcher();
    }

    @Override
    public boolean shouldRender(FallingBlockEntity $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        if (!super.shouldRender($$0, $$1, $$2, $$3, $$4)) {
            return false;
        }
        return $$0.getBlockState() != $$0.level().getBlockState($$0.blockPosition());
    }

    @Override
    public void render(FallingBlockRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        BlockState $$4 = $$0.blockState;
        if ($$4.getRenderShape() != RenderShape.MODEL) {
            return;
        }
        $$1.pushPose();
        $$1.translate(-0.5, 0.0, -0.5);
        List<BlockModelPart> $$5 = this.dispatcher.getBlockModel($$4).collectParts(RandomSource.create($$4.getSeed($$0.startBlockPos)));
        this.dispatcher.getModelRenderer().tesselateBlock($$0, $$5, $$4, $$0.blockPos, $$1, $$2.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType($$4)), false, OverlayTexture.NO_OVERLAY);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public FallingBlockRenderState createRenderState() {
        return new FallingBlockRenderState();
    }

    @Override
    public void extractRenderState(FallingBlockEntity $$0, FallingBlockRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        BlockPos $$3 = BlockPos.containing($$0.getX(), $$0.getBoundingBox().maxY, $$0.getZ());
        $$1.startBlockPos = $$0.getStartPos();
        $$1.blockPos = $$3;
        $$1.blockState = $$0.getBlockState();
        $$1.biome = $$0.level().getBiome($$3);
        $$1.level = $$0.level();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

