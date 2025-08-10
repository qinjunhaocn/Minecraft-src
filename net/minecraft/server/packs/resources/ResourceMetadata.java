/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
    public static final ResourceMetadata EMPTY = new ResourceMetadata(){

        @Override
        public <T> Optional<T> getSection(MetadataSectionType<T> $$0) {
            return Optional.empty();
        }
    };
    public static final IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> EMPTY;

    public static ResourceMetadata fromJsonStream(InputStream $$0) throws IOException {
        try (BufferedReader $$1 = new BufferedReader(new InputStreamReader($$0, StandardCharsets.UTF_8));){
            final JsonObject $$2 = GsonHelper.parse($$1);
            ResourceMetadata resourceMetadata = new ResourceMetadata(){

                @Override
                public <T> Optional<T> getSection(MetadataSectionType<T> $$0) {
                    String $$1 = $$0.name();
                    if ($$2.has($$1)) {
                        Object $$22 = $$0.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)$$2.get($$1)).getOrThrow(JsonParseException::new);
                        return Optional.of($$22);
                    }
                    return Optional.empty();
                }
            };
            return resourceMetadata;
        }
    }

    public <T> Optional<T> getSection(MetadataSectionType<T> var1);

    default public ResourceMetadata copySections(Collection<MetadataSectionType<?>> $$0) {
        Builder $$1 = new Builder();
        for (MetadataSectionType<?> $$2 : $$0) {
            this.copySection($$1, $$2);
        }
        return $$1.build();
    }

    private <T> void copySection(Builder $$0, MetadataSectionType<T> $$1) {
        this.getSection($$1).ifPresent($$2 -> $$0.put($$1, $$2));
    }

    public static class Builder {
        private final ImmutableMap.Builder<MetadataSectionType<?>, Object> map = ImmutableMap.builder();

        public <T> Builder put(MetadataSectionType<T> $$0, T $$1) {
            this.map.put($$0, $$1);
            return this;
        }

        public ResourceMetadata build() {
            final ImmutableMap<MetadataSectionType<?>, Object> $$0 = this.map.build();
            if ($$0.isEmpty()) {
                return EMPTY;
            }
            return new ResourceMetadata(){

                @Override
                public <T> Optional<T> getSection(MetadataSectionType<T> $$02) {
                    return Optional.ofNullable($$0.get($$02));
                }
            };
        }
    }
}

