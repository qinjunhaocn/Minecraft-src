/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class CycleButton<T>
extends AbstractButton {
    public static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = Screen::hasAltDown;
    private static final List<Boolean> BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    private final Component name;
    private int index;
    private T value;
    private final ValueListSupplier<T> values;
    private final Function<T, Component> valueStringifier;
    private final Function<CycleButton<T>, MutableComponent> narrationProvider;
    private final OnValueChange<T> onValueChange;
    private final boolean displayOnlyValue;
    private final OptionInstance.TooltipSupplier<T> tooltipSupplier;

    CycleButton(int $$0, int $$1, int $$2, int $$3, Component $$4, Component $$5, int $$6, T $$7, ValueListSupplier<T> $$8, Function<T, Component> $$9, Function<CycleButton<T>, MutableComponent> $$10, OnValueChange<T> $$11, OptionInstance.TooltipSupplier<T> $$12, boolean $$13) {
        super($$0, $$1, $$2, $$3, $$4);
        this.name = $$5;
        this.index = $$6;
        this.value = $$7;
        this.values = $$8;
        this.valueStringifier = $$9;
        this.narrationProvider = $$10;
        this.onValueChange = $$11;
        this.displayOnlyValue = $$13;
        this.tooltipSupplier = $$12;
        this.updateTooltip();
    }

    private void updateTooltip() {
        this.setTooltip(this.tooltipSupplier.apply(this.value));
    }

    @Override
    public void onPress() {
        if (Screen.hasShiftDown()) {
            this.cycleValue(-1);
        } else {
            this.cycleValue(1);
        }
    }

    private void cycleValue(int $$0) {
        List<T> $$1 = this.values.getSelectedList();
        this.index = Mth.positiveModulo(this.index + $$0, $$1.size());
        T $$2 = $$1.get(this.index);
        this.updateValue($$2);
        this.onValueChange.onValueChange(this, $$2);
    }

    private T getCycledValue(int $$0) {
        List<T> $$1 = this.values.getSelectedList();
        return $$1.get(Mth.positiveModulo(this.index + $$0, $$1.size()));
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if ($$3 > 0.0) {
            this.cycleValue(-1);
        } else if ($$3 < 0.0) {
            this.cycleValue(1);
        }
        return true;
    }

    public void setValue(T $$0) {
        List<T> $$1 = this.values.getSelectedList();
        int $$2 = $$1.indexOf($$0);
        if ($$2 != -1) {
            this.index = $$2;
        }
        this.updateValue($$0);
    }

    private void updateValue(T $$0) {
        Component $$1 = this.createLabelForValue($$0);
        this.setMessage($$1);
        this.value = $$0;
        this.updateTooltip();
    }

    private Component createLabelForValue(T $$0) {
        return this.displayOnlyValue ? this.valueStringifier.apply($$0) : this.createFullName($$0);
    }

    private MutableComponent createFullName(T $$0) {
        return CommonComponents.optionNameValue(this.name, this.valueStringifier.apply($$0));
    }

    public T getValue() {
        return this.value;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return this.narrationProvider.apply(this);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            T $$1 = this.getCycledValue(1);
            Component $$2 = this.createLabelForValue($$1);
            if (this.isFocused()) {
                $$0.add(NarratedElementType.USAGE, Component.a("narration.cycle_button.usage.focused", $$2));
            } else {
                $$0.add(NarratedElementType.USAGE, Component.a("narration.cycle_button.usage.hovered", $$2));
            }
        }
    }

    public MutableComponent createDefaultNarrationMessage() {
        return CycleButton.wrapDefaultNarrationMessage(this.displayOnlyValue ? this.createFullName(this.value) : this.getMessage());
    }

    public static <T> Builder<T> builder(Function<T, Component> $$0) {
        return new Builder<T>($$0);
    }

    public static Builder<Boolean> booleanBuilder(Component $$0, Component $$1) {
        return new Builder<Boolean>($$2 -> $$2 != false ? $$0 : $$1).withValues(BOOLEAN_OPTIONS);
    }

    public static Builder<Boolean> onOffBuilder() {
        return new Builder<Boolean>($$0 -> $$0 != false ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF).withValues(BOOLEAN_OPTIONS);
    }

    public static Builder<Boolean> onOffBuilder(boolean $$0) {
        return CycleButton.onOffBuilder().withInitialValue($$0);
    }

    public static interface ValueListSupplier<T> {
        public List<T> getSelectedList();

        public List<T> getDefaultList();

        public static <T> ValueListSupplier<T> create(Collection<T> $$0) {
            final ImmutableList<T> $$1 = ImmutableList.copyOf($$0);
            return new ValueListSupplier<T>(){

                @Override
                public List<T> getSelectedList() {
                    return $$1;
                }

                @Override
                public List<T> getDefaultList() {
                    return $$1;
                }
            };
        }

        public static <T> ValueListSupplier<T> create(final BooleanSupplier $$0, List<T> $$1, List<T> $$2) {
            final ImmutableList<T> $$3 = ImmutableList.copyOf($$1);
            final ImmutableList<T> $$4 = ImmutableList.copyOf($$2);
            return new ValueListSupplier<T>(){

                @Override
                public List<T> getSelectedList() {
                    return $$0.getAsBoolean() ? $$4 : $$3;
                }

                @Override
                public List<T> getDefaultList() {
                    return $$3;
                }
            };
        }
    }

    @FunctionalInterface
    public static interface OnValueChange<T> {
        public void onValueChange(CycleButton<T> var1, T var2);
    }

    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T initialValue;
        private final Function<T, Component> valueStringifier;
        private OptionInstance.TooltipSupplier<T> tooltipSupplier = $$0 -> null;
        private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
        private ValueListSupplier<T> values = ValueListSupplier.create(ImmutableList.of());
        private boolean displayOnlyValue;

        public Builder(Function<T, Component> $$02) {
            this.valueStringifier = $$02;
        }

        public Builder<T> withValues(Collection<T> $$0) {
            return this.withValues(ValueListSupplier.create($$0));
        }

        @SafeVarargs
        public final Builder<T> a(T ... $$0) {
            return this.withValues(ImmutableList.copyOf($$0));
        }

        public Builder<T> withValues(List<T> $$0, List<T> $$1) {
            return this.withValues(ValueListSupplier.create(DEFAULT_ALT_LIST_SELECTOR, $$0, $$1));
        }

        public Builder<T> withValues(BooleanSupplier $$0, List<T> $$1, List<T> $$2) {
            return this.withValues(ValueListSupplier.create($$0, $$1, $$2));
        }

        public Builder<T> withValues(ValueListSupplier<T> $$0) {
            this.values = $$0;
            return this;
        }

        public Builder<T> withTooltip(OptionInstance.TooltipSupplier<T> $$0) {
            this.tooltipSupplier = $$0;
            return this;
        }

        public Builder<T> withInitialValue(T $$0) {
            this.initialValue = $$0;
            int $$1 = this.values.getDefaultList().indexOf($$0);
            if ($$1 != -1) {
                this.initialIndex = $$1;
            }
            return this;
        }

        public Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> $$0) {
            this.narrationProvider = $$0;
            return this;
        }

        public Builder<T> displayOnlyValue(boolean $$0) {
            this.displayOnlyValue = $$0;
            return this;
        }

        public Builder<T> displayOnlyValue() {
            return this.displayOnlyValue(true);
        }

        public CycleButton<T> create(Component $$0, OnValueChange<T> $$1) {
            return this.create(0, 0, 150, 20, $$0, $$1);
        }

        public CycleButton<T> create(int $$02, int $$12, int $$2, int $$3, Component $$4) {
            return this.create($$02, $$12, $$2, $$3, $$4, ($$0, $$1) -> {});
        }

        public CycleButton<T> create(int $$0, int $$1, int $$2, int $$3, Component $$4, OnValueChange<T> $$5) {
            List<T> $$6 = this.values.getDefaultList();
            if ($$6.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            }
            T $$7 = this.initialValue != null ? this.initialValue : $$6.get(this.initialIndex);
            Component $$8 = this.valueStringifier.apply($$7);
            Component $$9 = this.displayOnlyValue ? $$8 : CommonComponents.optionNameValue($$4, $$8);
            return new CycleButton<T>($$0, $$1, $$2, $$3, $$9, $$4, this.initialIndex, $$7, this.values, this.valueStringifier, this.narrationProvider, $$5, this.tooltipSupplier, this.displayOnlyValue);
        }
    }
}

