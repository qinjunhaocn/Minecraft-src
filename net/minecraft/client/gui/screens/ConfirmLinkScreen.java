/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfirmLinkScreen
extends ConfirmScreen {
    private static final Component COPY_BUTTON_TEXT = Component.translatable("chat.copy");
    private static final Component WARNING_TEXT = Component.translatable("chat.link.warning").withColor(-13108);
    private static final int BUTTON_WIDTH = 100;
    private final String url;
    private final boolean showWarning;

    public ConfirmLinkScreen(BooleanConsumer $$0, String $$1, boolean $$2) {
        this($$0, (Component)ConfirmLinkScreen.confirmMessage($$2), (Component)Component.literal($$1), $$1, $$2 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, $$2);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, String $$2, boolean $$3) {
        this($$0, $$1, (Component)ConfirmLinkScreen.confirmMessage($$3, $$2), $$2, $$3 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, $$3);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, URI $$2, boolean $$3) {
        this($$0, $$1, $$2.toString(), $$3);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, Component $$2, URI $$3, Component $$4, boolean $$5) {
        this($$0, $$1, $$2, $$3.toString(), $$4, true);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, Component $$2, String $$3, Component $$4, boolean $$5) {
        super($$0, $$1, $$2);
        this.yesButtonComponent = $$5 ? CommonComponents.GUI_OPEN_IN_BROWSER : CommonComponents.GUI_YES;
        this.noButtonComponent = $$4;
        this.showWarning = !$$5;
        this.url = $$3;
    }

    protected static MutableComponent confirmMessage(boolean $$0, String $$1) {
        return ConfirmLinkScreen.confirmMessage($$0).append(CommonComponents.SPACE).append(Component.literal($$1));
    }

    protected static MutableComponent confirmMessage(boolean $$0) {
        return Component.translatable($$0 ? "chat.link.confirmTrusted" : "chat.link.confirm");
    }

    @Override
    protected void addAdditionalText() {
        if (this.showWarning) {
            this.layout.addChild(new StringWidget(WARNING_TEXT, this.font));
        }
    }

    @Override
    protected void addButtons(LinearLayout $$02) {
        this.yesButton = $$02.addChild(Button.builder(this.yesButtonComponent, $$0 -> this.callback.accept(true)).width(100).build());
        $$02.addChild(Button.builder(COPY_BUTTON_TEXT, $$0 -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }).width(100).build());
        this.noButton = $$02.addChild(Button.builder(this.noButtonComponent, $$0 -> this.callback.accept(false)).width(100).build());
    }

    public void copyToClipboard() {
        this.minecraft.keyboardHandler.setClipboard(this.url);
    }

    public static void confirmLinkNow(Screen $$0, String $$1, boolean $$2) {
        Minecraft $$32 = Minecraft.getInstance();
        $$32.setScreen(new ConfirmLinkScreen($$3 -> {
            if ($$3) {
                Util.getPlatform().openUri($$1);
            }
            $$32.setScreen($$0);
        }, $$1, $$2));
    }

    public static void confirmLinkNow(Screen $$0, URI $$1, boolean $$2) {
        Minecraft $$32 = Minecraft.getInstance();
        $$32.setScreen(new ConfirmLinkScreen($$3 -> {
            if ($$3) {
                Util.getPlatform().openUri($$1);
            }
            $$32.setScreen($$0);
        }, $$1.toString(), $$2));
    }

    public static void confirmLinkNow(Screen $$0, URI $$1) {
        ConfirmLinkScreen.confirmLinkNow($$0, $$1, true);
    }

    public static void confirmLinkNow(Screen $$0, String $$1) {
        ConfirmLinkScreen.confirmLinkNow($$0, $$1, true);
    }

    public static Button.OnPress confirmLink(Screen $$0, String $$1, boolean $$2) {
        return $$3 -> ConfirmLinkScreen.confirmLinkNow($$0, $$1, $$2);
    }

    public static Button.OnPress confirmLink(Screen $$0, URI $$1, boolean $$2) {
        return $$3 -> ConfirmLinkScreen.confirmLinkNow($$0, $$1, $$2);
    }

    public static Button.OnPress confirmLink(Screen $$0, String $$1) {
        return ConfirmLinkScreen.confirmLink($$0, $$1, true);
    }

    public static Button.OnPress confirmLink(Screen $$0, URI $$1) {
        return ConfirmLinkScreen.confirmLink($$0, $$1, true);
    }
}

