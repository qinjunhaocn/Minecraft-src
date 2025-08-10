/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.variant;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class VariantUtils {
    public static final String TAG_VARIANT = "variant";

    public static <T> Holder<T> getDefaultOrAny(RegistryAccess $$0, ResourceKey<T> $$1) {
        HolderLookup.RegistryLookup $$2 = $$0.lookupOrThrow($$1.registryKey());
        return (Holder)$$2.get($$1).or(((Registry)$$2)::getAny).orElseThrow();
    }

    public static <T> Holder<T> getAny(RegistryAccess $$0, ResourceKey<? extends Registry<T>> $$1) {
        return (Holder)$$0.lookupOrThrow($$1).getAny().orElseThrow();
    }

    public static <T> void writeVariant(ValueOutput $$0, Holder<T> $$12) {
        $$12.unwrapKey().ifPresent($$1 -> $$0.store(TAG_VARIANT, ResourceLocation.CODEC, $$1.location()));
    }

    public static <T> Optional<Holder<T>> readVariant(ValueInput $$0, ResourceKey<? extends Registry<T>> $$12) {
        return $$0.read(TAG_VARIANT, ResourceLocation.CODEC).map($$1 -> ResourceKey.create($$12, $$1)).flatMap($$0.lookup()::get);
    }

    public static <T extends PriorityProvider<SpawnContext, ?>> Optional<Holder.Reference<T>> selectVariantToSpawn(SpawnContext $$0, ResourceKey<Registry<T>> $$1) {
        ServerLevelAccessor $$2 = $$0.level();
        Stream $$3 = $$2.registryAccess().lookupOrThrow($$1).listElements();
        return PriorityProvider.pick($$3, Holder::value, $$2.getRandom(), $$0);
    }
}

