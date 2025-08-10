/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ShaderManager
extends SimplePreparableReloadListener<Configs>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final int MAX_LOG_LENGTH = 32768;
    public static final String SHADER_PATH = "shaders";
    private static final String SHADER_INCLUDE_PATH = "shaders/include/";
    private static final FileToIdConverter POST_CHAIN_ID_CONVERTER = FileToIdConverter.json("post_effect");
    final TextureManager textureManager;
    private final Consumer<Exception> recoveryHandler;
    private CompilationCache compilationCache = new CompilationCache(Configs.EMPTY);
    final CachedOrthoProjectionMatrixBuffer postChainProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("post", 0.1f, 1000.0f, false);

    public ShaderManager(TextureManager $$0, Consumer<Exception> $$1) {
        this.textureManager = $$0;
        this.recoveryHandler = $$1;
    }

    @Override
    protected Configs prepare(ResourceManager $$0, ProfilerFiller $$1) {
        ImmutableMap.Builder<ShaderSourceKey, String> $$2 = ImmutableMap.builder();
        Map<ResourceLocation, Resource> $$3 = $$0.listResources(SHADER_PATH, ShaderManager::isShader);
        for (Map.Entry<ResourceLocation, Resource> $$4 : $$3.entrySet()) {
            ResourceLocation $$5 = $$4.getKey();
            ShaderType $$6 = ShaderType.byLocation($$5);
            if ($$6 == null) continue;
            ShaderManager.loadShader($$5, $$4.getValue(), $$6, $$3, $$2);
        }
        ImmutableMap.Builder<ResourceLocation, PostChainConfig> $$7 = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, Resource> $$8 : POST_CHAIN_ID_CONVERTER.listMatchingResources($$0).entrySet()) {
            ShaderManager.loadPostChain($$8.getKey(), $$8.getValue(), $$7);
        }
        return new Configs($$2.build(), $$7.build());
    }

    private static void loadShader(ResourceLocation $$0, Resource $$1, ShaderType $$2, Map<ResourceLocation, Resource> $$3, ImmutableMap.Builder<ShaderSourceKey, String> $$4) {
        ResourceLocation $$5 = $$2.idConverter().fileToId($$0);
        GlslPreprocessor $$6 = ShaderManager.createPreprocessor($$3, $$0);
        try (BufferedReader $$7 = $$1.openAsReader();){
            String $$8 = IOUtils.toString((Reader)$$7);
            $$4.put(new ShaderSourceKey($$5, $$2), String.join((CharSequence)"", $$6.process($$8)));
        } catch (IOException $$9) {
            LOGGER.error("Failed to load shader source at {}", (Object)$$0, (Object)$$9);
        }
    }

    private static GlslPreprocessor createPreprocessor(final Map<ResourceLocation, Resource> $$0, ResourceLocation $$1) {
        final ResourceLocation $$2 = $$1.withPath(FileUtil::getFullResourcePath);
        return new GlslPreprocessor(){
            private final Set<ResourceLocation> importedLocations = new ObjectArraySet();

            /*
             * WARNING - void declaration
             */
            @Override
            public String applyImport(boolean $$02, String $$12) {
                String string;
                block13: {
                    void $$5;
                    try {
                        if ($$02) {
                            ResourceLocation $$22 = $$2.withPath($$1 -> FileUtil.normalizeResourcePath($$1 + $$12));
                        } else {
                            ResourceLocation $$3 = ResourceLocation.parse($$12).withPrefix(ShaderManager.SHADER_INCLUDE_PATH);
                        }
                    } catch (ResourceLocationException $$4) {
                        LOGGER.error("Malformed GLSL import {}: {}", (Object)$$12, (Object)$$4.getMessage());
                        return "#error " + $$4.getMessage();
                    }
                    if (!this.importedLocations.add((ResourceLocation)$$5)) {
                        return null;
                    }
                    BufferedReader $$6 = ((Resource)$$0.get($$5)).openAsReader();
                    try {
                        string = IOUtils.toString((Reader)$$6);
                        if ($$6 == null) break block13;
                    } catch (Throwable throwable) {
                        try {
                            if ($$6 != null) {
                                try {
                                    ((Reader)$$6).close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (IOException $$7) {
                            LOGGER.error("Could not open GLSL import {}: {}", (Object)$$5, (Object)$$7.getMessage());
                            return "#error " + $$7.getMessage();
                        }
                    }
                    ((Reader)$$6).close();
                }
                return string;
            }
        };
    }

    private static void loadPostChain(ResourceLocation $$0, Resource $$1, ImmutableMap.Builder<ResourceLocation, PostChainConfig> $$2) {
        ResourceLocation $$3 = POST_CHAIN_ID_CONVERTER.fileToId($$0);
        try (BufferedReader $$4 = $$1.openAsReader();){
            JsonElement $$5 = StrictJsonParser.parse($$4);
            $$2.put($$3, (PostChainConfig)((Object)PostChainConfig.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$5).getOrThrow(JsonSyntaxException::new)));
        } catch (JsonParseException | IOException $$6) {
            LOGGER.error("Failed to parse post chain at {}", (Object)$$0, (Object)$$6);
        }
    }

    private static boolean isShader(ResourceLocation $$0) {
        return ShaderType.byLocation($$0) != null || $$0.getPath().endsWith(".glsl");
    }

    @Override
    protected void apply(Configs $$02, ResourceManager $$1, ProfilerFiller $$2) {
        CompilationCache $$3 = new CompilationCache($$02);
        HashSet<RenderPipeline> $$4 = new HashSet<RenderPipeline>(RenderPipelines.getStaticPipelines());
        ArrayList<ResourceLocation> $$5 = new ArrayList<ResourceLocation>();
        GpuDevice $$6 = RenderSystem.getDevice();
        $$6.clearPipelineCache();
        for (RenderPipeline $$7 : $$4) {
            CompiledRenderPipeline $$8 = $$6.precompilePipeline($$7, $$3::getShaderSource);
            if ($$8.isValid()) continue;
            $$5.add($$7.getLocation());
        }
        if (!$$5.isEmpty()) {
            $$6.clearPipelineCache();
            throw new RuntimeException("Failed to load required shader programs:\n" + $$5.stream().map($$0 -> " - " + String.valueOf($$0)).collect(Collectors.joining("\n")));
        }
        this.compilationCache.close();
        this.compilationCache = $$3;
    }

    @Override
    public String getName() {
        return "Shader Loader";
    }

    private void tryTriggerRecovery(Exception $$0) {
        if (this.compilationCache.triggeredRecovery) {
            return;
        }
        this.recoveryHandler.accept($$0);
        this.compilationCache.triggeredRecovery = true;
    }

    @Nullable
    public PostChain getPostChain(ResourceLocation $$0, Set<ResourceLocation> $$1) {
        try {
            return this.compilationCache.getOrLoadPostChain($$0, $$1);
        } catch (CompilationException $$2) {
            LOGGER.error("Failed to load post chain: {}", (Object)$$0, (Object)$$2);
            this.compilationCache.postChains.put($$0, Optional.empty());
            this.tryTriggerRecovery($$2);
            return null;
        }
    }

    @Override
    public void close() {
        this.compilationCache.close();
        this.postChainProjectionMatrixBuffer.close();
    }

    public String getShader(ResourceLocation $$0, ShaderType $$1) {
        return this.compilationCache.getShaderSource($$0, $$1);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    class CompilationCache
    implements AutoCloseable {
        private final Configs configs;
        final Map<ResourceLocation, Optional<PostChain>> postChains = new HashMap<ResourceLocation, Optional<PostChain>>();
        boolean triggeredRecovery;

        CompilationCache(Configs $$0) {
            this.configs = $$0;
        }

        @Nullable
        public PostChain getOrLoadPostChain(ResourceLocation $$0, Set<ResourceLocation> $$1) throws CompilationException {
            Optional<PostChain> $$2 = this.postChains.get($$0);
            if ($$2 != null) {
                return $$2.orElse(null);
            }
            PostChain $$3 = this.loadPostChain($$0, $$1);
            this.postChains.put($$0, Optional.of($$3));
            return $$3;
        }

        private PostChain loadPostChain(ResourceLocation $$0, Set<ResourceLocation> $$1) throws CompilationException {
            PostChainConfig $$2 = this.configs.postChains.get($$0);
            if ($$2 == null) {
                throw new CompilationException("Could not find post chain with id: " + String.valueOf($$0));
            }
            return PostChain.load($$2, ShaderManager.this.textureManager, $$1, $$0, ShaderManager.this.postChainProjectionMatrixBuffer);
        }

        @Override
        public void close() {
            this.postChains.values().forEach($$0 -> $$0.ifPresent(PostChain::close));
            this.postChains.clear();
        }

        public String getShaderSource(ResourceLocation $$0, ShaderType $$1) {
            return this.configs.shaderSources.get((Object)new ShaderSourceKey($$0, $$1));
        }
    }

    public static final class Configs
    extends Record {
        final Map<ShaderSourceKey, String> shaderSources;
        final Map<ResourceLocation, PostChainConfig> postChains;
        public static final Configs EMPTY = new Configs(Map.of(), Map.of());

        public Configs(Map<ShaderSourceKey, String> $$0, Map<ResourceLocation, PostChainConfig> $$1) {
            this.shaderSources = $$0;
            this.postChains = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Configs.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Configs.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Configs.class, "shaderSources;postChains", "shaderSources", "postChains"}, this, $$0);
        }

        public Map<ShaderSourceKey, String> shaderSources() {
            return this.shaderSources;
        }

        public Map<ResourceLocation, PostChainConfig> postChains() {
            return this.postChains;
        }
    }

    record ShaderSourceKey(ResourceLocation id, ShaderType type) {
        public String toString() {
            return String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
        }
    }

    public static class CompilationException
    extends Exception {
        public CompilationException(String $$0) {
            super($$0);
        }
    }
}

