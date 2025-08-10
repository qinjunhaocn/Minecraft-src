/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CachedPerspectiveProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class CubeMap
implements AutoCloseable {
    private static final int SIDES = 6;
    private final GpuBuffer vertexBuffer;
    private final CachedPerspectiveProjectionMatrixBuffer projectionMatrixUbo;
    private final ResourceLocation location;

    public CubeMap(ResourceLocation $$0) {
        this.location = $$0;
        this.projectionMatrixUbo = new CachedPerspectiveProjectionMatrixBuffer("cubemap", 0.05f, 10.0f);
        this.vertexBuffer = CubeMap.initializeVertices();
    }

    public void render(Minecraft $$0, float $$1, float $$2) {
        RenderSystem.setProjectionMatrix(this.projectionMatrixUbo.getBuffer($$0.getWindow().getWidth(), $$0.getWindow().getHeight(), 85.0f), ProjectionType.PERSPECTIVE);
        RenderPipeline $$3 = RenderPipelines.PANORAMA;
        RenderTarget $$4 = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView $$5 = $$4.getColorTextureView();
        GpuTextureView $$6 = $$4.getDepthTextureView();
        RenderSystem.AutoStorageIndexBuffer $$7 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$8 = $$7.getBuffer(36);
        Matrix4fStack $$9 = RenderSystem.getModelViewStack();
        $$9.pushMatrix();
        $$9.rotationX((float)Math.PI);
        $$9.rotateX($$1 * ((float)Math.PI / 180));
        $$9.rotateY($$2 * ((float)Math.PI / 180));
        GpuBufferSlice $$10 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)new Matrix4f((Matrix4fc)$$9), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        $$9.popMatrix();
        try (RenderPass $$11 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", $$5, OptionalInt.empty(), $$6, OptionalDouble.empty());){
            $$11.setPipeline($$3);
            RenderSystem.bindDefaultUniforms($$11);
            $$11.setVertexBuffer(0, this.vertexBuffer);
            $$11.setIndexBuffer($$8, $$7.type());
            $$11.setUniform("DynamicTransforms", $$10);
            $$11.bindSampler("Sampler0", $$0.getTextureManager().getTexture(this.location).getTextureView());
            $$11.drawIndexed(0, 0, 36, 1);
        }
    }

    private static GpuBuffer initializeVertices() {
        try (ByteBufferBuilder $$0 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 4 * 6);){
            BufferBuilder $$1 = new BufferBuilder($$0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            $$1.addVertex(-1.0f, -1.0f, 1.0f);
            $$1.addVertex(-1.0f, 1.0f, 1.0f);
            $$1.addVertex(1.0f, 1.0f, 1.0f);
            $$1.addVertex(1.0f, -1.0f, 1.0f);
            $$1.addVertex(1.0f, -1.0f, 1.0f);
            $$1.addVertex(1.0f, 1.0f, 1.0f);
            $$1.addVertex(1.0f, 1.0f, -1.0f);
            $$1.addVertex(1.0f, -1.0f, -1.0f);
            $$1.addVertex(1.0f, -1.0f, -1.0f);
            $$1.addVertex(1.0f, 1.0f, -1.0f);
            $$1.addVertex(-1.0f, 1.0f, -1.0f);
            $$1.addVertex(-1.0f, -1.0f, -1.0f);
            $$1.addVertex(-1.0f, -1.0f, -1.0f);
            $$1.addVertex(-1.0f, 1.0f, -1.0f);
            $$1.addVertex(-1.0f, 1.0f, 1.0f);
            $$1.addVertex(-1.0f, -1.0f, 1.0f);
            $$1.addVertex(-1.0f, -1.0f, -1.0f);
            $$1.addVertex(-1.0f, -1.0f, 1.0f);
            $$1.addVertex(1.0f, -1.0f, 1.0f);
            $$1.addVertex(1.0f, -1.0f, -1.0f);
            $$1.addVertex(-1.0f, 1.0f, 1.0f);
            $$1.addVertex(-1.0f, 1.0f, -1.0f);
            $$1.addVertex(1.0f, 1.0f, -1.0f);
            $$1.addVertex(1.0f, 1.0f, 1.0f);
            MeshData $$2 = $$1.buildOrThrow();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, $$2.vertexBuffer());
                if ($$2 != null) {
                    $$2.close();
                }
                return gpuBuffer;
            } catch (Throwable throwable) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public void registerTextures(TextureManager $$0) {
        $$0.register(this.location, new CubeMapTexture(this.location));
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        this.projectionMatrixUbo.close();
    }
}

