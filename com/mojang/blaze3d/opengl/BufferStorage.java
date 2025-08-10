/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public abstract class BufferStorage {
    public static BufferStorage create(GLCapabilities $$0, Set<String> $$1) {
        if ($$0.GL_ARB_buffer_storage && GlDevice.USE_GL_ARB_buffer_storage) {
            $$1.add("GL_ARB_buffer_storage");
            return new Immutable();
        }
        return new Mutable();
    }

    public abstract GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, int var4);

    public abstract GlBuffer createBuffer(DirectStateAccess var1, @Nullable Supplier<String> var2, int var3, ByteBuffer var4);

    public abstract GlBuffer.GlMappedView mapBuffer(DirectStateAccess var1, GlBuffer var2, int var3, int var4, int var5);

    static class Immutable
    extends BufferStorage {
        Immutable() {
        }

        @Override
        public GlBuffer createBuffer(DirectStateAccess $$0, @Nullable Supplier<String> $$1, int $$2, int $$3) {
            int $$4 = $$0.createBuffer();
            $$0.bufferStorage($$4, $$3, GlConst.bufferUsageToGlFlag($$2));
            ByteBuffer $$5 = this.tryMapBufferPersistent($$0, $$2, $$4, $$3);
            return new GlBuffer($$1, $$0, $$2, $$3, $$4, $$5);
        }

        @Override
        public GlBuffer createBuffer(DirectStateAccess $$0, @Nullable Supplier<String> $$1, int $$2, ByteBuffer $$3) {
            int $$4 = $$0.createBuffer();
            int $$5 = $$3.remaining();
            $$0.bufferStorage($$4, $$3, GlConst.bufferUsageToGlFlag($$2));
            ByteBuffer $$6 = this.tryMapBufferPersistent($$0, $$2, $$4, $$5);
            return new GlBuffer($$1, $$0, $$2, $$5, $$4, $$6);
        }

        @Nullable
        private ByteBuffer tryMapBufferPersistent(DirectStateAccess $$0, int $$1, int $$2, int $$3) {
            ByteBuffer $$6;
            int $$4 = 0;
            if (($$1 & 1) != 0) {
                $$4 |= 1;
            }
            if (($$1 & 2) != 0) {
                $$4 |= 0x12;
            }
            if ($$4 != 0) {
                GlStateManager.clearGlErrors();
                ByteBuffer $$5 = $$0.mapBufferRange($$2, 0, $$3, $$4 | 0x40);
                if ($$5 == null) {
                    throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
                }
            } else {
                $$6 = null;
            }
            return $$6;
        }

        @Override
        public GlBuffer.GlMappedView mapBuffer(DirectStateAccess $$0, GlBuffer $$1, int $$2, int $$3, int $$4) {
            if ($$1.persistentBuffer == null) {
                throw new IllegalStateException("Somehow trying to map an unmappable buffer");
            }
            return new GlBuffer.GlMappedView(() -> {
                if (($$4 & 2) != 0) {
                    $$0.flushMappedBufferRange($$2.handle, $$2, $$3);
                }
            }, $$1, MemoryUtil.memSlice((ByteBuffer)$$1.persistentBuffer, (int)$$2, (int)$$3));
        }
    }

    static class Mutable
    extends BufferStorage {
        Mutable() {
        }

        @Override
        public GlBuffer createBuffer(DirectStateAccess $$0, @Nullable Supplier<String> $$1, int $$2, int $$3) {
            int $$4 = $$0.createBuffer();
            $$0.bufferData($$4, $$3, GlConst.bufferUsageToGlEnum($$2));
            return new GlBuffer($$1, $$0, $$2, $$3, $$4, null);
        }

        @Override
        public GlBuffer createBuffer(DirectStateAccess $$0, @Nullable Supplier<String> $$1, int $$2, ByteBuffer $$3) {
            int $$4 = $$0.createBuffer();
            int $$5 = $$3.remaining();
            $$0.bufferData($$4, $$3, GlConst.bufferUsageToGlEnum($$2));
            return new GlBuffer($$1, $$0, $$2, $$5, $$4, null);
        }

        @Override
        public GlBuffer.GlMappedView mapBuffer(DirectStateAccess $$0, GlBuffer $$1, int $$2, int $$3, int $$4) {
            GlStateManager.clearGlErrors();
            ByteBuffer $$5 = $$0.mapBufferRange($$1.handle, $$2, $$3, $$4);
            if ($$5 == null) {
                throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
            }
            return new GlBuffer.GlMappedView(() -> $$0.unmapBuffer($$1.handle), $$1, $$5);
        }
    }
}

