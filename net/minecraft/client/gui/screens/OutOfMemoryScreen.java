/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OutOfMemoryScreen
extends Screen {
    private static final Component TITLE = Component.translatable("outOfMemory.title");
    private static final Component MESSAGE = Component.translatable("outOfMemory.message");
    private static final int MESSAGE_WIDTH = 300;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public OutOfMemoryScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.layout.addToContents(new FocusableTextWidget(300, MESSAGE, this.font));
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        $$02.addChild(Button.builder(CommonComponents.GUI_TO_TITLE, $$0 -> this.minecraft.setScreen(new TitleScreen())).build());
        $$02.addChild(Button.builder(Component.translatable("menu.quit"), $$0 -> this.minecraft.stop()).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

