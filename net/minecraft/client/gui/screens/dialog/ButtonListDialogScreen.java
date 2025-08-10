/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogControlSet;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.ButtonListDialog;

public abstract class ButtonListDialogScreen<T extends ButtonListDialog>
extends DialogScreen<T> {
    public static final int FOOTER_MARGIN = 5;

    public ButtonListDialogScreen(@Nullable Screen $$0, T $$1, DialogConnectionAccess $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void populateBodyElements(LinearLayout $$0, DialogControlSet $$12, T $$2, DialogConnectionAccess $$3) {
        super.populateBodyElements($$0, $$12, $$2, $$3);
        List $$4 = this.createListActions($$2, $$3).map($$1 -> $$12.createActionButton((ActionButton)((Object)$$1)).build()).toList();
        $$0.addChild(ButtonListDialogScreen.packControlsIntoColumns($$4, $$2.columns()));
    }

    protected abstract Stream<ActionButton> createListActions(T var1, DialogConnectionAccess var2);

    @Override
    protected void updateHeaderAndFooter(HeaderAndFooterLayout $$0, DialogControlSet $$1, T $$22, DialogConnectionAccess $$3) {
        super.updateHeaderAndFooter($$0, $$1, $$22, $$3);
        $$22.exitAction().ifPresentOrElse($$2 -> $$0.addToFooter($$1.createActionButton((ActionButton)((Object)$$2)).build()), () -> $$0.setFooterHeight(5));
    }
}

