/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class AllOfCondition
extends CompositeLootItemCondition {
    public static final MapCodec<AllOfCondition> CODEC = AllOfCondition.createCodec(AllOfCondition::new);
    public static final Codec<AllOfCondition> INLINE_CODEC = AllOfCondition.createInlineCodec(AllOfCondition::new);

    AllOfCondition(List<LootItemCondition> $$0) {
        super($$0, Util.allOf($$0));
    }

    public static AllOfCondition allOf(List<LootItemCondition> $$0) {
        return new AllOfCondition(List.copyOf($$0));
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ALL_OF;
    }

    public static Builder a(LootItemCondition.Builder ... $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends CompositeLootItemCondition.Builder {
        public Builder(LootItemCondition.Builder ... $$0) {
            super($$0);
        }

        @Override
        public Builder and(LootItemCondition.Builder $$0) {
            this.addTerm($$0);
            return this;
        }

        @Override
        protected LootItemCondition create(List<LootItemCondition> $$0) {
            return new AllOfCondition($$0);
        }
    }
}

