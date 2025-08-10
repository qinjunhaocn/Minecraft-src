/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10).mapToObj($$0 -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + $$0)).collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream().map($$02 -> $$02.withPath($$0 -> "textures/" + $$0 + ".png")).collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    private final EntityModelSet entityModelSet;
    private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels;
    private final Map<ResourceLocation, ClientItem> clientInfos;
    final Map<ResourceLocation, ResolvedModel> resolvedModels;
    final ResolvedModel missingModel;

    public ModelBakery(EntityModelSet $$0, Map<BlockState, BlockStateModel.UnbakedRoot> $$1, Map<ResourceLocation, ClientItem> $$2, Map<ResourceLocation, ResolvedModel> $$3, ResolvedModel $$4) {
        this.entityModelSet = $$0;
        this.unbakedBlockStateModels = $$1;
        this.clientInfos = $$2;
        this.resolvedModels = $$3;
        this.missingModel = $$4;
    }

    public CompletableFuture<BakingResult> bakeModels(SpriteGetter $$0, Executor $$12) {
        MissingModels $$22 = MissingModels.bake(this.missingModel, $$0);
        ModelBakerImpl $$32 = new ModelBakerImpl($$0);
        CompletableFuture<Map<BlockState, BlockStateModel>> $$4 = ParallelMapTransform.schedule(this.unbakedBlockStateModels, ($$1, $$2) -> {
            try {
                return $$2.bake((BlockState)$$1, $$32);
            } catch (Exception $$3) {
                LOGGER.warn("Unable to bake model: '{}': {}", $$1, (Object)$$3);
                return null;
            }
        }, $$12);
        CompletableFuture<Map<ResourceLocation, ItemModel>> $$5 = ParallelMapTransform.schedule(this.clientInfos, ($$2, $$3) -> {
            try {
                return $$3.model().bake(new ItemModel.BakingContext($$32, this.entityModelSet, $$1.item, $$3.registrySwapper()));
            } catch (Exception $$4) {
                LOGGER.warn("Unable to bake item model: '{}'", $$2, (Object)$$4);
                return null;
            }
        }, $$12);
        HashMap $$6 = new HashMap(this.clientInfos.size());
        this.clientInfos.forEach(($$1, $$2) -> {
            ClientItem.Properties $$3 = $$2.properties();
            if (!$$3.equals((Object)ClientItem.Properties.DEFAULT)) {
                $$6.put($$1, $$3);
            }
        });
        return $$4.thenCombine($$5, ($$2, $$3) -> new BakingResult($$22, (Map<BlockState, BlockStateModel>)$$2, (Map<ResourceLocation, ItemModel>)$$3, $$6));
    }

    public static final class MissingModels
    extends Record {
        private final BlockStateModel block;
        final ItemModel item;

        public MissingModels(BlockStateModel $$0, ItemModel $$1) {
            this.block = $$0;
            this.item = $$1;
        }

        public static MissingModels bake(ResolvedModel $$0, final SpriteGetter $$1) {
            ModelBaker $$2 = new ModelBaker(){

                @Override
                public ResolvedModel getModel(ResourceLocation $$0) {
                    throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf($$0));
                }

                @Override
                public <T> T compute(ModelBaker.SharedOperationKey<T> $$0) {
                    return $$0.compute(this);
                }

                @Override
                public SpriteGetter sprites() {
                    return $$1;
                }
            };
            TextureSlots $$3 = $$0.getTopTextureSlots();
            boolean $$4 = $$0.getTopAmbientOcclusion();
            boolean $$5 = $$0.getTopGuiLight().lightLikeBlock();
            ItemTransforms $$6 = $$0.getTopTransforms();
            QuadCollection $$7 = $$0.bakeTopGeometry($$3, $$2, BlockModelRotation.X0_Y0);
            TextureAtlasSprite $$8 = $$0.resolveParticleSprite($$3, $$2);
            SingleVariant $$9 = new SingleVariant(new SimpleModelWrapper($$7, $$4, $$8));
            MissingItemModel $$10 = new MissingItemModel($$7.getAll(), new ModelRenderProperties($$5, $$8, $$6));
            return new MissingModels($$9, $$10);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{MissingModels.class, "block;item", "block", "item"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MissingModels.class, "block;item", "block", "item"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MissingModels.class, "block;item", "block", "item"}, this, $$0);
        }

        public BlockStateModel block() {
            return this.block;
        }

        public ItemModel item() {
            return this.item;
        }
    }

    class ModelBakerImpl
    implements ModelBaker {
        private final SpriteGetter sprites;
        private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache = new ConcurrentHashMap<ModelBaker.SharedOperationKey<Object>, Object>();
        private final Function<ModelBaker.SharedOperationKey<Object>, Object> cacheComputeFunction = $$0 -> $$0.compute(this);

        ModelBakerImpl(SpriteGetter $$02) {
            this.sprites = $$02;
        }

        @Override
        public SpriteGetter sprites() {
            return this.sprites;
        }

        @Override
        public ResolvedModel getModel(ResourceLocation $$0) {
            ResolvedModel $$1 = ModelBakery.this.resolvedModels.get($$0);
            if ($$1 == null) {
                LOGGER.warn("Requested a model that was not discovered previously: {}", (Object)$$0);
                return ModelBakery.this.missingModel;
            }
            return $$1;
        }

        @Override
        public <T> T compute(ModelBaker.SharedOperationKey<T> $$0) {
            return (T)this.operationCache.computeIfAbsent($$0, this.cacheComputeFunction);
        }
    }

    public record BakingResult(MissingModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<ResourceLocation, ItemModel> itemStackModels, Map<ResourceLocation, ClientItem.Properties> itemProperties) {
    }
}

