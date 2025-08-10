/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.ButtonListDialogScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.MultiActionDialog;

public class MultiButtonDialogScreen
extends ButtonListDialogScreen<MultiActionDialog> {
    public MultiButtonDialogScreen(@Nullable Screen $$0, MultiActionDialog $$1, DialogConnectionAccess $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected Stream<ActionButton> createListActions(MultiActionDialog $$0, DialogConnectionAccess $$1) {
        return $$0.actions().stream();
    }
}

