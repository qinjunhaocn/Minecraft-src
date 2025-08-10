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
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantedCountIncreaseFunction
extends LootItemConditionalFunction {
    public static final int NO_LIMIT = 0;
    public static final MapCodec<EnchantedCountIncreaseFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> EnchantedCountIncreaseFunction.commonFields($$02).and($$02.group((App)Enchantment.CODEC.fieldOf("enchantment").forGetter($$0 -> $$0.enchantment), (App)NumberProviders.CODEC.fieldOf("count").forGetter($$0 -> $$0.value), (App)Codec.INT.optionalFieldOf("limit", (Object)0).forGetter($$0 -> $$0.limit))).apply((Applicative)$$02, EnchantedCountIncreaseFunction::new));
    private final Holder<Enchantment> enchantment;
    private final NumberProvider value;
    private final int limit;

    EnchantedCountIncreaseFunction(List<LootItemCondition> $$0, Holder<Enchantment> $$1, NumberProvider $$2, int $$3) {
        super($$0);
        this.enchantment = $$1;
        this.value = $$2;
        this.limit = $$3;
    }

    public LootItemFunctionType<EnchantedCountIncreaseFunction> getType() {
        return LootItemFunctions.ENCHANTED_COUNT_INCREASE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Sets.union(ImmutableSet.of(LootContextParams.ATTACKING_ENTITY), this.value.getReferencedContextParams());
    }

    private boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Entity $$2 = $$1.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if ($$2 instanceof LivingEntity) {
            LivingEntity $$3 = (LivingEntity)$$2;
            int $$4 = EnchantmentHelper.getEnchantmentLevel(this.enchantment, $$3);
            if ($$4 == 0) {
                return $$0;
            }
            float $$5 = (float)$$4 * this.value.getFloat($$1);
            $$0.grow(Math.round($$5));
            if (this.hasLimit()) {
                $$0.limitSize(this.limit);
            }
        }
        return $$0;
    }

    public static Builder lootingMultiplier(HolderLookup.Provider $$0, NumberProvider $$1) {
        HolderGetter $$2 = $$0.lookupOrThrow(Registries.ENCHANTMENT);
        return new Builder($$2.getOrThrow(Enchantments.LOOTING), $$1);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Holder<Enchantment> enchantment;
        private final NumberProvider count;
        private int limit = 0;

        public Builder(Holder<Enchantment> $$0, NumberProvider $$1) {
            this.enchantment = $$0;
            this.count = $$1;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder setLimit(int $$0) {
            this.limit = $$0;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantedCountIncreaseFunction(this.getConditions(), this.enchantment, this.count, this.limit);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

