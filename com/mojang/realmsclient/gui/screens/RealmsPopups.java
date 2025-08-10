/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class RealmsPopups {
    private static final int COLOR_INFO = 8226750;
    private static final Component INFO = Component.translatable("mco.info").withColor(8226750);
    private static final Component WARNING = Component.translatable("mco.warning").withColor(-65536);

    public static PopupScreen customPopupScreen(Screen $$0, Component $$1, Component $$2, Consumer<PopupScreen> $$3) {
        return new PopupScreen.Builder($$0, $$1).setMessage($$2).addButton(CommonComponents.GUI_CONTINUE, $$3).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build();
    }

    public static PopupScreen infoPopupScreen(Screen $$0, Component $$1, Consumer<PopupScreen> $$2) {
        return new PopupScreen.Builder($$0, INFO).setMessage($$1).addButton(CommonComponents.GUI_CONTINUE, $$2).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build();
    }

    public static PopupScreen warningPopupScreen(Screen $$0, Component $$1, Consumer<PopupScreen> $$2) {
        return new PopupScreen.Builder($$0, WARNING).setMessage($$1).addButton(CommonComponents.GUI_CONTINUE, $$2).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build();
    }

    public static PopupScreen warningAcknowledgePopupScreen(Screen $$0, Component $$1, Consumer<PopupScreen> $$2) {
        return new PopupScreen.Builder($$0, WARNING).setMessage($$1).addButton(CommonComponents.GUI_OK, $$2).build();
    }
}

