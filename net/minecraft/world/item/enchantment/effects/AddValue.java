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

public record AddValue(LevelBasedValue value) implements EnchantmentValueEffect
{
    public static final MapCodec<AddValue> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("value").forGetter(AddValue::value)).apply((Applicative)$$0, AddValue::new));

    @Override
    public float process(int $$0, RandomSource $$1, float $$2) {
        return $$2 + this.value.calculate($$0);
    }

    public MapCodec<AddValue> codec() {
        return CODEC;
    }
}

