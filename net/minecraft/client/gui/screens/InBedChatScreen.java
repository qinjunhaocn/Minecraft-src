/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen
extends ChatScreen {
    private Button leaveBedButton;

    public InBedChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.leaveBedButton = Button.builder(Component.translatable("multiplayer.stopSleeping"), $$0 -> this.sendWakeUp()).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build();
        this.addRenderableWidget(this.leaveBedButton);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            this.leaveBedButton.render($$0, $$1, $$2, $$3);
            return;
        }
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void onClose() {
        this.sendWakeUp();
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            return true;
        }
        return super.a($$0, $$1);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.sendWakeUp();
        }
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            this.handleChatInput(this.input.getValue(), true);
            this.input.setValue("");
            this.minecraft.gui.getChat().resetChatScroll();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void sendWakeUp() {
        ClientPacketListener $$0 = this.minecraft.player.connection;
        $$0.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
    }

    public void onPlayerWokeUp() {
        if (this.input.getValue().isEmpty()) {
            this.minecraft.setScreen(null);
        } else {
            this.minecraft.setScreen(new ChatScreen(this.input.getValue()));
        }
    }
}

