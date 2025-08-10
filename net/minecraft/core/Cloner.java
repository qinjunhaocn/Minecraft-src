/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.JavaOps
 */
package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class Cloner<T> {
    private final Codec<T> directCodec;

    Cloner(Codec<T> $$0) {
        this.directCodec = $$0;
    }

    public T clone(T $$02, HolderLookup.Provider $$1, HolderLookup.Provider $$2) {
        RegistryOps $$3 = $$1.createSerializationContext(JavaOps.INSTANCE);
        RegistryOps $$4 = $$2.createSerializationContext(JavaOps.INSTANCE);
        Object $$5 = this.directCodec.encodeStart($$3, $$02).getOrThrow($$0 -> new IllegalStateException("Failed to encode: " + $$0));
        return (T)this.directCodec.parse($$4, $$5).getOrThrow($$0 -> new IllegalStateException("Failed to decode: " + $$0));
    }

    public static class Factory {
        private final Map<ResourceKey<? extends Registry<?>>, Cloner<?>> codecs = new HashMap();

        public <T> Factory addCodec(ResourceKey<? extends Registry<? extends T>> $$0, Codec<T> $$1) {
            this.codecs.put($$0, new Cloner<T>($$1));
            return this;
        }

        @Nullable
        public <T> Cloner<T> cloner(ResourceKey<? extends Registry<? extends T>> $$0) {
            return this.codecs.get($$0);
        }
    }
}

