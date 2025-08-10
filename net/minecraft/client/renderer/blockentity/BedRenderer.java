/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BedRenderer
implements BlockEntityRenderer<BedBlockEntity> {
    private final Model headModel;
    private final Model footModel;

    public BedRenderer(BlockEntityRendererProvider.Context $$0) {
        this($$0.getModelSet());
    }

    public BedRenderer(EntityModelSet $$0) {
        this.headModel = new Model.Simple($$0.bakeLayer(ModelLayers.BED_HEAD), RenderType::entitySolid);
        this.footModel = new Model.Simple($$0.bakeLayer(ModelLayers.BED_FOOT), RenderType::entitySolid);
    }

    public static LayerDefinition createHeadLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 6).addBox(0.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 1.5707964f));
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 18).addBox(-16.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, (float)Math.PI));
        return LayerDefinition.create($$0, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 22).addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f), PartPose.ZERO);
        $$1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(50, 0).addBox(0.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 0.0f));
        $$1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 12).addBox(-16.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f), PartPose.rotation(1.5707964f, 0.0f, 4.712389f));
        return LayerDefinition.create($$0, 64, 64);
    }

    @Override
    public void render(BedBlockEntity $$02, float $$12, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        Level $$7 = $$02.getLevel();
        if ($$7 != null) {
            Material $$8 = Sheets.getBedMaterial($$02.getColor());
            BlockState $$9 = $$02.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<BedBlockEntity> $$10 = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, $$9, $$7, $$02.getBlockPos(), ($$0, $$1) -> false);
            int $$11 = ((Int2IntFunction)$$10.apply(new BrightnessCombiner())).get($$4);
            this.renderPiece($$2, $$3, $$9.getValue(BedBlock.PART) == BedPart.HEAD ? this.headModel : this.footModel, (Direction)$$9.getValue(BedBlock.FACING), $$8, $$11, $$5, false);
        }
    }

    public void renderInHand(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3, Material $$4) {
        this.renderPiece($$0, $$1, this.headModel, Direction.SOUTH, $$4, $$2, $$3, false);
        this.renderPiece($$0, $$1, this.footModel, Direction.SOUTH, $$4, $$2, $$3, true);
    }

    private void renderPiece(PoseStack $$0, MultiBufferSource $$1, Model $$2, Direction $$3, Material $$4, int $$5, int $$6, boolean $$7) {
        $$0.pushPose();
        BedRenderer.preparePose($$0, $$7, $$3);
        VertexConsumer $$8 = $$4.buffer($$1, RenderType::entitySolid);
        $$2.renderToBuffer($$0, $$8, $$5, $$6);
        $$0.popPose();
    }

    private static void preparePose(PoseStack $$0, boolean $$1, Direction $$2) {
        $$0.translate(0.0f, 0.5625f, $$1 ? -1.0f : 0.0f);
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0f));
        $$0.translate(0.5f, 0.5f, 0.5f);
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0f + $$2.toYRot()));
        $$0.translate(-0.5f, -0.5f, -0.5f);
    }

    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        BedRenderer.preparePose($$1, false, Direction.SOUTH);
        this.headModel.root().getExtentsForGui($$1, $$0);
        $$1.setIdentity();
        BedRenderer.preparePose($$1, true, Direction.SOUTH);
        this.footModel.root().getExtentsForGui($$1, $$0);
    }
}

