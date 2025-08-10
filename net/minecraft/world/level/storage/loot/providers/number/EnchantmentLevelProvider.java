/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record EnchantmentLevelProvider(LevelBasedValue amount) implements NumberProvider
{
    public static final MapCodec<EnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentLevelProvider::amount)).apply((Applicative)$$0, EnchantmentLevelProvider::new));

    @Override
    public float getFloat(LootContext $$0) {
        int $$1 = $$0.getParameter(LootContextParams.ENCHANTMENT_LEVEL);
        return this.amount.calculate($$1);
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.ENCHANTMENT_LEVEL;
    }

    public static EnchantmentLevelProvider forEnchantmentLevel(LevelBasedValue $$0) {
        return new EnchantmentLevelProvider($$0);
    }
}

