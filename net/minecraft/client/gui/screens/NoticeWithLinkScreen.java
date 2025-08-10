/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import java.net.URI;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;

public class NoticeWithLinkScreen
extends Screen {
    private static final Component SYMLINK_WORLD_TITLE = Component.translatable("symlink_warning.title.world").withStyle(ChatFormatting.BOLD);
    private static final Component SYMLINK_WORLD_MESSAGE_TEXT = Component.a("symlink_warning.message.world", Component.translationArg(CommonLinks.SYMLINK_HELP));
    private static final Component SYMLINK_PACK_TITLE = Component.translatable("symlink_warning.title.pack").withStyle(ChatFormatting.BOLD);
    private static final Component SYMLINK_PACK_MESSAGE_TEXT = Component.a("symlink_warning.message.pack", Component.translationArg(CommonLinks.SYMLINK_HELP));
    private final Component message;
    private final URI uri;
    private final Runnable onClose;
    private final GridLayout layout = new GridLayout().rowSpacing(10);

    public NoticeWithLinkScreen(Component $$0, Component $$1, URI $$2, Runnable $$3) {
        super($$0);
        this.message = $$1;
        this.uri = $$2;
        this.onClose = $$3;
    }

    public static Screen createWorldSymlinkWarningScreen(Runnable $$0) {
        return new NoticeWithLinkScreen(SYMLINK_WORLD_TITLE, SYMLINK_WORLD_MESSAGE_TEXT, CommonLinks.SYMLINK_HELP, $$0);
    }

    public static Screen createPackSymlinkWarningScreen(Runnable $$0) {
        return new NoticeWithLinkScreen(SYMLINK_PACK_TITLE, SYMLINK_PACK_MESSAGE_TEXT, CommonLinks.SYMLINK_HELP, $$0);
    }

    @Override
    protected void init() {
        super.init();
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        GridLayout.RowHelper $$02 = this.layout.createRowHelper(1);
        $$02.addChild(new StringWidget(this.title, this.font));
        $$02.addChild(new MultiLineTextWidget(this.message, this.font).setMaxWidth(this.width - 50).setCentered(true));
        int $$1 = 120;
        GridLayout $$2 = new GridLayout().columnSpacing(5);
        GridLayout.RowHelper $$3 = $$2.createRowHelper(3);
        $$3.addChild(Button.builder(CommonComponents.GUI_OPEN_IN_BROWSER, $$0 -> Util.getPlatform().openUri(this.uri)).size(120, 20).build());
        $$3.addChild(Button.builder(CommonComponents.GUI_COPY_LINK_TO_CLIPBOARD, $$0 -> this.minecraft.keyboardHandler.setClipboard(this.uri.toString())).size(120, 20).build());
        $$3.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).size(120, 20).build());
        $$02.addChild($$2);
        this.repositionElements();
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), this.message);
    }

    @Override
    public void onClose() {
        this.onClose.run();
    }
}

