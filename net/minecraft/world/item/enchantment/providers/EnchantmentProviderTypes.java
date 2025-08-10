/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCost;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;

public interface EnchantmentProviderTypes {
    public static MapCodec<? extends EnchantmentProvider> bootstrap(Registry<MapCodec<? extends EnchantmentProvider>> $$0) {
        Registry.register($$0, "by_cost", EnchantmentsByCost.CODEC);
        Registry.register($$0, "by_cost_with_difficulty", EnchantmentsByCostWithDifficulty.CODEC);
        return Registry.register($$0, "single", SingleEnchantment.CODEC);
    }
}

