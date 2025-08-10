/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.StorageNbtProvider;

public class NbtProviders {
    private static final Codec<NbtProvider> TYPED_CODEC = BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE.byNameCodec().dispatch(NbtProvider::getType, LootNbtProviderType::codec);
    public static final Codec<NbtProvider> CODEC = Codec.lazyInitialized(() -> Codec.either(ContextNbtProvider.INLINE_CODEC, TYPED_CODEC).xmap(Either::unwrap, $$0 -> {
        Either either;
        if ($$0 instanceof ContextNbtProvider) {
            ContextNbtProvider $$1 = (ContextNbtProvider)$$0;
            either = Either.left((Object)$$1);
        } else {
            either = Either.right((Object)$$0);
        }
        return either;
    }));
    public static final LootNbtProviderType STORAGE = NbtProviders.register("storage", StorageNbtProvider.CODEC);
    public static final LootNbtProviderType CONTEXT = NbtProviders.register("context", ContextNbtProvider.CODEC);

    private static LootNbtProviderType register(String $$0, MapCodec<? extends NbtProvider> $$1) {
        return Registry.register(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, ResourceLocation.withDefaultNamespace($$0), new LootNbtProviderType($$1));
    }
}

