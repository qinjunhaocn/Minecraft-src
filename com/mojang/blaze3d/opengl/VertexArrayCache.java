/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBVertexAttribBinding
 *  org.lwjgl.opengl.GLCapabilities
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDebugLabel;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.lwjgl.opengl.GLCapabilities;

public abstract class VertexArrayCache {
    public static VertexArrayCache create(GLCapabilities $$0, GlDebugLabel $$1, Set<String> $$2) {
        if ($$0.GL_ARB_vertex_attrib_binding && GlDevice.USE_GL_ARB_vertex_attrib_binding) {
            $$2.add("GL_ARB_vertex_attrib_binding");
            return new Separate($$1);
        }
        return new Emulated($$1);
    }

    public abstract void bindVertexArray(VertexFormat var1, GlBuffer var2);

    static class Separate
    extends VertexArrayCache {
        private final Map<VertexFormat, VertexArray> cache = new HashMap<VertexFormat, VertexArray>();
        private final GlDebugLabel debugLabels;
        private final boolean needsMesaWorkaround;

        public Separate(GlDebugLabel $$0) {
            String $$1;
            this.debugLabels = $$0;
            this.needsMesaWorkaround = "Mesa".equals(GlStateManager._getString(7936)) ? ($$1 = GlStateManager._getString(7938)).contains("25.0.0") || $$1.contains("25.0.1") || $$1.contains("25.0.2") : false;
        }

        @Override
        public void bindVertexArray(VertexFormat $$0, GlBuffer $$1) {
            VertexArray $$2 = this.cache.get($$0);
            if ($$2 == null) {
                int $$3 = GlStateManager._glGenVertexArrays();
                GlStateManager._glBindVertexArray($$3);
                List<VertexFormatElement> $$4 = $$0.getElements();
                for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                    VertexFormatElement $$6 = $$4.get($$5);
                    GlStateManager._enableVertexAttribArray($$5);
                    switch ($$6.usage()) {
                        case POSITION: 
                        case GENERIC: 
                        case UV: {
                            if ($$6.type() == VertexFormatElement.Type.FLOAT) {
                                ARBVertexAttribBinding.glVertexAttribFormat((int)$$5, (int)$$6.count(), (int)GlConst.toGl($$6.type()), (boolean)false, (int)$$0.getOffset($$6));
                                break;
                            }
                            ARBVertexAttribBinding.glVertexAttribIFormat((int)$$5, (int)$$6.count(), (int)GlConst.toGl($$6.type()), (int)$$0.getOffset($$6));
                            break;
                        }
                        case NORMAL: 
                        case COLOR: {
                            ARBVertexAttribBinding.glVertexAttribFormat((int)$$5, (int)$$6.count(), (int)GlConst.toGl($$6.type()), (boolean)true, (int)$$0.getOffset($$6));
                        }
                    }
                    ARBVertexAttribBinding.glVertexAttribBinding((int)$$5, (int)0);
                }
                ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)$$1.handle, (long)0L, (int)$$0.getVertexSize());
                VertexArray $$7 = new VertexArray($$3, $$0, $$1);
                this.debugLabels.applyLabel($$7);
                this.cache.put($$0, $$7);
                return;
            }
            GlStateManager._glBindVertexArray($$2.id);
            if ($$2.lastVertexBuffer != $$1) {
                if (this.needsMesaWorkaround && $$2.lastVertexBuffer != null && $$2.lastVertexBuffer.handle == $$1.handle) {
                    ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)0, (long)0L, (int)0);
                }
                ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)$$1.handle, (long)0L, (int)$$0.getVertexSize());
                $$2.lastVertexBuffer = $$1;
            }
        }
    }

    static class Emulated
    extends VertexArrayCache {
        private final Map<VertexFormat, VertexArray> cache = new HashMap<VertexFormat, VertexArray>();
        private final GlDebugLabel debugLabels;

        public Emulated(GlDebugLabel $$0) {
            this.debugLabels = $$0;
        }

        @Override
        public void bindVertexArray(VertexFormat $$0, GlBuffer $$1) {
            VertexArray $$2 = this.cache.get($$0);
            if ($$2 == null) {
                int $$3 = GlStateManager._glGenVertexArrays();
                GlStateManager._glBindVertexArray($$3);
                GlStateManager._glBindBuffer(34962, $$1.handle);
                Emulated.setupCombinedAttributes($$0, true);
                VertexArray $$4 = new VertexArray($$3, $$0, $$1);
                this.debugLabels.applyLabel($$4);
                this.cache.put($$0, $$4);
                return;
            }
            GlStateManager._glBindVertexArray($$2.id);
            if ($$2.lastVertexBuffer != $$1) {
                GlStateManager._glBindBuffer(34962, $$1.handle);
                $$2.lastVertexBuffer = $$1;
                Emulated.setupCombinedAttributes($$0, false);
            }
        }

        private static void setupCombinedAttributes(VertexFormat $$0, boolean $$1) {
            int $$2 = $$0.getVertexSize();
            List<VertexFormatElement> $$3 = $$0.getElements();
            block4: for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                VertexFormatElement $$5 = $$3.get($$4);
                if ($$1) {
                    GlStateManager._enableVertexAttribArray($$4);
                }
                switch ($$5.usage()) {
                    case POSITION: 
                    case GENERIC: 
                    case UV: {
                        if ($$5.type() == VertexFormatElement.Type.FLOAT) {
                            GlStateManager._vertexAttribPointer($$4, $$5.count(), GlConst.toGl($$5.type()), false, $$2, $$0.getOffset($$5));
                            continue block4;
                        }
                        GlStateManager._vertexAttribIPointer($$4, $$5.count(), GlConst.toGl($$5.type()), $$2, $$0.getOffset($$5));
                        continue block4;
                    }
                    case NORMAL: 
                    case COLOR: {
                        GlStateManager._vertexAttribPointer($$4, $$5.count(), GlConst.toGl($$5.type()), true, $$2, $$0.getOffset($$5));
                    }
                }
            }
        }
    }

    public static class VertexArray {
        final int id;
        final VertexFormat format;
        @Nullable
        GlBuffer lastVertexBuffer;

        VertexArray(int $$0, VertexFormat $$1, @Nullable GlBuffer $$2) {
            this.id = $$0;
            this.format = $$1;
            this.lastVertexBuffer = $$2;
        }
    }
}

