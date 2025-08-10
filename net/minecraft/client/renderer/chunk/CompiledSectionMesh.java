/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompiledSectionMesh
implements SectionMesh {
    public static final SectionMesh UNCOMPILED = new SectionMesh(){

        @Override
        public boolean facesCanSeeEachother(Direction $$0, Direction $$1) {
            return false;
        }
    };
    public static final SectionMesh EMPTY = new SectionMesh(){

        @Override
        public boolean facesCanSeeEachother(Direction $$0, Direction $$1) {
            return true;
        }
    };
    private final List<BlockEntity> renderableBlockEntities;
    private final VisibilitySet visibilitySet;
    @Nullable
    private final MeshData.SortState transparencyState;
    @Nullable
    private TranslucencyPointOfView translucencyPointOfView;
    private final Map<ChunkSectionLayer, SectionBuffers> buffers = new EnumMap<ChunkSectionLayer, SectionBuffers>(ChunkSectionLayer.class);

    public CompiledSectionMesh(TranslucencyPointOfView $$0, SectionCompiler.Results $$1) {
        this.translucencyPointOfView = $$0;
        this.visibilitySet = $$1.visibilitySet;
        this.renderableBlockEntities = $$1.blockEntities;
        this.transparencyState = $$1.transparencyState;
    }

    public void setTranslucencyPointOfView(TranslucencyPointOfView $$0) {
        this.translucencyPointOfView = $$0;
    }

    @Override
    public boolean isDifferentPointOfView(TranslucencyPointOfView $$0) {
        return !$$0.equals(this.translucencyPointOfView);
    }

    @Override
    public boolean hasRenderableLayers() {
        return !this.buffers.isEmpty();
    }

    @Override
    public boolean isEmpty(ChunkSectionLayer $$0) {
        return !this.buffers.containsKey((Object)$$0);
    }

    @Override
    public List<BlockEntity> getRenderableBlockEntities() {
        return this.renderableBlockEntities;
    }

    @Override
    public boolean facesCanSeeEachother(Direction $$0, Direction $$1) {
        return this.visibilitySet.visibilityBetween($$0, $$1);
    }

    @Override
    @Nullable
    public SectionBuffers getBuffers(ChunkSectionLayer $$0) {
        return this.buffers.get((Object)$$0);
    }

    public void uploadMeshLayer(ChunkSectionLayer $$0, MeshData $$1, long $$2) {
        CommandEncoder $$3 = RenderSystem.getDevice().createCommandEncoder();
        SectionBuffers $$4 = this.getBuffers($$0);
        if ($$4 != null) {
            if ($$4.getVertexBuffer().size() < $$1.vertexBuffer().remaining()) {
                $$4.getVertexBuffer().close();
                $$4.setVertexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section vertex buffer - layer: " + $$0.label() + "; cords: " + SectionPos.x($$2) + ", " + SectionPos.y($$2) + ", " + SectionPos.z($$2), 40, $$1.vertexBuffer()));
            } else if (!$$4.getVertexBuffer().isClosed()) {
                $$3.writeToBuffer($$4.getVertexBuffer().slice(), $$1.vertexBuffer());
            }
            ByteBuffer $$5 = $$1.indexBuffer();
            if ($$5 != null) {
                if ($$4.getIndexBuffer() == null || $$4.getIndexBuffer().size() < $$5.remaining()) {
                    if ($$4.getIndexBuffer() != null) {
                        $$4.getIndexBuffer().close();
                    }
                    $$4.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + $$0.label() + "; cords: " + SectionPos.x($$2) + ", " + SectionPos.y($$2) + ", " + SectionPos.z($$2), 72, $$5));
                } else if (!$$4.getIndexBuffer().isClosed()) {
                    $$3.writeToBuffer($$4.getIndexBuffer().slice(), $$5);
                }
            } else if ($$4.getIndexBuffer() != null) {
                $$4.getIndexBuffer().close();
                $$4.setIndexBuffer(null);
            }
            $$4.setIndexCount($$1.drawState().indexCount());
            $$4.setIndexType($$1.drawState().indexType());
        } else {
            GpuBuffer $$6 = RenderSystem.getDevice().createBuffer(() -> "Section vertex buffer - layer: " + $$0.label() + "; cords: " + SectionPos.x($$2) + ", " + SectionPos.y($$2) + ", " + SectionPos.z($$2), 40, $$1.vertexBuffer());
            ByteBuffer $$7 = $$1.indexBuffer();
            GpuBuffer $$8 = $$7 != null ? RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + $$0.label() + "; cords: " + SectionPos.x($$2) + ", " + SectionPos.y($$2) + ", " + SectionPos.z($$2), 72, $$7) : null;
            SectionBuffers $$9 = new SectionBuffers($$6, $$8, $$1.drawState().indexCount(), $$1.drawState().indexType());
            this.buffers.put($$0, $$9);
        }
    }

    public void uploadLayerIndexBuffer(ChunkSectionLayer $$0, ByteBufferBuilder.Result $$1, long $$2) {
        SectionBuffers $$3 = this.getBuffers($$0);
        if ($$3 == null) {
            return;
        }
        if ($$3.getIndexBuffer() == null) {
            $$3.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + $$0.label() + "; cords: " + SectionPos.x($$2) + ", " + SectionPos.y($$2) + ", " + SectionPos.z($$2), 72, $$1.byteBuffer()));
        } else {
            CommandEncoder $$4 = RenderSystem.getDevice().createCommandEncoder();
            if (!$$3.getIndexBuffer().isClosed()) {
                $$4.writeToBuffer($$3.getIndexBuffer().slice(), $$1.byteBuffer());
            }
        }
    }

    @Override
    public boolean hasTranslucentGeometry() {
        return this.buffers.containsKey((Object)ChunkSectionLayer.TRANSLUCENT);
    }

    @Nullable
    public MeshData.SortState getTransparencyState() {
        return this.transparencyState;
    }

    @Override
    public void close() {
        this.buffers.values().forEach(SectionBuffers::close);
        this.buffers.clear();
    }
}

