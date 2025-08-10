/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public class LayeredRegistryAccess<T> {
    private final List<T> keys;
    private final List<RegistryAccess.Frozen> values;
    private final RegistryAccess.Frozen composite;

    public LayeredRegistryAccess(List<T> $$0) {
        this($$0, Util.make(() -> {
            Object[] $$1 = new RegistryAccess.Frozen[$$0.size()];
            Arrays.fill($$1, RegistryAccess.EMPTY);
            return Arrays.asList($$1);
        }));
    }

    private LayeredRegistryAccess(List<T> $$0, List<RegistryAccess.Frozen> $$1) {
        this.keys = List.copyOf($$0);
        this.values = List.copyOf($$1);
        this.composite = new RegistryAccess.ImmutableRegistryAccess(LayeredRegistryAccess.collectRegistries($$1.stream())).freeze();
    }

    private int getLayerIndexOrThrow(T $$0) {
        int $$1 = this.keys.indexOf($$0);
        if ($$1 == -1) {
            throw new IllegalStateException("Can't find " + String.valueOf($$0) + " inside " + String.valueOf(this.keys));
        }
        return $$1;
    }

    public RegistryAccess.Frozen getLayer(T $$0) {
        int $$1 = this.getLayerIndexOrThrow($$0);
        return this.values.get($$1);
    }

    public RegistryAccess.Frozen getAccessForLoading(T $$0) {
        int $$1 = this.getLayerIndexOrThrow($$0);
        return this.getCompositeAccessForLayers(0, $$1);
    }

    public RegistryAccess.Frozen getAccessFrom(T $$0) {
        int $$1 = this.getLayerIndexOrThrow($$0);
        return this.getCompositeAccessForLayers($$1, this.values.size());
    }

    private RegistryAccess.Frozen getCompositeAccessForLayers(int $$0, int $$1) {
        return new RegistryAccess.ImmutableRegistryAccess(LayeredRegistryAccess.collectRegistries(this.values.subList($$0, $$1).stream())).freeze();
    }

    public LayeredRegistryAccess<T> a(T $$0, RegistryAccess.Frozen ... $$1) {
        return this.replaceFrom($$0, Arrays.asList($$1));
    }

    public LayeredRegistryAccess<T> replaceFrom(T $$0, List<RegistryAccess.Frozen> $$1) {
        int $$2 = this.getLayerIndexOrThrow($$0);
        if ($$1.size() > this.values.size() - $$2) {
            throw new IllegalStateException("Too many values to replace");
        }
        ArrayList<RegistryAccess.Frozen> $$3 = new ArrayList<RegistryAccess.Frozen>();
        for (int $$4 = 0; $$4 < $$2; ++$$4) {
            $$3.add(this.values.get($$4));
        }
        $$3.addAll($$1);
        while ($$3.size() < this.values.size()) {
            $$3.add(RegistryAccess.EMPTY);
        }
        return new LayeredRegistryAccess<T>(this.keys, $$3);
    }

    public RegistryAccess.Frozen compositeAccess() {
        return this.composite;
    }

    private static Map<ResourceKey<? extends Registry<?>>, Registry<?>> collectRegistries(Stream<? extends RegistryAccess> $$0) {
        HashMap $$1 = new HashMap();
        $$0.forEach($$12 -> $$12.registries().forEach($$1 -> {
            if ($$1.put($$1.key(), $$1.value()) != null) {
                throw new IllegalStateException("Duplicated registry " + String.valueOf($$1.key()));
            }
        }));
        return $$1;
    }
}

