/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;

public class ComponentSerialization {
    public static final Codec<Component> CODEC = Codec.recursive((String)"Component", ComponentSerialization::createCodec);
    public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
    public static final StreamCodec<RegistryFriendlyByteBuf, Component> TRUSTED_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> TRUSTED_OPTIONAL_STREAM_CODEC = TRUSTED_STREAM_CODEC.apply(ByteBufCodecs::optional);
    public static final StreamCodec<ByteBuf, Component> TRUSTED_CONTEXT_FREE_STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC);

    public static Codec<Component> flatRestrictedCodec(final int $$0) {
        return new Codec<Component>(){

            public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> $$02, T $$1) {
                return CODEC.decode($$02, $$1).flatMap($$2 -> {
                    if (this.isTooLarge($$02, (Component)$$2.getFirst())) {
                        return DataResult.error(() -> "Component was too large: greater than max size " + $$0);
                    }
                    return DataResult.success((Object)$$2);
                });
            }

            public <T> DataResult<T> encode(Component $$02, DynamicOps<T> $$1, T $$2) {
                return CODEC.encodeStart($$1, (Object)$$02);
            }

            private <T> boolean isTooLarge(DynamicOps<T> $$02, Component $$1) {
                DataResult $$2 = CODEC.encodeStart(1.asJsonOps($$02), (Object)$$1);
                return $$2.isSuccess() && GsonHelper.encodesLongerThan((JsonElement)$$2.getOrThrow(), $$0);
            }

            private static <T> DynamicOps<JsonElement> asJsonOps(DynamicOps<T> $$02) {
                if ($$02 instanceof RegistryOps) {
                    RegistryOps $$1 = (RegistryOps)$$02;
                    return $$1.withParent(JsonOps.INSTANCE);
                }
                return JsonOps.INSTANCE;
            }

            public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                return this.encode((Component)object, dynamicOps, object2);
            }
        };
    }

    private static MutableComponent createFromList(List<Component> $$0) {
        MutableComponent $$1 = $$0.get(0).copy();
        for (int $$2 = 1; $$2 < $$0.size(); ++$$2) {
            $$1.append($$0.get($$2));
        }
        return $$1;
    }

    public static <T extends StringRepresentable, E> MapCodec<E> a(T[] $$0, Function<T, MapCodec<? extends E>> $$1, Function<E, T> $$22, String $$3) {
        FuzzyCodec<Object> $$4 = new FuzzyCodec<Object>(Stream.of($$0).map($$1).toList(), $$2 -> (MapEncoder)$$1.apply((StringRepresentable)$$22.apply($$2)));
        Codec $$5 = StringRepresentable.fromValues(() -> $$0);
        MapCodec $$6 = $$5.dispatchMap($$3, $$22, $$1);
        StrictEither<Object> $$7 = new StrictEither<Object>($$3, $$6, $$4);
        return ExtraCodecs.orCompressed($$7, $$6);
    }

    private static Codec<Component> createCodec(Codec<Component> $$03) {
        StringRepresentable[] $$1 = new ComponentContents.Type[]{PlainTextContents.TYPE, TranslatableContents.TYPE, KeybindContents.TYPE, ScoreContents.TYPE, SelectorContents.TYPE, NbtContents.TYPE};
        MapCodec $$22 = ComponentSerialization.a((StringRepresentable[])$$1, ComponentContents.Type::codec, ComponentContents::type, (String)"type");
        Codec $$3 = RecordCodecBuilder.create($$2 -> $$2.group((App)$$22.forGetter(Component::getContents), (App)ExtraCodecs.nonEmptyList($$03.listOf()).optionalFieldOf("extra", (Object)List.of()).forGetter(Component::getSiblings), (App)Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)).apply((Applicative)$$2, MutableComponent::new));
        return Codec.either((Codec)Codec.either((Codec)Codec.STRING, ExtraCodecs.nonEmptyList($$03.listOf())), (Codec)$$3).xmap($$02 -> (Component)$$02.map($$0 -> (Component)$$0.map(Component::literal, ComponentSerialization::createFromList), $$0 -> $$0), $$0 -> {
            String $$1 = $$0.tryCollapseToString();
            return $$1 != null ? Either.left((Object)Either.left((Object)$$1)) : Either.right((Object)$$0);
        });
    }

    static class FuzzyCodec<T>
    extends MapCodec<T> {
        private final List<MapCodec<? extends T>> codecs;
        private final Function<T, MapEncoder<? extends T>> encoderGetter;

        public FuzzyCodec(List<MapCodec<? extends T>> $$0, Function<T, MapEncoder<? extends T>> $$1) {
            this.codecs = $$0;
            this.encoderGetter = $$1;
        }

        public <S> DataResult<T> decode(DynamicOps<S> $$0, MapLike<S> $$1) {
            for (MapDecoder mapDecoder : this.codecs) {
                DataResult $$3 = mapDecoder.decode($$0, $$1);
                if (!$$3.result().isPresent()) continue;
                return $$3;
            }
            return DataResult.error(() -> "No matching codec found");
        }

        public <S> RecordBuilder<S> encode(T $$0, DynamicOps<S> $$1, RecordBuilder<S> $$2) {
            MapEncoder<? extends T> $$3 = this.encoderGetter.apply($$0);
            return $$3.encode($$0, $$1, $$2);
        }

        public <S> Stream<S> keys(DynamicOps<S> $$0) {
            return this.codecs.stream().flatMap($$1 -> $$1.keys($$0)).distinct();
        }

        public String toString() {
            return "FuzzyCodec[" + String.valueOf(this.codecs) + "]";
        }
    }

    static class StrictEither<T>
    extends MapCodec<T> {
        private final String typeFieldName;
        private final MapCodec<T> typed;
        private final MapCodec<T> fuzzy;

        public StrictEither(String $$0, MapCodec<T> $$1, MapCodec<T> $$2) {
            this.typeFieldName = $$0;
            this.typed = $$1;
            this.fuzzy = $$2;
        }

        public <O> DataResult<T> decode(DynamicOps<O> $$0, MapLike<O> $$1) {
            if ($$1.get(this.typeFieldName) != null) {
                return this.typed.decode($$0, $$1);
            }
            return this.fuzzy.decode($$0, $$1);
        }

        public <O> RecordBuilder<O> encode(T $$0, DynamicOps<O> $$1, RecordBuilder<O> $$2) {
            return this.fuzzy.encode($$0, $$1, $$2);
        }

        public <T1> Stream<T1> keys(DynamicOps<T1> $$0) {
            return Stream.concat(this.typed.keys($$0), this.fuzzy.keys($$0)).distinct();
        }
    }
}

