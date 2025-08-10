/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    public static final Codec<LootPool> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter($$0 -> $$0.entries), (App)LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", (Object)List.of()).forGetter($$0 -> $$0.conditions), (App)LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", (Object)List.of()).forGetter($$0 -> $$0.functions), (App)NumberProviders.CODEC.fieldOf("rolls").forGetter($$0 -> $$0.rolls), (App)NumberProviders.CODEC.fieldOf("bonus_rolls").orElse((Object)ConstantValue.exactly(0.0f)).forGetter($$0 -> $$0.bonusRolls)).apply((Applicative)$$02, LootPool::new));
    private final List<LootPoolEntryContainer> entries;
    private final List<LootItemCondition> conditions;
    private final Predicate<LootContext> compositeCondition;
    private final List<LootItemFunction> functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
    private final NumberProvider rolls;
    private final NumberProvider bonusRolls;

    LootPool(List<LootPoolEntryContainer> $$0, List<LootItemCondition> $$1, List<LootItemFunction> $$2, NumberProvider $$3, NumberProvider $$4) {
        this.entries = $$0;
        this.conditions = $$1;
        this.compositeCondition = Util.allOf($$1);
        this.functions = $$2;
        this.compositeFunction = LootItemFunctions.compose($$2);
        this.rolls = $$3;
        this.bonusRolls = $$4;
    }

    private void addRandomItem(Consumer<ItemStack> $$0, LootContext $$1) {
        RandomSource $$2 = $$1.getRandom();
        ArrayList<LootPoolEntry> $$32 = Lists.newArrayList();
        MutableInt $$4 = new MutableInt();
        for (LootPoolEntryContainer $$5 : this.entries) {
            $$5.expand($$1, $$3 -> {
                int $$4 = $$3.getWeight($$1.getLuck());
                if ($$4 > 0) {
                    $$32.add($$3);
                    $$4.add($$4);
                }
            });
        }
        int $$6 = $$32.size();
        if ($$4.intValue() == 0 || $$6 == 0) {
            return;
        }
        if ($$6 == 1) {
            ((LootPoolEntry)$$32.get(0)).createItemStack($$0, $$1);
            return;
        }
        int $$7 = $$2.nextInt($$4.intValue());
        for (LootPoolEntry $$8 : $$32) {
            if (($$7 -= $$8.getWeight($$1.getLuck())) >= 0) continue;
            $$8.createItemStack($$0, $$1);
            return;
        }
    }

    public void addRandomItems(Consumer<ItemStack> $$0, LootContext $$1) {
        if (!this.compositeCondition.test($$1)) {
            return;
        }
        Consumer<ItemStack> $$2 = LootItemFunction.decorate(this.compositeFunction, $$0, $$1);
        int $$3 = this.rolls.getInt($$1) + Mth.floor(this.bonusRolls.getFloat($$1) * $$1.getLuck());
        for (int $$4 = 0; $$4 < $$3; ++$$4) {
            this.addRandomItem($$2, $$1);
        }
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.conditions.size(); ++$$1) {
            this.conditions.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("conditions", $$1)));
        }
        for (int $$2 = 0; $$2 < this.functions.size(); ++$$2) {
            this.functions.get($$2).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("functions", $$2)));
        }
        for (int $$3 = 0; $$3 < this.entries.size(); ++$$3) {
            this.entries.get($$3).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("entries", $$3)));
        }
        this.rolls.validate($$0.forChild(new ProblemReporter.FieldPathElement("rolls")));
        this.bonusRolls.validate($$0.forChild(new ProblemReporter.FieldPathElement("bonus_rolls")));
    }

    public static Builder lootPool() {
        return new Builder();
    }

    public static class Builder
    implements FunctionUserBuilder<Builder>,
    ConditionUserBuilder<Builder> {
        private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
        private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();
        private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();
        private NumberProvider rolls = ConstantValue.exactly(1.0f);
        private NumberProvider bonusRolls = ConstantValue.exactly(0.0f);

        public Builder setRolls(NumberProvider $$0) {
            this.rolls = $$0;
            return this;
        }

        @Override
        public Builder unwrap() {
            return this;
        }

        public Builder setBonusRolls(NumberProvider $$0) {
            this.bonusRolls = $$0;
            return this;
        }

        public Builder add(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder when(LootItemCondition.Builder $$0) {
            this.conditions.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return this;
        }

        public LootPool build() {
            return new LootPool((List<LootPoolEntryContainer>)((Object)this.entries.build()), (List<LootItemCondition>)((Object)this.conditions.build()), (List<LootItemFunction>)((Object)this.functions.build()), this.rolls, this.bonusRolls);
        }

        @Override
        public /* synthetic */ FunctionUserBuilder unwrap() {
            return this.unwrap();
        }

        @Override
        public /* synthetic */ FunctionUserBuilder apply(LootItemFunction.Builder builder) {
            return this.apply(builder);
        }

        @Override
        public /* synthetic */ ConditionUserBuilder unwrap() {
            return this.unwrap();
        }

        @Override
        public /* synthetic */ ConditionUserBuilder when(LootItemCondition.Builder builder) {
            return this.when(builder);
        }
    }
}

