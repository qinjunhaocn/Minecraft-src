/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

@DontObfuscate
public interface RenderPass
extends AutoCloseable {
    public void pushDebugGroup(Supplier<String> var1);

    public void popDebugGroup();

    public void setPipeline(RenderPipeline var1);

    public void bindSampler(String var1, @Nullable GpuTextureView var2);

    public void setUniform(String var1, GpuBuffer var2);

    public void setUniform(String var1, GpuBufferSlice var2);

    public void enableScissor(int var1, int var2, int var3, int var4);

    public void disableScissor();

    public void setVertexBuffer(int var1, GpuBuffer var2);

    public void setIndexBuffer(GpuBuffer var1, VertexFormat.IndexType var2);

    public void drawIndexed(int var1, int var2, int var3, int var4);

    public <T> void drawMultipleIndexed(Collection<Draw<T>> var1, @Nullable GpuBuffer var2, @Nullable VertexFormat.IndexType var3, Collection<String> var4, T var5);

    public void draw(int var1, int var2);

    @Override
    public void close();

    public static interface UniformUploader {
        public void upload(String var1, GpuBufferSlice var2);
    }

    public record Draw<T>(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, @Nullable VertexFormat.IndexType indexType, int firstIndex, int indexCount, @Nullable BiConsumer<T, UniformUploader> uniformUploaderConsumer) {
        public Draw(int $$0, GpuBuffer $$1, GpuBuffer $$2, VertexFormat.IndexType $$3, int $$4, int $$5) {
            this($$0, $$1, $$2, $$3, $$4, $$5, null);
        }

        @Nullable
        public GpuBuffer indexBuffer() {
            return this.indexBuffer;
        }

        @Nullable
        public VertexFormat.IndexType indexType() {
            return this.indexType;
        }

        @Nullable
        public BiConsumer<T, UniformUploader> uniformUploaderConsumer() {
            return this.uniformUploaderConsumer;
        }
    }
}

