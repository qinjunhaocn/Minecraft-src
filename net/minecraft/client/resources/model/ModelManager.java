/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MissingBlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelGroupCollector;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager
implements PreparableReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES = Map.of((Object)Sheets.BANNER_SHEET, (Object)AtlasIds.BANNER_PATTERNS, (Object)Sheets.BED_SHEET, (Object)AtlasIds.BEDS, (Object)Sheets.CHEST_SHEET, (Object)AtlasIds.CHESTS, (Object)Sheets.SHIELD_SHEET, (Object)AtlasIds.SHIELD_PATTERNS, (Object)Sheets.SIGN_SHEET, (Object)AtlasIds.SIGNS, (Object)Sheets.SHULKER_SHEET, (Object)AtlasIds.SHULKER_BOXES, (Object)Sheets.ARMOR_TRIMS_SHEET, (Object)AtlasIds.ARMOR_TRIMS, (Object)Sheets.DECORATED_POT_SHEET, (Object)AtlasIds.DECORATED_POT, (Object)TextureAtlas.LOCATION_BLOCKS, (Object)AtlasIds.BLOCKS);
    private Map<ResourceLocation, ItemModel> bakedItemStackModels = Map.of();
    private Map<ResourceLocation, ClientItem.Properties> itemProperties = Map.of();
    private final AtlasSet atlases;
    private final BlockModelShaper blockModelShaper;
    private final BlockColors blockColors;
    private EntityModelSet entityModelSet = EntityModelSet.EMPTY;
    private SpecialBlockModelRenderer specialBlockModelRenderer = SpecialBlockModelRenderer.EMPTY;
    private int maxMipmapLevels;
    private ModelBakery.MissingModels missingModels;
    private Object2IntMap<BlockState> modelGroups = Object2IntMaps.emptyMap();

    public ModelManager(TextureManager $$0, BlockColors $$1, int $$2) {
        this.blockColors = $$1;
        this.maxMipmapLevels = $$2;
        this.blockModelShaper = new BlockModelShaper(this);
        this.atlases = new AtlasSet(VANILLA_ATLASES, $$0);
    }

    public BlockStateModel getMissingBlockStateModel() {
        return this.missingModels.block();
    }

    public ItemModel getItemModel(ResourceLocation $$0) {
        return this.bakedItemStackModels.getOrDefault($$0, this.missingModels.item());
    }

    public ClientItem.Properties getItemProperties(ResourceLocation $$0) {
        return this.itemProperties.getOrDefault($$0, ClientItem.Properties.DEFAULT);
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    @Override
    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$1, Executor $$2, Executor $$32) {
        CompletableFuture<EntityModelSet> $$4 = CompletableFuture.supplyAsync(EntityModelSet::vanilla, $$2);
        CompletionStage $$5 = $$4.thenApplyAsync(SpecialBlockModelRenderer::vanilla, $$2);
        CompletableFuture<Map<ResourceLocation, UnbakedModel>> $$6 = ModelManager.loadBlockModels($$1, $$2);
        CompletableFuture<BlockStateModelLoader.LoadedModels> $$7 = BlockStateModelLoader.loadBlockStates($$1, $$2);
        CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> $$8 = ClientItemInfoLoader.scheduleLoad($$1, $$2);
        CompletionStage $$9 = CompletableFuture.allOf($$6, $$7, $$8).thenApplyAsync($$3 -> ModelManager.discoverModelDependencies((Map)$$6.join(), (BlockStateModelLoader.LoadedModels)((Object)((Object)$$7.join())), (ClientItemInfoLoader.LoadedClientInfos)((Object)((Object)$$8.join()))), $$2);
        CompletionStage $$10 = $$7.thenApplyAsync($$0 -> ModelManager.buildModelGroups(this.blockColors, $$0), $$2);
        Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> $$11 = this.atlases.scheduleLoad($$1, this.maxMipmapLevels, $$2);
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])Stream.concat($$11.values().stream(), Stream.of($$9, $$10, $$7, $$8, $$4, $$5, $$6)).toArray(CompletableFuture[]::new)).thenComposeAsync(arg_0 -> ModelManager.lambda$reload$4($$11, (CompletableFuture)$$9, (CompletableFuture)$$10, $$6, $$4, $$7, $$8, (CompletableFuture)$$5, $$2, arg_0), $$2)).thenCompose($$0 -> $$0.readyForUpload.thenApply($$1 -> $$0))).thenCompose($$02::wait)).thenAcceptAsync($$0 -> this.apply((ReloadState)((Object)$$0), Profiler.get()), $$32);
    }

    private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager $$0, Executor $$12) {
        return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources($$0), $$12).thenCompose($$1 -> {
            ArrayList<CompletableFuture<Pair>> $$2 = new ArrayList<CompletableFuture<Pair>>($$1.size());
            for (Map.Entry $$3 : $$1.entrySet()) {
                $$2.add(CompletableFuture.supplyAsync(() -> {
                    Pair pair;
                    block8: {
                        Object $$1 = MODEL_LISTER.fileToId((ResourceLocation)$$3.getKey());
                        BufferedReader $$2 = ((Resource)$$3.getValue()).openAsReader();
                        try {
                            pair = Pair.of((Object)$$1, (Object)BlockModel.fromStream($$2));
                            if ($$2 == null) break block8;
                        } catch (Throwable throwable) {
                            try {
                                if ($$2 != null) {
                                    try {
                                        ((Reader)$$2).close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            } catch (Exception $$3) {
                                LOGGER.error("Failed to load model {}", $$3.getKey(), (Object)$$3);
                                return null;
                            }
                        }
                        ((Reader)$$2).close();
                    }
                    return pair;
                }, $$12));
            }
            return Util.sequence($$2).thenApply($$0 -> (Map)$$0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static ResolvedModels discoverModelDependencies(Map<ResourceLocation, UnbakedModel> $$0, BlockStateModelLoader.LoadedModels $$12, ClientItemInfoLoader.LoadedClientInfos $$2) {
        try (Zone $$3 = Profiler.get().zone("dependencies");){
            ModelDiscovery $$4 = new ModelDiscovery($$0, MissingBlockModel.missingModel());
            $$4.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
            $$12.models().values().forEach($$4::addRoot);
            $$2.contents().values().forEach($$1 -> $$4.addRoot($$1.model()));
            ResolvedModels resolvedModels = new ResolvedModels($$4.missingModel(), $$4.resolve());
            return resolvedModels;
        }
    }

    private static CompletableFuture<ReloadState> loadModels(final Map<ResourceLocation, AtlasSet.StitchResult> $$0, ModelBakery $$1, Object2IntMap<BlockState> $$2, EntityModelSet $$3, SpecialBlockModelRenderer $$4, Executor $$5) {
        CompletableFuture<Void> $$6 = CompletableFuture.allOf((CompletableFuture[])$$0.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new));
        final Multimap $$72 = Multimaps.synchronizedMultimap(HashMultimap.create());
        final Multimap $$8 = Multimaps.synchronizedMultimap(HashMultimap.create());
        return $$1.bakeModels(new SpriteGetter(){
            private final TextureAtlasSprite missingSprite;
            {
                this.missingSprite = ((AtlasSet.StitchResult)$$0.get(TextureAtlas.LOCATION_BLOCKS)).missing();
            }

            @Override
            public TextureAtlasSprite get(Material $$02, ModelDebugName $$1) {
                AtlasSet.StitchResult $$2 = (AtlasSet.StitchResult)$$0.get($$02.atlasLocation());
                TextureAtlasSprite $$3 = $$2.getSprite($$02.texture());
                if ($$3 != null) {
                    return $$3;
                }
                $$72.put($$1.debugName(), $$02);
                return $$2.missing();
            }

            @Override
            public TextureAtlasSprite reportMissingReference(String $$02, ModelDebugName $$1) {
                $$8.put($$1.debugName(), $$02);
                return this.missingSprite;
            }
        }, $$5).thenApply($$7 -> {
            $$72.asMap().forEach(($$02, $$1) -> LOGGER.warn("Missing textures in model {}:\n{}", $$02, (Object)$$1.stream().sorted(Material.COMPARATOR).map($$0 -> "    " + String.valueOf($$0.atlasLocation()) + ":" + String.valueOf($$0.texture())).collect(Collectors.joining("\n"))));
            $$8.asMap().forEach(($$02, $$1) -> LOGGER.warn("Missing texture references in model {}:\n{}", $$02, (Object)$$1.stream().sorted().map($$0 -> "    " + $$0).collect(Collectors.joining("\n"))));
            Map<BlockState, BlockStateModel> $$8 = ModelManager.createBlockStateToModelDispatch($$7.blockStateModels(), $$7.missingModels().block());
            return new ReloadState((ModelBakery.BakingResult)((Object)$$7), $$2, $$8, $$0, $$3, $$4, $$6);
        });
    }

    private static Map<BlockState, BlockStateModel> createBlockStateToModelDispatch(Map<BlockState, BlockStateModel> $$0, BlockStateModel $$1) {
        try (Zone $$22 = Profiler.get().zone("block state dispatch");){
            IdentityHashMap<BlockState, BlockStateModel> $$3 = new IdentityHashMap<BlockState, BlockStateModel>($$0);
            for (Block $$4 : BuiltInRegistries.BLOCK) {
                $$4.getStateDefinition().getPossibleStates().forEach($$2 -> {
                    if ($$0.putIfAbsent((BlockState)$$2, $$1) == null) {
                        LOGGER.warn("Missing model for variant: '{}'", $$2);
                    }
                });
            }
            IdentityHashMap<BlockState, BlockStateModel> identityHashMap = $$3;
            return identityHashMap;
        }
    }

    private static Object2IntMap<BlockState> buildModelGroups(BlockColors $$0, BlockStateModelLoader.LoadedModels $$1) {
        try (Zone $$2 = Profiler.get().zone("block groups");){
            Object2IntMap<BlockState> object2IntMap = ModelGroupCollector.build($$0, $$1);
            return object2IntMap;
        }
    }

    private void apply(ReloadState $$0, ProfilerFiller $$1) {
        $$1.push("upload");
        $$0.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
        ModelBakery.BakingResult $$2 = $$0.bakedModels;
        this.bakedItemStackModels = $$2.itemStackModels();
        this.itemProperties = $$2.itemProperties();
        this.modelGroups = $$0.modelGroups;
        this.missingModels = $$2.missingModels();
        $$1.popPush("cache");
        this.blockModelShaper.replaceCache($$0.modelCache);
        this.specialBlockModelRenderer = $$0.specialBlockModelRenderer;
        this.entityModelSet = $$0.entityModelSet;
        $$1.pop();
    }

    public boolean requiresRender(BlockState $$0, BlockState $$1) {
        int $$3;
        if ($$0 == $$1) {
            return false;
        }
        int $$2 = this.modelGroups.getInt((Object)$$0);
        if ($$2 != -1 && $$2 == ($$3 = this.modelGroups.getInt((Object)$$1))) {
            FluidState $$5;
            FluidState $$4 = $$0.getFluidState();
            return $$4 != ($$5 = $$1.getFluidState());
        }
        return true;
    }

    public TextureAtlas getAtlas(ResourceLocation $$0) {
        return this.atlases.getAtlas($$0);
    }

    @Override
    public void close() {
        this.atlases.close();
    }

    public void updateMaxMipLevel(int $$0) {
        this.maxMipmapLevels = $$0;
    }

    public Supplier<SpecialBlockModelRenderer> specialBlockModelRenderer() {
        return () -> this.specialBlockModelRenderer;
    }

    public Supplier<EntityModelSet> entityModels() {
        return () -> this.entityModelSet;
    }

    private static /* synthetic */ CompletionStage lambda$reload$4(Map $$02, CompletableFuture $$1, CompletableFuture $$2, CompletableFuture $$3, CompletableFuture $$4, CompletableFuture $$5, CompletableFuture $$6, CompletableFuture $$7, Executor $$8, Void $$9) {
        Map<ResourceLocation, AtlasSet.StitchResult> $$10 = Util.mapValues($$02, CompletableFuture::join);
        ResolvedModels $$11 = (ResolvedModels)((Object)$$1.join());
        Object2IntMap $$12 = (Object2IntMap)$$2.join();
        Sets.SetView $$13 = Sets.difference(((Map)$$3.join()).keySet(), $$11.models.keySet());
        if (!$$13.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", (Object)$$13.stream().sorted().map($$0 -> "\t" + String.valueOf($$0) + "\n").collect(Collectors.joining()));
        }
        ModelBakery $$14 = new ModelBakery((EntityModelSet)$$4.join(), ((BlockStateModelLoader.LoadedModels)((Object)$$5.join())).models(), ((ClientItemInfoLoader.LoadedClientInfos)((Object)$$6.join())).contents(), $$11.models(), $$11.missing());
        return ModelManager.loadModels($$10, $$14, (Object2IntMap<BlockState>)$$12, (EntityModelSet)$$4.join(), (SpecialBlockModelRenderer)$$7.join(), $$8);
    }

    static final class ResolvedModels
    extends Record {
        private final ResolvedModel missing;
        final Map<ResourceLocation, ResolvedModel> models;

        ResolvedModels(ResolvedModel $$0, Map<ResourceLocation, ResolvedModel> $$1) {
            this.missing = $$0;
            this.models = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ResolvedModels.class, "missing;models", "missing", "models"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ResolvedModels.class, "missing;models", "missing", "models"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ResolvedModels.class, "missing;models", "missing", "models"}, this, $$0);
        }

        public ResolvedModel missing() {
            return this.missing;
        }

        public Map<ResourceLocation, ResolvedModel> models() {
            return this.models;
        }
    }

    static final class ReloadState
    extends Record {
        final ModelBakery.BakingResult bakedModels;
        final Object2IntMap<BlockState> modelGroups;
        final Map<BlockState, BlockStateModel> modelCache;
        final Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations;
        final EntityModelSet entityModelSet;
        final SpecialBlockModelRenderer specialBlockModelRenderer;
        final CompletableFuture<Void> readyForUpload;

        ReloadState(ModelBakery.BakingResult $$0, Object2IntMap<BlockState> $$1, Map<BlockState, BlockStateModel> $$2, Map<ResourceLocation, AtlasSet.StitchResult> $$3, EntityModelSet $$4, SpecialBlockModelRenderer $$5, CompletableFuture<Void> $$6) {
            this.bakedModels = $$0;
            this.modelGroups = $$1;
            this.modelCache = $$2;
            this.atlasPreparations = $$3;
            this.entityModelSet = $$4;
            this.specialBlockModelRenderer = $$5;
            this.readyForUpload = $$6;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReloadState.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReloadState.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReloadState.class, "bakedModels;modelGroups;modelCache;atlasPreparations;entityModelSet;specialBlockModelRenderer;readyForUpload", "bakedModels", "modelGroups", "modelCache", "atlasPreparations", "entityModelSet", "specialBlockModelRenderer", "readyForUpload"}, this, $$0);
        }

        public ModelBakery.BakingResult bakedModels() {
            return this.bakedModels;
        }

        public Object2IntMap<BlockState> modelGroups() {
            return this.modelGroups;
        }

        public Map<BlockState, BlockStateModel> modelCache() {
            return this.modelCache;
        }

        public Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations() {
            return this.atlasPreparations;
        }

        public EntityModelSet entityModelSet() {
            return this.entityModelSet;
        }

        public SpecialBlockModelRenderer specialBlockModelRenderer() {
            return this.specialBlockModelRenderer;
        }

        public CompletableFuture<Void> readyForUpload() {
            return this.readyForUpload;
        }
    }
}

