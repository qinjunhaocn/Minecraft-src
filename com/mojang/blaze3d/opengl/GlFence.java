/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlStateManager;

public class GlFence
implements GpuFence {
    private long handle = GlStateManager._glFenceSync(37143, 0);

    @Override
    public void close() {
        if (this.handle != 0L) {
            GlStateManager._glDeleteSync(this.handle);
            this.handle = 0L;
        }
    }

    @Override
    public boolean awaitCompletion(long $$0) {
        if (this.handle == 0L) {
            return true;
        }
        int $$1 = GlStateManager._glClientWaitSync(this.handle, 0, $$0);
        if ($$1 == 37147) {
            return false;
        }
        if ($$1 == 37149) {
            throw new IllegalStateException("Failed to complete gpu fence");
        }
        return true;
    }
}

