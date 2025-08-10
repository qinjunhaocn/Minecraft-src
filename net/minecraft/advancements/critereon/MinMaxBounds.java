/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.advancements.critereon;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface MinMaxBounds<T extends Number> {
    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType((Message)Component.translatable("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType((Message)Component.translatable("argument.range.swapped"));

    public Optional<T> min();

    public Optional<T> max();

    default public boolean isAny() {
        return this.min().isEmpty() && this.max().isEmpty();
    }

    default public Optional<T> unwrapPoint() {
        Optional<T> $$1;
        Optional<T> $$0 = this.min();
        return $$0.equals($$1 = this.max()) ? $$0 : Optional.empty();
    }

    public static <T extends Number, R extends MinMaxBounds<T>> Codec<R> createCodec(Codec<T> $$02, BoundsFactory<T, R> $$1) {
        Codec $$22 = RecordCodecBuilder.create($$2 -> $$2.group((App)$$02.optionalFieldOf("min").forGetter(MinMaxBounds::min), (App)$$02.optionalFieldOf("max").forGetter(MinMaxBounds::max)).apply((Applicative)$$2, $$1::create));
        return Codec.either((Codec)$$22, $$02).xmap($$12 -> (MinMaxBounds)$$12.map($$0 -> $$0, $$1 -> $$1.create(Optional.of($$1), Optional.of($$1))), $$0 -> {
            Optional $$1 = $$0.unwrapPoint();
            return $$1.isPresent() ? Either.right((Object)((Number)$$1.get())) : Either.left((Object)$$0);
        });
    }

    public static <B extends ByteBuf, T extends Number, R extends MinMaxBounds<T>> StreamCodec<B, R> createStreamCodec(final StreamCodec<B, T> $$0, final BoundsFactory<T, R> $$1) {
        return new StreamCodec<B, R>(){
            private static final int MIN_FLAG = 1;
            public static final int MAX_FLAG = 2;

            @Override
            public R decode(B $$02) {
                byte $$12 = $$02.readByte();
                Optional $$2 = ($$12 & 1) != 0 ? Optional.of((Number)$$0.decode($$02)) : Optional.empty();
                Optional $$3 = ($$12 & 2) != 0 ? Optional.of((Number)$$0.decode($$02)) : Optional.empty();
                return $$1.create($$2, $$3);
            }

            @Override
            public void encode(B $$02, R $$12) {
                Optional<Number> $$22 = $$12.min();
                Optional<Number> $$3 = $$12.max();
                $$02.writeByte(($$22.isPresent() ? 1 : 0) | ($$3.isPresent() ? 2 : 0));
                $$22.ifPresent($$2 -> $$0.encode($$02, $$2));
                $$3.ifPresent($$2 -> $$0.encode($$02, $$2));
            }

            @Override
            public /* synthetic */ void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (R)((MinMaxBounds)object2));
            }

            @Override
            public /* synthetic */ Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader $$0, BoundsFromReaderFactory<T, R> $$1, Function<String, T> $$2, Supplier<DynamicCommandExceptionType> $$3, Function<T, T> $$4) throws CommandSyntaxException {
        if (!$$0.canRead()) {
            throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
        }
        int $$5 = $$0.getCursor();
        try {
            Optional<T> $$8;
            Optional<T> $$6 = MinMaxBounds.readNumber($$0, $$2, $$3).map($$4);
            if ($$0.canRead(2) && $$0.peek() == '.' && $$0.peek(1) == '.') {
                $$0.skip();
                $$0.skip();
                Optional<T> $$7 = MinMaxBounds.readNumber($$0, $$2, $$3).map($$4);
                if ($$6.isEmpty() && $$7.isEmpty()) {
                    throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
                }
            } else {
                $$8 = $$6;
            }
            if ($$6.isEmpty() && $$8.isEmpty()) {
                throw ERROR_EMPTY.createWithContext((ImmutableStringReader)$$0);
            }
            return $$1.create($$0, $$6, $$8);
        } catch (CommandSyntaxException $$9) {
            $$0.setCursor($$5);
            throw new CommandSyntaxException($$9.getType(), $$9.getRawMessage(), $$9.getInput(), $$5);
        }
    }

    private static <T extends Number> Optional<T> readNumber(StringReader $$0, Function<String, T> $$1, Supplier<DynamicCommandExceptionType> $$2) throws CommandSyntaxException {
        int $$3 = $$0.getCursor();
        while ($$0.canRead() && MinMaxBounds.isAllowedInputChat($$0)) {
            $$0.skip();
        }
        String $$4 = $$0.getString().substring($$3, $$0.getCursor());
        if ($$4.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of((Number)$$1.apply($$4));
        } catch (NumberFormatException $$5) {
            throw $$2.get().createWithContext((ImmutableStringReader)$$0, (Object)$$4);
        }
    }

    private static boolean isAllowedInputChat(StringReader $$0) {
        char $$1 = $$0.peek();
        if ($$1 >= '0' && $$1 <= '9' || $$1 == '-') {
            return true;
        }
        if ($$1 == '.') {
            return !$$0.canRead(2) || $$0.peek(1) != '.';
        }
        return false;
    }

    @FunctionalInterface
    public static interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(Optional<T> var1, Optional<T> var2);
    }

    @FunctionalInterface
    public static interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(StringReader var1, Optional<T> var2, Optional<T> var3) throws CommandSyntaxException;
    }

    public record Doubles(Optional<Double> min, Optional<Double> max, Optional<Double> minSq, Optional<Double> maxSq) implements MinMaxBounds<Double>
    {
        public static final Doubles ANY = new Doubles(Optional.empty(), Optional.empty());
        public static final Codec<Doubles> CODEC = MinMaxBounds.createCodec(Codec.DOUBLE, Doubles::new);
        public static final StreamCodec<ByteBuf, Doubles> STREAM_CODEC = MinMaxBounds.createStreamCodec(ByteBufCodecs.DOUBLE, Doubles::new);

        private Doubles(Optional<Double> $$0, Optional<Double> $$1) {
            this($$0, $$1, Doubles.squareOpt($$0), Doubles.squareOpt($$1));
        }

        private static Doubles create(StringReader $$0, Optional<Double> $$1, Optional<Double> $$2) throws CommandSyntaxException {
            if ($$1.isPresent() && $$2.isPresent() && $$1.get() > $$2.get()) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)$$0);
            }
            return new Doubles($$1, $$2);
        }

        private static Optional<Double> squareOpt(Optional<Double> $$02) {
            return $$02.map($$0 -> $$0 * $$0);
        }

        public static Doubles exactly(double $$0) {
            return new Doubles(Optional.of($$0), Optional.of($$0));
        }

        public static Doubles between(double $$0, double $$1) {
            return new Doubles(Optional.of($$0), Optional.of($$1));
        }

        public static Doubles atLeast(double $$0) {
            return new Doubles(Optional.of($$0), Optional.empty());
        }

        public static Doubles atMost(double $$0) {
            return new Doubles(Optional.empty(), Optional.of($$0));
        }

        public boolean matches(double $$0) {
            if (this.min.isPresent() && this.min.get() > $$0) {
                return false;
            }
            return this.max.isEmpty() || !(this.max.get() < $$0);
        }

        public boolean matchesSqr(double $$0) {
            if (this.minSq.isPresent() && this.minSq.get() > $$0) {
                return false;
            }
            return this.maxSq.isEmpty() || !(this.maxSq.get() < $$0);
        }

        public static Doubles fromReader(StringReader $$02) throws CommandSyntaxException {
            return Doubles.fromReader($$02, $$0 -> $$0);
        }

        public static Doubles fromReader(StringReader $$0, Function<Double, Double> $$1) throws CommandSyntaxException {
            return MinMaxBounds.fromReader($$0, Doubles::create, Double::parseDouble, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidDouble(), $$1);
        }
    }

    public record Ints(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSq, Optional<Long> maxSq) implements MinMaxBounds<Integer>
    {
        public static final Ints ANY = new Ints(Optional.empty(), Optional.empty());
        public static final Codec<Ints> CODEC = MinMaxBounds.createCodec(Codec.INT, Ints::new);
        public static final StreamCodec<ByteBuf, Ints> STREAM_CODEC = MinMaxBounds.createStreamCodec(ByteBufCodecs.INT, Ints::new);

        private Ints(Optional<Integer> $$02, Optional<Integer> $$1) {
            this($$02, $$1, $$02.map($$0 -> $$0.longValue() * $$0.longValue()), Ints.squareOpt($$1));
        }

        private static Ints create(StringReader $$0, Optional<Integer> $$1, Optional<Integer> $$2) throws CommandSyntaxException {
            if ($$1.isPresent() && $$2.isPresent() && $$1.get() > $$2.get()) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)$$0);
            }
            return new Ints($$1, $$2);
        }

        private static Optional<Long> squareOpt(Optional<Integer> $$02) {
            return $$02.map($$0 -> $$0.longValue() * $$0.longValue());
        }

        public static Ints exactly(int $$0) {
            return new Ints(Optional.of($$0), Optional.of($$0));
        }

        public static Ints between(int $$0, int $$1) {
            return new Ints(Optional.of($$0), Optional.of($$1));
        }

        public static Ints atLeast(int $$0) {
            return new Ints(Optional.of($$0), Optional.empty());
        }

        public static Ints atMost(int $$0) {
            return new Ints(Optional.empty(), Optional.of($$0));
        }

        public boolean matches(int $$0) {
            if (this.min.isPresent() && this.min.get() > $$0) {
                return false;
            }
            return this.max.isEmpty() || this.max.get() >= $$0;
        }

        public boolean matchesSqr(long $$0) {
            if (this.minSq.isPresent() && this.minSq.get() > $$0) {
                return false;
            }
            return this.maxSq.isEmpty() || this.maxSq.get() >= $$0;
        }

        public static Ints fromReader(StringReader $$02) throws CommandSyntaxException {
            return Ints.fromReader($$02, $$0 -> $$0);
        }

        public static Ints fromReader(StringReader $$0, Function<Integer, Integer> $$1) throws CommandSyntaxException {
            return MinMaxBounds.fromReader($$0, Ints::create, Integer::parseInt, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidInt(), $$1);
        }
    }
}

