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
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public record LootItemRandomChanceWithEnchantedBonusCondition(float unenchantedChance, LevelBasedValue enchantedChance, Holder<Enchantment> enchantment) implements LootItemCondition
{
    public static final MapCodec<LootItemRandomChanceWithEnchantedBonusCondition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("unenchanted_chance").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::unenchantedChance), (App)LevelBasedValue.CODEC.fieldOf("enchanted_chance").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::enchantedChance), (App)Enchantment.CODEC.fieldOf("enchantment").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::enchantment)).apply((Applicative)$$0, LootItemRandomChanceWithEnchantedBonusCondition::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ATTACKING_ENTITY);
    }

    @Override
    public boolean test(LootContext $$0) {
        int n;
        Entity $$1 = $$0.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)$$1;
            n = EnchantmentHelper.getEnchantmentLevel(this.enchantment, $$2);
        } else {
            n = 0;
        }
        int $$3 = n;
        float $$4 = $$3 > 0 ? this.enchantedChance.calculate($$3) : this.unenchantedChance;
        return $$0.getRandom().nextFloat() < $$4;
    }

    public static LootItemCondition.Builder randomChanceAndLootingBoost(HolderLookup.Provider $$0, float $$1, float $$2) {
        HolderGetter $$3 = $$0.lookupOrThrow(Registries.ENCHANTMENT);
        return () -> LootItemRandomChanceWithEnchantedBonusCondition.lambda$randomChanceAndLootingBoost$1($$1, $$2, (HolderLookup.RegistryLookup)$$3);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    private static /* synthetic */ LootItemCondition lambda$randomChanceAndLootingBoost$1(float $$0, float $$1, HolderLookup.RegistryLookup $$2) {
        return new LootItemRandomChanceWithEnchantedBonusCondition($$0, new LevelBasedValue.Linear($$0 + $$1, $$1), $$2.getOrThrow(Enchantments.LOOTING));
    }
}

