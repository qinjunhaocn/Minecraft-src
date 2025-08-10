/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class DynamicUniforms
implements AutoCloseable {
    public static final int TRANSFORM_UBO_SIZE = new Std140SizeCalculator().putMat4f().putVec4().putVec3().putMat4f().putFloat().get();
    private static final int INITIAL_CAPACITY = 2;
    private final DynamicUniformStorage<Transform> transforms = new DynamicUniformStorage("Dynamic Transforms UBO", TRANSFORM_UBO_SIZE, 2);

    public void reset() {
        this.transforms.endFrame();
    }

    @Override
    public void close() {
        this.transforms.close();
    }

    public GpuBufferSlice writeTransform(Matrix4fc $$0, Vector4fc $$1, Vector3fc $$2, Matrix4fc $$3, float $$4) {
        return this.transforms.writeUniform(new Transform((Matrix4fc)new Matrix4f($$0), (Vector4fc)new Vector4f($$1), (Vector3fc)new Vector3f($$2), (Matrix4fc)new Matrix4f($$3), $$4));
    }

    public GpuBufferSlice[] a(Transform ... $$0) {
        return this.transforms.a($$0);
    }

    public record Transform(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) implements DynamicUniformStorage.DynamicUniform
    {
        @Override
        public void write(ByteBuffer $$0) {
            Std140Builder.intoBuffer($$0).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix).putFloat(this.lineWidth);
        }
    }
}

