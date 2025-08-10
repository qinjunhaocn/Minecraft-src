/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P1
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolEntryContainer
implements ComposableEntryContainer {
    protected final List<LootItemCondition> conditions;
    private final Predicate<LootContext> compositeCondition;

    protected LootPoolEntryContainer(List<LootItemCondition> $$0) {
        this.conditions = $$0;
        this.compositeCondition = Util.allOf($$0);
    }

    protected static <T extends LootPoolEntryContainer> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> $$02) {
        return $$02.group((App)LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", (Object)List.of()).forGetter($$0 -> $$0.conditions));
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.conditions.size(); ++$$1) {
            this.conditions.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("conditions", $$1)));
        }
    }

    protected final boolean canRun(LootContext $$0) {
        return this.compositeCondition.test($$0);
    }

    public abstract LootPoolEntryType getType();

    public static abstract class Builder<T extends Builder<T>>
    implements ConditionUserBuilder<T> {
        private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();

        protected abstract T getThis();

        @Override
        public T when(LootItemCondition.Builder $$0) {
            this.conditions.add((Object)$$0.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected List<LootItemCondition> getConditions() {
            return this.conditions.build();
        }

        public AlternativesEntry.Builder otherwise(Builder<?> $$0) {
            return new AlternativesEntry.Builder(this, $$0);
        }

        public EntryGroup.Builder append(Builder<?> $$0) {
            return new EntryGroup.Builder(this, $$0);
        }

        public SequentialEntry.Builder then(Builder<?> $$0) {
            return new SequentialEntry.Builder(this, $$0);
        }

        public abstract LootPoolEntryContainer build();

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

