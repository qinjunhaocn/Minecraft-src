/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.resources;

import net.minecraft.resources.ResourceKey;

@FunctionalInterface
public interface DependantName<T, V> {
    public V get(ResourceKey<T> var1);

    public static <T, V> DependantName<T, V> fixed(V $$0) {
        return $$1 -> $$0;
    }
}

