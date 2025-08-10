/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup
extends CompositeEntryBase {
    public static final MapCodec<EntryGroup> CODEC = EntryGroup.createCodec(EntryGroup::new);

    EntryGroup(List<LootPoolEntryContainer> $$0, List<LootItemCondition> $$1) {
        super($$0, $$1);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.GROUP;
    }

    @Override
    protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> $$0) {
        return switch ($$0.size()) {
            case 0 -> ALWAYS_TRUE;
            case 1 -> $$0.get(0);
            case 2 -> {
                ComposableEntryContainer $$1 = $$0.get(0);
                ComposableEntryContainer $$2 = $$0.get(1);
                yield ($$2, $$3) -> {
                    $$1.expand($$2, $$3);
                    $$2.expand($$2, $$3);
                    return true;
                };
            }
            default -> ($$1, $$2) -> {
                for (ComposableEntryContainer $$3 : $$0) {
                    $$3.expand($$1, $$2);
                }
                return true;
            };
        };
    }

    public static Builder a(LootPoolEntryContainer.Builder<?> ... $$0) {
        return new Builder($$0);
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
        public Builder append(LootPoolEntryContainer.Builder<?> $$0) {
            this.entries.add((Object)$$0.build());
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new EntryGroup((List<LootPoolEntryContainer>)((Object)this.entries.build()), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntryContainer.Builder getThis() {
            return this.getThis();
        }
    }
}

