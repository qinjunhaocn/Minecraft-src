/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;

@DontObfuscate
public record GpuBufferSlice(GpuBuffer buffer, int offset, int length) {
    public GpuBufferSlice slice(int $$0, int $$1) {
        if ($$0 < 0 || $$1 < 0 || $$0 + $$1 >= this.length) {
            throw new IllegalArgumentException("Offset of " + $$0 + " and length " + $$1 + " would put new slice outside existing slice's range (of " + $$0 + "," + $$1 + ")");
        }
        return new GpuBufferSlice(this.buffer, this.offset + $$0, $$1);
    }
}

