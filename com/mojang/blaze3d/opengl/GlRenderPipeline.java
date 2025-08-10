/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;

public record GlRenderPipeline(RenderPipeline info, GlProgram program) implements CompiledRenderPipeline
{
    @Override
    public boolean isValid() {
        return this.program != GlProgram.INVALID_PROGRAM;
    }
}

