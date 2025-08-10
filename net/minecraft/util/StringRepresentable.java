/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;

public interface StringRepresentable {
    public static final int PRE_BUILT_MAP_THRESHOLD = 16;

    public String getSerializedName();

    public static <E extends Enum<E>> EnumCodec<E> fromEnum(Supplier<E[]> $$02) {
        return StringRepresentable.fromEnumWithMapping($$02, $$0 -> $$0);
    }

    public static <E extends Enum<E>> EnumCodec<E> fromEnumWithMapping(Supplier<E[]> $$0, Function<String, String> $$1) {
        Enum[] $$2 = (Enum[])$$0.get();
        Function $$3 = StringRepresentable.a((StringRepresentable[])$$2, $$1);
        return new EnumCodec($$2, $$3);
    }

    public static <T extends StringRepresentable> Codec<T> fromValues(Supplier<T[]> $$02) {
        StringRepresentable[] $$1 = (StringRepresentable[])$$02.get();
        Function $$2 = StringRepresentable.a((StringRepresentable[])$$1, (T $$0) -> $$0);
        ToIntFunction<StringRepresentable> $$3 = Util.createIndexLookup(Arrays.asList($$1));
        return new StringRepresentableCodec($$1, $$2, $$3);
    }

    public static <T extends StringRepresentable> Function<String, T> a(T[] $$02, Function<String, String> $$12) {
        if ($$02.length > 16) {
            Map<String, StringRepresentable> $$22 = Arrays.stream($$02).collect(Collectors.toMap($$1 -> (String)$$12.apply($$1.getSerializedName()), $$0 -> $$0));
            return $$1 -> $$1 == null ? null : (StringRepresentable)$$22.get($$1);
        }
        return $$2 -> {
            for (StringRepresentable $$3 : $$02) {
                if (!((String)$$12.apply($$3.getSerializedName())).equals($$2)) continue;
                return $$3;
            }
            return null;
        };
    }

    public static Keyable a(final StringRepresentable[] $$0) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> $$02) {
                return Arrays.stream($$0).map(StringRepresentable::getSerializedName).map(arg_0 -> $$02.createString(arg_0));
            }
        };
    }

    public static class EnumCodec<E extends Enum<E>>
    extends StringRepresentableCodec<E> {
        private final Function<String, E> resolver;

        public EnumCodec(E[] $$02, Function<String, E> $$1) {
            super($$02, $$1, $$0 -> ((Enum)$$0).ordinal());
            this.resolver = $$1;
        }

        @Nullable
        public E byName(@Nullable String $$0) {
            return (E)((Enum)this.resolver.apply($$0));
        }

        public E byName(@Nullable String $$0, E $$1) {
            return (E)((Enum)Objects.requireNonNullElse(this.byName($$0), $$1));
        }

        public E byName(@Nullable String $$0, Supplier<? extends E> $$1) {
            return (E)((Enum)Objects.requireNonNullElseGet(this.byName($$0), $$1));
        }
    }

    public static class StringRepresentableCodec<S extends StringRepresentable>
    implements Codec<S> {
        private final Codec<S> codec;

        public StringRepresentableCodec(S[] $$0, Function<String, S> $$12, ToIntFunction<S> $$2) {
            this.codec = ExtraCodecs.orCompressed(Codec.stringResolver(StringRepresentable::getSerializedName, $$12), ExtraCodecs.idResolverCodec($$2, $$1 -> $$1 >= 0 && $$1 < $$0.length ? $$0[$$1] : null, -1));
        }

        public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> $$0, T $$1) {
            return this.codec.decode($$0, $$1);
        }

        public <T> DataResult<T> encode(S $$0, DynamicOps<T> $$1, T $$2) {
            return this.codec.encode($$0, $$1, $$2);
        }

        public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
            return this.encode((S)((StringRepresentable)object), (DynamicOps<T>)dynamicOps, (T)object2);
        }
    }
}

