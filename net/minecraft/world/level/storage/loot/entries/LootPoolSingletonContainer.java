/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolSingletonContainer
extends LootPoolEntryContainer {
    public static final int DEFAULT_WEIGHT = 1;
    public static final int DEFAULT_QUALITY = 0;
    protected final int weight;
    protected final int quality;
    protected final List<LootItemFunction> functions;
    final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final LootPoolEntry entry = new EntryBase(){

        @Override
        public void createItemStack(Consumer<ItemStack> $$0, LootContext $$1) {
            LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, $$0, $$1), $$1);
        }
    };

    protected LootPoolSingletonContainer(int $$0, int $$1, List<LootItemCondition> $$2, List<LootItemFunction> $$3) {
        super($$2);
        this.weight = $$0;
        this.quality = $$1;
        this.functions = $$3;
        this.compositeFunction = LootItemFunctions.compose($$3);
    }

    protected static <T extends LootPoolSingletonContainer> Products.P4<RecordCodecBuilder.Mu<T>, Integer, Integer, List<LootItemCondition>, List<LootItemFunction>> singletonFields(RecordCodecBuilder.Instance<T> $$02) {
        return $$02.group((App)Codec.INT.optionalFieldOf("weight", (Object)1).forGetter($$0 -> $$0.weight), (App)Codec.INT.optionalFieldOf("quality", (Object)0).forGetter($$0 -> $$0.quality)).and(LootPoolSingletonContainer.commonFields($$02).t1()).and((App)LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", (Object)List.of()).forGetter($$0 -> $$0.functions));
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.functions.size(); ++$$1) {
            this.functions.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("functions", $$1)));
        }
    }

    protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (this.canRun($$0)) {
            $$1.accept(this.entry);
            return true;
        }
        return false;
    }

    public static Builder<?> simpleBuilder(EntryConstructor $$0) {
        return new DummyBuilder($$0);
    }

    static class DummyBuilder
    extends Builder<DummyBuilder> {
        private final EntryConstructor constructor;

        public DummyBuilder(EntryConstructor $$0) {
            this.constructor = $$0;
        }

        @Override
        protected DummyBuilder getThis() {
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected /* synthetic */ LootPoolEntryContainer.Builder getThis() {
            return this.getThis();
        }
    }

    @FunctionalInterface
    protected static interface EntryConstructor {
        public LootPoolSingletonContainer build(int var1, int var2, List<LootItemCondition> var3, List<LootItemFunction> var4);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntryContainer.Builder<T>
    implements FunctionUserBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();

        @Override
        public T apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return (T)((Builder)this.getThis());
        }

        protected List<LootItemFunction> getFunctions() {
            return this.functions.build();
        }

        public T setWeight(int $$0) {
            this.weight = $$0;
            return (T)((Builder)this.getThis());
        }

        public T setQuality(int $$0) {
            this.quality = $$0;
            return (T)((Builder)this.getThis());
        }

        @Override
        public /* synthetic */ FunctionUserBuilder unwrap() {
            return (FunctionUserBuilder)((Object)super.unwrap());
        }

        @Override
        public /* synthetic */ FunctionUserBuilder apply(LootItemFunction.Builder builder) {
            return this.apply(builder);
        }
    }

    protected abstract class EntryBase
    implements LootPoolEntry {
        protected EntryBase() {
        }

        @Override
        public int getWeight(float $$0) {
            return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * $$0), 0);
        }
    }
}

