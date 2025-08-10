/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;

public class PistonHeadRenderer
implements BlockEntityRenderer<PistonMovingBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public PistonHeadRenderer(BlockEntityRendererProvider.Context $$0) {
        this.blockRenderer = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(PistonMovingBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Level $$7 = $$0.getLevel();
        if ($$7 == null) {
            return;
        }
        BlockPos $$8 = $$0.getBlockPos().relative($$0.getMovementDirection().getOpposite());
        BlockState $$9 = $$0.getMovedState();
        if ($$9.isAir()) {
            return;
        }
        ModelBlockRenderer.enableCaching();
        $$2.pushPose();
        $$2.translate($$0.getXOff($$1), $$0.getYOff($$1), $$0.getZOff($$1));
        if ($$9.is(Blocks.PISTON_HEAD) && $$0.getProgress($$1) <= 4.0f) {
            $$9 = (BlockState)$$9.setValue(PistonHeadBlock.SHORT, $$0.getProgress($$1) <= 0.5f);
            this.renderBlock($$8, $$9, $$2, $$3, $$7, false, $$5);
        } else if ($$0.isSourcePiston() && !$$0.isExtending()) {
            PistonType $$10 = $$9.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState $$11 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, $$10)).setValue(PistonHeadBlock.FACING, (Direction)$$9.getValue(PistonBaseBlock.FACING));
            $$11 = (BlockState)$$11.setValue(PistonHeadBlock.SHORT, $$0.getProgress($$1) >= 0.5f);
            this.renderBlock($$8, $$11, $$2, $$3, $$7, false, $$5);
            BlockPos $$12 = $$8.relative($$0.getMovementDirection());
            $$2.popPose();
            $$2.pushPose();
            $$9 = (BlockState)$$9.setValue(PistonBaseBlock.EXTENDED, true);
            this.renderBlock($$12, $$9, $$2, $$3, $$7, true, $$5);
        } else {
            this.renderBlock($$8, $$9, $$2, $$3, $$7, false, $$5);
        }
        $$2.popPose();
        ModelBlockRenderer.clearCache();
    }

    private void renderBlock(BlockPos $$0, BlockState $$1, PoseStack $$2, MultiBufferSource $$3, Level $$4, boolean $$5, int $$6) {
        RenderType $$7 = ItemBlockRenderTypes.getMovingBlockRenderType($$1);
        VertexConsumer $$8 = $$3.getBuffer($$7);
        List<BlockModelPart> $$9 = this.blockRenderer.getBlockModel($$1).collectParts(RandomSource.create($$1.getSeed($$0)));
        this.blockRenderer.getModelRenderer().tesselateBlock($$4, $$9, $$1, $$0, $$2, $$8, $$5, $$6);
    }

    @Override
    public int getViewDistance() {
        return 68;
    }
}

