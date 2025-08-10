/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.enchantment.effects;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

public record MultiplyValue(LevelBasedValue factor) implements EnchantmentValueEffect
{
    public static final MapCodec<MultiplyValue> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("factor").forGetter(MultiplyValue::factor)).apply((Applicative)$$0, MultiplyValue::new));

    @Override
    public float process(int $$0, RandomSource $$1, float $$2) {
        return $$2 * this.factor.calculate($$0);
    }

    public MapCodec<MultiplyValue> codec() {
        return CODEC;
    }
}

