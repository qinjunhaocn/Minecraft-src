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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.ServerLinksDialog;
import net.minecraft.server.dialog.action.StaticAction;

public class ServerLinksDialogScreen
extends ButtonListDialogScreen<ServerLinksDialog> {
    public ServerLinksDialogScreen(@Nullable Screen $$0, ServerLinksDialog $$1, DialogConnectionAccess $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected Stream<ActionButton> createListActions(ServerLinksDialog $$0, DialogConnectionAccess $$12) {
        return $$12.serverLinks().entries().stream().map($$1 -> ServerLinksDialogScreen.createDialogClickAction($$0, $$1));
    }

    private static ActionButton createDialogClickAction(ServerLinksDialog $$0, ServerLinks.Entry $$1) {
        return new ActionButton(new CommonButtonData($$1.displayName(), $$0.buttonWidth()), Optional.of(new StaticAction(new ClickEvent.OpenUrl($$1.link()))));
    }
}

