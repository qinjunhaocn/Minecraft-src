/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.multiplayer.chat;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.StringUtils;

public class ChatListener {
    private static final Component CHAT_VALIDATION_ERROR = Component.translatable("chat.validation_error").a(ChatFormatting.RED, ChatFormatting.ITALIC);
    private final Minecraft minecraft;
    private final Deque<Message> delayedMessageQueue = Queues.newArrayDeque();
    private long messageDelay;
    private long previousMessageTime;

    public ChatListener(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void tick() {
        if (this.messageDelay == 0L) {
            return;
        }
        if (Util.getMillis() >= this.previousMessageTime + this.messageDelay) {
            Message $$0 = this.delayedMessageQueue.poll();
            while ($$0 != null && !$$0.accept()) {
                $$0 = this.delayedMessageQueue.poll();
            }
        }
    }

    public void setMessageDelay(double $$0) {
        long $$1 = (long)($$0 * 1000.0);
        if ($$1 == 0L && this.messageDelay > 0L) {
            this.delayedMessageQueue.forEach(Message::accept);
            this.delayedMessageQueue.clear();
        }
        this.messageDelay = $$1;
    }

    public void acceptNextDelayedMessage() {
        this.delayedMessageQueue.remove().accept();
    }

    public long queueSize() {
        return this.delayedMessageQueue.size();
    }

    public void clearQueue() {
        this.delayedMessageQueue.forEach(Message::accept);
        this.delayedMessageQueue.clear();
    }

    public boolean removeFromDelayedMessageQueue(MessageSignature $$0) {
        return this.delayedMessageQueue.removeIf($$1 -> $$0.equals((Object)$$1.signature()));
    }

    private boolean willDelayMessages() {
        return this.messageDelay > 0L && Util.getMillis() < this.previousMessageTime + this.messageDelay;
    }

    private void handleMessage(@Nullable MessageSignature $$0, BooleanSupplier $$1) {
        if (this.willDelayMessages()) {
            this.delayedMessageQueue.add(new Message($$0, $$1));
        } else {
            $$1.getAsBoolean();
        }
    }

    public void handlePlayerChatMessage(PlayerChatMessage $$0, GameProfile $$1, ChatType.Bound $$2) {
        boolean $$3 = this.minecraft.options.onlyShowSecureChat().get();
        PlayerChatMessage $$4 = $$3 ? $$0.removeUnsignedContent() : $$0;
        Component $$5 = $$2.decorate($$4.decoratedContent());
        Instant $$6 = Instant.now();
        this.handleMessage($$0.signature(), () -> {
            boolean $$6 = this.showMessageToPlayer($$2, $$0, $$5, $$1, $$3, $$6);
            ClientPacketListener $$7 = this.minecraft.getConnection();
            if ($$7 != null && $$0.signature() != null) {
                $$7.markMessageAsProcessed($$0.signature(), $$6);
            }
            return $$6;
        });
    }

    public void handleChatMessageError(UUID $$0, @Nullable MessageSignature $$1, ChatType.Bound $$2) {
        this.handleMessage(null, () -> {
            ClientPacketListener $$3 = this.minecraft.getConnection();
            if ($$3 != null && $$1 != null) {
                $$3.markMessageAsProcessed($$1, false);
            }
            if (this.minecraft.isBlocked($$0)) {
                return false;
            }
            Component $$4 = $$2.decorate(CHAT_VALIDATION_ERROR);
            this.minecraft.gui.getChat().addMessage($$4, null, GuiMessageTag.chatError());
            this.minecraft.getNarrator().saySystemChatQueued($$2.decorateNarration(CHAT_VALIDATION_ERROR));
            this.previousMessageTime = Util.getMillis();
            return true;
        });
    }

    public void handleDisguisedChatMessage(Component $$0, ChatType.Bound $$1) {
        Instant $$2 = Instant.now();
        this.handleMessage(null, () -> {
            Component $$3 = $$1.decorate($$0);
            this.minecraft.gui.getChat().addMessage($$3);
            this.narrateChatMessage($$1, $$0);
            this.logSystemMessage($$3, $$2);
            this.previousMessageTime = Util.getMillis();
            return true;
        });
    }

    private boolean showMessageToPlayer(ChatType.Bound $$0, PlayerChatMessage $$1, Component $$2, GameProfile $$3, boolean $$4, Instant $$5) {
        ChatTrustLevel $$6 = this.evaluateTrustLevel($$1, $$2, $$5);
        if ($$4 && $$6.isNotSecure()) {
            return false;
        }
        if (this.minecraft.isBlocked($$1.sender()) || $$1.isFullyFiltered()) {
            return false;
        }
        GuiMessageTag $$7 = $$6.createTag($$1);
        MessageSignature $$8 = $$1.signature();
        FilterMask $$9 = $$1.filterMask();
        if ($$9.isEmpty()) {
            this.minecraft.gui.getChat().addMessage($$2, $$8, $$7);
            this.narrateChatMessage($$0, $$1.decoratedContent());
        } else {
            Component $$10 = $$9.applyWithFormatting($$1.signedContent());
            if ($$10 != null) {
                this.minecraft.gui.getChat().addMessage($$0.decorate($$10), $$8, $$7);
                this.narrateChatMessage($$0, $$10);
            }
        }
        this.logPlayerMessage($$1, $$0, $$3, $$6);
        this.previousMessageTime = Util.getMillis();
        return true;
    }

    private void narrateChatMessage(ChatType.Bound $$0, Component $$1) {
        this.minecraft.getNarrator().sayChatQueued($$0.decorateNarration($$1));
    }

    private ChatTrustLevel evaluateTrustLevel(PlayerChatMessage $$0, Component $$1, Instant $$2) {
        if (this.isSenderLocalPlayer($$0.sender())) {
            return ChatTrustLevel.SECURE;
        }
        return ChatTrustLevel.evaluate($$0, $$1, $$2);
    }

    private void logPlayerMessage(PlayerChatMessage $$0, ChatType.Bound $$1, GameProfile $$2, ChatTrustLevel $$3) {
        ChatLog $$4 = this.minecraft.getReportingContext().chatLog();
        $$4.push(LoggedChatMessage.player($$2, $$0, $$3));
    }

    private void logSystemMessage(Component $$0, Instant $$1) {
        ChatLog $$2 = this.minecraft.getReportingContext().chatLog();
        $$2.push(LoggedChatMessage.system($$0, $$1));
    }

    public void handleSystemMessage(Component $$0, boolean $$1) {
        if (this.minecraft.options.hideMatchedNames().get().booleanValue() && this.minecraft.isBlocked(this.guessChatUUID($$0))) {
            return;
        }
        if ($$1) {
            this.minecraft.gui.setOverlayMessage($$0, false);
            this.minecraft.getNarrator().saySystemQueued($$0);
        } else {
            this.minecraft.gui.getChat().addMessage($$0);
            this.logSystemMessage($$0, Instant.now());
            this.minecraft.getNarrator().saySystemChatQueued($$0);
        }
    }

    private UUID guessChatUUID(Component $$0) {
        String $$1 = StringDecomposer.getPlainText($$0);
        String $$2 = StringUtils.substringBetween($$1, "<", ">");
        if ($$2 == null) {
            return Util.NIL_UUID;
        }
        return this.minecraft.getPlayerSocialManager().getDiscoveredUUID($$2);
    }

    private boolean isSenderLocalPlayer(UUID $$0) {
        if (this.minecraft.isLocalServer() && this.minecraft.player != null) {
            UUID $$1 = this.minecraft.player.getGameProfile().getId();
            return $$1.equals($$0);
        }
        return false;
    }

    record Message(@Nullable MessageSignature signature, BooleanSupplier handler) {
        public boolean accept() {
            return this.handler.getAsBoolean();
        }

        @Nullable
        public MessageSignature signature() {
            return this.signature;
        }
    }
}

