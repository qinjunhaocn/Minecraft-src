/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class ChatScreen
extends Screen {
    public static final double MOUSE_SCROLL_SPEED = 7.0;
    private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");
    private static final int TOOLTIP_MAX_WIDTH = 210;
    private String historyBuffer = "";
    private int historyPos = -1;
    protected EditBox input;
    private String initial;
    CommandSuggestions commandSuggestions;

    public ChatScreen(String $$0) {
        super(Component.translatable("chat_screen.title"));
        this.initial = $$0;
    }

    @Override
    protected void init() {
        this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
        this.input = new EditBox(this.minecraft.fontFilterFishy, 4, this.height - 12, this.width - 4, 12, (Component)Component.translatable("chat.editBox")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.input.setMaxLength(256);
        this.input.setBordered(false);
        this.input.setValue(this.initial);
        this.input.setResponder(this::onEdited);
        this.input.setCanLoseFocus(false);
        this.addRenderableWidget(this.input);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
        this.commandSuggestions.setAllowHiding(false);
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.input);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.input.getValue();
        this.init($$0, $$1, $$2);
        this.setChatLine($$3);
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public void removed() {
        this.minecraft.gui.getChat().resetChatScroll();
    }

    private void onEdited(String $$0) {
        String $$1 = this.input.getValue();
        this.commandSuggestions.setAllowSuggestions(!$$1.equals(this.initial));
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.commandSuggestions.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if ($$0 == 256) {
            this.minecraft.setScreen(null);
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            this.handleChatInput(this.input.getValue(), true);
            this.minecraft.setScreen(null);
            return true;
        }
        if ($$0 == 265) {
            this.moveInHistory(-1);
            return true;
        }
        if ($$0 == 264) {
            this.moveInHistory(1);
            return true;
        }
        if ($$0 == 266) {
            this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            return true;
        }
        if ($$0 == 267) {
            this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (this.commandSuggestions.mouseScrolled($$3 = Mth.clamp($$3, -1.0, 1.0))) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            $$3 *= 7.0;
        }
        this.minecraft.gui.getChat().scrollChat((int)$$3);
        return true;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.commandSuggestions.mouseClicked((int)$$0, (int)$$1, $$2)) {
            return true;
        }
        if ($$2 == 0) {
            ChatComponent $$3 = this.minecraft.gui.getChat();
            if ($$3.handleChatQueueClicked($$0, $$1)) {
                return true;
            }
            Style $$4 = this.getComponentStyleAt($$0, $$1);
            if ($$4 != null && this.handleComponentClicked($$4)) {
                this.initial = this.input.getValue();
                return true;
            }
        }
        if (this.input.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    protected void insertText(String $$0, boolean $$1) {
        if ($$1) {
            this.input.setValue($$0);
        } else {
            this.input.insertText($$0);
        }
    }

    public void moveInHistory(int $$0) {
        int $$1 = this.historyPos + $$0;
        int $$2 = this.minecraft.gui.getChat().getRecentChat().size();
        if (($$1 = Mth.clamp($$1, 0, $$2)) == this.historyPos) {
            return;
        }
        if ($$1 == $$2) {
            this.historyPos = $$2;
            this.input.setValue(this.historyBuffer);
            return;
        }
        if (this.historyPos == $$2) {
            this.historyBuffer = this.input.getValue();
        }
        this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get($$1));
        this.commandSuggestions.setAllowSuggestions(false);
        this.historyPos = $$1;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
        this.minecraft.gui.getChat().render($$0, this.minecraft.gui.getGuiTicks(), $$1, $$2, true);
        super.render($$0, $$1, $$2, $$3);
        this.commandSuggestions.render($$0, $$1, $$2);
        GuiMessageTag $$4 = this.minecraft.gui.getChat().getMessageTagAt($$1, $$2);
        if ($$4 != null && $$4.text() != null) {
            $$0.setTooltipForNextFrame(this.font, this.font.split($$4.text(), 210), $$1, $$2);
        } else {
            Style $$5 = this.getComponentStyleAt($$1, $$2);
            if ($$5 != null && $$5.getHoverEvent() != null) {
                $$0.renderComponentHoverEffect(this.font, $$5, $$1, $$2);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void setChatLine(String $$0) {
        this.input.setValue($$0);
    }

    @Override
    protected void updateNarrationState(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.getTitle());
        $$0.add(NarratedElementType.USAGE, USAGE_TEXT);
        String $$1 = this.input.getValue();
        if (!$$1.isEmpty()) {
            $$0.nest().add(NarratedElementType.TITLE, Component.a("chat_screen.message", $$1));
        }
    }

    @Nullable
    private Style getComponentStyleAt(double $$0, double $$1) {
        return this.minecraft.gui.getChat().getClickedComponentStyleAt($$0, $$1);
    }

    public void handleChatInput(String $$0, boolean $$1) {
        if (($$0 = this.normalizeChatMessage($$0)).isEmpty()) {
            return;
        }
        if ($$1) {
            this.minecraft.gui.getChat().addRecentChat($$0);
        }
        if ($$0.startsWith("/")) {
            this.minecraft.player.connection.sendCommand($$0.substring(1));
        } else {
            this.minecraft.player.connection.sendChat($$0);
        }
    }

    public String normalizeChatMessage(String $$0) {
        return StringUtil.trimChatMessage(StringUtils.normalizeSpace($$0.trim()));
    }
}

