/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.item.enchantment.effects.SetValue;

public interface EnchantmentValueEffect {
    public static final Codec<EnchantmentValueEffect> CODEC = BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentValueEffect::codec, Function.identity());

    public static MapCodec<? extends EnchantmentValueEffect> bootstrap(Registry<MapCodec<? extends EnchantmentValueEffect>> $$0) {
        Registry.register($$0, "add", AddValue.CODEC);
        Registry.register($$0, "all_of", AllOf.ValueEffects.CODEC);
        Registry.register($$0, "multiply", MultiplyValue.CODEC);
        Registry.register($$0, "remove_binomial", RemoveBinomial.CODEC);
        return Registry.register($$0, "set", SetValue.CODEC);
    }

    public float process(int var1, RandomSource var2, float var3);

    public MapCodec<? extends EnchantmentValueEffect> codec();
}

