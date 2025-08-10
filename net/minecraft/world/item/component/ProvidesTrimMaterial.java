/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public record ProvidesTrimMaterial(EitherHolder<TrimMaterial> material) {
    public static final Codec<ProvidesTrimMaterial> CODEC = EitherHolder.codec(Registries.TRIM_MATERIAL, TrimMaterial.CODEC).xmap(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
    public static final StreamCodec<RegistryFriendlyByteBuf, ProvidesTrimMaterial> STREAM_CODEC = EitherHolder.streamCodec(Registries.TRIM_MATERIAL, TrimMaterial.STREAM_CODEC).map(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);

    public ProvidesTrimMaterial(Holder<TrimMaterial> $$0) {
        this(new EitherHolder<TrimMaterial>($$0));
    }

    @Deprecated
    public ProvidesTrimMaterial(ResourceKey<TrimMaterial> $$0) {
        this(new EitherHolder<TrimMaterial>($$0));
    }

    public Optional<Holder<TrimMaterial>> unwrap(HolderLookup.Provider $$0) {
        return this.material.unwrap($$0);
    }
}

