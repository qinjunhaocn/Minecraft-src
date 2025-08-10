/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogControlSet;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.SimpleDialog;

public class SimpleDialogScreen<T extends SimpleDialog>
extends DialogScreen<T> {
    public SimpleDialogScreen(@Nullable Screen $$0, T $$1, DialogConnectionAccess $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void updateHeaderAndFooter(HeaderAndFooterLayout $$0, DialogControlSet $$1, T $$2, DialogConnectionAccess $$3) {
        super.updateHeaderAndFooter($$0, $$1, $$2, $$3);
        LinearLayout $$4 = LinearLayout.horizontal().spacing(8);
        for (ActionButton $$5 : $$2.mainActions()) {
            $$4.addChild($$1.createActionButton($$5).build());
        }
        $$0.addToFooter($$4);
    }
}

