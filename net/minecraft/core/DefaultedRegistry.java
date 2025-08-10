/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface DefaultedRegistry<T>
extends Registry<T> {
    @Override
    @Nonnull
    public ResourceLocation getKey(T var1);

    @Override
    @Nonnull
    public T getValue(@Nullable ResourceLocation var1);

    @Override
    @Nonnull
    public T byId(int var1);

    public ResourceLocation getDefaultKey();
}

