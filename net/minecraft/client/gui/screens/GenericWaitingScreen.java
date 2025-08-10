/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GenericWaitingScreen
extends Screen {
    private static final int TITLE_Y = 80;
    private static final int MESSAGE_Y = 120;
    private static final int MESSAGE_MAX_WIDTH = 360;
    @Nullable
    private final Component messageText;
    private final Component buttonLabel;
    private final Runnable buttonCallback;
    @Nullable
    private MultiLineLabel message;
    private Button button;
    private int disableButtonTicks;

    public static GenericWaitingScreen createWaiting(Component $$0, Component $$1, Runnable $$2) {
        return new GenericWaitingScreen($$0, null, $$1, $$2, 0);
    }

    public static GenericWaitingScreen createCompleted(Component $$0, Component $$1, Component $$2, Runnable $$3) {
        return new GenericWaitingScreen($$0, $$1, $$2, $$3, 20);
    }

    protected GenericWaitingScreen(Component $$0, @Nullable Component $$1, Component $$2, Runnable $$3, int $$4) {
        super($$0);
        this.messageText = $$1;
        this.buttonLabel = $$2;
        this.buttonCallback = $$3;
        this.disableButtonTicks = $$4;
    }

    @Override
    protected void init() {
        super.init();
        if (this.messageText != null) {
            this.message = MultiLineLabel.create(this.font, this.messageText, 360);
        }
        int $$02 = 150;
        int $$1 = 20;
        int $$2 = this.message != null ? this.message.getLineCount() : 1;
        int $$3 = Math.max($$2, 5) * this.font.lineHeight;
        int $$4 = Math.min(120 + $$3, this.height - 40);
        this.button = this.addRenderableWidget(Button.builder(this.buttonLabel, $$0 -> this.onClose()).bounds((this.width - 150) / 2, $$4, 150, 20).build());
    }

    @Override
    public void tick() {
        if (this.disableButtonTicks > 0) {
            --this.disableButtonTicks;
        }
        this.button.active = this.disableButtonTicks == 0;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 80, -1);
        if (this.message == null) {
            String $$4 = LoadingDotsText.get(Util.getMillis());
            $$0.drawCenteredString(this.font, $$4, this.width / 2, 120, -6250336);
        } else {
            this.message.renderCentered($$0, this.width / 2, 120);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.message != null && this.button.active;
    }

    @Override
    public void onClose() {
        this.buttonCallback.run();
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(this.title, this.messageText != null ? this.messageText : CommonComponents.EMPTY);
    }
}

