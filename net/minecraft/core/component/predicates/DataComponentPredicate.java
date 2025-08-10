/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.core.component.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
    public static final Codec<Map<Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, Single<?>> SINGLE_STREAM_CODEC = ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE).dispatch(Single::type, Type::singleStreamCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<Type<?>, DataComponentPredicate>> STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map($$0 -> $$0.stream().collect(Collectors.toMap(Single::type, Single::predicate)), $$0 -> $$0.entrySet().stream().map(Single::fromEntry).toList());

    public static MapCodec<Single<?>> singleCodec(String $$0) {
        return BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec().dispatchMap($$0, Single::type, Type::wrappedCodec);
    }

    public boolean matches(DataComponentGetter var1);

    public record Single<T extends DataComponentPredicate>(Type<T> type, T predicate) {
        private static <T extends DataComponentPredicate> Single<T> fromEntry(Map.Entry<Type<?>, T> $$0) {
            return new Single<DataComponentPredicate>($$0.getKey(), (DataComponentPredicate)$$0.getValue());
        }
    }

    public static final class Type<T extends DataComponentPredicate> {
        private final Codec<T> codec;
        private final MapCodec<Single<T>> wrappedCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec;

        public Type(Codec<T> $$02) {
            this.codec = $$02;
            this.wrappedCodec = RecordCodecBuilder.mapCodec($$1 -> $$1.group((App)$$02.fieldOf("value").forGetter(Single::predicate)).apply((Applicative)$$1, $$0 -> new Single<DataComponentPredicate>(this, (DataComponentPredicate)$$0)));
            this.singleStreamCodec = ByteBufCodecs.fromCodecWithRegistries($$02).map($$0 -> new Single<DataComponentPredicate>(this, (DataComponentPredicate)$$0), Single::predicate);
        }

        public Codec<T> codec() {
            return this.codec;
        }

        public MapCodec<Single<T>> wrappedCodec() {
            return this.wrappedCodec;
        }

        public StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec() {
            return this.singleStreamCodec;
        }
    }
}

