/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider
implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;

    public ModelProvider(PackOutput $$0) {
        this.blockStatePathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        ItemInfoCollector $$1 = new ItemInfoCollector();
        BlockStateGeneratorCollector $$2 = new BlockStateGeneratorCollector();
        SimpleModelCollector $$3 = new SimpleModelCollector();
        new BlockModelGenerators($$2, $$1, $$3).run();
        new ItemModelGenerators($$1, $$3).run();
        $$2.validate();
        $$1.finalizeAndValidate();
        return CompletableFuture.allOf($$2.save($$0, this.blockStatePathProvider), $$3.save($$0, this.modelPathProvider), $$1.save($$0, this.itemInfoPathProvider));
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    static class ItemInfoCollector
    implements ItemModelOutput {
        private final Map<Item, ClientItem> itemInfos = new HashMap<Item, ClientItem>();
        private final Map<Item, Item> copies = new HashMap<Item, Item>();

        ItemInfoCollector() {
        }

        @Override
        public void accept(Item $$0, ItemModel.Unbaked $$1) {
            this.register($$0, new ClientItem($$1, ClientItem.Properties.DEFAULT));
        }

        private void register(Item $$0, ClientItem $$1) {
            ClientItem $$2 = this.itemInfos.put($$0, $$1);
            if ($$2 != null) {
                throw new IllegalStateException("Duplicate item model definition for " + String.valueOf($$0));
            }
        }

        @Override
        public void copy(Item $$0, Item $$1) {
            this.copies.put($$1, $$0);
        }

        public void finalizeAndValidate() {
            BuiltInRegistries.ITEM.forEach($$0 -> {
                BlockItem $$1;
                if (this.copies.containsKey($$0)) {
                    return;
                }
                if ($$0 instanceof BlockItem && !this.itemInfos.containsKey($$1 = (BlockItem)$$0)) {
                    ResourceLocation $$2 = ModelLocationUtils.getModelLocation($$1.getBlock());
                    this.accept($$1, ItemModelUtils.plainModel($$2));
                }
            });
            this.copies.forEach(($$0, $$1) -> {
                ClientItem $$2 = this.itemInfos.get($$1);
                if ($$2 == null) {
                    throw new IllegalStateException("Missing donor: " + String.valueOf($$1) + " -> " + String.valueOf($$0));
                }
                this.register((Item)$$0, $$2);
            });
            List $$02 = BuiltInRegistries.ITEM.listElements().filter($$0 -> !this.itemInfos.containsKey($$0.value())).map($$0 -> $$0.key().location()).toList();
            if (!$$02.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + String.valueOf($$02));
            }
        }

        public CompletableFuture<?> save(CachedOutput $$0, PackOutput.PathProvider $$12) {
            return DataProvider.saveAll($$0, ClientItem.CODEC, $$1 -> $$12.json($$1.builtInRegistryHolder().key().location()), this.itemInfos);
        }
    }

    static class BlockStateGeneratorCollector
    implements Consumer<BlockModelDefinitionGenerator> {
        private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap<Block, BlockModelDefinitionGenerator>();

        BlockStateGeneratorCollector() {
        }

        @Override
        public void accept(BlockModelDefinitionGenerator $$0) {
            Block $$1 = $$0.block();
            BlockModelDefinitionGenerator $$2 = this.generators.put($$1, $$0);
            if ($$2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf($$1));
            }
        }

        public void validate() {
            Stream<Holder.Reference> $$02 = BuiltInRegistries.BLOCK.listElements().filter($$0 -> true);
            List $$1 = $$02.filter($$0 -> !this.generators.containsKey($$0.value())).map($$0 -> $$0.key().location()).toList();
            if (!$$1.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf($$1));
            }
        }

        public CompletableFuture<?> save(CachedOutput $$0, PackOutput.PathProvider $$12) {
            Map $$2 = Maps.transformValues(this.generators, BlockModelDefinitionGenerator::create);
            Function<Block, Path> $$3 = $$1 -> $$12.json($$1.builtInRegistryHolder().key().location());
            return DataProvider.saveAll($$0, BlockModelDefinition.CODEC, $$3, $$2);
        }

        @Override
        public /* synthetic */ void accept(Object object) {
            this.accept((BlockModelDefinitionGenerator)object);
        }
    }

    static class SimpleModelCollector
    implements BiConsumer<ResourceLocation, ModelInstance> {
        private final Map<ResourceLocation, ModelInstance> models = new HashMap<ResourceLocation, ModelInstance>();

        SimpleModelCollector() {
        }

        @Override
        public void accept(ResourceLocation $$0, ModelInstance $$1) {
            Supplier $$2 = this.models.put($$0, $$1);
            if ($$2 != null) {
                throw new IllegalStateException("Duplicate model definition for " + String.valueOf($$0));
            }
        }

        public CompletableFuture<?> save(CachedOutput $$0, PackOutput.PathProvider $$1) {
            return DataProvider.saveAll($$0, Supplier::get, $$1::json, this.models);
        }

        @Override
        public /* synthetic */ void accept(Object object, Object object2) {
            this.accept((ResourceLocation)object, (ModelInstance)object2);
        }
    }
}

