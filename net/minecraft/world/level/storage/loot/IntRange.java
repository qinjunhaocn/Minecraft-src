/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class IntRange {
    private static final Codec<IntRange> RECORD_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)NumberProviders.CODEC.optionalFieldOf("min").forGetter($$0 -> Optional.ofNullable($$0.min)), (App)NumberProviders.CODEC.optionalFieldOf("max").forGetter($$0 -> Optional.ofNullable($$0.max))).apply((Applicative)$$02, IntRange::new));
    public static final Codec<IntRange> CODEC = Codec.either((Codec)Codec.INT, RECORD_CODEC).xmap($$0 -> (IntRange)$$0.map(IntRange::exact, Function.identity()), $$0 -> {
        OptionalInt $$1 = $$0.unpackExact();
        if ($$1.isPresent()) {
            return Either.left((Object)$$1.getAsInt());
        }
        return Either.right((Object)$$0);
    });
    @Nullable
    private final NumberProvider min;
    @Nullable
    private final NumberProvider max;
    private final IntLimiter limiter;
    private final IntChecker predicate;

    public Set<ContextKey<?>> getReferencedContextParams() {
        ImmutableSet.Builder $$0 = ImmutableSet.builder();
        if (this.min != null) {
            $$0.addAll(this.min.getReferencedContextParams());
        }
        if (this.max != null) {
            $$0.addAll(this.max.getReferencedContextParams());
        }
        return $$0.build();
    }

    private IntRange(Optional<NumberProvider> $$0, Optional<NumberProvider> $$1) {
        this((NumberProvider)$$0.orElse(null), (NumberProvider)$$1.orElse(null));
    }

    private IntRange(@Nullable NumberProvider $$02, @Nullable NumberProvider $$12) {
        this.min = $$02;
        this.max = $$12;
        if ($$02 == null) {
            if ($$12 == null) {
                this.limiter = ($$0, $$1) -> $$1;
                this.predicate = ($$0, $$1) -> true;
            } else {
                this.limiter = ($$1, $$2) -> Math.min($$12.getInt($$1), $$2);
                this.predicate = ($$1, $$2) -> $$2 <= $$12.getInt($$1);
            }
        } else if ($$12 == null) {
            this.limiter = ($$1, $$2) -> Math.max($$02.getInt($$1), $$2);
            this.predicate = ($$1, $$2) -> $$2 >= $$02.getInt($$1);
        } else {
            this.limiter = ($$2, $$3) -> Mth.clamp($$3, $$02.getInt($$2), $$12.getInt($$2));
            this.predicate = ($$2, $$3) -> $$3 >= $$02.getInt($$2) && $$3 <= $$12.getInt($$2);
        }
    }

    public static IntRange exact(int $$0) {
        ConstantValue $$1 = ConstantValue.exactly($$0);
        return new IntRange(Optional.of($$1), Optional.of($$1));
    }

    public static IntRange range(int $$0, int $$1) {
        return new IntRange(Optional.of(ConstantValue.exactly($$0)), Optional.of(ConstantValue.exactly($$1)));
    }

    public static IntRange lowerBound(int $$0) {
        return new IntRange(Optional.of(ConstantValue.exactly($$0)), Optional.empty());
    }

    public static IntRange upperBound(int $$0) {
        return new IntRange(Optional.empty(), Optional.of(ConstantValue.exactly($$0)));
    }

    public int clamp(LootContext $$0, int $$1) {
        return this.limiter.apply($$0, $$1);
    }

    public boolean test(LootContext $$0, int $$1) {
        return this.predicate.test($$0, $$1);
    }

    private OptionalInt unpackExact() {
        ConstantValue $$0;
        NumberProvider numberProvider;
        if (Objects.equals(this.min, this.max) && (numberProvider = this.min) instanceof ConstantValue && Math.floor(($$0 = (ConstantValue)numberProvider).value()) == (double)$$0.value()) {
            return OptionalInt.of((int)$$0.value());
        }
        return OptionalInt.empty();
    }

    @FunctionalInterface
    static interface IntLimiter {
        public int apply(LootContext var1, int var2);
    }

    @FunctionalInterface
    static interface IntChecker {
        public boolean test(LootContext var1, int var2);
    }
}

