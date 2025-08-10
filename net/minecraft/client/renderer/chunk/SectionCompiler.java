/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SectionCompiler {
    private final BlockRenderDispatcher blockRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderer;

    public SectionCompiler(BlockRenderDispatcher $$0, BlockEntityRenderDispatcher $$1) {
        this.blockRenderer = $$0;
        this.blockEntityRenderer = $$1;
    }

    public Results compile(SectionPos $$0, RenderSectionRegion $$1, VertexSorting $$2, SectionBufferBuilderPack $$3) {
        Results $$4 = new Results();
        BlockPos $$5 = $$0.origin();
        BlockPos $$6 = $$5.offset(15, 15, 15);
        VisGraph $$7 = new VisGraph();
        PoseStack $$8 = new PoseStack();
        ModelBlockRenderer.enableCaching();
        EnumMap<ChunkSectionLayer, BufferBuilder> $$9 = new EnumMap<ChunkSectionLayer, BufferBuilder>(ChunkSectionLayer.class);
        RandomSource $$10 = RandomSource.create();
        ObjectArrayList $$11 = new ObjectArrayList();
        for (BlockPos blockPos : BlockPos.betweenClosed($$5, $$6)) {
            FluidState $$15;
            BlockEntity $$14;
            BlockState $$13 = $$1.getBlockState(blockPos);
            if ($$13.isSolidRender()) {
                $$7.setOpaque(blockPos);
            }
            if ($$13.hasBlockEntity() && ($$14 = $$1.getBlockEntity(blockPos)) != null) {
                this.handleBlockEntity($$4, $$14);
            }
            if (!($$15 = $$13.getFluidState()).isEmpty()) {
                ChunkSectionLayer $$16 = ItemBlockRenderTypes.getRenderLayer($$15);
                BufferBuilder $$17 = this.getOrBeginLayer($$9, $$3, $$16);
                this.blockRenderer.renderLiquid(blockPos, $$1, $$17, $$13, $$15);
            }
            if ($$13.getRenderShape() != RenderShape.MODEL) continue;
            ChunkSectionLayer $$18 = ItemBlockRenderTypes.getChunkRenderType($$13);
            BufferBuilder $$19 = this.getOrBeginLayer($$9, $$3, $$18);
            $$10.setSeed($$13.getSeed(blockPos));
            this.blockRenderer.getBlockModel($$13).collectParts($$10, (List<BlockModelPart>)$$11);
            $$8.pushPose();
            $$8.translate(SectionPos.sectionRelative(blockPos.getX()), SectionPos.sectionRelative(blockPos.getY()), SectionPos.sectionRelative(blockPos.getZ()));
            this.blockRenderer.renderBatched($$13, blockPos, $$1, $$8, $$19, true, (List<BlockModelPart>)$$11);
            $$8.popPose();
            $$11.clear();
        }
        for (Map.Entry entry : $$9.entrySet()) {
            ChunkSectionLayer $$21 = (ChunkSectionLayer)((Object)entry.getKey());
            MeshData $$22 = ((BufferBuilder)entry.getValue()).build();
            if ($$22 == null) continue;
            if ($$21 == ChunkSectionLayer.TRANSLUCENT) {
                $$4.transparencyState = $$22.sortQuads($$3.buffer($$21), $$2);
            }
            $$4.renderedLayers.put($$21, $$22);
        }
        ModelBlockRenderer.clearCache();
        $$4.visibilitySet = $$7.resolve();
        return $$4;
    }

    private BufferBuilder getOrBeginLayer(Map<ChunkSectionLayer, BufferBuilder> $$0, SectionBufferBuilderPack $$1, ChunkSectionLayer $$2) {
        BufferBuilder $$3 = $$0.get((Object)$$2);
        if ($$3 == null) {
            ByteBufferBuilder $$4 = $$1.buffer($$2);
            $$3 = new BufferBuilder($$4, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
            $$0.put($$2, $$3);
        }
        return $$3;
    }

    private <E extends BlockEntity> void handleBlockEntity(Results $$0, E $$1) {
        BlockEntityRenderer<E> $$2 = this.blockEntityRenderer.getRenderer($$1);
        if ($$2 != null && !$$2.shouldRenderOffScreen()) {
            $$0.blockEntities.add($$1);
        }
    }

    public static final class Results {
        public final List<BlockEntity> blockEntities = new ArrayList<BlockEntity>();
        public final Map<ChunkSectionLayer, MeshData> renderedLayers = new EnumMap<ChunkSectionLayer, MeshData>(ChunkSectionLayer.class);
        public VisibilitySet visibilitySet = new VisibilitySet();
        @Nullable
        public MeshData.SortState transparencyState;

        public void release() {
            this.renderedLayers.values().forEach(MeshData::close);
        }
    }
}

