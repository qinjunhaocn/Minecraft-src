/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;

public class MappableRingBuffer
implements AutoCloseable {
    private static final int BUFFER_COUNT = 3;
    private final GpuBuffer[] buffers = new GpuBuffer[3];
    private final GpuFence[] fences = new GpuFence[3];
    private final int size;
    private int current = 0;

    public MappableRingBuffer(Supplier<String> $$0, int $$1, int $$2) {
        GpuDevice $$3 = RenderSystem.getDevice();
        if (($$1 & 1) == 0 && ($$1 & 2) == 0) {
            throw new IllegalArgumentException("MappableRingBuffer requires at least one of USAGE_MAP_READ or USAGE_MAP_WRITE");
        }
        for (int $$4 = 0; $$4 < 3; ++$$4) {
            int $$5 = $$4;
            this.buffers[$$4] = $$3.createBuffer(() -> (String)$$0.get() + " #" + $$5, $$1, $$2);
            this.fences[$$4] = null;
        }
        this.size = $$2;
    }

    public int size() {
        return this.size;
    }

    public GpuBuffer currentBuffer() {
        GpuFence $$0 = this.fences[this.current];
        if ($$0 != null) {
            $$0.awaitCompletion(Long.MAX_VALUE);
            $$0.close();
            this.fences[this.current] = null;
        }
        return this.buffers[this.current];
    }

    public void rotate() {
        if (this.fences[this.current] != null) {
            this.fences[this.current].close();
        }
        this.fences[this.current] = RenderSystem.getDevice().createCommandEncoder().createFence();
        this.current = (this.current + 1) % 3;
    }

    @Override
    public void close() {
        for (int $$0 = 0; $$0 < 3; ++$$0) {
            this.buffers[$$0].close();
            if (this.fences[$$0] == null) continue;
            this.fences[$$0].close();
        }
    }
}

