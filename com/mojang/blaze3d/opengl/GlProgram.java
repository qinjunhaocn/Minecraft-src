/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.MatchException
 *  org.jetbrains.annotations.VisibleForTesting
 *  org.lwjgl.opengl.GL31
 */
package com.mojang.blaze3d.opengl;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderManager;
import org.jetbrains.annotations.VisibleForTesting;
import org.lwjgl.opengl.GL31;
import org.slf4j.Logger;

public class GlProgram
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Set<String> BUILT_IN_UNIFORMS = Sets.newHashSet("Projection", "Lighting", "Fog", "Globals");
    public static GlProgram INVALID_PROGRAM = new GlProgram(-1, "invalid");
    private final Map<String, Uniform> uniformsByName = new HashMap<String, Uniform>();
    private final int programId;
    private final String debugLabel;

    private GlProgram(int $$0, String $$1) {
        this.programId = $$0;
        this.debugLabel = $$1;
    }

    public static GlProgram link(GlShaderModule $$0, GlShaderModule $$1, VertexFormat $$2, String $$3) throws ShaderManager.CompilationException {
        int $$4 = GlStateManager.glCreateProgram();
        if ($$4 <= 0) {
            throw new ShaderManager.CompilationException("Could not create shader program (returned program ID " + $$4 + ")");
        }
        int $$5 = 0;
        for (String $$6 : $$2.getElementAttributeNames()) {
            GlStateManager._glBindAttribLocation($$4, $$5, $$6);
            ++$$5;
        }
        GlStateManager.glAttachShader($$4, $$0.getShaderId());
        GlStateManager.glAttachShader($$4, $$1.getShaderId());
        GlStateManager.glLinkProgram($$4);
        int $$7 = GlStateManager.glGetProgrami($$4, 35714);
        String $$8 = GlStateManager.glGetProgramInfoLog($$4, 32768);
        if ($$7 == 0 || $$8.contains("Failed for unknown reason")) {
            throw new ShaderManager.CompilationException("Error encountered when linking program containing VS " + String.valueOf($$0.getId()) + " and FS " + String.valueOf($$1.getId()) + ". Log output: " + $$8);
        }
        if (!$$8.isEmpty()) {
            LOGGER.info("Info log when linking program containing VS {} and FS {}. Log output: {}", $$0.getId(), $$1.getId(), $$8);
        }
        return new GlProgram($$4, $$3);
    }

    public void setupUniforms(List<RenderPipeline.UniformDescription> $$0, List<String> $$1) {
        int $$2 = 0;
        int $$3 = 0;
        for (RenderPipeline.UniformDescription $$4 : $$0) {
            String $$5 = $$4.name();
            Uniform.Utb $$10 = switch ($$4.type()) {
                default -> throw new MatchException(null, null);
                case UniformType.UNIFORM_BUFFER -> {
                    int $$6 = GL31.glGetUniformBlockIndex((int)this.programId, (CharSequence)$$5);
                    if ($$6 == -1) {
                        yield null;
                    }
                    int $$7 = $$2++;
                    GL31.glUniformBlockBinding((int)this.programId, (int)$$6, (int)$$7);
                    yield new Uniform.Ubo($$7);
                }
                case UniformType.TEXEL_BUFFER -> {
                    int $$8 = GlStateManager._glGetUniformLocation(this.programId, $$5);
                    if ($$8 == -1) {
                        LOGGER.warn("{} shader program does not use utb {} defined in the pipeline. This might be a bug.", (Object)this.debugLabel, (Object)$$5);
                        yield null;
                    }
                    int $$9 = $$3++;
                    yield new Uniform.Utb($$8, $$9, Objects.requireNonNull($$4.textureFormat()));
                }
            };
            if ($$10 == null) continue;
            this.uniformsByName.put($$5, $$10);
        }
        for (String $$11 : $$1) {
            int $$12 = GlStateManager._glGetUniformLocation(this.programId, $$11);
            if ($$12 == -1) {
                LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", (Object)this.debugLabel, (Object)$$11);
                continue;
            }
            int $$13 = $$3++;
            this.uniformsByName.put($$11, new Uniform.Sampler($$12, $$13));
        }
        int $$14 = GlStateManager.glGetProgrami(this.programId, 35382);
        for (int $$15 = 0; $$15 < $$14; ++$$15) {
            String $$16 = GL31.glGetActiveUniformBlockName((int)this.programId, (int)$$15);
            if (this.uniformsByName.containsKey($$16)) continue;
            if (!$$1.contains($$16) && BUILT_IN_UNIFORMS.contains($$16)) {
                int $$17 = $$2++;
                GL31.glUniformBlockBinding((int)this.programId, (int)$$15, (int)$$17);
                this.uniformsByName.put($$16, new Uniform.Ubo($$17));
                continue;
            }
            LOGGER.warn("Found unknown and unsupported uniform {} in {}", (Object)$$16, (Object)this.debugLabel);
        }
    }

    @Override
    public void close() {
        this.uniformsByName.values().forEach(Uniform::close);
        GlStateManager.glDeleteProgram(this.programId);
    }

    @Nullable
    public Uniform getUniform(String $$0) {
        RenderSystem.assertOnRenderThread();
        return this.uniformsByName.get($$0);
    }

    @VisibleForTesting
    public int getProgramId() {
        return this.programId;
    }

    public String toString() {
        return this.debugLabel;
    }

    public String getDebugLabel() {
        return this.debugLabel;
    }

    public Map<String, Uniform> getUniforms() {
        return this.uniformsByName;
    }
}

