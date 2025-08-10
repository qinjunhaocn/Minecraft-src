/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.gui.screens.dialog.input;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandler;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;
import org.slf4j.Logger;

public class InputControlHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MapCodec<? extends InputControl>, InputControlHandler<?>> HANDLERS = new HashMap();

    private static <T extends InputControl> void register(MapCodec<T> $$0, InputControlHandler<? super T> $$1) {
        HANDLERS.put($$0, $$1);
    }

    @Nullable
    private static <T extends InputControl> InputControlHandler<T> get(T $$0) {
        return HANDLERS.get($$0.mapCodec());
    }

    public static <T extends InputControl> void createHandler(T $$0, Screen $$1, InputControlHandler.Output $$2) {
        InputControlHandler<T> $$3 = InputControlHandlers.get($$0);
        if ($$3 == null) {
            LOGGER.warn("Unrecognized input control {}", (Object)$$0);
            return;
        }
        $$3.addControl($$0, $$1, $$2);
    }

    public static void bootstrap() {
        InputControlHandlers.register(TextInput.MAP_CODEC, new TextInputHandler());
        InputControlHandlers.register(SingleOptionInput.MAP_CODEC, new SingleOptionHandler());
        InputControlHandlers.register(BooleanInput.MAP_CODEC, new BooleanHandler());
        InputControlHandlers.register(NumberRangeInput.MAP_CODEC, new NumberRangeHandler());
    }

    static class TextInputHandler
    implements InputControlHandler<TextInput> {
        TextInputHandler() {
        }

        @Override
        public void addControl(TextInput $$0, Screen $$1, InputControlHandler.Output $$2) {
            Supplier<String> $$11;
            EditBox $$10;
            Font $$3 = $$1.getFont();
            if ($$0.multiline().isPresent()) {
                TextInput.MultilineOptions $$4 = $$0.multiline().get();
                int $$5 = $$4.height().orElseGet(() -> {
                    int $$2 = $$4.maxLines().orElse(4);
                    return Math.min($$1.lineHeight * $$2 + 8, 512);
                });
                MultiLineEditBox $$6 = MultiLineEditBox.builder().build($$3, $$0.width(), $$5, CommonComponents.EMPTY);
                $$6.setCharacterLimit($$0.maxLength());
                $$4.maxLines().ifPresent($$6::setLineLimit);
                $$6.setValue($$0.initial());
                MultiLineEditBox $$7 = $$6;
                Supplier<String> $$8 = $$6::getValue;
            } else {
                EditBox $$9 = new EditBox($$3, $$0.width(), 20, $$0.label());
                $$9.setMaxLength($$0.maxLength());
                $$9.setValue($$0.initial());
                $$10 = $$9;
                $$11 = $$9::getValue;
            }
            EditBox $$12 = $$0.labelVisible() ? CommonLayouts.labeledElement($$3, $$10, $$0.label()) : $$10;
            $$2.accept($$12, new Action.ValueGetter(){

                @Override
                public String asTemplateSubstitution() {
                    return StringTag.escapeWithoutQuotes((String)$$11.get());
                }

                @Override
                public Tag asTag() {
                    return StringTag.valueOf((String)$$11.get());
                }
            });
        }

        @Override
        public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((TextInput)inputControl, screen, output);
        }
    }

    static class SingleOptionHandler
    implements InputControlHandler<SingleOptionInput> {
        SingleOptionHandler() {
        }

        @Override
        public void addControl(SingleOptionInput $$0, Screen $$1, InputControlHandler.Output $$2) {
            CycleButton.Builder<SingleOptionInput.Entry> $$3 = CycleButton.builder(SingleOptionInput.Entry::displayOrDefault).withValues($$0.entries()).displayOnlyValue(!$$0.labelVisible());
            Optional<SingleOptionInput.Entry> $$4 = $$0.initial();
            if ($$4.isPresent()) {
                $$3 = $$3.withInitialValue($$4.get());
            }
            CycleButton<SingleOptionInput.Entry> $$5 = $$3.create(0, 0, $$0.width(), 20, $$0.label());
            $$2.accept($$5, Action.ValueGetter.of(() -> ((SingleOptionInput.Entry)((Object)((Object)$$5.getValue()))).id()));
        }

        @Override
        public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((SingleOptionInput)inputControl, screen, output);
        }
    }

    static class BooleanHandler
    implements InputControlHandler<BooleanInput> {
        BooleanHandler() {
        }

        @Override
        public void addControl(final BooleanInput $$0, Screen $$1, InputControlHandler.Output $$2) {
            Font $$3 = $$1.getFont();
            final Checkbox $$4 = Checkbox.builder($$0.label(), $$3).selected($$0.initial()).build();
            $$2.accept($$4, new Action.ValueGetter(){

                @Override
                public String asTemplateSubstitution() {
                    return $$4.selected() ? $$0.onTrue() : $$0.onFalse();
                }

                @Override
                public Tag asTag() {
                    return ByteTag.valueOf($$4.selected());
                }
            });
        }

        @Override
        public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((BooleanInput)inputControl, screen, output);
        }
    }

    static class NumberRangeHandler
    implements InputControlHandler<NumberRangeInput> {
        NumberRangeHandler() {
        }

        @Override
        public void addControl(NumberRangeInput $$0, Screen $$1, InputControlHandler.Output $$2) {
            float $$3 = $$0.rangeInfo().initialSliderValue();
            final SliderImpl $$4 = new SliderImpl($$0, $$3);
            $$2.accept($$4, new Action.ValueGetter(){

                @Override
                public String asTemplateSubstitution() {
                    return $$4.stringValueToSend();
                }

                @Override
                public Tag asTag() {
                    return FloatTag.valueOf($$4.floatValueToSend());
                }
            });
        }

        @Override
        public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
            this.addControl((NumberRangeInput)inputControl, screen, output);
        }

        static class SliderImpl
        extends AbstractSliderButton {
            private final NumberRangeInput input;

            SliderImpl(NumberRangeInput $$0, double $$1) {
                super(0, 0, $$0.width(), 20, SliderImpl.computeMessage($$0, $$1), $$1);
                this.input = $$0;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(SliderImpl.computeMessage(this.input, this.value));
            }

            @Override
            protected void applyValue() {
            }

            public String stringValueToSend() {
                return SliderImpl.sliderValueToString(this.input, this.value);
            }

            public float floatValueToSend() {
                return SliderImpl.scaledValue(this.input, this.value);
            }

            private static float scaledValue(NumberRangeInput $$0, double $$1) {
                return $$0.rangeInfo().computeScaledValue((float)$$1);
            }

            private static String sliderValueToString(NumberRangeInput $$0, double $$1) {
                return SliderImpl.valueToString(SliderImpl.scaledValue($$0, $$1));
            }

            private static Component computeMessage(NumberRangeInput $$0, double $$1) {
                return $$0.computeLabel(SliderImpl.sliderValueToString($$0, $$1));
            }

            private static String valueToString(float $$0) {
                int $$1 = (int)$$0;
                if ((float)$$1 == $$0) {
                    return Integer.toString($$1);
                }
                return Float.toString($$0);
            }
        }
    }
}

