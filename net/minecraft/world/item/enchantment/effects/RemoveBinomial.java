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

public record RemoveBinomial(LevelBasedValue chance) implements EnchantmentValueEffect
{
    public static final MapCodec<RemoveBinomial> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("chance").forGetter(RemoveBinomial::chance)).apply((Applicative)$$0, RemoveBinomial::new));

    @Override
    public float process(int $$0, RandomSource $$1, float $$2) {
        float $$3 = this.chance.calculate($$0);
        int $$4 = 0;
        int $$5 = 0;
        while ((float)$$5 < $$2) {
            if ($$1.nextFloat() < $$3) {
                ++$$4;
            }
            ++$$5;
        }
        return $$2 - (float)$$4;
    }

    public MapCodec<RemoveBinomial> codec() {
        return CODEC;
    }
}

