/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.ResourceLocation;

@DontObfuscate
public class RenderPipeline {
    private final ResourceLocation location;
    private final ResourceLocation vertexShader;
    private final ResourceLocation fragmentShader;
    private final ShaderDefines shaderDefines;
    private final List<String> samplers;
    private final List<UniformDescription> uniforms;
    private final DepthTestFunction depthTestFunction;
    private final PolygonMode polygonMode;
    private final boolean cull;
    private final LogicOp colorLogic;
    private final Optional<BlendFunction> blendFunction;
    private final boolean writeColor;
    private final boolean writeAlpha;
    private final boolean writeDepth;
    private final VertexFormat vertexFormat;
    private final VertexFormat.Mode vertexFormatMode;
    private final float depthBiasScaleFactor;
    private final float depthBiasConstant;
    private final int sortKey;
    private static int sortKeySeed;

    protected RenderPipeline(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2, ShaderDefines $$3, List<String> $$4, List<UniformDescription> $$5, Optional<BlendFunction> $$6, DepthTestFunction $$7, PolygonMode $$8, boolean $$9, boolean $$10, boolean $$11, boolean $$12, LogicOp $$13, VertexFormat $$14, VertexFormat.Mode $$15, float $$16, float $$17, int $$18) {
        this.location = $$0;
        this.vertexShader = $$1;
        this.fragmentShader = $$2;
        this.shaderDefines = $$3;
        this.samplers = $$4;
        this.uniforms = $$5;
        this.depthTestFunction = $$7;
        this.polygonMode = $$8;
        this.cull = $$9;
        this.blendFunction = $$6;
        this.writeColor = $$10;
        this.writeAlpha = $$11;
        this.writeDepth = $$12;
        this.colorLogic = $$13;
        this.vertexFormat = $$14;
        this.vertexFormatMode = $$15;
        this.depthBiasScaleFactor = $$16;
        this.depthBiasConstant = $$17;
        this.sortKey = $$18;
    }

    public int getSortKey() {
        return this.sortKey;
    }

    public static void updateSortKeySeed() {
        sortKeySeed = Math.round(100000.0f * (float)Math.random());
    }

    public String toString() {
        return this.location.toString();
    }

    public DepthTestFunction getDepthTestFunction() {
        return this.depthTestFunction;
    }

    public PolygonMode getPolygonMode() {
        return this.polygonMode;
    }

    public boolean isCull() {
        return this.cull;
    }

    public LogicOp getColorLogic() {
        return this.colorLogic;
    }

    public Optional<BlendFunction> getBlendFunction() {
        return this.blendFunction;
    }

    public boolean isWriteColor() {
        return this.writeColor;
    }

    public boolean isWriteAlpha() {
        return this.writeAlpha;
    }

    public boolean isWriteDepth() {
        return this.writeDepth;
    }

    public float getDepthBiasScaleFactor() {
        return this.depthBiasScaleFactor;
    }

    public float getDepthBiasConstant() {
        return this.depthBiasConstant;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public VertexFormat.Mode getVertexFormatMode() {
        return this.vertexFormatMode;
    }

    public ResourceLocation getVertexShader() {
        return this.vertexShader;
    }

    public ResourceLocation getFragmentShader() {
        return this.fragmentShader;
    }

    public ShaderDefines getShaderDefines() {
        return this.shaderDefines;
    }

    public List<String> getSamplers() {
        return this.samplers;
    }

    public List<UniformDescription> getUniforms() {
        return this.uniforms;
    }

    public boolean wantsDepthTexture() {
        return this.depthTestFunction != DepthTestFunction.NO_DEPTH_TEST || this.depthBiasConstant != 0.0f || this.depthBiasScaleFactor != 0.0f || this.writeDepth;
    }

    public static Builder builder(Snippet ... $$0) {
        Builder $$1 = new Builder();
        for (Snippet $$2 : $$0) {
            $$1.withSnippet($$2);
        }
        return $$1;
    }

    @DontObfuscate
    public static class Builder {
        private static int nextPipelineSortKey;
        private Optional<ResourceLocation> location = Optional.empty();
        private Optional<ResourceLocation> fragmentShader = Optional.empty();
        private Optional<ResourceLocation> vertexShader = Optional.empty();
        private Optional<ShaderDefines.Builder> definesBuilder = Optional.empty();
        private Optional<List<String>> samplers = Optional.empty();
        private Optional<List<UniformDescription>> uniforms = Optional.empty();
        private Optional<DepthTestFunction> depthTestFunction = Optional.empty();
        private Optional<PolygonMode> polygonMode = Optional.empty();
        private Optional<Boolean> cull = Optional.empty();
        private Optional<Boolean> writeColor = Optional.empty();
        private Optional<Boolean> writeAlpha = Optional.empty();
        private Optional<Boolean> writeDepth = Optional.empty();
        private Optional<LogicOp> colorLogic = Optional.empty();
        private Optional<BlendFunction> blendFunction = Optional.empty();
        private Optional<VertexFormat> vertexFormat = Optional.empty();
        private Optional<VertexFormat.Mode> vertexFormatMode = Optional.empty();
        private float depthBiasScaleFactor;
        private float depthBiasConstant;

        Builder() {
        }

        public Builder withLocation(String $$0) {
            this.location = Optional.of(ResourceLocation.withDefaultNamespace($$0));
            return this;
        }

        public Builder withLocation(ResourceLocation $$0) {
            this.location = Optional.of($$0);
            return this;
        }

        public Builder withFragmentShader(String $$0) {
            this.fragmentShader = Optional.of(ResourceLocation.withDefaultNamespace($$0));
            return this;
        }

        public Builder withFragmentShader(ResourceLocation $$0) {
            this.fragmentShader = Optional.of($$0);
            return this;
        }

        public Builder withVertexShader(String $$0) {
            this.vertexShader = Optional.of(ResourceLocation.withDefaultNamespace($$0));
            return this;
        }

        public Builder withVertexShader(ResourceLocation $$0) {
            this.vertexShader = Optional.of($$0);
            return this;
        }

        public Builder withShaderDefine(String $$0) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            this.definesBuilder.get().define($$0);
            return this;
        }

        public Builder withShaderDefine(String $$0, int $$1) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            this.definesBuilder.get().define($$0, $$1);
            return this;
        }

        public Builder withShaderDefine(String $$0, float $$1) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            this.definesBuilder.get().define($$0, $$1);
            return this;
        }

        public Builder withSampler(String $$0) {
            if (this.samplers.isEmpty()) {
                this.samplers = Optional.of(new ArrayList());
            }
            this.samplers.get().add($$0);
            return this;
        }

        public Builder withUniform(String $$0, UniformType $$1) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList());
            }
            if ($$1 == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
            }
            this.uniforms.get().add(new UniformDescription($$0, $$1));
            return this;
        }

        public Builder withUniform(String $$0, UniformType $$1, TextureFormat $$2) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList());
            }
            if ($$1 != UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Only texel buffer can specify texture format");
            }
            this.uniforms.get().add(new UniformDescription($$0, $$2));
            return this;
        }

        public Builder withDepthTestFunction(DepthTestFunction $$0) {
            this.depthTestFunction = Optional.of($$0);
            return this;
        }

        public Builder withPolygonMode(PolygonMode $$0) {
            this.polygonMode = Optional.of($$0);
            return this;
        }

        public Builder withCull(boolean $$0) {
            this.cull = Optional.of($$0);
            return this;
        }

        public Builder withBlend(BlendFunction $$0) {
            this.blendFunction = Optional.of($$0);
            return this;
        }

        public Builder withoutBlend() {
            this.blendFunction = Optional.empty();
            return this;
        }

        public Builder withColorWrite(boolean $$0) {
            this.writeColor = Optional.of($$0);
            this.writeAlpha = Optional.of($$0);
            return this;
        }

        public Builder withColorWrite(boolean $$0, boolean $$1) {
            this.writeColor = Optional.of($$0);
            this.writeAlpha = Optional.of($$1);
            return this;
        }

        public Builder withDepthWrite(boolean $$0) {
            this.writeDepth = Optional.of($$0);
            return this;
        }

        @Deprecated
        public Builder withColorLogic(LogicOp $$0) {
            this.colorLogic = Optional.of($$0);
            return this;
        }

        public Builder withVertexFormat(VertexFormat $$0, VertexFormat.Mode $$1) {
            this.vertexFormat = Optional.of($$0);
            this.vertexFormatMode = Optional.of($$1);
            return this;
        }

        public Builder withDepthBias(float $$0, float $$1) {
            this.depthBiasScaleFactor = $$0;
            this.depthBiasConstant = $$1;
            return this;
        }

        void withSnippet(Snippet $$02) {
            if ($$02.vertexShader.isPresent()) {
                this.vertexShader = $$02.vertexShader;
            }
            if ($$02.fragmentShader.isPresent()) {
                this.fragmentShader = $$02.fragmentShader;
            }
            if ($$02.shaderDefines.isPresent()) {
                if (this.definesBuilder.isEmpty()) {
                    this.definesBuilder = Optional.of(ShaderDefines.builder());
                }
                ShaderDefines $$1 = $$02.shaderDefines.get();
                for (Map.Entry<String, String> $$2 : $$1.values().entrySet()) {
                    this.definesBuilder.get().define($$2.getKey(), $$2.getValue());
                }
                for (String $$3 : $$1.flags()) {
                    this.definesBuilder.get().define($$3);
                }
            }
            $$02.samplers.ifPresent($$0 -> {
                if (this.samplers.isPresent()) {
                    this.samplers.get().addAll((Collection<String>)$$0);
                } else {
                    this.samplers = Optional.of(new ArrayList($$0));
                }
            });
            $$02.uniforms.ifPresent($$0 -> {
                if (this.uniforms.isPresent()) {
                    this.uniforms.get().addAll((Collection<UniformDescription>)$$0);
                } else {
                    this.uniforms = Optional.of(new ArrayList($$0));
                }
            });
            if ($$02.depthTestFunction.isPresent()) {
                this.depthTestFunction = $$02.depthTestFunction;
            }
            if ($$02.cull.isPresent()) {
                this.cull = $$02.cull;
            }
            if ($$02.writeColor.isPresent()) {
                this.writeColor = $$02.writeColor;
            }
            if ($$02.writeAlpha.isPresent()) {
                this.writeAlpha = $$02.writeAlpha;
            }
            if ($$02.writeDepth.isPresent()) {
                this.writeDepth = $$02.writeDepth;
            }
            if ($$02.colorLogic.isPresent()) {
                this.colorLogic = $$02.colorLogic;
            }
            if ($$02.blendFunction.isPresent()) {
                this.blendFunction = $$02.blendFunction;
            }
            if ($$02.vertexFormat.isPresent()) {
                this.vertexFormat = $$02.vertexFormat;
            }
            if ($$02.vertexFormatMode.isPresent()) {
                this.vertexFormatMode = $$02.vertexFormatMode;
            }
        }

        public Snippet buildSnippet() {
            return new Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(ShaderDefines.Builder::build), this.samplers.map(Collections::unmodifiableList), this.uniforms.map(Collections::unmodifiableList), this.blendFunction, this.depthTestFunction, this.polygonMode, this.cull, this.writeColor, this.writeAlpha, this.writeDepth, this.colorLogic, this.vertexFormat, this.vertexFormatMode);
        }

        public RenderPipeline build() {
            if (this.location.isEmpty()) {
                throw new IllegalStateException("Missing location");
            }
            if (this.vertexShader.isEmpty()) {
                throw new IllegalStateException("Missing vertex shader");
            }
            if (this.fragmentShader.isEmpty()) {
                throw new IllegalStateException("Missing fragment shader");
            }
            if (this.vertexFormat.isEmpty()) {
                throw new IllegalStateException("Missing vertex buffer format");
            }
            if (this.vertexFormatMode.isEmpty()) {
                throw new IllegalStateException("Missing vertex mode");
            }
            return new RenderPipeline(this.location.get(), this.vertexShader.get(), this.fragmentShader.get(), this.definesBuilder.orElse(ShaderDefines.builder()).build(), List.copyOf((Collection)this.samplers.orElse(new ArrayList())), this.uniforms.orElse(Collections.emptyList()), this.blendFunction, this.depthTestFunction.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST), this.polygonMode.orElse(PolygonMode.FILL), this.cull.orElse(true), this.writeColor.orElse(true), this.writeAlpha.orElse(true), this.writeDepth.orElse(true), this.colorLogic.orElse(LogicOp.NONE), this.vertexFormat.get(), this.vertexFormatMode.get(), this.depthBiasScaleFactor, this.depthBiasConstant, nextPipelineSortKey++);
        }
    }

    @DontObfuscate
    public static final class Snippet
    extends Record {
        final Optional<ResourceLocation> vertexShader;
        final Optional<ResourceLocation> fragmentShader;
        final Optional<ShaderDefines> shaderDefines;
        final Optional<List<String>> samplers;
        final Optional<List<UniformDescription>> uniforms;
        final Optional<BlendFunction> blendFunction;
        final Optional<DepthTestFunction> depthTestFunction;
        private final Optional<PolygonMode> polygonMode;
        final Optional<Boolean> cull;
        final Optional<Boolean> writeColor;
        final Optional<Boolean> writeAlpha;
        final Optional<Boolean> writeDepth;
        final Optional<LogicOp> colorLogic;
        final Optional<VertexFormat> vertexFormat;
        final Optional<VertexFormat.Mode> vertexFormatMode;

        public Snippet(Optional<ResourceLocation> $$0, Optional<ResourceLocation> $$1, Optional<ShaderDefines> $$2, Optional<List<String>> $$3, Optional<List<UniformDescription>> $$4, Optional<BlendFunction> $$5, Optional<DepthTestFunction> $$6, Optional<PolygonMode> $$7, Optional<Boolean> $$8, Optional<Boolean> $$9, Optional<Boolean> $$10, Optional<Boolean> $$11, Optional<LogicOp> $$12, Optional<VertexFormat> $$13, Optional<VertexFormat.Mode> $$14) {
            this.vertexShader = $$0;
            this.fragmentShader = $$1;
            this.shaderDefines = $$2;
            this.samplers = $$3;
            this.uniforms = $$4;
            this.blendFunction = $$5;
            this.depthTestFunction = $$6;
            this.polygonMode = $$7;
            this.cull = $$8;
            this.writeColor = $$9;
            this.writeAlpha = $$10;
            this.writeDepth = $$11;
            this.colorLogic = $$12;
            this.vertexFormat = $$13;
            this.vertexFormatMode = $$14;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this, $$0);
        }

        public Optional<ResourceLocation> vertexShader() {
            return this.vertexShader;
        }

        public Optional<ResourceLocation> fragmentShader() {
            return this.fragmentShader;
        }

        public Optional<ShaderDefines> shaderDefines() {
            return this.shaderDefines;
        }

        public Optional<List<String>> samplers() {
            return this.samplers;
        }

        public Optional<List<UniformDescription>> uniforms() {
            return this.uniforms;
        }

        public Optional<BlendFunction> blendFunction() {
            return this.blendFunction;
        }

        public Optional<DepthTestFunction> depthTestFunction() {
            return this.depthTestFunction;
        }

        public Optional<PolygonMode> polygonMode() {
            return this.polygonMode;
        }

        public Optional<Boolean> cull() {
            return this.cull;
        }

        public Optional<Boolean> writeColor() {
            return this.writeColor;
        }

        public Optional<Boolean> writeAlpha() {
            return this.writeAlpha;
        }

        public Optional<Boolean> writeDepth() {
            return this.writeDepth;
        }

        public Optional<LogicOp> colorLogic() {
            return this.colorLogic;
        }

        public Optional<VertexFormat> vertexFormat() {
            return this.vertexFormat;
        }

        public Optional<VertexFormat.Mode> vertexFormatMode() {
            return this.vertexFormatMode;
        }
    }

    @DontObfuscate
    public record UniformDescription(String name, UniformType type, @Nullable TextureFormat textureFormat) {
        public UniformDescription(String $$0, UniformType $$1) {
            this($$0, $$1, null);
            if ($$1 == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Texel buffer needs a texture format");
            }
        }

        public UniformDescription(String $$0, TextureFormat $$1) {
            this($$0, UniformType.TEXEL_BUFFER, $$1);
        }

        @Nullable
        public TextureFormat textureFormat() {
            return this.textureFormat;
        }
    }
}

