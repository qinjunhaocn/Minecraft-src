/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.screens.worldselection;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class SwitchGrid {
    private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 44;
    private final List<LabeledSwitch> switches;
    private final Layout layout;

    SwitchGrid(List<LabeledSwitch> $$0, Layout $$1) {
        this.switches = $$0;
        this.layout = $$1;
    }

    public Layout layout() {
        return this.layout;
    }

    public void refreshStates() {
        this.switches.forEach(LabeledSwitch::refreshState);
    }

    public static Builder builder(int $$0) {
        return new Builder($$0);
    }

    public static class Builder {
        final int width;
        private final List<SwitchBuilder> switchBuilders = new ArrayList<SwitchBuilder>();
        int paddingLeft;
        int rowSpacing = 4;
        int rowCount;
        Optional<InfoUnderneathSettings> infoUnderneath = Optional.empty();

        public Builder(int $$0) {
            this.width = $$0;
        }

        void increaseRow() {
            ++this.rowCount;
        }

        public SwitchBuilder addSwitch(Component $$0, BooleanSupplier $$1, Consumer<Boolean> $$2) {
            SwitchBuilder $$3 = new SwitchBuilder($$0, $$1, $$2, 44);
            this.switchBuilders.add($$3);
            return $$3;
        }

        public Builder withPaddingLeft(int $$0) {
            this.paddingLeft = $$0;
            return this;
        }

        public Builder withRowSpacing(int $$0) {
            this.rowSpacing = $$0;
            return this;
        }

        public SwitchGrid build() {
            GridLayout $$0 = new GridLayout().rowSpacing(this.rowSpacing);
            $$0.addChild(SpacerElement.width(this.width - 44), 0, 0);
            $$0.addChild(SpacerElement.width(44), 0, 1);
            ArrayList<LabeledSwitch> $$1 = new ArrayList<LabeledSwitch>();
            this.rowCount = 0;
            for (SwitchBuilder $$2 : this.switchBuilders) {
                $$1.add($$2.build(this, $$0, 0));
            }
            $$0.arrangeElements();
            SwitchGrid $$3 = new SwitchGrid($$1, $$0);
            $$3.refreshStates();
            return $$3;
        }

        public Builder withInfoUnderneath(int $$0, boolean $$1) {
            this.infoUnderneath = Optional.of(new InfoUnderneathSettings($$0, $$1));
            return this;
        }
    }

    static final class InfoUnderneathSettings
    extends Record {
        final int maxInfoRows;
        final boolean alwaysMaxHeight;

        InfoUnderneathSettings(int $$0, boolean $$1) {
            this.maxInfoRows = $$0;
            this.alwaysMaxHeight = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{InfoUnderneathSettings.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InfoUnderneathSettings.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InfoUnderneathSettings.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this, $$0);
        }

        public int maxInfoRows() {
            return this.maxInfoRows;
        }

        public boolean alwaysMaxHeight() {
            return this.alwaysMaxHeight;
        }
    }

    record LabeledSwitch(CycleButton<Boolean> button, BooleanSupplier stateSupplier, @Nullable BooleanSupplier isActiveCondition) {
        public void refreshState() {
            this.button.setValue(this.stateSupplier.getAsBoolean());
            if (this.isActiveCondition != null) {
                this.button.active = this.isActiveCondition.getAsBoolean();
            }
        }

        @Nullable
        public BooleanSupplier isActiveCondition() {
            return this.isActiveCondition;
        }
    }

    public static class SwitchBuilder {
        private final Component label;
        private final BooleanSupplier stateSupplier;
        private final Consumer<Boolean> onClicked;
        @Nullable
        private Component info;
        @Nullable
        private BooleanSupplier isActiveCondition;
        private final int buttonWidth;

        SwitchBuilder(Component $$0, BooleanSupplier $$1, Consumer<Boolean> $$2, int $$3) {
            this.label = $$0;
            this.stateSupplier = $$1;
            this.onClicked = $$2;
            this.buttonWidth = $$3;
        }

        public SwitchBuilder withIsActiveCondition(BooleanSupplier $$0) {
            this.isActiveCondition = $$0;
            return this;
        }

        public SwitchBuilder withInfo(Component $$0) {
            this.info = $$0;
            return this;
        }

        LabeledSwitch build(Builder $$02, GridLayout $$12, int $$2) {
            boolean $$6;
            $$02.increaseRow();
            StringWidget $$32 = new StringWidget(this.label, Minecraft.getInstance().font).alignLeft();
            $$12.addChild($$32, $$02.rowCount, $$2, $$12.newCellSettings().align(0.0f, 0.5f).paddingLeft($$02.paddingLeft));
            Optional<InfoUnderneathSettings> $$4 = $$02.infoUnderneath;
            CycleButton.Builder<Boolean> $$5 = CycleButton.onOffBuilder(this.stateSupplier.getAsBoolean());
            $$5.displayOnlyValue();
            boolean bl = $$6 = this.info != null && $$4.isEmpty();
            if ($$6) {
                Tooltip $$7 = Tooltip.create(this.info);
                $$5.withTooltip($$1 -> $$7);
            }
            if (this.info != null && !$$6) {
                $$5.withCustomNarration($$0 -> CommonComponents.a(this.label, $$0.createDefaultNarrationMessage(), this.info));
            } else {
                $$5.withCustomNarration($$0 -> CommonComponents.a(this.label, $$0.createDefaultNarrationMessage()));
            }
            CycleButton<Boolean> $$8 = $$5.create(0, 0, this.buttonWidth, 20, Component.empty(), ($$0, $$1) -> this.onClicked.accept((Boolean)$$1));
            if (this.isActiveCondition != null) {
                $$8.active = this.isActiveCondition.getAsBoolean();
            }
            $$12.addChild($$8, $$02.rowCount, $$2 + 1, $$12.newCellSettings().alignHorizontallyRight());
            if (this.info != null) {
                $$4.ifPresent($$3 -> {
                    MutableComponent $$4 = this.info.copy().withStyle(ChatFormatting.GRAY);
                    Font $$5 = Minecraft.getInstance().font;
                    MultiLineTextWidget $$6 = new MultiLineTextWidget($$4, $$5);
                    $$6.setMaxWidth($$0.width - $$0.paddingLeft - this.buttonWidth);
                    $$6.setMaxRows($$3.maxInfoRows());
                    $$02.increaseRow();
                    int $$7 = $$3.alwaysMaxHeight ? $$5.lineHeight * $$3.maxInfoRows - $$6.getHeight() : 0;
                    $$12.addChild($$6, $$0.rowCount, $$2, $$12.newCellSettings().paddingTop(-$$0.rowSpacing).paddingBottom($$7));
                });
            }
            return new LabeledSwitch($$8, this.stateSupplier, this.isActiveCondition);
        }
    }
}

