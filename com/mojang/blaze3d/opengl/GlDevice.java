/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GLCapabilities
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.BufferStorage;
import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDebug;
import com.mojang.blaze3d.opengl.GlDebugLabel;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.opengl.VertexArrayCache;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

public class GlDevice
implements GpuDevice {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static boolean USE_GL_ARB_vertex_attrib_binding = true;
    protected static boolean USE_GL_KHR_debug = true;
    protected static boolean USE_GL_EXT_debug_label = true;
    protected static boolean USE_GL_ARB_debug_output = true;
    protected static boolean USE_GL_ARB_direct_state_access = true;
    protected static boolean USE_GL_ARB_buffer_storage = true;
    private final CommandEncoder encoder;
    @Nullable
    private final GlDebug debugLog;
    private final GlDebugLabel debugLabels;
    private final int maxSupportedTextureSize;
    private final DirectStateAccess directStateAccess;
    private final BiFunction<ResourceLocation, ShaderType, String> defaultShaderSource;
    private final Map<RenderPipeline, GlRenderPipeline> pipelineCache = new IdentityHashMap<RenderPipeline, GlRenderPipeline>();
    private final Map<ShaderCompilationKey, GlShaderModule> shaderCache = new HashMap<ShaderCompilationKey, GlShaderModule>();
    private final VertexArrayCache vertexArrayCache;
    private final BufferStorage bufferStorage;
    private final Set<String> enabledExtensions = new HashSet<String>();
    private final int uniformOffsetAlignment;

    public GlDevice(long $$0, int $$1, boolean $$2, BiFunction<ResourceLocation, ShaderType, String> $$3, boolean $$4) {
        GLFW.glfwMakeContextCurrent((long)$$0);
        GLCapabilities $$5 = GL.createCapabilities();
        int $$6 = GlDevice.getMaxSupportedTextureSize();
        GLFW.glfwSetWindowSizeLimits((long)$$0, (int)-1, (int)-1, (int)$$6, (int)$$6);
        this.debugLog = GlDebug.enableDebugCallback($$1, $$2, this.enabledExtensions);
        this.debugLabels = GlDebugLabel.create($$5, $$4, this.enabledExtensions);
        this.vertexArrayCache = VertexArrayCache.create($$5, this.debugLabels, this.enabledExtensions);
        this.bufferStorage = BufferStorage.create($$5, this.enabledExtensions);
        this.directStateAccess = DirectStateAccess.create($$5, this.enabledExtensions);
        this.maxSupportedTextureSize = $$6;
        this.defaultShaderSource = $$3;
        this.encoder = new GlCommandEncoder(this);
        this.uniformOffsetAlignment = GL11.glGetInteger((int)35380);
        GL11.glEnable((int)34895);
    }

    public GlDebugLabel debugLabels() {
        return this.debugLabels;
    }

    @Override
    public CommandEncoder createCommandEncoder() {
        return this.encoder;
    }

    @Override
    public GpuTexture createTexture(@Nullable Supplier<String> $$0, int $$1, TextureFormat $$2, int $$3, int $$4, int $$5, int $$6) {
        return this.createTexture(this.debugLabels.exists() && $$0 != null ? $$0.get() : null, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    public GpuTexture createTexture(@Nullable String $$0, int $$1, TextureFormat $$2, int $$3, int $$4, int $$5, int $$6) {
        int $$14;
        int $$10;
        boolean $$7;
        if ($$6 < 1) {
            throw new IllegalArgumentException("mipLevels must be at least 1");
        }
        if ($$5 < 1) {
            throw new IllegalArgumentException("depthOrLayers must be at least 1");
        }
        boolean bl = $$7 = ($$1 & 0x10) != 0;
        if ($$7) {
            if ($$3 != $$4) {
                throw new IllegalArgumentException("Cubemap compatible textures must be square, but size is " + $$3 + "x" + $$4);
            }
            if ($$5 % 6 != 0) {
                throw new IllegalArgumentException("Cubemap compatible textures must have a layer count with a multiple of 6, was " + $$5);
            }
            if ($$5 > 6) {
                throw new UnsupportedOperationException("Array textures are not yet supported");
            }
        } else if ($$5 > 1) {
            throw new UnsupportedOperationException("Array or 3D textures are not yet supported");
        }
        GlStateManager.clearGlErrors();
        int $$8 = GlStateManager._genTexture();
        if ($$0 == null) {
            $$0 = String.valueOf($$8);
        }
        if ($$7) {
            GL11.glBindTexture((int)34067, (int)$$8);
            int $$9 = 34067;
        } else {
            GlStateManager._bindTexture($$8);
            $$10 = 3553;
        }
        GlStateManager._texParameter($$10, 33085, $$6 - 1);
        GlStateManager._texParameter($$10, 33082, 0);
        GlStateManager._texParameter($$10, 33083, $$6 - 1);
        if ($$2.hasDepthAspect()) {
            GlStateManager._texParameter($$10, 34892, 0);
        }
        if ($$7) {
            for (int $$11 : GlConst.CUBEMAP_TARGETS) {
                for (int $$12 = 0; $$12 < $$6; ++$$12) {
                    GlStateManager._texImage2D($$11, $$12, GlConst.toGlInternalId($$2), $$3 >> $$12, $$4 >> $$12, 0, GlConst.toGlExternalId($$2), GlConst.toGlType($$2), null);
                }
            }
        } else {
            for (int $$13 = 0; $$13 < $$6; ++$$13) {
                GlStateManager._texImage2D($$10, $$13, GlConst.toGlInternalId($$2), $$3 >> $$13, $$4 >> $$13, 0, GlConst.toGlExternalId($$2), GlConst.toGlType($$2), null);
            }
        }
        if (($$14 = GlStateManager._getError()) == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate texture of " + $$3 + "x" + $$4 + " for " + $$0);
        }
        if ($$14 != 0) {
            throw new IllegalStateException("OpenGL error " + $$14);
        }
        GlTexture $$15 = new GlTexture($$1, $$0, $$2, $$3, $$4, $$5, $$6, $$8);
        this.debugLabels.applyLabel($$15);
        return $$15;
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture $$0) {
        return this.createTextureView($$0, 0, $$0.getMipLevels());
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture $$0, int $$1, int $$2) {
        if ($$0.isClosed()) {
            throw new IllegalArgumentException("Can't create texture view with closed texture");
        }
        if ($$1 < 0 || $$1 + $$2 > $$0.getMipLevels()) {
            throw new IllegalArgumentException($$2 + " mip levels starting from " + $$1 + " would be out of range for texture with only " + $$0.getMipLevels() + " mip levels");
        }
        return new GlTextureView((GlTexture)$$0, $$1, $$2);
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> $$0, int $$1, int $$2) {
        if ($$2 <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than zero");
        }
        GlStateManager.clearGlErrors();
        GlBuffer $$3 = this.bufferStorage.createBuffer(this.directStateAccess, $$0, $$1, $$2);
        int $$4 = GlStateManager._getError();
        if ($$4 == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate buffer of " + $$2 + " for " + String.valueOf($$0));
        }
        if ($$4 != 0) {
            throw new IllegalStateException("OpenGL error " + $$4);
        }
        this.debugLabels.applyLabel($$3);
        return $$3;
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> $$0, int $$1, ByteBuffer $$2) {
        if (!$$2.hasRemaining()) {
            throw new IllegalArgumentException("Buffer source must not be empty");
        }
        GlStateManager.clearGlErrors();
        long $$3 = $$2.remaining();
        GlBuffer $$4 = this.bufferStorage.createBuffer(this.directStateAccess, $$0, $$1, $$2);
        int $$5 = GlStateManager._getError();
        if ($$5 == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate buffer of " + $$3 + " for " + String.valueOf($$0));
        }
        if ($$5 != 0) {
            throw new IllegalStateException("OpenGL error " + $$5);
        }
        this.debugLabels.applyLabel($$4);
        return $$4;
    }

    @Override
    public String getImplementationInformation() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
    }

    @Override
    public List<String> getLastDebugMessages() {
        return this.debugLog == null ? Collections.emptyList() : this.debugLog.getLastOpenGlDebugMessages();
    }

    @Override
    public boolean isDebuggingEnabled() {
        return this.debugLog != null;
    }

    @Override
    public String getRenderer() {
        return GlStateManager._getString(7937);
    }

    @Override
    public String getVendor() {
        return GlStateManager._getString(7936);
    }

    @Override
    public String getBackendName() {
        return "OpenGL";
    }

    @Override
    public String getVersion() {
        return GlStateManager._getString(7938);
    }

    private static int getMaxSupportedTextureSize() {
        int $$0 = GlStateManager._getInteger(3379);
        for (int $$1 = Math.max(32768, $$0); $$1 >= 1024; $$1 >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, $$1, $$1, 0, 6408, 5121, null);
            int $$2 = GlStateManager._getTexLevelParameter(32868, 0, 4096);
            if ($$2 == 0) continue;
            return $$1;
        }
        int $$3 = Math.max($$0, 1024);
        LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)$$3);
        return $$3;
    }

    @Override
    public int getMaxTextureSize() {
        return this.maxSupportedTextureSize;
    }

    @Override
    public int getUniformOffsetAlignment() {
        return this.uniformOffsetAlignment;
    }

    @Override
    public void clearPipelineCache() {
        for (GlRenderPipeline $$0 : this.pipelineCache.values()) {
            if ($$0.program() == GlProgram.INVALID_PROGRAM) continue;
            $$0.program().close();
        }
        this.pipelineCache.clear();
        for (GlShaderModule $$1 : this.shaderCache.values()) {
            if ($$1 == GlShaderModule.INVALID_SHADER) continue;
            $$1.close();
        }
        this.shaderCache.clear();
        String $$2 = GlStateManager._getString(7937);
        if ($$2.contains("AMD")) {
            GlDevice.amdDummyShaderWorkaround();
        }
    }

    private static void amdDummyShaderWorkaround() {
        int $$0 = GlStateManager.glCreateShader(35633);
        GlStateManager.glShaderSource($$0, "#version 150\nvoid main() {\n    gl_Position = vec4(0.0);\n}\n");
        GlStateManager.glCompileShader($$0);
        int $$1 = GlStateManager.glCreateShader(35632);
        GlStateManager.glShaderSource($$1, "#version 150\nlayout(std140) uniform Dummy {\n    float Value;\n};\nout vec4 fragColor;\nvoid main() {\n    fragColor = vec4(0.0);\n}\n");
        GlStateManager.glCompileShader($$1);
        int $$2 = GlStateManager.glCreateProgram();
        GlStateManager.glAttachShader($$2, $$0);
        GlStateManager.glAttachShader($$2, $$1);
        GlStateManager.glLinkProgram($$2);
        GL31.glGetUniformBlockIndex((int)$$2, (CharSequence)"Dummy");
        GlStateManager.glDeleteShader($$0);
        GlStateManager.glDeleteShader($$1);
        GlStateManager.glDeleteProgram($$2);
    }

    @Override
    public List<String> getEnabledExtensions() {
        return new ArrayList<String>(this.enabledExtensions);
    }

    @Override
    public void close() {
        this.clearPipelineCache();
    }

    public DirectStateAccess directStateAccess() {
        return this.directStateAccess;
    }

    protected GlRenderPipeline getOrCompilePipeline(RenderPipeline $$0) {
        return this.pipelineCache.computeIfAbsent($$0, $$1 -> this.compilePipeline($$0, this.defaultShaderSource));
    }

    protected GlShaderModule getOrCompileShader(ResourceLocation $$0, ShaderType $$1, ShaderDefines $$22, BiFunction<ResourceLocation, ShaderType, String> $$3) {
        ShaderCompilationKey $$4 = new ShaderCompilationKey($$0, $$1, $$22);
        return this.shaderCache.computeIfAbsent($$4, $$2 -> this.compileShader($$4, $$3));
    }

    @Override
    public GlRenderPipeline precompilePipeline(RenderPipeline $$0, @Nullable BiFunction<ResourceLocation, ShaderType, String> $$1) {
        BiFunction<ResourceLocation, ShaderType, String> $$22 = $$1 == null ? this.defaultShaderSource : $$1;
        return this.pipelineCache.computeIfAbsent($$0, $$2 -> this.compilePipeline($$0, $$22));
    }

    private GlShaderModule compileShader(ShaderCompilationKey $$0, BiFunction<ResourceLocation, ShaderType, String> $$1) {
        String $$2 = $$1.apply($$0.id, $$0.type);
        if ($$2 == null) {
            LOGGER.error("Couldn't find source for {} shader ({})", (Object)$$0.type, (Object)$$0.id);
            return GlShaderModule.INVALID_SHADER;
        }
        String $$3 = GlslPreprocessor.injectDefines($$2, $$0.defines);
        int $$4 = GlStateManager.glCreateShader(GlConst.toGl($$0.type));
        GlStateManager.glShaderSource($$4, $$3);
        GlStateManager.glCompileShader($$4);
        if (GlStateManager.glGetShaderi($$4, 35713) == 0) {
            String $$5 = StringUtils.trim(GlStateManager.glGetShaderInfoLog($$4, 32768));
            LOGGER.error("Couldn't compile {} shader ({}): {}", $$0.type.getName(), $$0.id, $$5);
            return GlShaderModule.INVALID_SHADER;
        }
        GlShaderModule $$6 = new GlShaderModule($$4, $$0.id, $$0.type);
        this.debugLabels.applyLabel($$6);
        return $$6;
    }

    /*
     * WARNING - void declaration
     */
    private GlRenderPipeline compilePipeline(RenderPipeline $$0, BiFunction<ResourceLocation, ShaderType, String> $$1) {
        void $$6;
        GlShaderModule $$2 = this.getOrCompileShader($$0.getVertexShader(), ShaderType.VERTEX, $$0.getShaderDefines(), $$1);
        GlShaderModule $$3 = this.getOrCompileShader($$0.getFragmentShader(), ShaderType.FRAGMENT, $$0.getShaderDefines(), $$1);
        if ($$2 == GlShaderModule.INVALID_SHADER) {
            LOGGER.error("Couldn't compile pipeline {}: vertex shader {} was invalid", (Object)$$0.getLocation(), (Object)$$0.getVertexShader());
            return new GlRenderPipeline($$0, GlProgram.INVALID_PROGRAM);
        }
        if ($$3 == GlShaderModule.INVALID_SHADER) {
            LOGGER.error("Couldn't compile pipeline {}: fragment shader {} was invalid", (Object)$$0.getLocation(), (Object)$$0.getFragmentShader());
            return new GlRenderPipeline($$0, GlProgram.INVALID_PROGRAM);
        }
        try {
            GlProgram $$4 = GlProgram.link($$2, $$3, $$0.getVertexFormat(), $$0.getLocation().toString());
        } catch (ShaderManager.CompilationException $$5) {
            LOGGER.error("Couldn't compile program for pipeline {}: {}", (Object)$$0.getLocation(), (Object)$$5);
            return new GlRenderPipeline($$0, GlProgram.INVALID_PROGRAM);
        }
        $$6.setupUniforms($$0.getUniforms(), $$0.getSamplers());
        this.debugLabels.applyLabel((GlProgram)$$6);
        return new GlRenderPipeline($$0, (GlProgram)$$6);
    }

    public VertexArrayCache vertexArrayCache() {
        return this.vertexArrayCache;
    }

    public BufferStorage getBufferStorage() {
        return this.bufferStorage;
    }

    public /* synthetic */ CompiledRenderPipeline precompilePipeline(RenderPipeline renderPipeline, @Nullable BiFunction biFunction) {
        return this.precompilePipeline(renderPipeline, biFunction);
    }

    static final class ShaderCompilationKey
    extends Record {
        final ResourceLocation id;
        final ShaderType type;
        final ShaderDefines defines;

        ShaderCompilationKey(ResourceLocation $$0, ShaderType $$1, ShaderDefines $$2) {
            this.id = $$0;
            this.type = $$1;
            this.defines = $$2;
        }

        public String toString() {
            String $$0 = String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
            if (!this.defines.isEmpty()) {
                return $$0 + " with " + String.valueOf((Object)this.defines);
            }
            return $$0;
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShaderCompilationKey.class, "id;type;defines", "id", "type", "defines"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShaderCompilationKey.class, "id;type;defines", "id", "type", "defines"}, this, $$0);
        }

        public ResourceLocation id() {
            return this.id;
        }

        public ShaderType type() {
            return this.type;
        }

        public ShaderDefines defines() {
            return this.defines;
        }
    }
}

