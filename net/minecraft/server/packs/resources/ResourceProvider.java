/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

@FunctionalInterface
public interface ResourceProvider {
    public static final ResourceProvider EMPTY = $$0 -> Optional.empty();

    public Optional<Resource> getResource(ResourceLocation var1);

    default public Resource getResourceOrThrow(ResourceLocation $$0) throws FileNotFoundException {
        return this.getResource($$0).orElseThrow(() -> new FileNotFoundException($$0.toString()));
    }

    default public InputStream open(ResourceLocation $$0) throws IOException {
        return this.getResourceOrThrow($$0).open();
    }

    default public BufferedReader openAsReader(ResourceLocation $$0) throws IOException {
        return this.getResourceOrThrow($$0).openAsReader();
    }

    public static ResourceProvider fromMap(Map<ResourceLocation, Resource> $$0) {
        return $$1 -> Optional.ofNullable((Resource)$$0.get($$1));
    }
}

