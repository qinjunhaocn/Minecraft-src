/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.reporting.ReportPlayerScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class PlayerEntry
extends ContainerObjectSelectionList.Entry<PlayerEntry> {
    private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
    private static final Duration TOOLTIP_DELAY = Duration.ofMillis(500L);
    private static final WidgetSprites REPORT_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/report_button"), ResourceLocation.withDefaultNamespace("social_interactions/report_button_disabled"), ResourceLocation.withDefaultNamespace("social_interactions/report_button_highlighted"));
    private static final WidgetSprites MUTE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/mute_button"), ResourceLocation.withDefaultNamespace("social_interactions/mute_button_highlighted"));
    private static final WidgetSprites UNMUTE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("social_interactions/unmute_button"), ResourceLocation.withDefaultNamespace("social_interactions/unmute_button_highlighted"));
    private final Minecraft minecraft;
    private final List<AbstractWidget> children;
    private final UUID id;
    private final String playerName;
    private final Supplier<PlayerSkin> skinGetter;
    private boolean isRemoved;
    private boolean hasRecentMessages;
    private final boolean reportingEnabled;
    private boolean hasDraftReport;
    private final boolean chatReportable;
    @Nullable
    private Button hideButton;
    @Nullable
    private Button showButton;
    @Nullable
    private Button reportButton;
    private float tooltipHoverTime;
    private static final Component HIDDEN = Component.translatable("gui.socialInteractions.status_hidden").withStyle(ChatFormatting.ITALIC);
    private static final Component BLOCKED = Component.translatable("gui.socialInteractions.status_blocked").withStyle(ChatFormatting.ITALIC);
    private static final Component OFFLINE = Component.translatable("gui.socialInteractions.status_offline").withStyle(ChatFormatting.ITALIC);
    private static final Component HIDDEN_OFFLINE = Component.translatable("gui.socialInteractions.status_hidden_offline").withStyle(ChatFormatting.ITALIC);
    private static final Component BLOCKED_OFFLINE = Component.translatable("gui.socialInteractions.status_blocked_offline").withStyle(ChatFormatting.ITALIC);
    private static final Component REPORT_DISABLED_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report.disabled");
    private static final Component HIDE_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.hide");
    private static final Component SHOW_TEXT_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.show");
    private static final Component REPORT_PLAYER_TOOLTIP = Component.translatable("gui.socialInteractions.tooltip.report");
    private static final int SKIN_SIZE = 24;
    private static final int PADDING = 4;
    public static final int SKIN_SHADE = ARGB.color(190, 0, 0, 0);
    private static final int CHAT_TOGGLE_ICON_SIZE = 20;
    public static final int BG_FILL = ARGB.color(255, 74, 74, 74);
    public static final int BG_FILL_REMOVED = ARGB.color(255, 48, 48, 48);
    public static final int PLAYERNAME_COLOR = ARGB.color(255, 255, 255, 255);
    public static final int PLAYER_STATUS_COLOR = ARGB.color(140, 255, 255, 255);

    public PlayerEntry(Minecraft $$0, SocialInteractionsScreen $$1, UUID $$2, String $$32, Supplier<PlayerSkin> $$4, boolean $$5) {
        boolean $$11;
        this.minecraft = $$0;
        this.id = $$2;
        this.playerName = $$32;
        this.skinGetter = $$4;
        ReportingContext $$6 = $$0.getReportingContext();
        this.reportingEnabled = $$6.sender().isEnabled();
        this.chatReportable = $$5;
        this.refreshHasDraftReport($$6);
        MutableComponent $$7 = Component.a("gui.socialInteractions.narration.hide", $$32);
        MutableComponent $$8 = Component.a("gui.socialInteractions.narration.show", $$32);
        PlayerSocialManager $$9 = $$0.getPlayerSocialManager();
        boolean $$10 = $$0.getChatStatus().isChatAllowed($$0.isLocalServer());
        boolean bl = $$11 = !$$0.player.getUUID().equals($$2);
        if ($$11 && $$10 && !$$9.isBlocked($$2)) {
            this.reportButton = new ImageButton(0, 0, 20, 20, REPORT_BUTTON_SPRITES, $$3 -> $$6.draftReportHandled($$0, $$1, () -> $$0.setScreen(new ReportPlayerScreen($$1, $$6, this)), false), Component.translatable("gui.socialInteractions.report")){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
                }
            };
            this.reportButton.active = this.reportingEnabled;
            this.reportButton.setTooltip(this.createReportButtonTooltip());
            this.reportButton.setTooltipDelay(TOOLTIP_DELAY);
            this.hideButton = new ImageButton(0, 0, 20, 20, MUTE_BUTTON_SPRITES, $$3 -> {
                $$9.hidePlayer($$2);
                this.onHiddenOrShown(true, Component.a("gui.socialInteractions.hidden_in_chat", $$32));
            }, Component.translatable("gui.socialInteractions.hide")){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
                }
            };
            this.hideButton.setTooltip(Tooltip.create(HIDE_TEXT_TOOLTIP, $$7));
            this.hideButton.setTooltipDelay(TOOLTIP_DELAY);
            this.showButton = new ImageButton(0, 0, 20, 20, UNMUTE_BUTTON_SPRITES, $$3 -> {
                $$9.showPlayer($$2);
                this.onHiddenOrShown(false, Component.a("gui.socialInteractions.shown_in_chat", $$32));
            }, Component.translatable("gui.socialInteractions.show")){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return PlayerEntry.this.getEntryNarationMessage(super.createNarrationMessage());
                }
            };
            this.showButton.setTooltip(Tooltip.create(SHOW_TEXT_TOOLTIP, $$8));
            this.showButton.setTooltipDelay(TOOLTIP_DELAY);
            this.children = new ArrayList<AbstractWidget>();
            this.children.add(this.hideButton);
            this.children.add(this.reportButton);
            this.updateHideAndShowButton($$9.isHidden(this.id));
        } else {
            this.children = ImmutableList.of();
        }
    }

    public void refreshHasDraftReport(ReportingContext $$0) {
        this.hasDraftReport = $$0.hasDraftReportFor(this.id);
    }

    private Tooltip createReportButtonTooltip() {
        if (!this.reportingEnabled) {
            return Tooltip.create(REPORT_DISABLED_TOOLTIP);
        }
        return Tooltip.create(REPORT_PLAYER_TOOLTIP, Component.a("gui.socialInteractions.narration.report", this.playerName));
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
        int $$15;
        int $$10 = $$3 + 4;
        int $$11 = $$2 + ($$5 - 24) / 2;
        int $$12 = $$10 + 24 + 4;
        Component $$13 = this.getStatusComponent();
        if ($$13 == CommonComponents.EMPTY) {
            $$0.fill($$3, $$2, $$3 + $$4, $$2 + $$5, BG_FILL);
            int $$14 = $$2 + ($$5 - this.minecraft.font.lineHeight) / 2;
        } else {
            $$0.fill($$3, $$2, $$3 + $$4, $$2 + $$5, BG_FILL_REMOVED);
            $$15 = $$2 + ($$5 - (this.minecraft.font.lineHeight + this.minecraft.font.lineHeight)) / 2;
            $$0.drawString(this.minecraft.font, $$13, $$12, $$15 + 12, PLAYER_STATUS_COLOR);
        }
        PlayerFaceRenderer.draw($$0, this.skinGetter.get(), $$10, $$11, 24);
        $$0.drawString(this.minecraft.font, this.playerName, $$12, $$15, PLAYERNAME_COLOR);
        if (this.isRemoved) {
            $$0.fill($$10, $$11, $$10 + 24, $$11 + 24, SKIN_SHADE);
        }
        if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
            float $$16 = this.tooltipHoverTime;
            this.hideButton.setX($$3 + ($$4 - this.hideButton.getWidth() - 4) - 20 - 4);
            this.hideButton.setY($$2 + ($$5 - this.hideButton.getHeight()) / 2);
            this.hideButton.render($$0, $$6, $$7, $$9);
            this.showButton.setX($$3 + ($$4 - this.showButton.getWidth() - 4) - 20 - 4);
            this.showButton.setY($$2 + ($$5 - this.showButton.getHeight()) / 2);
            this.showButton.render($$0, $$6, $$7, $$9);
            this.reportButton.setX($$3 + ($$4 - this.showButton.getWidth() - 4));
            this.reportButton.setY($$2 + ($$5 - this.showButton.getHeight()) / 2);
            this.reportButton.render($$0, $$6, $$7, $$9);
            if ($$16 == this.tooltipHoverTime) {
                this.tooltipHoverTime = 0.0f;
            }
        }
        if (this.hasDraftReport && this.reportButton != null) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_SPRITE, this.reportButton.getX() + 5, this.reportButton.getY() + 1, 15, 15);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getPlayerId() {
        return this.id;
    }

    public Supplier<PlayerSkin> getSkinGetter() {
        return this.skinGetter;
    }

    public void setRemoved(boolean $$0) {
        this.isRemoved = $$0;
    }

    public boolean isRemoved() {
        return this.isRemoved;
    }

    public void setHasRecentMessages(boolean $$0) {
        this.hasRecentMessages = $$0;
    }

    public boolean hasRecentMessages() {
        return this.hasRecentMessages;
    }

    public boolean isChatReportable() {
        return this.chatReportable;
    }

    private void onHiddenOrShown(boolean $$0, Component $$1) {
        this.updateHideAndShowButton($$0);
        this.minecraft.gui.getChat().addMessage($$1);
        this.minecraft.getNarrator().saySystemNow($$1);
    }

    private void updateHideAndShowButton(boolean $$0) {
        this.showButton.visible = $$0;
        this.hideButton.visible = !$$0;
        this.children.set(0, $$0 ? this.showButton : this.hideButton);
    }

    MutableComponent getEntryNarationMessage(MutableComponent $$0) {
        Component $$1 = this.getStatusComponent();
        if ($$1 == CommonComponents.EMPTY) {
            return Component.literal(this.playerName).append(", ").append($$0);
        }
        return Component.literal(this.playerName).append(", ").append($$1).append(", ").append($$0);
    }

    private Component getStatusComponent() {
        boolean $$0 = this.minecraft.getPlayerSocialManager().isHidden(this.id);
        boolean $$1 = this.minecraft.getPlayerSocialManager().isBlocked(this.id);
        if ($$1 && this.isRemoved) {
            return BLOCKED_OFFLINE;
        }
        if ($$0 && this.isRemoved) {
            return HIDDEN_OFFLINE;
        }
        if ($$1) {
            return BLOCKED;
        }
        if ($$0) {
            return HIDDEN;
        }
        if (this.isRemoved) {
            return OFFLINE;
        }
        return CommonComponents.EMPTY;
    }
}

