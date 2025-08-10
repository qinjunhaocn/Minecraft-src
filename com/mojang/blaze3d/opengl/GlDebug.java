/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 */
package com.mojang.blaze3d.opengl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

public class GlDebug {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CIRCULAR_LOG_SIZE = 10;
    private final Queue<LogEntry> MESSAGE_BUFFER = EvictingQueue.create(10);
    @Nullable
    private volatile LogEntry lastEntry;
    private static final List<Integer> DEBUG_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
    private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of(37190, 37191, 37192);

    private static String printUnknownToken(int $$0) {
        return "Unknown (0x" + Integer.toHexString($$0).toUpperCase() + ")";
    }

    public static String sourceToString(int $$0) {
        switch ($$0) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return GlDebug.printUnknownToken($$0);
    }

    public static String typeToString(int $$0) {
        switch ($$0) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return GlDebug.printUnknownToken($$0);
    }

    public static String severityToString(int $$0) {
        switch ($$0) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return GlDebug.printUnknownToken($$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private void printDebugLog(int $$0, int $$1, int $$2, int $$3, int $$4, long $$5, long $$6) {
        void $$9;
        String $$7 = GLDebugMessageCallback.getMessage((int)$$4, (long)$$5);
        Queue<LogEntry> queue = this.MESSAGE_BUFFER;
        synchronized (queue) {
            LogEntry $$8 = this.lastEntry;
            if ($$8 == null || !$$8.isSame($$0, $$1, $$2, $$3, $$7)) {
                $$8 = new LogEntry($$0, $$1, $$2, $$3, $$7);
                this.MESSAGE_BUFFER.add($$8);
                this.lastEntry = $$8;
            } else {
                ++$$8.count;
            }
        }
        LOGGER.info("OpenGL debug message: {}", (Object)$$9);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getLastOpenGlDebugMessages() {
        Queue<LogEntry> queue = this.MESSAGE_BUFFER;
        synchronized (queue) {
            ArrayList<String> $$0 = Lists.newArrayListWithCapacity(this.MESSAGE_BUFFER.size());
            for (LogEntry $$1 : this.MESSAGE_BUFFER) {
                $$0.add(String.valueOf($$1) + " x " + $$1.count);
            }
            return $$0;
        }
    }

    @Nullable
    public static GlDebug enableDebugCallback(int $$0, boolean $$1, Set<String> $$2) {
        if ($$0 <= 0) {
            return null;
        }
        GLCapabilities $$3 = GL.getCapabilities();
        if ($$3.GL_KHR_debug && GlDevice.USE_GL_KHR_debug) {
            GlDebug $$4 = new GlDebug();
            $$2.add("GL_KHR_debug");
            GL11.glEnable((int)37600);
            if ($$1) {
                GL11.glEnable((int)33346);
            }
            for (int $$5 = 0; $$5 < DEBUG_LEVELS.size(); ++$$5) {
                boolean $$6 = $$5 < $$0;
                KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)DEBUG_LEVELS.get($$5), (int[])null, (boolean)$$6);
            }
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create($$4::printDebugLog), DebugMemoryUntracker::untrack)), (long)0L);
            return $$4;
        }
        if ($$3.GL_ARB_debug_output && GlDevice.USE_GL_ARB_debug_output) {
            GlDebug $$7 = new GlDebug();
            $$2.add("GL_ARB_debug_output");
            if ($$1) {
                GL11.glEnable((int)33346);
            }
            for (int $$8 = 0; $$8 < DEBUG_LEVELS_ARB.size(); ++$$8) {
                boolean $$9 = $$8 < $$0;
                ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)DEBUG_LEVELS_ARB.get($$8), (int[])null, (boolean)$$9);
            }
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create($$7::printDebugLog), DebugMemoryUntracker::untrack)), (long)0L);
            return $$7;
        }
        return null;
    }

    static class LogEntry {
        private final int id;
        private final int source;
        private final int type;
        private final int severity;
        private final String message;
        int count = 1;

        LogEntry(int $$0, int $$1, int $$2, int $$3, String $$4) {
            this.id = $$2;
            this.source = $$0;
            this.type = $$1;
            this.severity = $$3;
            this.message = $$4;
        }

        boolean isSame(int $$0, int $$1, int $$2, int $$3, String $$4) {
            return $$1 == this.type && $$0 == this.source && $$2 == this.id && $$3 == this.severity && $$4.equals(this.message);
        }

        public String toString() {
            return "id=" + this.id + ", source=" + GlDebug.sourceToString(this.source) + ", type=" + GlDebug.typeToString(this.type) + ", severity=" + GlDebug.severityToString(this.severity) + ", message='" + this.message + "'";
        }
    }
}

