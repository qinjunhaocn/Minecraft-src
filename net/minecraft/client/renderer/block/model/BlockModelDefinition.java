/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.VariantSelector;
import net.minecraft.client.renderer.block.model.multipart.MultiPartModel;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public record BlockModelDefinition(Optional<SimpleModelSelectors> simpleModels, Optional<MultiPartDefinition> multiPart) {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<BlockModelDefinition> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)SimpleModelSelectors.CODEC.optionalFieldOf("variants").forGetter(BlockModelDefinition::simpleModels), (App)MultiPartDefinition.CODEC.optionalFieldOf("multipart").forGetter(BlockModelDefinition::multiPart)).apply((Applicative)$$0, BlockModelDefinition::new)).validate($$0 -> {
        if ($$0.simpleModels().isEmpty() && $$0.multiPart().isEmpty()) {
            return DataResult.error(() -> "Neither 'variants' nor 'multipart' found");
        }
        return DataResult.success((Object)$$0);
    });

    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> $$0, Supplier<String> $$1) {
        IdentityHashMap<BlockState, BlockStateModel.UnbakedRoot> $$22 = new IdentityHashMap<BlockState, BlockStateModel.UnbakedRoot>();
        this.simpleModels.ifPresent($$3 -> $$3.instantiate($$0, $$1, ($$1, $$2) -> {
            Object $$3 = $$22.put((BlockState)$$1, (BlockStateModel.UnbakedRoot)$$2);
            if ($$3 != null) {
                throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf($$1));
            }
        }));
        this.multiPart.ifPresent($$2 -> {
            ImmutableList $$3 = $$0.getPossibleStates();
            MultiPartModel.Unbaked $$4 = $$2.instantiate($$0);
            for (BlockState $$5 : $$3) {
                $$22.putIfAbsent($$5, $$4);
            }
        });
        return $$22;
    }

    public record MultiPartDefinition(List<Selector> selectors) {
        public static final Codec<MultiPartDefinition> CODEC = ExtraCodecs.nonEmptyList(Selector.CODEC.listOf()).xmap(MultiPartDefinition::new, MultiPartDefinition::selectors);

        public MultiPartModel.Unbaked instantiate(StateDefinition<Block, BlockState> $$0) {
            ImmutableList.Builder $$1 = ImmutableList.builderWithExpectedSize(this.selectors.size());
            for (Selector $$2 : this.selectors) {
                $$1.add(new MultiPartModel.Selector<BlockStateModel.Unbaked>($$2.instantiate($$0), $$2.variant()));
            }
            return new MultiPartModel.Unbaked((List<MultiPartModel.Selector<BlockStateModel.Unbaked>>)((Object)$$1.build()));
        }
    }

    public record SimpleModelSelectors(Map<String, BlockStateModel.Unbaked> models) {
        public static final Codec<SimpleModelSelectors> CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap((Codec)Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(SimpleModelSelectors::new, SimpleModelSelectors::models);

        public void instantiate(StateDefinition<Block, BlockState> $$0, Supplier<String> $$1, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> $$2) {
            this.models.forEach(($$3, $$4) -> {
                try {
                    Predicate $$5 = VariantSelector.predicate($$0, $$3);
                    BlockStateModel.UnbakedRoot $$6 = $$4.asRoot();
                    for (BlockState $$7 : $$0.getPossibleStates()) {
                        if (!$$5.test($$7)) continue;
                        $$2.accept($$7, $$6);
                    }
                } catch (Exception $$8) {
                    LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", $$1.get(), $$3, $$8.getMessage());
                }
            });
        }
    }
}

