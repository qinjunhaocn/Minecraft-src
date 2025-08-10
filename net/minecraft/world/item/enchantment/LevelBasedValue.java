/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;

public interface LevelBasedValue {
    public static final Codec<LevelBasedValue> DISPATCH_CODEC = BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.byNameCodec().dispatch(LevelBasedValue::codec, $$0 -> $$0);
    public static final Codec<LevelBasedValue> CODEC = Codec.either(Constant.CODEC, DISPATCH_CODEC).xmap($$02 -> (LevelBasedValue)$$02.map($$0 -> $$0, $$0 -> $$0), $$0 -> {
        Either either;
        if ($$0 instanceof Constant) {
            Constant $$1 = (Constant)$$0;
            either = Either.left((Object)$$1);
        } else {
            either = Either.right((Object)$$0);
        }
        return either;
    });

    public static MapCodec<? extends LevelBasedValue> bootstrap(Registry<MapCodec<? extends LevelBasedValue>> $$0) {
        Registry.register($$0, "clamped", Clamped.CODEC);
        Registry.register($$0, "fraction", Fraction.CODEC);
        Registry.register($$0, "levels_squared", LevelsSquared.CODEC);
        Registry.register($$0, "linear", Linear.CODEC);
        return Registry.register($$0, "lookup", Lookup.CODEC);
    }

    public static Constant constant(float $$0) {
        return new Constant($$0);
    }

    public static Linear perLevel(float $$0, float $$1) {
        return new Linear($$0, $$1);
    }

    public static Linear perLevel(float $$0) {
        return LevelBasedValue.perLevel($$0, $$0);
    }

    public static Lookup lookup(List<Float> $$0, LevelBasedValue $$1) {
        return new Lookup($$0, $$1);
    }

    public float calculate(int var1);

    public MapCodec<? extends LevelBasedValue> codec();

    public record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue
    {
        public static final MapCodec<Clamped> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.fieldOf("value").forGetter(Clamped::value), (App)Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), (App)Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply((Applicative)$$0, Clamped::new)).validate($$0 -> {
            if ($$0.max <= $$0.min) {
                return DataResult.error(() -> "Max must be larger than min, min: " + $$0.min + ", max: " + $$0.max);
            }
            return DataResult.success((Object)$$0);
        });

        @Override
        public float calculate(int $$0) {
            return Mth.clamp(this.value.calculate($$0), this.min, this.max);
        }

        public MapCodec<Clamped> codec() {
            return CODEC;
        }
    }

    public record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue
    {
        public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.fieldOf("numerator").forGetter(Fraction::numerator), (App)CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply((Applicative)$$0, Fraction::new));

        @Override
        public float calculate(int $$0) {
            float $$1 = this.denominator.calculate($$0);
            if ($$1 == 0.0f) {
                return 0.0f;
            }
            return this.numerator.calculate($$0) / $$1;
        }

        public MapCodec<Fraction> codec() {
            return CODEC;
        }
    }

    public record LevelsSquared(float added) implements LevelBasedValue
    {
        public static final MapCodec<LevelsSquared> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply((Applicative)$$0, LevelsSquared::new));

        @Override
        public float calculate(int $$0) {
            return (float)Mth.square($$0) + this.added;
        }

        public MapCodec<LevelsSquared> codec() {
            return CODEC;
        }
    }

    public record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue
    {
        public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.FLOAT.fieldOf("base").forGetter(Linear::base), (App)Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply((Applicative)$$0, Linear::new));

        @Override
        public float calculate(int $$0) {
            return this.base + this.perLevelAboveFirst * (float)($$0 - 1);
        }

        public MapCodec<Linear> codec() {
            return CODEC;
        }
    }

    public record Lookup(List<Float> values, LevelBasedValue fallback) implements LevelBasedValue
    {
        public static final MapCodec<Lookup> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.FLOAT.listOf().fieldOf("values").forGetter(Lookup::values), (App)CODEC.fieldOf("fallback").forGetter(Lookup::fallback)).apply((Applicative)$$0, Lookup::new));

        @Override
        public float calculate(int $$0) {
            return $$0 <= this.values.size() ? this.values.get($$0 - 1).floatValue() : this.fallback.calculate($$0);
        }

        public MapCodec<Lookup> codec() {
            return CODEC;
        }
    }

    public record Constant(float value) implements LevelBasedValue
    {
        public static final Codec<Constant> CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
        public static final MapCodec<Constant> TYPED_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply((Applicative)$$0, Constant::new));

        @Override
        public float calculate(int $$0) {
            return this.value;
        }

        public MapCodec<Constant> codec() {
            return TYPED_CODEC;
        }
    }
}

