/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class RenderSystem {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    public static final int PROJECTION_MATRIX_UBO_SIZE = new Std140SizeCalculator().putMat4f().get();
    @Nullable
    private static Thread renderThread;
    @Nullable
    private static GpuDevice DEVICE;
    private static double lastDrawTime;
    private static final AutoStorageIndexBuffer sharedSequential;
    private static final AutoStorageIndexBuffer sharedSequentialQuad;
    private static final AutoStorageIndexBuffer sharedSequentialLines;
    private static ProjectionType projectionType;
    private static ProjectionType savedProjectionType;
    private static final Matrix4fStack modelViewStack;
    private static Matrix4f textureMatrix;
    public static final int TEXTURE_COUNT = 12;
    private static final GpuTextureView[] shaderTextures;
    @Nullable
    private static GpuBufferSlice shaderFog;
    @Nullable
    private static GpuBufferSlice shaderLightDirections;
    @Nullable
    private static GpuBufferSlice projectionMatrixBuffer;
    @Nullable
    private static GpuBufferSlice savedProjectionMatrixBuffer;
    private static final Vector3f modelOffset;
    private static float shaderLineWidth;
    private static String apiDescription;
    private static final AtomicLong pollEventsWaitStart;
    private static final AtomicBoolean pollingEvents;
    @Nullable
    private static GpuBuffer QUAD_VERTEX_BUFFER;
    private static final ArrayListDeque<GpuAsyncTask> PENDING_FENCES;
    @Nullable
    public static GpuTextureView outputColorTextureOverride;
    @Nullable
    public static GpuTextureView outputDepthTextureOverride;
    @Nullable
    private static GpuBuffer globalSettingsUniform;
    @Nullable
    private static DynamicUniforms dynamicUniforms;
    private static ScissorState scissorStateForRenderTypeDraws;

    public static void initRenderThread() {
        if (renderThread != null) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static void assertOnRenderThread() {
        if (!RenderSystem.isOnRenderThread()) {
            throw RenderSystem.constructThreadException();
        }
    }

    private static IllegalStateException constructThreadException() {
        return new IllegalStateException("Rendersystem called from wrong thread");
    }

    private static void pollEvents() {
        pollEventsWaitStart.set(Util.getMillis());
        pollingEvents.set(true);
        GLFW.glfwPollEvents();
        pollingEvents.set(false);
    }

    public static boolean isFrozenAtPollEvents() {
        return pollingEvents.get() && Util.getMillis() - pollEventsWaitStart.get() > 200L;
    }

    public static void flipFrame(long $$0, @Nullable TracyFrameCapture $$1) {
        RenderSystem.pollEvents();
        Tesselator.getInstance().clear();
        GLFW.glfwSwapBuffers((long)$$0);
        if ($$1 != null) {
            $$1.endFrame();
        }
        dynamicUniforms.reset();
        Minecraft.getInstance().levelRenderer.endFrame();
        RenderSystem.pollEvents();
    }

    public static void limitDisplayFPS(int $$0) {
        double $$1 = lastDrawTime + 1.0 / (double)$$0;
        double $$2 = GLFW.glfwGetTime();
        while ($$2 < $$1) {
            GLFW.glfwWaitEventsTimeout((double)($$1 - $$2));
            $$2 = GLFW.glfwGetTime();
        }
        lastDrawTime = $$2;
    }

    public static void setShaderFog(GpuBufferSlice $$0) {
        shaderFog = $$0;
    }

    @Nullable
    public static GpuBufferSlice getShaderFog() {
        return shaderFog;
    }

    public static void setShaderLights(GpuBufferSlice $$0) {
        shaderLightDirections = $$0;
    }

    @Nullable
    public static GpuBufferSlice getShaderLights() {
        return shaderLightDirections;
    }

    public static void lineWidth(float $$0) {
        RenderSystem.assertOnRenderThread();
        shaderLineWidth = $$0;
    }

    public static float getShaderLineWidth() {
        RenderSystem.assertOnRenderThread();
        return shaderLineWidth;
    }

    public static void enableScissorForRenderTypeDraws(int $$0, int $$1, int $$2, int $$3) {
        scissorStateForRenderTypeDraws.enable($$0, $$1, $$2, $$3);
    }

    public static void disableScissorForRenderTypeDraws() {
        scissorStateForRenderTypeDraws.disable();
    }

    public static ScissorState getScissorStateForRenderTypeDraws() {
        return scissorStateForRenderTypeDraws;
    }

    public static String getBackendDescription() {
        return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
    }

    public static String getApiDescription() {
        return apiDescription;
    }

    public static TimeSource.NanoTimeSource initBackendSystem() {
        return GLX._initGlfw()::getAsLong;
    }

    public static void initRenderer(long $$0, int $$1, boolean $$2, BiFunction<ResourceLocation, ShaderType, String> $$3, boolean $$4) {
        DEVICE = new GlDevice($$0, $$1, $$2, $$3, $$4);
        apiDescription = RenderSystem.getDevice().getImplementationInformation();
        dynamicUniforms = new DynamicUniforms();
        try (ByteBufferBuilder $$5 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 4);){
            BufferBuilder $$6 = new BufferBuilder($$5, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            $$6.addVertex(0.0f, 0.0f, 0.0f);
            $$6.addVertex(1.0f, 0.0f, 0.0f);
            $$6.addVertex(1.0f, 1.0f, 0.0f);
            $$6.addVertex(0.0f, 1.0f, 0.0f);
            try (MeshData $$7 = $$6.buildOrThrow();){
                QUAD_VERTEX_BUFFER = RenderSystem.getDevice().createBuffer(() -> "Quad", 32, $$7.vertexBuffer());
            }
        }
    }

    public static void setErrorCallback(GLFWErrorCallbackI $$0) {
        GLX._setGlfwErrorCallback($$0);
    }

    public static void setupDefaultState() {
        modelViewStack.clear();
        textureMatrix.identity();
    }

    public static void setupOverlayColor(@Nullable GpuTextureView $$0) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, $$0);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, null);
    }

    public static void setShaderTexture(int $$0, @Nullable GpuTextureView $$1) {
        RenderSystem.assertOnRenderThread();
        if ($$0 >= 0 && $$0 < shaderTextures.length) {
            RenderSystem.shaderTextures[$$0] = $$1;
        }
    }

    @Nullable
    public static GpuTextureView getShaderTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 >= 0 && $$0 < shaderTextures.length) {
            return shaderTextures[$$0];
        }
        return null;
    }

    public static void setProjectionMatrix(GpuBufferSlice $$0, ProjectionType $$1) {
        RenderSystem.assertOnRenderThread();
        projectionMatrixBuffer = $$0;
        projectionType = $$1;
    }

    public static void setTextureMatrix(Matrix4f $$0) {
        RenderSystem.assertOnRenderThread();
        textureMatrix = new Matrix4f((Matrix4fc)$$0);
    }

    public static void resetTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        textureMatrix.identity();
    }

    public static void backupProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        savedProjectionMatrixBuffer = projectionMatrixBuffer;
        savedProjectionType = projectionType;
    }

    public static void restoreProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        projectionMatrixBuffer = savedProjectionMatrixBuffer;
        projectionType = savedProjectionType;
    }

    @Nullable
    public static GpuBufferSlice getProjectionMatrixBuffer() {
        RenderSystem.assertOnRenderThread();
        return projectionMatrixBuffer;
    }

    public static Matrix4f getModelViewMatrix() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4fStack getModelViewStack() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        return textureMatrix;
    }

    public static AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode $$0) {
        RenderSystem.assertOnRenderThread();
        return switch ($$0) {
            case VertexFormat.Mode.QUADS -> sharedSequentialQuad;
            case VertexFormat.Mode.LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }

    public static void setGlobalSettingsUniform(GpuBuffer $$0) {
        globalSettingsUniform = $$0;
    }

    @Nullable
    public static GpuBuffer getGlobalSettingsUniform() {
        return globalSettingsUniform;
    }

    public static ProjectionType getProjectionType() {
        RenderSystem.assertOnRenderThread();
        return projectionType;
    }

    public static GpuBuffer getQuadVertexBuffer() {
        if (QUAD_VERTEX_BUFFER == null) {
            throw new IllegalStateException("Can't getQuadVertexBuffer() before renderer was initialized");
        }
        return QUAD_VERTEX_BUFFER;
    }

    public static void setModelOffset(float $$0, float $$1, float $$2) {
        RenderSystem.assertOnRenderThread();
        modelOffset.set($$0, $$1, $$2);
    }

    public static void resetModelOffset() {
        RenderSystem.assertOnRenderThread();
        modelOffset.set(0.0f, 0.0f, 0.0f);
    }

    public static Vector3f getModelOffset() {
        RenderSystem.assertOnRenderThread();
        return modelOffset;
    }

    public static void queueFencedTask(Runnable $$0) {
        PENDING_FENCES.addLast(new GpuAsyncTask($$0, RenderSystem.getDevice().createCommandEncoder().createFence()));
    }

    public static void executePendingTasks() {
        GpuAsyncTask $$0 = PENDING_FENCES.peekFirst();
        while ($$0 != null) {
            if ($$0.fence.awaitCompletion(0L)) {
                try {
                    $$0.callback.run();
                } finally {
                    $$0.fence.close();
                }
                PENDING_FENCES.removeFirst();
                $$0 = PENDING_FENCES.peekFirst();
                continue;
            }
            return;
        }
    }

    public static GpuDevice getDevice() {
        if (DEVICE == null) {
            throw new IllegalStateException("Can't getDevice() before it was initialized");
        }
        return DEVICE;
    }

    @Nullable
    public static GpuDevice tryGetDevice() {
        return DEVICE;
    }

    public static DynamicUniforms getDynamicUniforms() {
        if (dynamicUniforms == null) {
            throw new IllegalStateException("Can't getDynamicUniforms() before device was initialized");
        }
        return dynamicUniforms;
    }

    public static void bindDefaultUniforms(RenderPass $$0) {
        GpuBufferSlice $$4;
        GpuBuffer $$3;
        GpuBufferSlice $$2;
        GpuBufferSlice $$1 = RenderSystem.getProjectionMatrixBuffer();
        if ($$1 != null) {
            $$0.setUniform("Projection", $$1);
        }
        if (($$2 = RenderSystem.getShaderFog()) != null) {
            $$0.setUniform("Fog", $$2);
        }
        if (($$3 = RenderSystem.getGlobalSettingsUniform()) != null) {
            $$0.setUniform("Globals", $$3);
        }
        if (($$4 = RenderSystem.getShaderLights()) != null) {
            $$0.setUniform("Lighting", $$4);
        }
    }

    static {
        lastDrawTime = Double.MIN_VALUE;
        sharedSequential = new AutoStorageIndexBuffer(1, 1, java.util.function.IntConsumer::accept);
        sharedSequentialQuad = new AutoStorageIndexBuffer(4, 6, ($$0, $$1) -> {
            $$0.accept($$1);
            $$0.accept($$1 + 1);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 3);
            $$0.accept($$1);
        });
        sharedSequentialLines = new AutoStorageIndexBuffer(4, 6, ($$0, $$1) -> {
            $$0.accept($$1);
            $$0.accept($$1 + 1);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 3);
            $$0.accept($$1 + 2);
            $$0.accept($$1 + 1);
        });
        projectionType = ProjectionType.PERSPECTIVE;
        savedProjectionType = ProjectionType.PERSPECTIVE;
        modelViewStack = new Matrix4fStack(16);
        textureMatrix = new Matrix4f();
        shaderTextures = new GpuTextureView[12];
        shaderFog = null;
        modelOffset = new Vector3f();
        shaderLineWidth = 1.0f;
        apiDescription = "Unknown";
        pollEventsWaitStart = new AtomicLong();
        pollingEvents = new AtomicBoolean(false);
        PENDING_FENCES = new ArrayListDeque();
        scissorStateForRenderTypeDraws = new ScissorState();
    }

    public static final class AutoStorageIndexBuffer {
        private final int vertexStride;
        private final int indexStride;
        private final IndexGenerator generator;
        @Nullable
        private GpuBuffer buffer;
        private VertexFormat.IndexType type = VertexFormat.IndexType.SHORT;
        private int indexCount;

        AutoStorageIndexBuffer(int $$0, int $$1, IndexGenerator $$2) {
            this.vertexStride = $$0;
            this.indexStride = $$1;
            this.generator = $$2;
        }

        public boolean hasStorage(int $$0) {
            return $$0 <= this.indexCount;
        }

        public GpuBuffer getBuffer(int $$0) {
            this.ensureStorage($$0);
            return this.buffer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void ensureStorage(int $$0) {
            if (this.hasStorage($$0)) {
                return;
            }
            $$0 = Mth.roundToward($$0 * 2, this.indexStride);
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.indexCount, (Object)$$0);
            int $$1 = $$0 / this.indexStride;
            int $$2 = $$1 * this.vertexStride;
            VertexFormat.IndexType $$3 = VertexFormat.IndexType.least($$2);
            int $$4 = Mth.roundToward($$0 * $$3.bytes, 4);
            ByteBuffer $$5 = MemoryUtil.memAlloc((int)$$4);
            try {
                this.type = $$3;
                IntConsumer $$6 = this.intConsumer($$5);
                for (int $$7 = 0; $$7 < $$0; $$7 += this.indexStride) {
                    this.generator.accept($$6, $$7 * this.vertexStride / this.indexStride);
                }
                $$5.flip();
                if (this.buffer != null) {
                    this.buffer.close();
                }
                this.buffer = RenderSystem.getDevice().createBuffer(() -> "Auto Storage index buffer", 64, $$5);
            } finally {
                MemoryUtil.memFree((Buffer)$$5);
            }
            this.indexCount = $$0;
        }

        private IntConsumer intConsumer(ByteBuffer $$0) {
            switch (this.type) {
                case SHORT: {
                    return $$1 -> $$0.putShort((short)$$1);
                }
            }
            return $$0::putInt;
        }

        public VertexFormat.IndexType type() {
            return this.type;
        }

        static interface IndexGenerator {
            public void accept(IntConsumer var1, int var2);
        }
    }

    static final class GpuAsyncTask
    extends Record {
        final Runnable callback;
        final GpuFence fence;

        GpuAsyncTask(Runnable $$0, GpuFence $$1) {
            this.callback = $$0;
            this.fence = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GpuAsyncTask.class, "callback;fence", "callback", "fence"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GpuAsyncTask.class, "callback;fence", "callback", "fence"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GpuAsyncTask.class, "callback;fence", "callback", "fence"}, this, $$0);
        }

        public Runnable callback() {
            return this.callback;
        }

        public GpuFence fence() {
            return this.fence;
        }
    }
}

