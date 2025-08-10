/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ChestRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    private final ChestModel singleModel;
    private final ChestModel doubleLeftModel;
    private final ChestModel doubleRightModel;
    private final boolean xmasTextures = ChestRenderer.xmasTextures();

    public ChestRenderer(BlockEntityRendererProvider.Context $$0) {
        this.singleModel = new ChestModel($$0.bakeLayer(ModelLayers.CHEST));
        this.doubleLeftModel = new ChestModel($$0.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleRightModel = new ChestModel($$0.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT));
    }

    public static boolean xmasTextures() {
        Calendar $$0 = Calendar.getInstance();
        return $$0.get(2) + 1 == 12 && $$0.get(5) >= 24 && $$0.get(5) <= 26;
    }

    @Override
    public void render(T $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        DoubleBlockCombiner.NeighborCombineResult<ChestBlockEntity> $$16;
        Level $$7 = ((BlockEntity)$$0).getLevel();
        boolean $$8 = $$7 != null;
        BlockState $$9 = $$8 ? ((BlockEntity)$$0).getBlockState() : (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        ChestType $$10 = $$9.hasProperty(ChestBlock.TYPE) ? $$9.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Block $$11 = $$9.getBlock();
        if (!($$11 instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock $$12 = (AbstractChestBlock)$$11;
        boolean $$13 = $$10 != ChestType.SINGLE;
        $$2.pushPose();
        float $$14 = $$9.getValue(ChestBlock.FACING).toYRot();
        $$2.translate(0.5f, 0.5f, 0.5f);
        $$2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-$$14));
        $$2.translate(-0.5f, -0.5f, -0.5f);
        if ($$8) {
            DoubleBlockCombiner.NeighborCombineResult<ChestBlockEntity> $$15 = $$12.combine($$9, $$7, ((BlockEntity)$$0).getBlockPos(), true);
        } else {
            $$16 = DoubleBlockCombiner.Combiner::acceptNone;
        }
        float $$17 = $$16.apply(ChestBlock.opennessCombiner((LidBlockEntity)$$0)).get($$1);
        $$17 = 1.0f - $$17;
        $$17 = 1.0f - $$17 * $$17 * $$17;
        int $$18 = ((Int2IntFunction)$$16.apply(new BrightnessCombiner())).applyAsInt($$4);
        Material $$19 = Sheets.chooseMaterial($$0, $$10, this.xmasTextures);
        VertexConsumer $$20 = $$19.buffer($$3, RenderType::entityCutout);
        if ($$13) {
            if ($$10 == ChestType.LEFT) {
                this.render($$2, $$20, this.doubleLeftModel, $$17, $$18, $$5);
            } else {
                this.render($$2, $$20, this.doubleRightModel, $$17, $$18, $$5);
            }
        } else {
            this.render($$2, $$20, this.singleModel, $$17, $$18, $$5);
        }
        $$2.popPose();
    }

    private void render(PoseStack $$0, VertexConsumer $$1, ChestModel $$2, float $$3, int $$4, int $$5) {
        $$2.setupAnim($$3);
        $$2.renderToBuffer($$0, $$1, $$4, $$5);
    }
}

