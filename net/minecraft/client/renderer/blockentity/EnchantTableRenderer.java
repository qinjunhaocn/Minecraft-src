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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class EnchantTableRenderer
implements BlockEntityRenderer<EnchantingTableBlockEntity> {
    public static final Material BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/enchanting_table_book"));
    private final BookModel bookModel;

    public EnchantTableRenderer(BlockEntityRendererProvider.Context $$0) {
        this.bookModel = new BookModel($$0.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(EnchantingTableBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        float $$8;
        $$2.pushPose();
        $$2.translate(0.5f, 0.75f, 0.5f);
        float $$7 = (float)$$0.time + $$1;
        $$2.translate(0.0f, 0.1f + Mth.sin($$7 * 0.1f) * 0.01f, 0.0f);
        for ($$8 = $$0.rot - $$0.oRot; $$8 >= (float)Math.PI; $$8 -= (float)Math.PI * 2) {
        }
        while ($$8 < (float)(-Math.PI)) {
            $$8 += (float)Math.PI * 2;
        }
        float $$9 = $$0.oRot + $$8 * $$1;
        $$2.mulPose((Quaternionfc)Axis.YP.rotation(-$$9));
        $$2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(80.0f));
        float $$10 = Mth.lerp($$1, $$0.oFlip, $$0.flip);
        float $$11 = Mth.frac($$10 + 0.25f) * 1.6f - 0.3f;
        float $$12 = Mth.frac($$10 + 0.75f) * 1.6f - 0.3f;
        float $$13 = Mth.lerp($$1, $$0.oOpen, $$0.open);
        this.bookModel.setupAnim($$7, Mth.clamp($$11, 0.0f, 1.0f), Mth.clamp($$12, 0.0f, 1.0f), $$13);
        VertexConsumer $$14 = BOOK_LOCATION.buffer($$3, RenderType::entitySolid);
        this.bookModel.renderToBuffer($$2, $$14, $$4, $$5);
        $$2.popPose();
    }
}

