/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class GlShaderModule
implements AutoCloseable {
    private static final int NOT_ALLOCATED = -1;
    public static final GlShaderModule INVALID_SHADER = new GlShaderModule(-1, ResourceLocation.withDefaultNamespace("invalid"), ShaderType.VERTEX);
    private final ResourceLocation id;
    private int shaderId;
    private final ShaderType type;

    public GlShaderModule(int $$0, ResourceLocation $$1, ShaderType $$2) {
        this.id = $$1;
        this.shaderId = $$0;
        this.type = $$2;
    }

    public static GlShaderModule compile(ResourceLocation $$0, ShaderType $$1, String $$2) throws ShaderManager.CompilationException {
        RenderSystem.assertOnRenderThread();
        int $$3 = GlStateManager.glCreateShader(GlConst.toGl($$1));
        GlStateManager.glShaderSource($$3, $$2);
        GlStateManager.glCompileShader($$3);
        if (GlStateManager.glGetShaderi($$3, 35713) == 0) {
            String $$4 = StringUtils.trim(GlStateManager.glGetShaderInfoLog($$3, 32768));
            throw new ShaderManager.CompilationException("Couldn't compile " + $$1.getName() + " shader (" + String.valueOf($$0) + ") : " + $$4);
        }
        return new GlShaderModule($$3, $$0, $$1);
    }

    @Override
    public void close() {
        if (this.shaderId == -1) {
            throw new IllegalStateException("Already closed");
        }
        RenderSystem.assertOnRenderThread();
        GlStateManager.glDeleteShader(this.shaderId);
        this.shaderId = -1;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public int getShaderId() {
        return this.shaderId;
    }

    public String getDebugLabel() {
        return this.type.idConverter().idToFile(this.id).toString();
    }
}

