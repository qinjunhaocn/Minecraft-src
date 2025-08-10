/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<EnchantRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> EnchantRandomlyFunction.commonFields($$02).and($$02.group((App)RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter($$0 -> $$0.options), (App)Codec.BOOL.optionalFieldOf("only_compatible", (Object)true).forGetter($$0 -> $$0.onlyCompatible))).apply((Applicative)$$02, EnchantRandomlyFunction::new));
    private final Optional<HolderSet<Enchantment>> options;
    private final boolean onlyCompatible;

    EnchantRandomlyFunction(List<LootItemCondition> $$0, Optional<HolderSet<Enchantment>> $$1, boolean $$2) {
        super($$0);
        this.options = $$1;
        this.onlyCompatible = $$2;
    }

    public LootItemFunctionType<EnchantRandomlyFunction> getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        RandomSource $$22 = $$1.getRandom();
        boolean $$3 = $$0.is(Items.BOOK);
        boolean $$4 = !$$3 && this.onlyCompatible;
        Stream<Holder> $$5 = this.options.map(HolderSet::stream).orElseGet(() -> $$1.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity())).filter($$2 -> !$$4 || ((Enchantment)((Object)((Object)$$2.value()))).canEnchant($$0));
        List $$6 = $$5.toList();
        Optional $$7 = Util.getRandomSafe($$6, $$22);
        if ($$7.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)$$0);
            return $$0;
        }
        return EnchantRandomlyFunction.enchantItem($$0, (Holder)$$7.get(), $$22);
    }

    private static ItemStack enchantItem(ItemStack $$0, Holder<Enchantment> $$1, RandomSource $$2) {
        int $$3 = Mth.nextInt($$2, $$1.value().getMinLevel(), $$1.value().getMaxLevel());
        if ($$0.is(Items.BOOK)) {
            $$0 = new ItemStack(Items.ENCHANTED_BOOK);
        }
        $$0.enchant($$1, $$3);
        return $$0;
    }

    public static Builder randomEnchantment() {
        return new Builder();
    }

    public static Builder randomApplicableEnchantment(HolderLookup.Provider $$0) {
        return EnchantRandomlyFunction.randomEnchantment().withOneOf($$0.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private Optional<HolderSet<Enchantment>> options = Optional.empty();
        private boolean onlyCompatible = true;

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Holder<Enchantment> $$0) {
            this.options = Optional.of(HolderSet.a($$0));
            return this;
        }

        public Builder withOneOf(HolderSet<Enchantment> $$0) {
            this.options = Optional.of($$0);
            return this;
        }

        public Builder allowingIncompatibleEnchantments() {
            this.onlyCompatible = false;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantRandomlyFunction(this.getConditions(), this.options, this.onlyCompatible);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

