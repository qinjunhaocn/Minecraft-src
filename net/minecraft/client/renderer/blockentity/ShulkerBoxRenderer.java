/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class ShulkerBoxRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerBoxModel model;

    public ShulkerBoxRenderer(BlockEntityRendererProvider.Context $$0) {
        this($$0.getModelSet());
    }

    public ShulkerBoxRenderer(EntityModelSet $$0) {
        this.model = new ShulkerBoxModel($$0.bakeLayer(ModelLayers.SHULKER_BOX));
    }

    @Override
    public void render(ShulkerBoxBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Material $$10;
        Direction $$7 = $$0.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
        DyeColor $$8 = $$0.getColor();
        if ($$8 == null) {
            Material $$9 = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
            $$10 = Sheets.getShulkerBoxMaterial($$8);
        }
        float $$11 = $$0.getProgress($$1);
        this.render($$2, $$3, $$4, $$5, $$7, $$11, $$10);
    }

    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, Direction $$4, float $$5, Material $$6) {
        $$0.pushPose();
        this.prepareModel($$0, $$4, $$5);
        VertexConsumer $$7 = $$6.buffer($$1, this.model::renderType);
        this.model.renderToBuffer($$0, $$7, $$2, $$3);
        $$0.popPose();
    }

    private void prepareModel(PoseStack $$0, Direction $$1, float $$2) {
        $$0.translate(0.5f, 0.5f, 0.5f);
        float $$3 = 0.9995f;
        $$0.scale(0.9995f, 0.9995f, 0.9995f);
        $$0.mulPose((Quaternionfc)$$1.getRotation());
        $$0.scale(1.0f, -1.0f, -1.0f);
        $$0.translate(0.0f, -1.0f, 0.0f);
        this.model.animate($$2);
    }

    public void getExtents(Direction $$0, float $$1, Set<Vector3f> $$2) {
        PoseStack $$3 = new PoseStack();
        this.prepareModel($$3, $$0, $$1);
        this.model.root().getExtentsForGui($$3, $$2);
    }

    static class ShulkerBoxModel
    extends Model {
        private final ModelPart lid;

        public ShulkerBoxModel(ModelPart $$0) {
            super($$0, RenderType::entityCutoutNoCull);
            this.lid = $$0.getChild("lid");
        }

        public void animate(float $$0) {
            this.lid.setPos(0.0f, 24.0f - $$0 * 0.5f * 16.0f, 0.0f);
            this.lid.yRot = 270.0f * $$0 * ((float)Math.PI / 180);
        }
    }
}

