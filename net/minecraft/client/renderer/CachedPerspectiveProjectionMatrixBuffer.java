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

public class CachedPerspectiveProjectionMatrixBuffer
implements AutoCloseable {
    private final GpuBuffer buffer;
    private final GpuBufferSlice bufferSlice;
    private final float zNear;
    private final float zFar;
    private int width;
    private int height;
    private float fov;

    public CachedPerspectiveProjectionMatrixBuffer(String $$0, float $$1, float $$2) {
        this.zNear = $$1;
        this.zFar = $$2;
        GpuDevice $$3 = RenderSystem.getDevice();
        this.buffer = $$3.createBuffer(() -> "Projection matrix UBO " + $$0, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        this.bufferSlice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
    }

    public GpuBufferSlice getBuffer(int $$0, int $$1, float $$2) {
        if (this.width != $$0 || this.height != $$1 || this.fov != $$2) {
            Matrix4f $$3 = this.createProjectionMatrix($$0, $$1, $$2);
            try (MemoryStack $$4 = MemoryStack.stackPush();){
                ByteBuffer $$5 = Std140Builder.onStack($$4, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f((Matrix4fc)$$3).get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), $$5);
            }
            this.width = $$0;
            this.height = $$1;
            this.fov = $$2;
        }
        return this.bufferSlice;
    }

    private Matrix4f createProjectionMatrix(int $$0, int $$1, float $$2) {
        return new Matrix4f().perspective($$2 * ((float)Math.PI / 180), (float)$$0 / (float)$$1, this.zNear, this.zFar);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

