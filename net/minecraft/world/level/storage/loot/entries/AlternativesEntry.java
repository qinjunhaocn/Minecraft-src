/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AlternativesEntry
extends CompositeEntryBase {
    public static final MapCodec<AlternativesEntry> CODEC = AlternativesEntry.createCodec(AlternativesEntry::new);
    public static final ProblemReporter.Problem UNREACHABLE_PROBLEM = new ProblemReporter.Problem(){

        @Override
        public String description() {
            return "Unreachable entry!";
        }
    };

    AlternativesEntry(List<LootPoolEntryContainer> $$0, List<LootItemCondition> $$1) {
        super($$0, $$1);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ALTERNATIVES;
    }

    @Override
    protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> $$0) {
        return switch ($$0.size()) {
            case 0 -> ALWAYS_FALSE;
            case 1 -> $$0.get(0);
            case 2 -> $$0.get(0).or($$0.get(1));
            default -> ($$1, $$2) -> {
                for (ComposableEntryContainer $$3 : $$0) {
                    if (!$$3.expand($$1, $$2)) continue;
                    return true;
                }
                return false;
            };
        };
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        for (int $$1 = 0; $$1 < this.children.size() - 1; ++$$1) {
            if (!((LootPoolEntryContainer)this.children.get((int)$$1)).conditions.isEmpty()) continue;
            $$0.reportProblem(UNREACHABLE_PROBLEM);
        }
    }

    public static Builder a(LootPoolEntryContainer.Builder<?> ... $$0) {
        return new Builder($$0);
    }

    public static <E> Builder alternatives(Collection<E> $$0, Function<E, LootPoolEntryContainer.Builder<?>> $$1) {
        return new Builder((LootPoolEntryContainer.Builder[])$$0.stream().map($$1::apply).toArray(LootPoolEntryContainer.Builder[]::new));
    }

    public static class Builder
    extends LootPoolEntryContainer.Builder<Builder> {
        private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();

        public Builder(LootPoolEntryContainer.Builder<?> ... $$0) {
            for (LootPoolEntryContainer.Builder<?> $$1 : $$0) {
                this.entries.add((Object)$$1.build());
            }
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public Builder otherwise(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new AlternativesEntry((List<LootPoolEntryContainer>)((Object)this.entries.build()), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntryContainer.Builder getThis() {
            return this.getThis();
        }
    }
}

