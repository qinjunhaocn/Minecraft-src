/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;

public class GlRenderPass
implements RenderPass {
    protected static final int MAX_VERTEX_BUFFERS = 1;
    public static final boolean VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
    private final GlCommandEncoder encoder;
    private final boolean hasDepthTexture;
    private boolean closed;
    @Nullable
    protected GlRenderPipeline pipeline;
    protected final GpuBuffer[] vertexBuffers = new GpuBuffer[1];
    @Nullable
    protected GpuBuffer indexBuffer;
    protected VertexFormat.IndexType indexType = VertexFormat.IndexType.INT;
    private final ScissorState scissorState = new ScissorState();
    protected final HashMap<String, GpuBufferSlice> uniforms = new HashMap();
    protected final HashMap<String, GpuTextureView> samplers = new HashMap();
    protected final Set<String> dirtyUniforms = new HashSet<String>();
    protected int pushedDebugGroups;

    public GlRenderPass(GlCommandEncoder $$0, boolean $$1) {
        this.encoder = $$0;
        this.hasDepthTexture = $$1;
    }

    public boolean hasDepthTexture() {
        return this.hasDepthTexture;
    }

    @Override
    public void pushDebugGroup(Supplier<String> $$0) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        ++this.pushedDebugGroups;
        this.encoder.getDevice().debugLabels().pushDebugGroup($$0);
    }

    @Override
    public void popDebugGroup() {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        if (this.pushedDebugGroups == 0) {
            throw new IllegalStateException("Can't pop more debug groups than was pushed!");
        }
        --this.pushedDebugGroups;
        this.encoder.getDevice().debugLabels().popDebugGroup();
    }

    @Override
    public void setPipeline(RenderPipeline $$0) {
        if (this.pipeline == null || this.pipeline.info() != $$0) {
            this.dirtyUniforms.addAll(this.uniforms.keySet());
            this.dirtyUniforms.addAll(this.samplers.keySet());
        }
        this.pipeline = this.encoder.getDevice().getOrCompilePipeline($$0);
    }

    @Override
    public void bindSampler(String $$0, @Nullable GpuTextureView $$1) {
        if ($$1 == null) {
            this.samplers.remove($$0);
        } else {
            this.samplers.put($$0, $$1);
        }
        this.dirtyUniforms.add($$0);
    }

    @Override
    public void setUniform(String $$0, GpuBuffer $$1) {
        this.uniforms.put($$0, $$1.slice());
        this.dirtyUniforms.add($$0);
    }

    @Override
    public void setUniform(String $$0, GpuBufferSlice $$1) {
        int $$2 = this.encoder.getDevice().getUniformOffsetAlignment();
        if ($$1.offset() % $$2 > 0) {
            throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + $$2);
        }
        this.uniforms.put($$0, $$1);
        this.dirtyUniforms.add($$0);
    }

    @Override
    public void enableScissor(int $$0, int $$1, int $$2, int $$3) {
        this.scissorState.enable($$0, $$1, $$2, $$3);
    }

    @Override
    public void disableScissor() {
        this.scissorState.disable();
    }

    public boolean isScissorEnabled() {
        return this.scissorState.enabled();
    }

    public int getScissorX() {
        return this.scissorState.x();
    }

    public int getScissorY() {
        return this.scissorState.y();
    }

    public int getScissorWidth() {
        return this.scissorState.width();
    }

    public int getScissorHeight() {
        return this.scissorState.height();
    }

    @Override
    public void setVertexBuffer(int $$0, GpuBuffer $$1) {
        if ($$0 < 0 || $$0 >= 1) {
            throw new IllegalArgumentException("Vertex buffer slot is out of range: " + $$0);
        }
        this.vertexBuffers[$$0] = $$1;
    }

    @Override
    public void setIndexBuffer(@Nullable GpuBuffer $$0, VertexFormat.IndexType $$1) {
        this.indexBuffer = $$0;
        this.indexType = $$1;
    }

    @Override
    public void drawIndexed(int $$0, int $$1, int $$2, int $$3) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDraw(this, $$0, $$1, $$2, this.indexType, $$3);
    }

    @Override
    public <T> void drawMultipleIndexed(Collection<RenderPass.Draw<T>> $$0, @Nullable GpuBuffer $$1, @Nullable VertexFormat.IndexType $$2, Collection<String> $$3, T $$4) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDrawMultiple(this, $$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void draw(int $$0, int $$1) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDraw(this, $$0, 0, $$1, null, 1);
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (this.pushedDebugGroups > 0) {
                throw new IllegalStateException("Render pass had debug groups left open!");
            }
            this.closed = true;
            this.encoder.finishRenderPass();
        }
    }
}

