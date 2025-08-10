/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.UniformType;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PostChain
implements AutoCloseable {
    public static final ResourceLocation MAIN_TARGET_ID = ResourceLocation.withDefaultNamespace("main");
    private final List<PostPass> passes;
    private final Map<ResourceLocation, PostChainConfig.InternalTarget> internalTargets;
    private final Set<ResourceLocation> externalTargets;
    private final Map<ResourceLocation, RenderTarget> persistentTargets = new HashMap<ResourceLocation, RenderTarget>();
    private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer;

    private PostChain(List<PostPass> $$0, Map<ResourceLocation, PostChainConfig.InternalTarget> $$1, Set<ResourceLocation> $$2, CachedOrthoProjectionMatrixBuffer $$3) {
        this.passes = $$0;
        this.internalTargets = $$1;
        this.externalTargets = $$2;
        this.projectionMatrixBuffer = $$3;
    }

    public static PostChain load(PostChainConfig $$0, TextureManager $$12, Set<ResourceLocation> $$2, ResourceLocation $$3, CachedOrthoProjectionMatrixBuffer $$4) throws ShaderManager.CompilationException {
        Stream $$5 = $$0.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
        Set<ResourceLocation> $$6 = $$5.filter($$1 -> !$$0.internalTargets().containsKey($$1)).collect(Collectors.toSet());
        Sets.SetView $$7 = Sets.difference($$6, $$2);
        if (!$$7.isEmpty()) {
            throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf($$7));
        }
        ImmutableList.Builder $$8 = ImmutableList.builder();
        for (int $$9 = 0; $$9 < $$0.passes().size(); ++$$9) {
            PostChainConfig.Pass $$10 = $$0.passes().get($$9);
            $$8.add(PostChain.createPass($$12, $$10, $$3.withSuffix("/" + $$9)));
        }
        return new PostChain((List<PostPass>)((Object)$$8.build()), $$0.internalTargets(), $$6, $$4);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static PostPass createPass(TextureManager $$02, PostChainConfig.Pass $$1, ResourceLocation $$2) throws ShaderManager.CompilationException {
        RenderPipeline.Builder $$3 = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET).withFragmentShader($$1.fragmentShaderId()).withVertexShader($$1.vertexShaderId()).withLocation($$2);
        for (PostChainConfig.Input $$4 : $$1.inputs()) {
            $$3.withSampler($$4.samplerName() + "Sampler");
        }
        $$3.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);
        for (String $$5 : $$1.uniforms().keySet()) {
            $$3.withUniform($$5, UniformType.UNIFORM_BUFFER);
        }
        RenderPipeline $$6 = $$3.build();
        ArrayList<PostPass.Input> $$7 = new ArrayList<PostPass.Input>();
        Iterator<PostChainConfig.Input> iterator = $$1.inputs().iterator();
        block9: while (true) {
            PostChainConfig.Input input;
            if (!iterator.hasNext()) {
                return new PostPass($$6, $$1.outputTarget(), $$1.uniforms(), $$7);
            }
            PostChainConfig.Input $$8 = iterator.next();
            Objects.requireNonNull($$8);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostChainConfig.TextureInput.class, PostChainConfig.TargetInput.class}, (Object)input, (int)n)) {
                case 0: {
                    int n2;
                    Object object;
                    PostChainConfig.TextureInput textureInput = (PostChainConfig.TextureInput)input;
                    Object $$9 = object = textureInput.samplerName();
                    Object $$10 = object = textureInput.location();
                    int $$11 = n2 = textureInput.width();
                    int $$12 = n2 = textureInput.height();
                    int $$13 = n2 = (int)(textureInput.bilinear() ? 1 : 0);
                    AbstractTexture $$14 = $$02.getTexture(((ResourceLocation)$$10).withPath($$0 -> "textures/effect/" + $$0 + ".png"));
                    $$14.setFilter($$13 != 0, false);
                    $$7.add(new PostPass.TextureInput((String)$$9, $$14, $$11, $$12));
                    continue block9;
                }
                case 1: {
                    Object object = (PostChainConfig.TargetInput)input;
                    try {
                        boolean bl;
                        Object object2 = ((PostChainConfig.TargetInput)object).samplerName();
                        String $$15 = object2;
                        Object $$16 = object2 = ((PostChainConfig.TargetInput)object).targetId();
                        boolean $$17 = bl = ((PostChainConfig.TargetInput)object).useDepthBuffer();
                        boolean $$18 = bl = ((PostChainConfig.TargetInput)object).bilinear();
                        $$7.add(new PostPass.TargetInput($$15, (ResourceLocation)$$16, $$17, $$18));
                    } catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    continue block9;
                }
            }
            break;
        }
        throw new MatchException(null, null);
    }

    public void addToFrame(FrameGraphBuilder $$0, int $$1, int $$2, TargetBundle $$3) {
        GpuBufferSlice $$4 = this.projectionMatrixBuffer.getBuffer($$1, $$2);
        HashMap<ResourceLocation, ResourceHandle<RenderTarget>> $$5 = new HashMap<ResourceLocation, ResourceHandle<RenderTarget>>(this.internalTargets.size() + this.externalTargets.size());
        for (ResourceLocation resourceLocation : this.externalTargets) {
            $$5.put(resourceLocation, $$3.getOrThrow(resourceLocation));
        }
        for (Map.Entry entry : this.internalTargets.entrySet()) {
            ResourceLocation $$8 = (ResourceLocation)entry.getKey();
            PostChainConfig.InternalTarget $$9 = (PostChainConfig.InternalTarget)((Object)entry.getValue());
            RenderTargetDescriptor $$10 = new RenderTargetDescriptor($$9.width().orElse($$1), $$9.height().orElse($$2), true, $$9.clearColor());
            if ($$9.persistent()) {
                RenderTarget $$11 = this.getOrCreatePersistentTarget($$8, $$10);
                $$5.put($$8, $$0.importExternal($$8.toString(), $$11));
                continue;
            }
            $$5.put($$8, $$0.createInternal($$8.toString(), $$10));
        }
        for (PostPass postPass : this.passes) {
            postPass.addToFrame($$0, $$5, $$4);
        }
        for (ResourceLocation resourceLocation : this.externalTargets) {
            $$3.replace(resourceLocation, (ResourceHandle)$$5.get(resourceLocation));
        }
    }

    @Deprecated
    public void process(RenderTarget $$0, GraphicsResourceAllocator $$1) {
        FrameGraphBuilder $$2 = new FrameGraphBuilder();
        TargetBundle $$3 = TargetBundle.of(MAIN_TARGET_ID, $$2.importExternal("main", $$0));
        this.addToFrame($$2, $$0.width, $$0.height, $$3);
        $$2.execute($$1);
    }

    private RenderTarget getOrCreatePersistentTarget(ResourceLocation $$0, RenderTargetDescriptor $$1) {
        RenderTarget $$2 = this.persistentTargets.get($$0);
        if ($$2 == null || $$2.width != $$1.width() || $$2.height != $$1.height()) {
            if ($$2 != null) {
                $$2.destroyBuffers();
            }
            $$2 = $$1.allocate();
            $$1.prepare($$2);
            this.persistentTargets.put($$0, $$2);
        }
        return $$2;
    }

    @Override
    public void close() {
        this.persistentTargets.values().forEach(RenderTarget::destroyBuffers);
        this.persistentTargets.clear();
        for (PostPass $$0 : this.passes) {
            $$0.close();
        }
    }

    public static interface TargetBundle {
        public static TargetBundle of(final ResourceLocation $$0, final ResourceHandle<RenderTarget> $$1) {
            return new TargetBundle(){
                private ResourceHandle<RenderTarget> handle;
                {
                    this.handle = $$1;
                }

                @Override
                public void replace(ResourceLocation $$02, ResourceHandle<RenderTarget> $$12) {
                    if (!$$02.equals($$0)) {
                        throw new IllegalArgumentException("No target with id " + String.valueOf($$02));
                    }
                    this.handle = $$12;
                }

                @Override
                @Nullable
                public ResourceHandle<RenderTarget> get(ResourceLocation $$02) {
                    return $$02.equals($$0) ? this.handle : null;
                }
            };
        }

        public void replace(ResourceLocation var1, ResourceHandle<RenderTarget> var2);

        @Nullable
        public ResourceHandle<RenderTarget> get(ResourceLocation var1);

        default public ResourceHandle<RenderTarget> getOrThrow(ResourceLocation $$0) {
            ResourceHandle<RenderTarget> $$1 = this.get($$0);
            if ($$1 == null) {
                throw new IllegalArgumentException("Missing target with id " + String.valueOf($$0));
            }
            return $$1;
        }
    }
}

