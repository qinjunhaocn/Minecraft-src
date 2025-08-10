/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.slf4j.Logger;

public class ChatComponent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_CHAT_HISTORY = 100;
    private static final int MESSAGE_NOT_FOUND = -1;
    private static final int MESSAGE_INDENT = 4;
    private static final int MESSAGE_TAG_MARGIN_LEFT = 4;
    private static final int BOTTOM_MARGIN = 40;
    private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
    private static final Component DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").a(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    private final Minecraft minecraft;
    private final ArrayListDeque<String> recentChat = new ArrayListDeque(100);
    private final List<GuiMessage> allMessages = Lists.newArrayList();
    private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList<DelayedMessageDeletion>();

    public ChatComponent(Minecraft $$0) {
        this.minecraft = $$0;
        this.recentChat.addAll($$0.commandHistory().history());
    }

    public void tick() {
        if (!this.messageDeletionQueue.isEmpty()) {
            this.processMessageDeletionQueue();
        }
    }

    private int forEachLine(int $$0, int $$1, boolean $$2, int $$3, LineConsumer $$4) {
        int $$5 = this.getLineHeight();
        int $$6 = 0;
        for (int $$7 = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, $$0) - 1; $$7 >= 0; --$$7) {
            float $$11;
            int $$8 = $$7 + this.chatScrollbarPos;
            GuiMessage.Line $$9 = this.trimmedMessages.get($$8);
            if ($$9 == null) continue;
            int $$10 = $$1 - $$9.addedTime();
            float f = $$11 = $$2 ? 1.0f : (float)ChatComponent.getTimeFactor($$10);
            if (!($$11 > 1.0E-5f)) continue;
            ++$$6;
            int $$12 = $$3 - $$7 * $$5;
            int $$13 = $$12 - $$5;
            $$4.accept(0, $$13, $$12, $$9, $$7, $$11);
        }
        return $$6;
    }

    public void render(GuiGraphics $$0, int $$1, int $$2, int $$32, boolean $$42) {
        if (this.isChatHidden()) {
            return;
        }
        int $$52 = this.getLinesPerPage();
        int $$62 = this.trimmedMessages.size();
        if ($$62 <= 0) {
            return;
        }
        ProfilerFiller $$72 = Profiler.get();
        $$72.push("chat");
        float $$82 = (float)this.getScale();
        int $$92 = Mth.ceil((float)this.getWidth() / $$82);
        int $$102 = $$0.guiHeight();
        $$0.pose().pushMatrix();
        $$0.pose().scale($$82, $$82);
        $$0.pose().translate(4.0f, 0.0f);
        int $$112 = Mth.floor((float)($$102 - 40) / $$82);
        int $$12 = this.getMessageEndIndexAt(this.screenToChatX($$2), this.screenToChatY($$32));
        float $$13 = this.minecraft.options.chatOpacity().get().floatValue() * 0.9f + 0.1f;
        float $$14 = this.minecraft.options.textBackgroundOpacity().get().floatValue();
        double $$15 = this.minecraft.options.chatLineSpacing().get();
        int $$16 = (int)Math.round(-8.0 * ($$15 + 1.0) + 4.0 * $$15);
        this.forEachLine($$52, $$1, $$42, $$112, ($$6, $$7, $$8, $$9, $$10, $$11) -> {
            $$0.fill($$6 - 4, $$7, $$6 + $$92 + 4 + 4, $$8, ARGB.color($$11 * $$14, -16777216));
            GuiMessageTag $$12 = $$9.tag();
            if ($$12 != null) {
                int $$13 = ARGB.color($$11 * $$13, $$12.indicatorColor());
                $$0.fill($$6 - 4, $$7, $$6 - 2, $$8, $$13);
                if ($$10 == $$12 && $$12.icon() != null) {
                    int $$14 = this.getTagIconLeft($$9);
                    int $$15 = $$8 + $$16 + this.minecraft.font.lineHeight;
                    this.drawTagIcon($$0, $$14, $$15, $$12.icon());
                }
            }
        });
        int $$17 = this.forEachLine($$52, $$1, $$42, $$112, ($$3, $$4, $$5, $$6, $$7, $$8) -> {
            int $$9 = $$5 + $$16;
            $$0.drawString(this.minecraft.font, $$6.content(), $$3, $$9, ARGB.color($$8 * $$13, -1));
        });
        long $$18 = this.minecraft.getChatListener().queueSize();
        if ($$18 > 0L) {
            int $$19 = (int)(128.0f * $$13);
            int $$20 = (int)(255.0f * $$14);
            $$0.pose().pushMatrix();
            $$0.pose().translate(0.0f, (float)$$112);
            $$0.fill(-2, 0, $$92 + 4, 9, $$20 << 24);
            $$0.drawString(this.minecraft.font, Component.a("chat.queue", $$18), 0, 1, ARGB.color($$19, -1));
            $$0.pose().popMatrix();
        }
        if ($$42) {
            int $$21 = this.getLineHeight();
            int $$22 = $$62 * $$21;
            int $$23 = $$17 * $$21;
            int $$24 = this.chatScrollbarPos * $$23 / $$62 - $$112;
            int $$25 = $$23 * $$23 / $$22;
            if ($$22 != $$23) {
                int $$26 = $$24 > 0 ? 170 : 96;
                int $$27 = this.newMessageSinceScroll ? 0xCC3333 : 0x3333AA;
                int $$28 = $$92 + 4;
                $$0.fill($$28, -$$24, $$28 + 2, -$$24 - $$25, ARGB.color($$26, $$27));
                $$0.fill($$28 + 2, -$$24, $$28 + 1, -$$24 - $$25, ARGB.color($$26, 0xCCCCCC));
            }
        }
        $$0.pose().popMatrix();
        $$72.pop();
    }

    private void drawTagIcon(GuiGraphics $$0, int $$1, int $$2, GuiMessageTag.Icon $$3) {
        int $$4 = $$2 - $$3.height - 1;
        $$3.draw($$0, $$1, $$4);
    }

    private int getTagIconLeft(GuiMessage.Line $$0) {
        return this.minecraft.font.width($$0.content()) + 4;
    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
    }

    private static double getTimeFactor(int $$0) {
        double $$1 = (double)$$0 / 200.0;
        $$1 = 1.0 - $$1;
        $$1 *= 10.0;
        $$1 = Mth.clamp($$1, 0.0, 1.0);
        $$1 *= $$1;
        return $$1;
    }

    public void clearMessages(boolean $$0) {
        this.minecraft.getChatListener().clearQueue();
        this.messageDeletionQueue.clear();
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if ($$0) {
            this.recentChat.clear();
            this.recentChat.addAll(this.minecraft.commandHistory().history());
        }
    }

    public void addMessage(Component $$0) {
        this.addMessage($$0, null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
    }

    public void addMessage(Component $$0, @Nullable MessageSignature $$1, @Nullable GuiMessageTag $$2) {
        GuiMessage $$3 = new GuiMessage(this.minecraft.gui.getGuiTicks(), $$0, $$1, $$2);
        this.logChatMessage($$3);
        this.addMessageToDisplayQueue($$3);
        this.addMessageToQueue($$3);
    }

    private void logChatMessage(GuiMessage $$0) {
        String $$1 = $$0.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String $$2 = Optionull.map($$0.tag(), GuiMessageTag::logTag);
        if ($$2 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)$$2, (Object)$$1);
        } else {
            LOGGER.info("[CHAT] {}", (Object)$$1);
        }
    }

    private void addMessageToDisplayQueue(GuiMessage $$0) {
        int $$1 = Mth.floor((double)this.getWidth() / this.getScale());
        GuiMessageTag.Icon $$2 = $$0.icon();
        if ($$2 != null) {
            $$1 -= $$2.width + 4 + 2;
        }
        List<FormattedCharSequence> $$3 = ComponentRenderUtils.wrapComponents($$0.content(), $$1, this.minecraft.font);
        boolean $$4 = this.isChatFocused();
        for (int $$5 = 0; $$5 < $$3.size(); ++$$5) {
            FormattedCharSequence $$6 = $$3.get($$5);
            if ($$4 && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }
            boolean $$7 = $$5 == $$3.size() - 1;
            this.trimmedMessages.add(0, new GuiMessage.Line($$0.addedTime(), $$6, $$0.tag(), $$7));
        }
        while (this.trimmedMessages.size() > 100) {
            this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
        }
    }

    private void addMessageToQueue(GuiMessage $$0) {
        this.allMessages.add(0, $$0);
        while (this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
        }
    }

    private void processMessageDeletionQueue() {
        int $$0 = this.minecraft.gui.getGuiTicks();
        this.messageDeletionQueue.removeIf($$1 -> {
            if ($$0 >= $$1.deletableAfter()) {
                return this.deleteMessageOrDelay($$1.signature()) == null;
            }
            return false;
        });
    }

    public void deleteMessage(MessageSignature $$0) {
        DelayedMessageDeletion $$1 = this.deleteMessageOrDelay($$0);
        if ($$1 != null) {
            this.messageDeletionQueue.add($$1);
        }
    }

    @Nullable
    private DelayedMessageDeletion deleteMessageOrDelay(MessageSignature $$0) {
        int $$1 = this.minecraft.gui.getGuiTicks();
        ListIterator<GuiMessage> $$2 = this.allMessages.listIterator();
        while ($$2.hasNext()) {
            GuiMessage $$3 = $$2.next();
            if (!$$0.equals((Object)$$3.signature())) continue;
            int $$4 = $$3.addedTime() + 60;
            if ($$1 >= $$4) {
                $$2.set(this.createDeletedMarker($$3));
                this.refreshTrimmedMessages();
                return null;
            }
            return new DelayedMessageDeletion($$0, $$4);
        }
        return null;
    }

    private GuiMessage createDeletedMarker(GuiMessage $$0) {
        return new GuiMessage($$0.addedTime(), DELETED_CHAT_MESSAGE, null, GuiMessageTag.system());
    }

    public void rescaleChat() {
        this.resetChatScroll();
        this.refreshTrimmedMessages();
    }

    private void refreshTrimmedMessages() {
        this.trimmedMessages.clear();
        for (GuiMessage $$0 : Lists.reverse(this.allMessages)) {
            this.addMessageToDisplayQueue($$0);
        }
    }

    public ArrayListDeque<String> getRecentChat() {
        return this.recentChat;
    }

    public void addRecentChat(String $$0) {
        if (!$$0.equals(this.recentChat.peekLast())) {
            if (this.recentChat.size() >= 100) {
                this.recentChat.removeFirst();
            }
            this.recentChat.addLast($$0);
        }
        if ($$0.startsWith("/")) {
            this.minecraft.commandHistory().addCommand($$0);
        }
    }

    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }

    public void scrollChat(int $$0) {
        this.chatScrollbarPos += $$0;
        int $$1 = this.trimmedMessages.size();
        if (this.chatScrollbarPos > $$1 - this.getLinesPerPage()) {
            this.chatScrollbarPos = $$1 - this.getLinesPerPage();
        }
        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }
    }

    public boolean handleChatQueueClicked(double $$0, double $$1) {
        if (!this.isChatFocused() || this.minecraft.options.hideGui || this.isChatHidden()) {
            return false;
        }
        ChatListener $$2 = this.minecraft.getChatListener();
        if ($$2.queueSize() == 0L) {
            return false;
        }
        double $$3 = $$0 - 2.0;
        double $$4 = (double)this.minecraft.getWindow().getGuiScaledHeight() - $$1 - 40.0;
        if ($$3 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && $$4 < 0.0 && $$4 > (double)Mth.floor(-9.0 * this.getScale())) {
            $$2.acceptNextDelayedMessage();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getClickedComponentStyleAt(double $$0, double $$1) {
        double $$3;
        double $$2 = this.screenToChatX($$0);
        int $$4 = this.getMessageLineIndexAt($$2, $$3 = this.screenToChatY($$1));
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size()) {
            GuiMessage.Line $$5 = this.trimmedMessages.get($$4);
            return this.minecraft.font.getSplitter().componentStyleAtWidth($$5.content(), Mth.floor($$2));
        }
        return null;
    }

    @Nullable
    public GuiMessageTag getMessageTagAt(double $$0, double $$1) {
        GuiMessage.Line $$5;
        GuiMessageTag $$6;
        double $$3;
        double $$2 = this.screenToChatX($$0);
        int $$4 = this.getMessageEndIndexAt($$2, $$3 = this.screenToChatY($$1));
        if ($$4 >= 0 && $$4 < this.trimmedMessages.size() && ($$6 = ($$5 = this.trimmedMessages.get($$4)).tag()) != null && this.hasSelectedMessageTag($$2, $$5, $$6)) {
            return $$6;
        }
        return null;
    }

    private boolean hasSelectedMessageTag(double $$0, GuiMessage.Line $$1, GuiMessageTag $$2) {
        if ($$0 < 0.0) {
            return true;
        }
        GuiMessageTag.Icon $$3 = $$2.icon();
        if ($$3 != null) {
            int $$4 = this.getTagIconLeft($$1);
            int $$5 = $$4 + $$3.width;
            return $$0 >= (double)$$4 && $$0 <= (double)$$5;
        }
        return false;
    }

    private double screenToChatX(double $$0) {
        return $$0 / this.getScale() - 4.0;
    }

    private double screenToChatY(double $$0) {
        double $$1 = (double)this.minecraft.getWindow().getGuiScaledHeight() - $$0 - 40.0;
        return $$1 / (this.getScale() * (double)this.getLineHeight());
    }

    private int getMessageEndIndexAt(double $$0, double $$1) {
        int $$2 = this.getMessageLineIndexAt($$0, $$1);
        if ($$2 == -1) {
            return -1;
        }
        while ($$2 >= 0) {
            if (this.trimmedMessages.get($$2).endOfEntry()) {
                return $$2;
            }
            --$$2;
        }
        return $$2;
    }

    private int getMessageLineIndexAt(double $$0, double $$1) {
        int $$3;
        if (!this.isChatFocused() || this.isChatHidden()) {
            return -1;
        }
        if ($$0 < -4.0 || $$0 > (double)Mth.floor((double)this.getWidth() / this.getScale())) {
            return -1;
        }
        int $$2 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
        if ($$1 >= 0.0 && $$1 < (double)$$2 && ($$3 = Mth.floor($$1 + (double)this.chatScrollbarPos)) >= 0 && $$3 < this.trimmedMessages.size()) {
            return $$3;
        }
        return -1;
    }

    public boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }

    public int getWidth() {
        return ChatComponent.getWidth(this.minecraft.options.chatWidth().get());
    }

    public int getHeight() {
        return ChatComponent.getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused().get() : this.minecraft.options.chatHeightUnfocused().get());
    }

    public double getScale() {
        return this.minecraft.options.chatScale().get();
    }

    public static int getWidth(double $$0) {
        int $$1 = 320;
        int $$2 = 40;
        return Mth.floor($$0 * 280.0 + 40.0);
    }

    public static int getHeight(double $$0) {
        int $$1 = 180;
        int $$2 = 20;
        return Mth.floor($$0 * 160.0 + 20.0);
    }

    public static double defaultUnfocusedPct() {
        int $$0 = 180;
        int $$1 = 20;
        return 70.0 / (double)(ChatComponent.getHeight(1.0) - 20);
    }

    public int getLinesPerPage() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        return (int)((double)this.minecraft.font.lineHeight * (this.minecraft.options.chatLineSpacing().get() + 1.0));
    }

    public State storeState() {
        return new State(List.copyOf(this.allMessages), List.copyOf(this.recentChat), List.copyOf(this.messageDeletionQueue));
    }

    public void restoreState(State $$0) {
        this.recentChat.clear();
        this.recentChat.addAll($$0.history);
        this.messageDeletionQueue.clear();
        this.messageDeletionQueue.addAll($$0.delayedMessageDeletions);
        this.allMessages.clear();
        this.allMessages.addAll($$0.messages);
        this.refreshTrimmedMessages();
    }

    @FunctionalInterface
    static interface LineConsumer {
        public void accept(int var1, int var2, int var3, GuiMessage.Line var4, int var5, float var6);
    }

    record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
    }

    public static class State {
        final List<GuiMessage> messages;
        final List<String> history;
        final List<DelayedMessageDeletion> delayedMessageDeletions;

        public State(List<GuiMessage> $$0, List<String> $$1, List<DelayedMessageDeletion> $$2) {
            this.messages = $$0;
            this.history = $$1;
            this.delayedMessageDeletions = $$2;
        }
    }
}

