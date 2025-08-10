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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetEnchantmentsFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetEnchantmentsFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetEnchantmentsFunction.commonFields($$02).and($$02.group((App)Codec.unboundedMap(Enchantment.CODEC, NumberProviders.CODEC).optionalFieldOf("enchantments", (Object)Map.of()).forGetter($$0 -> $$0.enchantments), (App)Codec.BOOL.fieldOf("add").orElse((Object)false).forGetter($$0 -> $$0.add))).apply((Applicative)$$02, SetEnchantmentsFunction::new));
    private final Map<Holder<Enchantment>, NumberProvider> enchantments;
    private final boolean add;

    SetEnchantmentsFunction(List<LootItemCondition> $$0, Map<Holder<Enchantment>, NumberProvider> $$1, boolean $$2) {
        super($$0);
        this.enchantments = Map.copyOf($$1);
        this.add = $$2;
    }

    public LootItemFunctionType<SetEnchantmentsFunction> getType() {
        return LootItemFunctions.SET_ENCHANTMENTS;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.enchantments.values().stream().flatMap($$0 -> $$0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$12) {
        if ($$0.is(Items.BOOK)) {
            $$0 = $$0.transmuteCopy(Items.ENCHANTED_BOOK);
        }
        EnchantmentHelper.updateEnchantments($$0, $$1 -> {
            if (this.add) {
                this.enchantments.forEach(($$2, $$3) -> $$1.set((Holder<Enchantment>)$$2, Mth.clamp($$1.getLevel((Holder<Enchantment>)$$2) + $$3.getInt($$12), 0, 255)));
            } else {
                this.enchantments.forEach(($$2, $$3) -> $$1.set((Holder<Enchantment>)$$2, Mth.clamp($$3.getInt($$12), 0, 255)));
            }
        });
        return $$0;
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final ImmutableMap.Builder<Holder<Enchantment>, NumberProvider> enchantments = ImmutableMap.builder();
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean $$0) {
            this.add = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Holder<Enchantment> $$0, NumberProvider $$1) {
            this.enchantments.put($$0, $$1);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetEnchantmentsFunction(this.getConditions(), this.enchantments.build(), this.add);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

