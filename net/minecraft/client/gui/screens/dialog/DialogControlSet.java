/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.action.Action;

public class DialogControlSet {
    public static final Supplier<Optional<ClickEvent>> EMPTY_ACTION = Optional::empty;
    private final DialogScreen<?> screen;
    private final Map<String, Action.ValueGetter> valueGetters = new HashMap<String, Action.ValueGetter>();

    public DialogControlSet(DialogScreen<?> $$0) {
        this.screen = $$0;
    }

    public void addInput(Input $$0, Consumer<LayoutElement> $$1) {
        String $$22 = $$0.key();
        InputControlHandlers.createHandler($$0.control(), this.screen, ($$2, $$3) -> {
            this.valueGetters.put($$22, $$3);
            $$1.accept($$2);
        });
    }

    private static Button.Builder createDialogButton(CommonButtonData $$0, Button.OnPress $$1) {
        Button.Builder $$2 = Button.builder($$0.label(), $$1);
        $$2.width($$0.width());
        if ($$0.tooltip().isPresent()) {
            $$2 = $$2.tooltip(Tooltip.create($$0.tooltip().get()));
        }
        return $$2;
    }

    public Supplier<Optional<ClickEvent>> bindAction(Optional<Action> $$0) {
        if ($$0.isPresent()) {
            Action $$1 = $$0.get();
            return () -> $$1.createAction(this.valueGetters);
        }
        return EMPTY_ACTION;
    }

    public Button.Builder createActionButton(ActionButton $$0) {
        Supplier<Optional<ClickEvent>> $$12 = this.bindAction($$0.action());
        return DialogControlSet.createDialogButton($$0.button(), $$1 -> this.screen.runAction((Optional)$$12.get()));
    }
}

