/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record ShaderDefines(Map<String, String> values, Set<String> flags) {
    public static final ShaderDefines EMPTY = new ShaderDefines(Map.of(), Set.of());
    public static final Codec<ShaderDefines> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING).optionalFieldOf("values", (Object)Map.of()).forGetter(ShaderDefines::values), (App)Codec.STRING.listOf().xmap((Function<List, Set>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, copyOf(java.util.Collection ), (Ljava/util/List;)Ljava/util/Set;)(), (Function<Set, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, copyOf(java.util.Collection ), (Ljava/util/Set;)Ljava/util/List;)()).optionalFieldOf("flags", (Object)Set.of()).forGetter(ShaderDefines::flags)).apply((Applicative)$$0, ShaderDefines::new));

    public static Builder builder() {
        return new Builder();
    }

    public ShaderDefines withOverrides(ShaderDefines $$0) {
        if (this.isEmpty()) {
            return $$0;
        }
        if ($$0.isEmpty()) {
            return this;
        }
        ImmutableMap.Builder $$1 = ImmutableMap.builderWithExpectedSize(this.values.size() + $$0.values.size());
        $$1.putAll(this.values);
        $$1.putAll($$0.values);
        ImmutableSet.Builder $$2 = ImmutableSet.builderWithExpectedSize(this.flags.size() + $$0.flags.size());
        $$2.addAll(this.flags);
        $$2.addAll($$0.flags);
        return new ShaderDefines($$1.buildKeepingLast(), (Set<String>)((Object)$$2.build()));
    }

    public String asSourceDirectives() {
        StringBuilder $$0 = new StringBuilder();
        for (Map.Entry<String, String> $$1 : this.values.entrySet()) {
            String $$2 = $$1.getKey();
            String $$3 = $$1.getValue();
            $$0.append("#define ").append($$2).append(" ").append($$3).append('\n');
        }
        for (String $$4 : this.flags) {
            $$0.append("#define ").append($$4).append('\n');
        }
        return $$0.toString();
    }

    public boolean isEmpty() {
        return this.values.isEmpty() && this.flags.isEmpty();
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
        private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

        Builder() {
        }

        public Builder define(String $$0, String $$1) {
            if ($$1.isBlank()) {
                throw new IllegalArgumentException("Cannot define empty string");
            }
            this.values.put($$0, Builder.escapeNewLines($$1));
            return this;
        }

        private static String escapeNewLines(String $$0) {
            return $$0.replaceAll("\n", "\\\\\n");
        }

        public Builder define(String $$0, float $$1) {
            this.values.put($$0, String.valueOf($$1));
            return this;
        }

        public Builder define(String $$0, int $$1) {
            this.values.put($$0, String.valueOf($$1));
            return this;
        }

        public Builder define(String $$0) {
            this.flags.add((Object)$$0);
            return this;
        }

        public ShaderDefines build() {
            return new ShaderDefines(this.values.build(), (Set<String>)((Object)this.flags.build()));
        }
    }
}

