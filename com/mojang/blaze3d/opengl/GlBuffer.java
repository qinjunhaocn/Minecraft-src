/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class GlBuffer
extends GpuBuffer {
    protected static final MemoryPool MEMORY_POOl = TracyClient.createMemoryPool((String)"GPU Buffers");
    protected boolean closed;
    @Nullable
    protected final Supplier<String> label;
    private final DirectStateAccess dsa;
    protected final int handle;
    @Nullable
    protected ByteBuffer persistentBuffer;

    protected GlBuffer(@Nullable Supplier<String> $$0, DirectStateAccess $$1, int $$2, int $$3, int $$4, @Nullable ByteBuffer $$5) {
        super($$2, $$3);
        this.label = $$0;
        this.dsa = $$1;
        this.handle = $$4;
        this.persistentBuffer = $$5;
        MEMORY_POOl.malloc((long)$$4, $$3);
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.persistentBuffer != null) {
            this.dsa.unmapBuffer(this.handle);
            this.persistentBuffer = null;
        }
        GlStateManager._glDeleteBuffers(this.handle);
        MEMORY_POOl.free((long)this.handle);
    }

    public static class GlMappedView
    implements GpuBuffer.MappedView {
        private final Runnable unmap;
        private final GlBuffer buffer;
        private final ByteBuffer data;
        private boolean closed;

        protected GlMappedView(Runnable $$0, GlBuffer $$1, ByteBuffer $$2) {
            this.unmap = $$0;
            this.buffer = $$1;
            this.data = $$2;
        }

        @Override
        public ByteBuffer data() {
            return this.data;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.unmap.run();
        }
    }
}

