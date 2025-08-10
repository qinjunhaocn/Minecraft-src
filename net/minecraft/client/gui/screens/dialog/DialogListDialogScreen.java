/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.ButtonListDialogScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.action.StaticAction;

public class DialogListDialogScreen
extends ButtonListDialogScreen<DialogListDialog> {
    public DialogListDialogScreen(@Nullable Screen $$0, DialogListDialog $$1, DialogConnectionAccess $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected Stream<ActionButton> createListActions(DialogListDialog $$0, DialogConnectionAccess $$12) {
        return $$0.dialogs().stream().map($$1 -> DialogListDialogScreen.createDialogClickAction($$0, $$1));
    }

    private static ActionButton createDialogClickAction(DialogListDialog $$0, Holder<Dialog> $$1) {
        return new ActionButton(new CommonButtonData($$1.value().common().computeExternalTitle(), $$0.buttonWidth()), Optional.of(new StaticAction(new ClickEvent.ShowDialog($$1))));
    }
}

