/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DisconnectedScreen
extends Screen {
    private static final Component TO_SERVER_LIST = Component.translatable("gui.toMenu");
    private static final Component TO_TITLE = Component.translatable("gui.toTitle");
    private static final Component REPORT_TO_SERVER_TITLE = Component.translatable("gui.report_to_server");
    private static final Component OPEN_REPORT_DIR_TITLE = Component.translatable("gui.open_report_dir");
    private final Screen parent;
    private final DisconnectionDetails details;
    private final Component buttonText;
    private final LinearLayout layout = LinearLayout.vertical();

    public DisconnectedScreen(Screen $$0, Component $$1, Component $$2) {
        this($$0, $$1, new DisconnectionDetails($$2));
    }

    public DisconnectedScreen(Screen $$0, Component $$1, Component $$2, Component $$3) {
        this($$0, $$1, new DisconnectionDetails($$2), $$3);
    }

    public DisconnectedScreen(Screen $$0, Component $$1, DisconnectionDetails $$2) {
        this($$0, $$1, $$2, TO_SERVER_LIST);
    }

    public DisconnectedScreen(Screen $$0, Component $$1, DisconnectionDetails $$2, Component $$3) {
        super($$1);
        this.parent = $$0;
        this.details = $$2;
        this.buttonText = $$3;
    }

    @Override
    protected void init() {
        Button $$1;
        this.layout.defaultCellSetting().alignHorizontallyCenter().padding(10);
        this.layout.addChild(new StringWidget(this.title, this.font));
        this.layout.addChild(new MultiLineTextWidget(this.details.reason(), this.font).setMaxWidth(this.width - 50).setCentered(true));
        this.layout.defaultCellSetting().padding(2);
        this.details.bugReportLink().ifPresent($$0 -> this.layout.addChild(Button.builder(REPORT_TO_SERVER_TITLE, ConfirmLinkScreen.confirmLink((Screen)this, $$0, false)).width(200).build()));
        this.details.report().ifPresent($$0 -> this.layout.addChild(Button.builder(OPEN_REPORT_DIR_TITLE, $$1 -> Util.getPlatform().openPath($$0.getParent())).width(200).build()));
        if (this.minecraft.allowsMultiplayer()) {
            Button $$02 = Button.builder(this.buttonText, $$0 -> this.minecraft.setScreen(this.parent)).width(200).build();
        } else {
            $$1 = Button.builder(TO_TITLE, $$0 -> this.minecraft.setScreen(new TitleScreen())).width(200).build();
        }
        this.layout.addChild($$1);
        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.title, this.details.reason());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

