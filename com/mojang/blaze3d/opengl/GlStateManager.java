/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.Plot
 *  com.mojang.jtracy.TracyClient
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL32
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.opengl;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@DontObfuscate
public class GlStateManager {
    private static final boolean ON_LINUX = Util.getPlatform() == Util.OS.LINUX;
    private static final Plot PLOT_TEXTURES = TracyClient.createPlot((String)"GPU Textures");
    private static int numTextures = 0;
    private static final Plot PLOT_BUFFERS = TracyClient.createPlot((String)"GPU Buffers");
    private static int numBuffers = 0;
    private static final BlendState BLEND = new BlendState();
    private static final DepthState DEPTH = new DepthState();
    private static final CullState CULL = new CullState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final ColorLogicState COLOR_LOGIC = new ColorLogicState();
    private static final ScissorState SCISSOR = new ScissorState();
    private static int activeTexture;
    private static final TextureState[] TEXTURES;
    private static final ColorMask COLOR_MASK;
    private static int readFbo;
    private static int writeFbo;

    public static void _disableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.mode.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.mode.enable();
    }

    public static void _scissorBox(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        GL20.glScissor((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.mode.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.mode.enable();
    }

    public static void _depthFunc(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = $$0;
            GL11.glDepthFunc((int)$$0);
        }
    }

    public static void _depthMask(boolean $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = $$0;
            GL11.glDepthMask((boolean)$$0);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.mode.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.mode.enable();
    }

    public static void _blendFuncSeparate(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.BLEND.srcRgb || $$1 != GlStateManager.BLEND.dstRgb || $$2 != GlStateManager.BLEND.srcAlpha || $$3 != GlStateManager.BLEND.dstAlpha) {
            GlStateManager.BLEND.srcRgb = $$0;
            GlStateManager.BLEND.dstRgb = $$1;
            GlStateManager.BLEND.srcAlpha = $$2;
            GlStateManager.BLEND.dstAlpha = $$3;
            GlStateManager.glBlendFuncSeparate($$0, $$1, $$2, $$3);
        }
    }

    public static int glGetProgrami(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgrami((int)$$0, (int)$$1);
    }

    public static void glAttachShader(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glAttachShader((int)$$0, (int)$$1);
    }

    public static void glDeleteShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteShader((int)$$0);
    }

    public static int glCreateShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateShader((int)$$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void glShaderSource(int $$0, String $$1) {
        RenderSystem.assertOnRenderThread();
        byte[] $$2 = $$1.getBytes(Charsets.UTF_8);
        ByteBuffer $$3 = MemoryUtil.memAlloc((int)($$2.length + 1));
        $$3.put($$2);
        $$3.put((byte)0);
        $$3.flip();
        try (MemoryStack $$4 = MemoryStack.stackPush();){
            PointerBuffer $$5 = $$4.mallocPointer(1);
            $$5.put($$3);
            GL20C.nglShaderSource((int)$$0, (int)1, (long)$$5.address0(), (long)0L);
        } finally {
            MemoryUtil.memFree((Buffer)$$3);
        }
    }

    public static void glCompileShader(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glCompileShader((int)$$0);
    }

    public static int glGetShaderi(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderi((int)$$0, (int)$$1);
    }

    public static void _glUseProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glUseProgram((int)$$0);
    }

    public static int glCreateProgram() {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteProgram((int)$$0);
    }

    public static void glLinkProgram(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glLinkProgram((int)$$0);
    }

    public static int _glGetUniformLocation(int $$0, CharSequence $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetUniformLocation((int)$$0, (CharSequence)$$1);
    }

    public static void _glUniform1(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform1i(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1i((int)$$0, (int)$$1);
    }

    public static void _glUniform1(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform2(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform2fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform3(int $$0, IntBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3iv((int)$$0, (IntBuffer)$$1);
    }

    public static void _glUniform3(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform3fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniform4(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform4fv((int)$$0, (FloatBuffer)$$1);
    }

    public static void _glUniformMatrix4(int $$0, FloatBuffer $$1) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniformMatrix4fv((int)$$0, (boolean)false, (FloatBuffer)$$1);
    }

    public static void _glBindAttribLocation(int $$0, int $$1, CharSequence $$2) {
        RenderSystem.assertOnRenderThread();
        GL20.glBindAttribLocation((int)$$0, (int)$$1, (CharSequence)$$2);
    }

    public static int _glGenBuffers() {
        RenderSystem.assertOnRenderThread();
        PLOT_BUFFERS.setValue((double)(++numBuffers));
        return GL15.glGenBuffers();
    }

    public static int _glGenVertexArrays() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenVertexArrays();
    }

    public static void _glBindBuffer(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL15.glBindBuffer((int)$$0, (int)$$1);
    }

    public static void _glBindVertexArray(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL30.glBindVertexArray((int)$$0);
    }

    public static void _glBufferData(int $$0, ByteBuffer $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)$$0, (ByteBuffer)$$1, (int)$$2);
    }

    public static void _glBufferSubData(int $$0, int $$1, ByteBuffer $$2) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferSubData((int)$$0, (long)$$1, (ByteBuffer)$$2);
    }

    public static void _glBufferData(int $$0, long $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)$$0, (long)$$1, (int)$$2);
    }

    @Nullable
    public static ByteBuffer _glMapBufferRange(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        return GL30.glMapBufferRange((int)$$0, (long)$$1, (long)$$2, (int)$$3);
    }

    public static void _glUnmapBuffer(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL15.glUnmapBuffer((int)$$0);
    }

    public static void _glDeleteBuffers(int $$0) {
        RenderSystem.assertOnRenderThread();
        PLOT_BUFFERS.setValue((double)(--numBuffers));
        GL15.glDeleteBuffers((int)$$0);
    }

    public static void _glBindFramebuffer(int $$0, int $$1) {
        if (($$0 == 36008 || $$0 == 36160) && readFbo != $$1) {
            GL30.glBindFramebuffer((int)36008, (int)$$1);
            readFbo = $$1;
        }
        if (($$0 == 36009 || $$0 == 36160) && writeFbo != $$1) {
            GL30.glBindFramebuffer((int)36009, (int)$$1);
            writeFbo = $$1;
        }
    }

    public static int getFrameBuffer(int $$0) {
        if ($$0 == 36008) {
            return readFbo;
        }
        if ($$0 == 36009) {
            return writeFbo;
        }
        return 0;
    }

    public static void _glBlitFrameBuffer(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        RenderSystem.assertOnRenderThread();
        GL30.glBlitFramebuffer((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (int)$$8, (int)$$9);
    }

    public static void _glDeleteFramebuffers(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteFramebuffers((int)$$0);
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenFramebuffers();
    }

    public static void _glFramebufferTexture2D(int $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.assertOnRenderThread();
        GL30.glFramebufferTexture2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4);
    }

    public static void glActiveTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL13.glActiveTexture((int)$$0);
    }

    public static void glBlendFuncSeparate(int $$0, int $$1, int $$2, int $$3) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendFuncSeparate((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static String glGetShaderInfoLog(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderInfoLog((int)$$0, (int)$$1);
    }

    public static String glGetProgramInfoLog(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgramInfoLog((int)$$0, (int)$$1);
    }

    public static void _enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.enable.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.enable.disable();
    }

    public static void _polygonMode(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL11.glPolygonMode((int)$$0, (int)$$1);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.fill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.fill.disable();
    }

    public static void _polygonOffset(float $$0, float $$1) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.POLY_OFFSET.factor || $$1 != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = $$0;
            GlStateManager.POLY_OFFSET.units = $$1;
            GL11.glPolygonOffset((float)$$0, (float)$$1);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.enable.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.enable.disable();
    }

    public static void _logicOp(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = $$0;
            GL11.glLogicOp((int)$$0);
        }
    }

    public static void _activeTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        if (activeTexture != $$0 - 33984) {
            activeTexture = $$0 - 33984;
            GlStateManager.glActiveTexture($$0);
        }
    }

    public static void _texParameter(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexParameteri((int)$$0, (int)$$1, (int)$$2);
    }

    public static int _getTexLevelParameter(int $$0, int $$1, int $$2) {
        return GL11.glGetTexLevelParameteri((int)$$0, (int)$$1, (int)$$2);
    }

    public static int _genTexture() {
        RenderSystem.assertOnRenderThread();
        PLOT_TEXTURES.setValue((double)(++numTextures));
        return GL11.glGenTextures();
    }

    public static void _deleteTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL11.glDeleteTextures((int)$$0);
        for (TextureState $$1 : TEXTURES) {
            if ($$1.binding != $$0) continue;
            $$1.binding = -1;
        }
        PLOT_TEXTURES.setValue((double)(--numTextures));
    }

    public static void _bindTexture(int $$0) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = $$0;
            GL11.glBindTexture((int)3553, (int)$$0);
        }
    }

    public static int _getActiveTexture() {
        return activeTexture + 33984;
    }

    public static void _texImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, @Nullable IntBuffer $$8) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (IntBuffer)$$8);
    }

    public static void _texSubImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, long $$8) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (long)$$8);
    }

    public static void _texSubImage2D(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, IntBuffer $$8) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (int)$$6, (int)$$7, (IntBuffer)$$8);
    }

    public static void _viewport(int $$0, int $$1, int $$2, int $$3) {
        GL11.glViewport((int)$$0, (int)$$1, (int)$$2, (int)$$3);
    }

    public static void _colorMask(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        RenderSystem.assertOnRenderThread();
        if ($$0 != GlStateManager.COLOR_MASK.red || $$1 != GlStateManager.COLOR_MASK.green || $$2 != GlStateManager.COLOR_MASK.blue || $$3 != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = $$0;
            GlStateManager.COLOR_MASK.green = $$1;
            GlStateManager.COLOR_MASK.blue = $$2;
            GlStateManager.COLOR_MASK.alpha = $$3;
            GL11.glColorMask((boolean)$$0, (boolean)$$1, (boolean)$$2, (boolean)$$3);
        }
    }

    public static void _clear(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL11.glClear((int)$$0);
        if (MacosUtil.IS_MACOS) {
            GlStateManager._getError();
        }
    }

    public static void _vertexAttribPointer(int $$0, int $$1, int $$2, boolean $$3, int $$4, long $$5) {
        RenderSystem.assertOnRenderThread();
        GL20.glVertexAttribPointer((int)$$0, (int)$$1, (int)$$2, (boolean)$$3, (int)$$4, (long)$$5);
    }

    public static void _vertexAttribIPointer(int $$0, int $$1, int $$2, int $$3, long $$4) {
        RenderSystem.assertOnRenderThread();
        GL30.glVertexAttribIPointer((int)$$0, (int)$$1, (int)$$2, (int)$$3, (long)$$4);
    }

    public static void _enableVertexAttribArray(int $$0) {
        RenderSystem.assertOnRenderThread();
        GL20.glEnableVertexAttribArray((int)$$0);
    }

    public static void _drawElements(int $$0, int $$1, int $$2, long $$3) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawElements((int)$$0, (int)$$1, (int)$$2, (long)$$3);
    }

    public static void _drawArrays(int $$0, int $$1, int $$2) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawArrays((int)$$0, (int)$$1, (int)$$2);
    }

    public static void _pixelStore(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        GL11.glPixelStorei((int)$$0, (int)$$1);
    }

    public static void _readPixels(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, long $$6) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)$$0, (int)$$1, (int)$$2, (int)$$3, (int)$$4, (int)$$5, (long)$$6);
    }

    public static int _getError() {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetError();
    }

    public static void clearGlErrors() {
        RenderSystem.assertOnRenderThread();
        while (GL11.glGetError() != 0) {
        }
    }

    public static String _getString(int $$0) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetString((int)$$0);
    }

    public static int _getInteger(int $$0) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetInteger((int)$$0);
    }

    public static long _glFenceSync(int $$0, int $$1) {
        RenderSystem.assertOnRenderThread();
        return GL32.glFenceSync((int)$$0, (int)$$1);
    }

    public static int _glClientWaitSync(long $$0, int $$1, long $$2) {
        RenderSystem.assertOnRenderThread();
        return GL32.glClientWaitSync((long)$$0, (int)$$1, (long)$$2);
    }

    public static void _glDeleteSync(long $$0) {
        RenderSystem.assertOnRenderThread();
        GL32.glDeleteSync((long)$$0);
    }

    static {
        TEXTURES = (TextureState[])IntStream.range(0, 12).mapToObj($$0 -> new TextureState()).toArray(TextureState[]::new);
        COLOR_MASK = new ColorMask();
    }

    static class ScissorState {
        public final BooleanState mode = new BooleanState(3089);

        ScissorState() {
        }
    }

    static class BooleanState {
        private final int state;
        private boolean enabled;

        public BooleanState(int $$0) {
            this.state = $$0;
        }

        public void disable() {
            this.setEnabled(false);
        }

        public void enable() {
            this.setEnabled(true);
        }

        public void setEnabled(boolean $$0) {
            RenderSystem.assertOnRenderThread();
            if ($$0 != this.enabled) {
                this.enabled = $$0;
                if ($$0) {
                    GL11.glEnable((int)this.state);
                } else {
                    GL11.glDisable((int)this.state);
                }
            }
        }
    }

    static class DepthState {
        public final BooleanState mode = new BooleanState(2929);
        public boolean mask = true;
        public int func = 513;

        DepthState() {
        }
    }

    static class BlendState {
        public final BooleanState mode = new BooleanState(3042);
        public int srcRgb = 1;
        public int dstRgb = 0;
        public int srcAlpha = 1;
        public int dstAlpha = 0;

        BlendState() {
        }
    }

    static class CullState {
        public final BooleanState enable = new BooleanState(2884);

        CullState() {
        }
    }

    static class PolygonOffsetState {
        public final BooleanState fill = new BooleanState(32823);
        public float factor;
        public float units;

        PolygonOffsetState() {
        }
    }

    static class ColorLogicState {
        public final BooleanState enable = new BooleanState(3058);
        public int op = 5379;

        ColorLogicState() {
        }
    }

    static class TextureState {
        public int binding;

        TextureState() {
        }
    }

    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        ColorMask() {
        }
    }
}

