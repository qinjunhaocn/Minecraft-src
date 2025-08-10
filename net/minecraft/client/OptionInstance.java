/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import org.slf4j.Logger;

public final class OptionInstance<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Enum<Boolean> BOOLEAN_VALUES = new Enum<Boolean>((List<Boolean>)ImmutableList.of(Boolean.TRUE, Boolean.FALSE), (Codec<Boolean>)Codec.BOOL);
    public static final CaptionBasedToString<Boolean> BOOLEAN_TO_STRING = ($$0, $$1) -> $$1 != false ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
    private final TooltipSupplier<T> tooltip;
    final Function<T, Component> toString;
    private final ValueSet<T> values;
    private final Codec<T> codec;
    private final T initialValue;
    private final Consumer<T> onValueUpdate;
    final Component caption;
    T value;

    public static OptionInstance<Boolean> createBoolean(String $$0, boolean $$1, Consumer<Boolean> $$2) {
        return OptionInstance.createBoolean($$0, OptionInstance.noTooltip(), $$1, $$2);
    }

    public static OptionInstance<Boolean> createBoolean(String $$02, boolean $$1) {
        return OptionInstance.createBoolean($$02, OptionInstance.noTooltip(), $$1, $$0 -> {});
    }

    public static OptionInstance<Boolean> createBoolean(String $$02, TooltipSupplier<Boolean> $$1, boolean $$2) {
        return OptionInstance.createBoolean($$02, $$1, $$2, $$0 -> {});
    }

    public static OptionInstance<Boolean> createBoolean(String $$0, TooltipSupplier<Boolean> $$1, boolean $$2, Consumer<Boolean> $$3) {
        return OptionInstance.createBoolean($$0, $$1, BOOLEAN_TO_STRING, $$2, $$3);
    }

    public static OptionInstance<Boolean> createBoolean(String $$0, TooltipSupplier<Boolean> $$1, CaptionBasedToString<Boolean> $$2, boolean $$3, Consumer<Boolean> $$4) {
        return new OptionInstance<Boolean>($$0, $$1, $$2, BOOLEAN_VALUES, $$3, $$4);
    }

    public OptionInstance(String $$0, TooltipSupplier<T> $$1, CaptionBasedToString<T> $$2, ValueSet<T> $$3, T $$4, Consumer<T> $$5) {
        this($$0, $$1, $$2, $$3, $$3.codec(), $$4, $$5);
    }

    public OptionInstance(String $$0, TooltipSupplier<T> $$12, CaptionBasedToString<T> $$2, ValueSet<T> $$3, Codec<T> $$4, T $$5, Consumer<T> $$6) {
        this.caption = Component.translatable($$0);
        this.tooltip = $$12;
        this.toString = $$1 -> $$2.toString(this.caption, $$1);
        this.values = $$3;
        this.codec = $$4;
        this.initialValue = $$5;
        this.onValueUpdate = $$6;
        this.value = this.initialValue;
    }

    public static <T> TooltipSupplier<T> noTooltip() {
        return $$0 -> null;
    }

    public static <T> TooltipSupplier<T> cachedConstantTooltip(Component $$0) {
        return $$1 -> Tooltip.create($$0);
    }

    public static <T extends OptionEnum> CaptionBasedToString<T> forOptionEnum() {
        return ($$0, $$1) -> $$1.getCaption();
    }

    public AbstractWidget createButton(Options $$0) {
        return this.createButton($$0, 0, 0, 150);
    }

    public AbstractWidget createButton(Options $$02, int $$1, int $$2, int $$3) {
        return this.createButton($$02, $$1, $$2, $$3, $$0 -> {});
    }

    public AbstractWidget createButton(Options $$0, int $$1, int $$2, int $$3, Consumer<T> $$4) {
        return this.values.createButton(this.tooltip, $$0, $$1, $$2, $$3, $$4).apply(this);
    }

    public T get() {
        return this.value;
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public String toString() {
        return this.caption.getString();
    }

    public void set(T $$0) {
        Object $$1 = this.values.validateValue($$0).orElseGet(() -> {
            LOGGER.error("Illegal option value " + String.valueOf($$0) + " for " + String.valueOf(this.caption));
            return this.initialValue;
        });
        if (!Minecraft.getInstance().isRunning()) {
            this.value = $$1;
            return;
        }
        if (!Objects.equals(this.value, $$1)) {
            this.value = $$1;
            this.onValueUpdate.accept(this.value);
        }
    }

    public ValueSet<T> values() {
        return this.values;
    }

    @FunctionalInterface
    public static interface TooltipSupplier<T> {
        @Nullable
        public Tooltip apply(T var1);
    }

    public static interface CaptionBasedToString<T> {
        public Component toString(Component var1, T var2);
    }

    public record Enum<T>(List<T> values, Codec<T> codec) implements CycleableValueSet<T>
    {
        @Override
        public Optional<T> validateValue(T $$0) {
            return this.values.contains($$0) ? Optional.of($$0) : Optional.empty();
        }

        @Override
        public CycleButton.ValueListSupplier<T> valueListSupplier() {
            return CycleButton.ValueListSupplier.create(this.values);
        }
    }

    static interface ValueSet<T> {
        public Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6);

        public Optional<T> validateValue(T var1);

        public Codec<T> codec();
    }

    public static final class UnitDouble
    extends java.lang.Enum<UnitDouble>
    implements SliderableValueSet<Double> {
        public static final /* enum */ UnitDouble INSTANCE = new UnitDouble();
        private static final /* synthetic */ UnitDouble[] $VALUES;

        public static UnitDouble[] values() {
            return (UnitDouble[])$VALUES.clone();
        }

        public static UnitDouble valueOf(String $$0) {
            return java.lang.Enum.valueOf(UnitDouble.class, $$0);
        }

        @Override
        public Optional<Double> validateValue(Double $$0) {
            return $$0 >= 0.0 && $$0 <= 1.0 ? Optional.of($$0) : Optional.empty();
        }

        @Override
        public double toSliderValue(Double $$0) {
            return $$0;
        }

        @Override
        public Double fromSliderValue(double $$0) {
            return $$0;
        }

        public <R> SliderableValueSet<R> xmap(final DoubleFunction<? extends R> $$0, final ToDoubleFunction<? super R> $$1) {
            return new SliderableValueSet<R>(){

                @Override
                public Optional<R> validateValue(R $$02) {
                    return this.validateValue($$1.applyAsDouble($$02)).map($$0::apply);
                }

                @Override
                public double toSliderValue(R $$02) {
                    return this.toSliderValue($$1.applyAsDouble($$02));
                }

                @Override
                public R fromSliderValue(double $$02) {
                    return $$0.apply(this.fromSliderValue($$02));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap($$0::apply, $$1::applyAsDouble);
                }
            };
        }

        @Override
        public Codec<Double> codec() {
            return Codec.withAlternative((Codec)Codec.doubleRange((double)0.0, (double)1.0), (Codec)Codec.BOOL, $$0 -> $$0 != false ? 1.0 : 0.0);
        }

        @Override
        public /* synthetic */ Object fromSliderValue(double d) {
            return this.fromSliderValue(d);
        }

        private static /* synthetic */ UnitDouble[] b() {
            return new UnitDouble[]{INSTANCE};
        }

        static {
            $VALUES = UnitDouble.b();
        }
    }

    public record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntRangeBase,
    SliderableOrCyclableValueSet<Integer>
    {
        @Override
        public Optional<Integer> validateValue(Integer $$0) {
            return Optional.of(Mth.clamp($$0, this.minInclusive(), this.maxInclusive()));
        }

        @Override
        public int maxInclusive() {
            return this.maxSupplier.getAsInt();
        }

        @Override
        public Codec<Integer> codec() {
            return Codec.INT.validate($$0 -> {
                int $$1 = this.encodableMaxInclusive + 1;
                if ($$0.compareTo(this.minInclusive) >= 0 && $$0.compareTo($$1) <= 0) {
                    return DataResult.success((Object)$$0);
                }
                return DataResult.error(() -> "Value " + $$0 + " outside of range [" + this.minInclusive + ":" + $$1 + "]", (Object)$$0);
            });
        }

        @Override
        public boolean createCycleButton() {
            return true;
        }

        @Override
        public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
            return CycleButton.ValueListSupplier.create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
        }
    }

    public record IntRange(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements IntRangeBase
    {
        public IntRange(int $$0, int $$1) {
            this($$0, $$1, true);
        }

        @Override
        public Optional<Integer> validateValue(Integer $$0) {
            return $$0.compareTo(this.minInclusive()) >= 0 && $$0.compareTo(this.maxInclusive()) <= 0 ? Optional.of($$0) : Optional.empty();
        }

        @Override
        public Codec<Integer> codec() {
            return Codec.intRange((int)this.minInclusive, (int)(this.maxInclusive + 1));
        }
    }

    static interface IntRangeBase
    extends SliderableValueSet<Integer> {
        public int minInclusive();

        public int maxInclusive();

        @Override
        default public double toSliderValue(Integer $$0) {
            if ($$0.intValue() == this.minInclusive()) {
                return 0.0;
            }
            if ($$0.intValue() == this.maxInclusive()) {
                return 1.0;
            }
            return Mth.map((double)$$0.intValue() + 0.5, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0, 0.0, 1.0);
        }

        @Override
        default public Integer fromSliderValue(double $$0) {
            if ($$0 >= 1.0) {
                $$0 = 0.99999f;
            }
            return Mth.floor(Mth.map($$0, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0));
        }

        default public <R> SliderableValueSet<R> xmap(final IntFunction<? extends R> $$0, final ToIntFunction<? super R> $$1) {
            return new SliderableValueSet<R>(){

                @Override
                public Optional<R> validateValue(R $$02) {
                    return this.validateValue($$1.applyAsInt($$02)).map($$0::apply);
                }

                @Override
                public double toSliderValue(R $$02) {
                    return this.toSliderValue($$1.applyAsInt($$02));
                }

                @Override
                public R fromSliderValue(double $$02) {
                    return $$0.apply(this.fromSliderValue($$02));
                }

                @Override
                public Codec<R> codec() {
                    return this.codec().xmap($$0::apply, $$1::applyAsInt);
                }
            };
        }

        @Override
        default public /* synthetic */ Object fromSliderValue(double d) {
            return this.fromSliderValue(d);
        }
    }

    public static final class OptionInstanceSliderButton<N>
    extends AbstractOptionSliderButton {
        private final OptionInstance<N> instance;
        private final SliderableValueSet<N> values;
        private final TooltipSupplier<N> tooltipSupplier;
        private final Consumer<N> onValueChanged;
        @Nullable
        private Long delayedApplyAt;
        private final boolean applyValueImmediately;

        OptionInstanceSliderButton(Options $$0, int $$1, int $$2, int $$3, int $$4, OptionInstance<N> $$5, SliderableValueSet<N> $$6, TooltipSupplier<N> $$7, Consumer<N> $$8, boolean $$9) {
            super($$0, $$1, $$2, $$3, $$4, $$6.toSliderValue($$5.get()));
            this.instance = $$5;
            this.values = $$6;
            this.tooltipSupplier = $$7;
            this.onValueChanged = $$8;
            this.applyValueImmediately = $$9;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(this.instance.toString.apply(this.values.fromSliderValue(this.value)));
            this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
        }

        @Override
        protected void applyValue() {
            if (this.applyValueImmediately) {
                this.applyUnsavedValue();
            } else {
                this.delayedApplyAt = Util.getMillis() + 600L;
            }
        }

        public void applyUnsavedValue() {
            N $$0 = this.values.fromSliderValue(this.value);
            if (!Objects.equals($$0, this.instance.get())) {
                this.instance.set($$0);
                this.onValueChanged.accept(this.instance.get());
            }
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            super.renderWidget($$0, $$1, $$2, $$3);
            if (this.delayedApplyAt != null && Util.getMillis() >= this.delayedApplyAt) {
                this.delayedApplyAt = null;
                this.applyUnsavedValue();
            }
        }
    }

    public record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CycleableValueSet<T>
    {
        @Override
        public Optional<T> validateValue(T $$0) {
            return this.validateValue.apply($$0);
        }

        @Override
        public CycleButton.ValueListSupplier<T> valueListSupplier() {
            return CycleButton.ValueListSupplier.create((Collection)this.values.get());
        }
    }

    public record AltEnum<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) implements CycleableValueSet<T>
    {
        @Override
        public CycleButton.ValueListSupplier<T> valueListSupplier() {
            return CycleButton.ValueListSupplier.create(this.altCondition, this.values, this.altValues);
        }

        @Override
        public Optional<T> validateValue(T $$0) {
            return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains($$0) ? Optional.of($$0) : Optional.empty();
        }
    }

    static interface SliderableOrCyclableValueSet<T>
    extends CycleableValueSet<T>,
    SliderableValueSet<T> {
        public boolean createCycleButton();

        @Override
        default public Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> $$0, Options $$1, int $$2, int $$3, int $$4, Consumer<T> $$5) {
            if (this.createCycleButton()) {
                return CycleableValueSet.super.createButton($$0, $$1, $$2, $$3, $$4, $$5);
            }
            return SliderableValueSet.super.createButton($$0, $$1, $$2, $$3, $$4, $$5);
        }
    }

    static interface CycleableValueSet<T>
    extends ValueSet<T> {
        public CycleButton.ValueListSupplier<T> valueListSupplier();

        default public ValueSetter<T> valueSetter() {
            return OptionInstance::set;
        }

        @Override
        default public Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> $$0, Options $$1, int $$2, int $$3, int $$4, Consumer<T> $$5) {
            return $$6 -> CycleButton.builder($$6.toString).withValues(this.valueListSupplier()).withTooltip($$0).withInitialValue($$6.value).create($$2, $$3, $$4, 20, $$6.caption, ($$3, $$4) -> {
                this.valueSetter().set((OptionInstance<Object>)$$6, $$4);
                $$1.save();
                $$5.accept($$4);
            });
        }

        public static interface ValueSetter<T> {
            public void set(OptionInstance<T> var1, T var2);
        }
    }

    static interface SliderableValueSet<T>
    extends ValueSet<T> {
        public double toSliderValue(T var1);

        public T fromSliderValue(double var1);

        default public boolean applyValueImmediately() {
            return true;
        }

        @Override
        default public Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> $$0, Options $$1, int $$2, int $$3, int $$4, Consumer<T> $$5) {
            return $$6 -> new OptionInstanceSliderButton($$1, $$2, $$3, $$4, 20, $$6, this, $$0, $$5, this.applyValueImmediately());
        }
    }
}

