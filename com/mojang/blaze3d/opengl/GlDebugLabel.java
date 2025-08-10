/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.opengl.EXTDebugLabel
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.KHRDebug
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.VertexArrayCache;
import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.util.StringUtil;
import org.lwjgl.opengl.EXTDebugLabel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

public abstract class GlDebugLabel {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void applyLabel(GlBuffer $$0) {
    }

    public void applyLabel(GlTexture $$0) {
    }

    public void applyLabel(GlShaderModule $$0) {
    }

    public void applyLabel(GlProgram $$0) {
    }

    public void applyLabel(VertexArrayCache.VertexArray $$0) {
    }

    public void pushDebugGroup(Supplier<String> $$0) {
    }

    public void popDebugGroup() {
    }

    public static GlDebugLabel create(GLCapabilities $$0, boolean $$1, Set<String> $$2) {
        if ($$1) {
            if ($$0.GL_KHR_debug && GlDevice.USE_GL_KHR_debug) {
                $$2.add("GL_KHR_debug");
                return new Core();
            }
            if ($$0.GL_EXT_debug_label && GlDevice.USE_GL_EXT_debug_label) {
                $$2.add("GL_EXT_debug_label");
                return new Ext();
            }
            LOGGER.warn("Debug labels unavailable: neither KHR_debug nor EXT_debug_label are supported");
        }
        return new Empty();
    }

    public boolean exists() {
        return false;
    }

    static class Core
    extends GlDebugLabel {
        private final int maxLabelLength = GL11.glGetInteger((int)33512);

        Core() {
        }

        @Override
        public void applyLabel(GlBuffer $$0) {
            Supplier<String> $$1 = $$0.label;
            if ($$1 != null) {
                KHRDebug.glObjectLabel((int)33504, (int)$$0.handle, (CharSequence)StringUtil.truncateStringIfNecessary($$1.get(), this.maxLabelLength, true));
            }
        }

        @Override
        public void applyLabel(GlTexture $$0) {
            KHRDebug.glObjectLabel((int)5890, (int)$$0.id, (CharSequence)StringUtil.truncateStringIfNecessary($$0.getLabel(), this.maxLabelLength, true));
        }

        @Override
        public void applyLabel(GlShaderModule $$0) {
            KHRDebug.glObjectLabel((int)33505, (int)$$0.getShaderId(), (CharSequence)StringUtil.truncateStringIfNecessary($$0.getDebugLabel(), this.maxLabelLength, true));
        }

        @Override
        public void applyLabel(GlProgram $$0) {
            KHRDebug.glObjectLabel((int)33506, (int)$$0.getProgramId(), (CharSequence)StringUtil.truncateStringIfNecessary($$0.getDebugLabel(), this.maxLabelLength, true));
        }

        @Override
        public void applyLabel(VertexArrayCache.VertexArray $$0) {
            KHRDebug.glObjectLabel((int)32884, (int)$$0.id, (CharSequence)StringUtil.truncateStringIfNecessary($$0.format.toString(), this.maxLabelLength, true));
        }

        @Override
        public void pushDebugGroup(Supplier<String> $$0) {
            KHRDebug.glPushDebugGroup((int)33354, (int)0, (CharSequence)$$0.get());
        }

        @Override
        public void popDebugGroup() {
            KHRDebug.glPopDebugGroup();
        }

        @Override
        public boolean exists() {
            return true;
        }
    }

    static class Ext
    extends GlDebugLabel {
        Ext() {
        }

        @Override
        public void applyLabel(GlBuffer $$0) {
            Supplier<String> $$1 = $$0.label;
            if ($$1 != null) {
                EXTDebugLabel.glLabelObjectEXT((int)37201, (int)$$0.handle, (CharSequence)StringUtil.truncateStringIfNecessary($$1.get(), 256, true));
            }
        }

        @Override
        public void applyLabel(GlTexture $$0) {
            EXTDebugLabel.glLabelObjectEXT((int)5890, (int)$$0.id, (CharSequence)StringUtil.truncateStringIfNecessary($$0.getLabel(), 256, true));
        }

        @Override
        public void applyLabel(GlShaderModule $$0) {
            EXTDebugLabel.glLabelObjectEXT((int)35656, (int)$$0.getShaderId(), (CharSequence)StringUtil.truncateStringIfNecessary($$0.getDebugLabel(), 256, true));
        }

        @Override
        public void applyLabel(GlProgram $$0) {
            EXTDebugLabel.glLabelObjectEXT((int)35648, (int)$$0.getProgramId(), (CharSequence)StringUtil.truncateStringIfNecessary($$0.getDebugLabel(), 256, true));
        }

        @Override
        public void applyLabel(VertexArrayCache.VertexArray $$0) {
            EXTDebugLabel.glLabelObjectEXT((int)32884, (int)$$0.id, (CharSequence)StringUtil.truncateStringIfNecessary($$0.format.toString(), 256, true));
        }

        @Override
        public boolean exists() {
            return true;
        }
    }

    static class Empty
    extends GlDebugLabel {
        Empty() {
        }
    }
}

