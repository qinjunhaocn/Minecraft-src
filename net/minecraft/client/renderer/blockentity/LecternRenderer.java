/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class LecternRenderer
implements BlockEntityRenderer<LecternBlockEntity> {
    private final BookModel bookModel;

    public LecternRenderer(BlockEntityRendererProvider.Context $$0) {
        this.bookModel = new BookModel($$0.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(LecternBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        BlockState $$7 = $$0.getBlockState();
        if (!$$7.getValue(LecternBlock.HAS_BOOK).booleanValue()) {
            return;
        }
        $$2.pushPose();
        $$2.translate(0.5f, 1.0625f, 0.5f);
        float $$8 = $$7.getValue(LecternBlock.FACING).getClockWise().toYRot();
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-$$8));
        $$2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(67.5f));
        $$2.translate(0.0f, -0.125f, 0.0f);
        this.bookModel.setupAnim(0.0f, 0.1f, 0.9f, 1.2f);
        VertexConsumer $$9 = EnchantTableRenderer.BOOK_LOCATION.buffer($$3, RenderType::entitySolid);
        this.bookModel.renderToBuffer($$2, $$9, $$4, $$5);
        $$2.popPose();
    }
}

