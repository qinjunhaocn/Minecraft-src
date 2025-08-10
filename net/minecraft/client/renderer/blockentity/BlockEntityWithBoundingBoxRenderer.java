/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class BlockEntityWithBoundingBoxRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    public BlockEntityWithBoundingBoxRenderer(BlockEntityRendererProvider.Context $$0) {
    }

    @Override
    public void render(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        if (!Minecraft.getInstance().player.canUseGameMasterBlocks() && !Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        BoundingBoxRenderable.Mode $$7 = ((BoundingBoxRenderable)$$0).renderMode();
        if ($$7 == BoundingBoxRenderable.Mode.NONE) {
            return;
        }
        BoundingBoxRenderable.RenderableBox $$8 = ((BoundingBoxRenderable)$$0).getRenderableBox();
        BlockPos $$9 = $$8.localPos();
        Vec3i $$10 = $$8.size();
        if ($$10.getX() < 1 || $$10.getY() < 1 || $$10.getZ() < 1) {
            return;
        }
        float $$11 = 1.0f;
        float $$12 = 0.9f;
        float $$13 = 0.5f;
        VertexConsumer $$14 = $$3.getBuffer(RenderType.lines());
        BlockPos $$15 = $$9.offset($$10);
        ShapeRenderer.renderLineBox($$2, $$14, $$9.getX(), $$9.getY(), $$9.getZ(), $$15.getX(), $$15.getY(), $$15.getZ(), 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
        if ($$7 == BoundingBoxRenderable.Mode.BOX_AND_INVISIBLE_BLOCKS && ((BlockEntity)$$0).getLevel() != null) {
            this.renderInvisibleBlocks($$0, ((BlockEntity)$$0).getLevel(), $$9, $$10, $$3, $$2);
        }
    }

    private void renderInvisibleBlocks(T $$0, BlockGetter $$1, BlockPos $$2, Vec3i $$3, MultiBufferSource $$4, PoseStack $$5) {
        VertexConsumer $$6 = $$4.getBuffer(RenderType.lines());
        BlockPos $$7 = ((BlockEntity)$$0).getBlockPos();
        BlockPos $$8 = $$7.offset($$2);
        for (BlockPos $$9 : BlockPos.betweenClosed($$8, $$8.offset($$3).offset(-1, -1, -1))) {
            boolean $$15;
            BlockState $$10 = $$1.getBlockState($$9);
            boolean $$11 = $$10.isAir();
            boolean $$12 = $$10.is(Blocks.STRUCTURE_VOID);
            boolean $$13 = $$10.is(Blocks.BARRIER);
            boolean $$14 = $$10.is(Blocks.LIGHT);
            boolean bl = $$15 = $$12 || $$13 || $$14;
            if (!$$11 && !$$15) continue;
            float $$16 = $$11 ? 0.05f : 0.0f;
            double $$17 = (float)($$9.getX() - $$7.getX()) + 0.45f - $$16;
            double $$18 = (float)($$9.getY() - $$7.getY()) + 0.45f - $$16;
            double $$19 = (float)($$9.getZ() - $$7.getZ()) + 0.45f - $$16;
            double $$20 = (float)($$9.getX() - $$7.getX()) + 0.55f + $$16;
            double $$21 = (float)($$9.getY() - $$7.getY()) + 0.55f + $$16;
            double $$22 = (float)($$9.getZ() - $$7.getZ()) + 0.55f + $$16;
            if ($$11) {
                ShapeRenderer.renderLineBox($$5, $$6, $$17, $$18, $$19, $$20, $$21, $$22, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            if ($$12) {
                ShapeRenderer.renderLineBox($$5, $$6, $$17, $$18, $$19, $$20, $$21, $$22, 1.0f, 0.75f, 0.75f, 1.0f, 1.0f, 0.75f, 0.75f);
                continue;
            }
            if ($$13) {
                ShapeRenderer.renderLineBox($$5, $$6, $$17, $$18, $$19, $$20, $$21, $$22, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f);
                continue;
            }
            if (!$$14) continue;
            ShapeRenderer.renderLineBox($$5, $$6, $$17, $$18, $$19, $$20, $$21, $$22, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        }
    }

    private void renderStructureVoids(T $$0, BlockPos $$1, Vec3i $$2, VertexConsumer $$3, PoseStack $$42) {
        Level $$52 = ((BlockEntity)$$0).getLevel();
        if ($$52 == null) {
            return;
        }
        BlockPos $$62 = ((BlockEntity)$$0).getBlockPos();
        BitSetDiscreteVoxelShape $$72 = new BitSetDiscreteVoxelShape($$2.getX(), $$2.getY(), $$2.getZ());
        for (BlockPos $$8 : BlockPos.betweenClosed($$1, $$1.offset($$2).offset(-1, -1, -1))) {
            if (!$$52.getBlockState($$8).is(Blocks.STRUCTURE_VOID)) continue;
            ((DiscreteVoxelShape)$$72).fill($$8.getX() - $$1.getX(), $$8.getY() - $$1.getY(), $$8.getZ() - $$1.getZ());
        }
        $$72.forAllFaces(($$4, $$5, $$6, $$7) -> {
            float $$8 = 0.48f;
            float $$9 = (float)($$5 + $$1.getX() - $$62.getX()) + 0.5f - 0.48f;
            float $$10 = (float)($$6 + $$1.getY() - $$62.getY()) + 0.5f - 0.48f;
            float $$11 = (float)($$7 + $$1.getZ() - $$62.getZ()) + 0.5f - 0.48f;
            float $$12 = (float)($$5 + $$1.getX() - $$62.getX()) + 0.5f + 0.48f;
            float $$13 = (float)($$6 + $$1.getY() - $$62.getY()) + 0.5f + 0.48f;
            float $$14 = (float)($$7 + $$1.getZ() - $$62.getZ()) + 0.5f + 0.48f;
            ShapeRenderer.renderFace($$42, $$3, $$4, $$9, $$10, $$11, $$12, $$13, $$14, 0.75f, 0.75f, 1.0f, 0.2f);
        });
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}

