/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState
extends LootItemConditionalFunction {
    public static final MapCodec<CopyBlockState> CODEC = RecordCodecBuilder.mapCodec($$02 -> CopyBlockState.commonFields($$02).and($$02.group((App)BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter($$0 -> $$0.block), (App)Codec.STRING.listOf().fieldOf("properties").forGetter($$0 -> $$0.properties.stream().map(Property::getName).toList()))).apply((Applicative)$$02, CopyBlockState::new));
    private final Holder<Block> block;
    private final Set<Property<?>> properties;

    CopyBlockState(List<LootItemCondition> $$0, Holder<Block> $$1, Set<Property<?>> $$2) {
        super($$0);
        this.block = $$1;
        this.properties = $$2;
    }

    private CopyBlockState(List<LootItemCondition> $$0, Holder<Block> $$1, List<String> $$2) {
        this($$0, $$1, $$2.stream().map($$1.value().getStateDefinition()::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public LootItemFunctionType<CopyBlockState> getType() {
        return LootItemFunctions.COPY_STATE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$12) {
        BlockState $$2 = $$12.getOptionalParameter(LootContextParams.BLOCK_STATE);
        if ($$2 != null) {
            $$0.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, $$1 -> {
                for (Property<?> $$2 : this.properties) {
                    if (!$$2.hasProperty($$2)) continue;
                    $$1 = $$1.with($$2, $$2);
                }
                return $$1;
            });
        }
        return $$0;
    }

    public static Builder copyState(Block $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Holder<Block> block;
        private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

        Builder(Block $$0) {
            this.block = $$0.builtInRegistryHolder();
        }

        public Builder copy(Property<?> $$0) {
            if (!this.block.value().getStateDefinition().getProperties().contains($$0)) {
                throw new IllegalStateException("Property " + String.valueOf($$0) + " is not present on block " + String.valueOf(this.block));
            }
            this.properties.add((Object)$$0);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyBlockState(this.getConditions(), this.block, (Set<Property<?>>)((Object)this.properties.build()));
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

