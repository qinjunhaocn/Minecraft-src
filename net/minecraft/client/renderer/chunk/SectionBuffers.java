/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nullable;

public final class SectionBuffers
implements AutoCloseable {
    private GpuBuffer vertexBuffer;
    @Nullable
    private GpuBuffer indexBuffer;
    private int indexCount;
    private VertexFormat.IndexType indexType;

    public SectionBuffers(GpuBuffer $$0, @Nullable GpuBuffer $$1, int $$2, VertexFormat.IndexType $$3) {
        this.vertexBuffer = $$0;
        this.indexBuffer = $$1;
        this.indexCount = $$2;
        this.indexType = $$3;
    }

    public GpuBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    @Nullable
    public GpuBuffer getIndexBuffer() {
        return this.indexBuffer;
    }

    public void setIndexBuffer(@Nullable GpuBuffer $$0) {
        this.indexBuffer = $$0;
    }

    public int getIndexCount() {
        return this.indexCount;
    }

    public VertexFormat.IndexType getIndexType() {
        return this.indexType;
    }

    public void setIndexType(VertexFormat.IndexType $$0) {
        this.indexType = $$0;
    }

    public void setIndexCount(int $$0) {
        this.indexCount = $$0;
    }

    public void setVertexBuffer(GpuBuffer $$0) {
        this.vertexBuffer = $$0;
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
        }
    }
}

