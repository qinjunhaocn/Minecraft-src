/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.AllMissingGlyphProvider;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class FontManager
implements PreparableReloadListener,
AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_PATH = "fonts.json";
    public static final ResourceLocation MISSING_FONT = ResourceLocation.withDefaultNamespace("missing");
    private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final FontSet missingFontSet;
    private final List<GlyphProvider> providersToClose = new ArrayList<GlyphProvider>();
    private final Map<ResourceLocation, FontSet> fontSets = new HashMap<ResourceLocation, FontSet>();
    private final TextureManager textureManager;
    @Nullable
    private volatile FontSet lastFontSetCache;

    public FontManager(TextureManager $$02) {
        this.textureManager = $$02;
        this.missingFontSet = Util.make(new FontSet($$02, MISSING_FONT), $$0 -> $$0.reload(List.of((Object)FontManager.createFallbackProvider()), Set.of()));
    }

    private static GlyphProvider.Conditional createFallbackProvider() {
        return new GlyphProvider.Conditional(new AllMissingGlyphProvider(), FontOption.Filter.ALWAYS_PASS);
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$1, Executor $$2, Executor $$3) {
        return ((CompletableFuture)this.prepare($$1, $$2).thenCompose($$02::wait)).thenAcceptAsync($$0 -> this.apply((Preparation)((Object)$$0), Profiler.get()), $$3);
    }

    private CompletableFuture<Preparation> prepare(ResourceManager $$0, Executor $$12) {
        ArrayList<CompletableFuture<UnresolvedBuilderBundle>> $$2 = new ArrayList<CompletableFuture<UnresolvedBuilderBundle>>();
        for (Map.Entry<ResourceLocation, List<Resource>> $$3 : FONT_DEFINITIONS.listMatchingResourceStacks($$0).entrySet()) {
            ResourceLocation $$4 = FONT_DEFINITIONS.fileToId($$3.getKey());
            $$2.add(CompletableFuture.supplyAsync(() -> {
                List<Pair<BuilderId, GlyphProviderDefinition.Conditional>> $$4 = FontManager.loadResourceStack((List)$$3.getValue(), $$4);
                UnresolvedBuilderBundle $$52 = new UnresolvedBuilderBundle($$4);
                for (Pair<BuilderId, GlyphProviderDefinition.Conditional> $$6 : $$4) {
                    BuilderId $$7 = (BuilderId)((Object)((Object)$$6.getFirst()));
                    FontOption.Filter $$8 = ((GlyphProviderDefinition.Conditional)((Object)((Object)$$6.getSecond()))).filter();
                    ((GlyphProviderDefinition.Conditional)((Object)((Object)$$6.getSecond()))).definition().unpack().ifLeft($$5 -> {
                        CompletableFuture<Optional<GlyphProvider>> $$6 = this.safeLoad($$7, (GlyphProviderDefinition.Loader)$$5, $$0, $$12);
                        $$52.add($$7, $$8, $$6);
                    }).ifRight($$3 -> $$52.add($$7, $$8, (GlyphProviderDefinition.Reference)((Object)((Object)$$3))));
                }
                return $$52;
            }, $$12));
        }
        return Util.sequence($$2).thenCompose($$1 -> {
            List $$2 = $$1.stream().flatMap(UnresolvedBuilderBundle::listBuilders).collect(Util.toMutableList());
            GlyphProvider.Conditional $$32 = FontManager.createFallbackProvider();
            $$2.add(CompletableFuture.completedFuture(Optional.of($$32.provider())));
            return Util.sequence($$2).thenCompose($$3 -> {
                Map<ResourceLocation, List<GlyphProvider.Conditional>> $$4 = this.resolveProviders((List<UnresolvedBuilderBundle>)$$1);
                CompletableFuture[] $$5 = (CompletableFuture[])$$4.values().stream().map($$2 -> CompletableFuture.runAsync(() -> this.finalizeProviderLoading((List<GlyphProvider.Conditional>)$$2, $$32), $$12)).toArray(CompletableFuture[]::new);
                return CompletableFuture.allOf($$5).thenApply($$2 -> {
                    List $$3 = $$3.stream().flatMap((Function<Optional, Stream>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, stream(), (Ljava/util/Optional;)Ljava/util/stream/Stream;)()).toList();
                    return new Preparation($$4, $$3);
                });
            });
        });
    }

    private CompletableFuture<Optional<GlyphProvider>> safeLoad(BuilderId $$0, GlyphProviderDefinition.Loader $$1, ResourceManager $$2, Executor $$3) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of($$1.load($$2));
            } catch (Exception $$3) {
                LOGGER.warn("Failed to load builder {}, rejecting", (Object)$$0, (Object)$$3);
                return Optional.empty();
            }
        }, $$3);
    }

    private Map<ResourceLocation, List<GlyphProvider.Conditional>> resolveProviders(List<UnresolvedBuilderBundle> $$0) {
        HashMap<ResourceLocation, List<GlyphProvider.Conditional>> $$12 = new HashMap<ResourceLocation, List<GlyphProvider.Conditional>>();
        DependencySorter<ResourceLocation, UnresolvedBuilderBundle> $$2 = new DependencySorter<ResourceLocation, UnresolvedBuilderBundle>();
        $$0.forEach($$1 -> $$2.addEntry($$1.fontId, (UnresolvedBuilderBundle)$$1));
        $$2.orderByDependencies(($$1, $$22) -> $$22.resolve($$12::get).ifPresent($$2 -> $$12.put((ResourceLocation)$$1, (List<GlyphProvider.Conditional>)$$2)));
        return $$12;
    }

    private void finalizeProviderLoading(List<GlyphProvider.Conditional> $$0, GlyphProvider.Conditional $$12) {
        $$0.add(0, $$12);
        IntOpenHashSet $$2 = new IntOpenHashSet();
        for (GlyphProvider.Conditional $$3 : $$0) {
            $$2.addAll((IntCollection)$$3.provider().getSupportedGlyphs());
        }
        $$2.forEach($$1 -> {
            GlyphProvider.Conditional $$2;
            if ($$1 == 32) {
                return;
            }
            Iterator iterator = Lists.reverse($$0).iterator();
            while (iterator.hasNext() && ($$2 = (GlyphProvider.Conditional)iterator.next()).provider().getGlyph($$1) == null) {
            }
        });
    }

    private static Set<FontOption> getFontOptions(Options $$0) {
        EnumSet<FontOption> $$1 = EnumSet.noneOf(FontOption.class);
        if ($$0.forceUnicodeFont().get().booleanValue()) {
            $$1.add(FontOption.UNIFORM);
        }
        if ($$0.japaneseGlyphVariants().get().booleanValue()) {
            $$1.add(FontOption.JAPANESE_VARIANTS);
        }
        return $$1;
    }

    private void apply(Preparation $$0, ProfilerFiller $$12) {
        $$12.push("closing");
        this.lastFontSetCache = null;
        this.fontSets.values().forEach(FontSet::close);
        this.fontSets.clear();
        this.providersToClose.forEach(GlyphProvider::close);
        this.providersToClose.clear();
        Set<FontOption> $$22 = FontManager.getFontOptions(Minecraft.getInstance().options);
        $$12.popPush("reloading");
        $$0.fontSets().forEach(($$1, $$2) -> {
            FontSet $$3 = new FontSet(this.textureManager, (ResourceLocation)$$1);
            $$3.reload(Lists.reverse($$2), $$22);
            this.fontSets.put((ResourceLocation)$$1, $$3);
        });
        this.providersToClose.addAll($$0.allProviders);
        $$12.pop();
        if (!this.fontSets.containsKey(Minecraft.DEFAULT_FONT)) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    public void updateOptions(Options $$0) {
        Set<FontOption> $$1 = FontManager.getFontOptions($$0);
        for (FontSet $$2 : this.fontSets.values()) {
            $$2.reload($$1);
        }
    }

    private static List<Pair<BuilderId, GlyphProviderDefinition.Conditional>> loadResourceStack(List<Resource> $$0, ResourceLocation $$1) {
        ArrayList<Pair<BuilderId, GlyphProviderDefinition.Conditional>> $$2 = new ArrayList<Pair<BuilderId, GlyphProviderDefinition.Conditional>>();
        for (Resource $$3 : $$0) {
            try {
                BufferedReader $$4 = $$3.openAsReader();
                try {
                    JsonElement $$5 = (JsonElement)GSON.fromJson((Reader)$$4, JsonElement.class);
                    FontDefinitionFile $$6 = (FontDefinitionFile)((Object)FontDefinitionFile.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$5).getOrThrow(JsonParseException::new));
                    List<GlyphProviderDefinition.Conditional> $$7 = $$6.providers;
                    for (int $$8 = $$7.size() - 1; $$8 >= 0; --$$8) {
                        BuilderId $$9 = new BuilderId($$1, $$3.sourcePackId(), $$8);
                        $$2.add((Pair<BuilderId, GlyphProviderDefinition.Conditional>)Pair.of((Object)((Object)$$9), (Object)((Object)$$7.get($$8))));
                    }
                } finally {
                    if ($$4 == null) continue;
                    ((Reader)$$4).close();
                }
            } catch (Exception $$10) {
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", $$1, FONTS_PATH, $$3.sourcePackId(), $$10);
            }
        }
        return $$2;
    }

    public Font createFont() {
        return new Font(this::getFontSetCached, false);
    }

    public Font createFontFilterFishy() {
        return new Font(this::getFontSetCached, true);
    }

    private FontSet getFontSetRaw(ResourceLocation $$0) {
        return this.fontSets.getOrDefault($$0, this.missingFontSet);
    }

    private FontSet getFontSetCached(ResourceLocation $$0) {
        FontSet $$2;
        FontSet $$1 = this.lastFontSetCache;
        if ($$1 != null && $$0.equals($$1.name())) {
            return $$1;
        }
        this.lastFontSetCache = $$2 = this.getFontSetRaw($$0);
        return $$2;
    }

    @Override
    public void close() {
        this.fontSets.values().forEach(FontSet::close);
        this.providersToClose.forEach(GlyphProvider::close);
        this.missingFontSet.close();
    }

    record BuilderId(ResourceLocation fontId, String pack, int index) {
        public String toString() {
            return "(" + String.valueOf(this.fontId) + ": builder #" + this.index + " from pack " + this.pack + ")";
        }
    }

    static final class Preparation
    extends Record {
        private final Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets;
        final List<GlyphProvider> allProviders;

        Preparation(Map<ResourceLocation, List<GlyphProvider.Conditional>> $$0, List<GlyphProvider> $$1) {
            this.fontSets = $$0;
            this.allProviders = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Preparation.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Preparation.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Preparation.class, "fontSets;allProviders", "fontSets", "allProviders"}, this, $$0);
        }

        public Map<ResourceLocation, List<GlyphProvider.Conditional>> fontSets() {
            return this.fontSets;
        }

        public List<GlyphProvider> allProviders() {
            return this.allProviders;
        }
    }

    static final class FontDefinitionFile
    extends Record {
        final List<GlyphProviderDefinition.Conditional> providers;
        public static final Codec<FontDefinitionFile> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)GlyphProviderDefinition.Conditional.CODEC.listOf().fieldOf("providers").forGetter(FontDefinitionFile::providers)).apply((Applicative)$$0, FontDefinitionFile::new));

        private FontDefinitionFile(List<GlyphProviderDefinition.Conditional> $$0) {
            this.providers = $$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontDefinitionFile.class, "providers", "providers"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontDefinitionFile.class, "providers", "providers"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontDefinitionFile.class, "providers", "providers"}, this, $$0);
        }

        public List<GlyphProviderDefinition.Conditional> providers() {
            return this.providers;
        }
    }

    static final class UnresolvedBuilderBundle
    extends Record
    implements DependencySorter.Entry<ResourceLocation> {
        final ResourceLocation fontId;
        private final List<BuilderResult> builders;
        private final Set<ResourceLocation> dependencies;

        public UnresolvedBuilderBundle(ResourceLocation $$0) {
            this($$0, new ArrayList<BuilderResult>(), new HashSet<ResourceLocation>());
        }

        private UnresolvedBuilderBundle(ResourceLocation $$0, List<BuilderResult> $$1, Set<ResourceLocation> $$2) {
            this.fontId = $$0;
            this.builders = $$1;
            this.dependencies = $$2;
        }

        public void add(BuilderId $$0, FontOption.Filter $$1, GlyphProviderDefinition.Reference $$2) {
            this.builders.add(new BuilderResult($$0, $$1, (Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation>)Either.right((Object)$$2.id())));
            this.dependencies.add($$2.id());
        }

        public void add(BuilderId $$0, FontOption.Filter $$1, CompletableFuture<Optional<GlyphProvider>> $$2) {
            this.builders.add(new BuilderResult($$0, $$1, (Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation>)Either.left($$2)));
        }

        private Stream<CompletableFuture<Optional<GlyphProvider>>> listBuilders() {
            return this.builders.stream().flatMap($$0 -> $$0.result.left().stream());
        }

        public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> $$0) {
            ArrayList $$1 = new ArrayList();
            for (BuilderResult $$2 : this.builders) {
                Optional<List<GlyphProvider.Conditional>> $$3 = $$2.resolve($$0);
                if ($$3.isPresent()) {
                    $$1.addAll($$3.get());
                    continue;
                }
                return Optional.empty();
            }
            return Optional.of($$1);
        }

        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> $$0) {
            this.dependencies.forEach($$0);
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> $$0) {
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnresolvedBuilderBundle.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnresolvedBuilderBundle.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnresolvedBuilderBundle.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this, $$0);
        }

        public ResourceLocation fontId() {
            return this.fontId;
        }

        public List<BuilderResult> builders() {
            return this.builders;
        }

        public Set<ResourceLocation> dependencies() {
            return this.dependencies;
        }
    }

    static final class BuilderResult
    extends Record {
        private final BuilderId id;
        private final FontOption.Filter filter;
        final Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result;

        BuilderResult(BuilderId $$0, FontOption.Filter $$1, Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> $$2) {
            this.id = $$0;
            this.filter = $$1;
            this.result = $$2;
        }

        public Optional<List<GlyphProvider.Conditional>> resolve(Function<ResourceLocation, List<GlyphProvider.Conditional>> $$0) {
            return (Optional)this.result.map($$02 -> ((Optional)$$02.join()).map($$0 -> List.of((Object)new GlyphProvider.Conditional((GlyphProvider)$$0, this.filter))), $$1 -> {
                List $$2 = (List)$$0.apply((ResourceLocation)$$1);
                if ($$2 == null) {
                    LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", $$1, (Object)this.id);
                    return Optional.empty();
                }
                return Optional.of($$2.stream().map(this::mergeFilters).toList());
            });
        }

        private GlyphProvider.Conditional mergeFilters(GlyphProvider.Conditional $$0) {
            return new GlyphProvider.Conditional($$0.provider(), this.filter.merge($$0.filter()));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BuilderResult.class, "id;filter;result", "id", "filter", "result"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BuilderResult.class, "id;filter;result", "id", "filter", "result"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BuilderResult.class, "id;filter;result", "id", "filter", "result"}, this, $$0);
        }

        public BuilderId id() {
            return this.id;
        }

        public FontOption.Filter filter() {
            return this.filter;
        }

        public Either<CompletableFuture<Optional<GlyphProvider>>, ResourceLocation> result() {
            return this.result;
        }
    }
}

