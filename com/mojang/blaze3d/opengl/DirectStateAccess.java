/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBBufferStorage
 *  org.lwjgl.opengl.ARBDirectStateAccess
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GLCapabilities
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

public abstract class DirectStateAccess {
    public static DirectStateAccess create(GLCapabilities $$0, Set<String> $$1) {
        if ($$0.GL_ARB_direct_state_access && GlDevice.USE_GL_ARB_direct_state_access) {
            $$1.add("GL_ARB_direct_state_access");
            return new Core();
        }
        return new Emulated();
    }

    abstract int createBuffer();

    abstract void bufferData(int var1, long var2, int var4);

    abstract void bufferData(int var1, ByteBuffer var2, int var3);

    abstract void bufferSubData(int var1, int var2, ByteBuffer var3);

    abstract void bufferStorage(int var1, long var2, int var4);

    abstract void bufferStorage(int var1, ByteBuffer var2, int var3);

    @Nullable
    abstract ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4);

    abstract void unmapBuffer(int var1);

    abstract int createFrameBufferObject();

    abstract void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5);

    abstract void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

    abstract void flushMappedBufferRange(int var1, int var2, int var3);

    abstract void copyBufferSubData(int var1, int var2, int var3, int var4, int var5);

    static class Core
    extends DirectStateAccess {
        Core() {
        }

        @Override
        int createBuffer() {
            return ARBDirectStateAccess.glCreateBuffers();
        }

        @Override
        void bufferData(int $$0, long $$1, int $$2) {
            ARBDirectStateAccess.glNamedBufferData((int)$$0, (long)$$1, (int)$$2);
        }

        @Override
        void bufferData(int $$0, ByteBuffer $$1, int $$2) {
            ARBDirectStateAccess.glNamedBufferData((int)$$0, (ByteBuffer)$$1, (int)$$2);
        }

        @Override
        void bufferSubData(int $$0, int $$1, ByteBuffer $$2) {
            ARBDirectStateAccess.glNamedBufferSubData((int)$$0, (long)$$1, (ByteBuffer)$$2);
        }

        @Override
        void bufferStorage(int $$0, long $$1, int $$2) {
            ARBDirectStateAccess.glNamedBufferStorage((int)$$0, (long)$$1, (int)$$2);
        }

        @Override
        void bufferStorage(int $$0, ByteBuffer $$1, int $$2) {
            ARBDirectStateAccess.glNamedBufferStorage((int)$$0, (ByteBuffer)$$1, (int)$$2);
        }

        @Override
        @Nullable
        ByteBuffer mapBufferRange(int $$0, int $$1, int $$2, int $$3) {
            return ARBDirectStateAccess.glMapNamedBufferRange((int)$$0, (long)$$1, (long)$$2, (int)$$3);
        }

        @Override
        void unmapBuffer(int $$0) {
            ARBDirectStateAccess.glUnmapNamedBuffer((int)$$0);
        }

        @Override
        public int createFrameBufferObject() {
            return ARBDirectStateAccess.glCreateFramebuffers();
        }

        @Override
        public void bindFrameBufferTextures(int $$0, int $$1, int $$2, int $$3, int $$4) {
            ARBDirectStateAccess.glNamedFramebufferTexture((int)$$0, (int)36064, (int)$$1, (int)$$3);
            ARBDirectStateAccess.glNamedFramebufferTexture((int)$$0, (int)36096, (int)$$2, (int)$$3);
            if ($$4 != 0) {
                GlStateManager._glBindFramebuffer($$4, $$0);
            }
        }

        @Override
        public void blitFrameBuffers(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11) {
            ARBDirectStateAccess.glBlitNamedFramebuffer((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (int)$$8, (int)$$9, (int)$$10, (int)$$11);
        }

        @Override
        void flushMappedBufferRange(int $$0, int $$1, int $$2) {
            ARBDirectStateAccess.glFlushMappedNamedBufferRange((int)$$0, (long)$$1, (long)$$2);
        }

        @Override
        void copyBufferSubData(int $$0, int $$1, int $$2, int $$3, int $$4) {
            ARBDirectStateAccess.glCopyNamedBufferSubData((int)$$0, (int)$$1, (long)$$2, (long)$$3, (long)$$4);
        }
    }

    static class Emulated
    extends DirectStateAccess {
        Emulated() {
        }

        @Override
        int createBuffer() {
            return GlStateManager._glGenBuffers();
        }

        @Override
        void bufferData(int $$0, long $$1, int $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            GlStateManager._glBufferData(36663, $$1, GlConst.bufferUsageToGlEnum($$2));
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void bufferData(int $$0, ByteBuffer $$1, int $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            GlStateManager._glBufferData(36663, $$1, GlConst.bufferUsageToGlEnum($$2));
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void bufferSubData(int $$0, int $$1, ByteBuffer $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            GlStateManager._glBufferSubData(36663, $$1, $$2);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void bufferStorage(int $$0, long $$1, int $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            ARBBufferStorage.glBufferStorage((int)36663, (long)$$1, (int)$$2);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void bufferStorage(int $$0, ByteBuffer $$1, int $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            ARBBufferStorage.glBufferStorage((int)36663, (ByteBuffer)$$1, (int)$$2);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        @Nullable
        ByteBuffer mapBufferRange(int $$0, int $$1, int $$2, int $$3) {
            GlStateManager._glBindBuffer(36663, $$0);
            ByteBuffer $$4 = GlStateManager._glMapBufferRange(36663, $$1, $$2, $$3);
            GlStateManager._glBindBuffer(36663, 0);
            return $$4;
        }

        @Override
        void unmapBuffer(int $$0) {
            GlStateManager._glBindBuffer(36663, $$0);
            GlStateManager._glUnmapBuffer(36663);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void flushMappedBufferRange(int $$0, int $$1, int $$2) {
            GlStateManager._glBindBuffer(36663, $$0);
            GL30.glFlushMappedBufferRange((int)36663, (long)$$1, (long)$$2);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        void copyBufferSubData(int $$0, int $$1, int $$2, int $$3, int $$4) {
            GlStateManager._glBindBuffer(36662, $$0);
            GlStateManager._glBindBuffer(36663, $$1);
            GL31.glCopyBufferSubData((int)36662, (int)36663, (long)$$2, (long)$$3, (long)$$4);
            GlStateManager._glBindBuffer(36662, 0);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        public int createFrameBufferObject() {
            return GlStateManager.glGenFramebuffers();
        }

        @Override
        public void bindFrameBufferTextures(int $$0, int $$1, int $$2, int $$3, int $$4) {
            int $$5 = $$4 == 0 ? 36009 : $$4;
            int $$6 = GlStateManager.getFrameBuffer($$5);
            GlStateManager._glBindFramebuffer($$5, $$0);
            GlStateManager._glFramebufferTexture2D($$5, 36064, 3553, $$1, $$3);
            GlStateManager._glFramebufferTexture2D($$5, 36096, 3553, $$2, $$3);
            if ($$4 == 0) {
                GlStateManager._glBindFramebuffer($$5, $$6);
            }
        }

        @Override
        public void blitFrameBuffers(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10, int $$11) {
            int $$12 = GlStateManager.getFrameBuffer(36008);
            int $$13 = GlStateManager.getFrameBuffer(36009);
            GlStateManager._glBindFramebuffer(36008, $$0);
            GlStateManager._glBindFramebuffer(36009, $$1);
            GlStateManager._glBlitFrameBuffer($$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11);
            GlStateManager._glBindFramebuffer(36008, $$12);
            GlStateManager._glBindFramebuffer(36009, $$13);
        }
    }
}

