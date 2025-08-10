/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

public class PerspectiveProjectionMatrixBuffer
implements AutoCloseable {
    private final GpuBuffer buffer;
    private final GpuBufferSlice bufferSlice;

    public PerspectiveProjectionMatrixBuffer(String $$0) {
        GpuDevice $$1 = RenderSystem.getDevice();
        this.buffer = $$1.createBuffer(() -> "Projection matrix UBO " + $$0, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        this.bufferSlice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
    }

    public GpuBufferSlice getBuffer(Matrix4f $$0) {
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            ByteBuffer $$2 = Std140Builder.onStack($$1, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f((Matrix4fc)$$0).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), $$2);
        }
        return this.bufferSlice;
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

