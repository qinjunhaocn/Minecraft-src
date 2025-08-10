/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class WaitingForResponseScreen
extends Screen {
    private static final Component TITLE = Component.translatable("gui.waitingForResponse.title");
    private static final Component[] BUTTON_LABELS = new Component[]{Component.empty(), Component.a("gui.waitingForResponse.button.inactive", 4), Component.a("gui.waitingForResponse.button.inactive", 3), Component.a("gui.waitingForResponse.button.inactive", 2), Component.a("gui.waitingForResponse.button.inactive", 1), CommonComponents.GUI_BACK};
    private static final int BUTTON_VISIBLE_AFTER = 1;
    private static final int BUTTON_ACTIVE_AFTER = 5;
    @Nullable
    private final Screen previousScreen;
    private final HeaderAndFooterLayout layout;
    private final Button closeButton;
    private int ticks;

    public WaitingForResponseScreen(@Nullable Screen $$02) {
        super(TITLE);
        this.previousScreen = $$02;
        this.layout = new HeaderAndFooterLayout(this, 33, 0);
        this.closeButton = Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).width(200).build();
    }

    @Override
    protected void init() {
        super.init();
        this.layout.addTitleHeader(TITLE, this.font);
        this.layout.addToContents(this.closeButton);
        this.closeButton.visible = false;
        this.closeButton.active = false;
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.closeButton.active) {
            int $$0;
            this.closeButton.visible = ($$0 = this.ticks++ / 20) >= 1;
            this.closeButton.setMessage(BUTTON_LABELS[$$0]);
            if ($$0 == 5) {
                this.closeButton.active = true;
                this.triggerImmediateNarration(true);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.closeButton.active;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.previousScreen);
    }

    @Nullable
    public Screen previousScreen() {
        return this.previousScreen;
    }
}

