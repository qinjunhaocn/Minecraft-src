/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.EnchantmentLevelProvider;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ScoreboardValue;
import net.minecraft.world.level.storage.loot.providers.number.StorageValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class NumberProviders {
    private static final Codec<NumberProvider> TYPED_CODEC = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.byNameCodec().dispatch(NumberProvider::getType, LootNumberProviderType::codec);
    public static final Codec<NumberProvider> CODEC = Codec.lazyInitialized(() -> {
        Codec $$02 = Codec.withAlternative(TYPED_CODEC, (Codec)UniformGenerator.CODEC.codec());
        return Codec.either(ConstantValue.INLINE_CODEC, (Codec)$$02).xmap(Either::unwrap, $$0 -> {
            Either either;
            if ($$0 instanceof ConstantValue) {
                ConstantValue $$1 = (ConstantValue)$$0;
                either = Either.left((Object)$$1);
            } else {
                either = Either.right((Object)$$0);
            }
            return either;
        });
    });
    public static final LootNumberProviderType CONSTANT = NumberProviders.register("constant", ConstantValue.CODEC);
    public static final LootNumberProviderType UNIFORM = NumberProviders.register("uniform", UniformGenerator.CODEC);
    public static final LootNumberProviderType BINOMIAL = NumberProviders.register("binomial", BinomialDistributionGenerator.CODEC);
    public static final LootNumberProviderType SCORE = NumberProviders.register("score", ScoreboardValue.CODEC);
    public static final LootNumberProviderType STORAGE = NumberProviders.register("storage", StorageValue.CODEC);
    public static final LootNumberProviderType ENCHANTMENT_LEVEL = NumberProviders.register("enchantment_level", EnchantmentLevelProvider.CODEC);

    private static LootNumberProviderType register(String $$0, MapCodec<? extends NumberProvider> $$1) {
        return Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, ResourceLocation.withDefaultNamespace($$0), new LootNumberProviderType($$1));
    }
}

