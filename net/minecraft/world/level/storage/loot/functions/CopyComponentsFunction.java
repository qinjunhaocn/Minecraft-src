/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction
extends LootItemConditionalFunction {
    public static final MapCodec<CopyComponentsFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> CopyComponentsFunction.commonFields($$02).and($$02.group((App)Source.CODEC.fieldOf("source").forGetter($$0 -> $$0.source), (App)DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter($$0 -> $$0.include), (App)DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter($$0 -> $$0.exclude))).apply((Applicative)$$02, CopyComponentsFunction::new));
    private final Source source;
    private final Optional<List<DataComponentType<?>>> include;
    private final Optional<List<DataComponentType<?>>> exclude;
    private final Predicate<DataComponentType<?>> bakedPredicate;

    CopyComponentsFunction(List<LootItemCondition> $$0, Source $$13, Optional<List<DataComponentType<?>>> $$2, Optional<List<DataComponentType<?>>> $$3) {
        super($$0);
        this.source = $$13;
        this.include = $$2.map((Function<List, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, copyOf(java.util.Collection ), (Ljava/util/List;)Ljava/util/List;)());
        this.exclude = $$3.map((Function<List, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, copyOf(java.util.Collection ), (Ljava/util/List;)Ljava/util/List;)());
        ArrayList $$4 = new ArrayList(2);
        $$3.ifPresent($$12 -> $$4.add($$1 -> !$$12.contains($$1)));
        $$2.ifPresent($$1 -> $$4.add($$1::contains));
        this.bakedPredicate = Util.allOf($$4);
    }

    public LootItemFunctionType<CopyComponentsFunction> getType() {
        return LootItemFunctions.COPY_COMPONENTS;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        DataComponentMap $$2 = this.source.get($$1);
        $$0.applyComponents($$2.filter(this.bakedPredicate));
        return $$0;
    }

    public static Builder copyComponents(Source $$0) {
        return new Builder($$0);
    }

    public static final class Source
    extends Enum<Source>
    implements StringRepresentable {
        public static final /* enum */ Source BLOCK_ENTITY = new Source("block_entity");
        public static final Codec<Source> CODEC;
        private final String name;
        private static final /* synthetic */ Source[] $VALUES;

        public static Source[] values() {
            return (Source[])$VALUES.clone();
        }

        public static Source valueOf(String $$0) {
            return Enum.valueOf(Source.class, $$0);
        }

        private Source(String $$0) {
            this.name = $$0;
        }

        public DataComponentMap get(LootContext $$0) {
            switch (this.ordinal()) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: 
            }
            BlockEntity $$1 = $$0.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            return $$1 != null ? $$1.collectComponents() : DataComponentMap.EMPTY;
        }

        public Set<ContextKey<?>> getReferencedContextParams() {
            switch (this.ordinal()) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: 
            }
            return Set.of(LootContextParams.BLOCK_ENTITY);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Source[] b() {
            return new Source[]{BLOCK_ENTITY};
        }

        static {
            $VALUES = Source.b();
            CODEC = StringRepresentable.fromValues(Source::values);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Source source;
        private Optional<ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
        private Optional<ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

        Builder(Source $$0) {
            this.source = $$0;
        }

        public Builder include(DataComponentType<?> $$0) {
            if (this.include.isEmpty()) {
                this.include = Optional.of(ImmutableList.builder());
            }
            this.include.get().add((Object)$$0);
            return this;
        }

        public Builder exclude(DataComponentType<?> $$0) {
            if (this.exclude.isEmpty()) {
                this.exclude = Optional.of(ImmutableList.builder());
            }
            this.exclude.get().add((Object)$$0);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyComponentsFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

