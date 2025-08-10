/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.social;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonLinks;

public class SocialInteractionsScreen
extends Screen {
    private static final Component TITLE = Component.translatable("gui.socialInteractions.title");
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("social_interactions/background");
    private static final ResourceLocation SEARCH_SPRITE = ResourceLocation.withDefaultNamespace("icon/search");
    private static final Component TAB_ALL = Component.translatable("gui.socialInteractions.tab_all");
    private static final Component TAB_HIDDEN = Component.translatable("gui.socialInteractions.tab_hidden");
    private static final Component TAB_BLOCKED = Component.translatable("gui.socialInteractions.tab_blocked");
    private static final Component TAB_ALL_SELECTED = TAB_ALL.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_HIDDEN_SELECTED = TAB_HIDDEN.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component TAB_BLOCKED_SELECTED = TAB_BLOCKED.plainCopy().withStyle(ChatFormatting.UNDERLINE);
    private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    static final Component EMPTY_SEARCH = Component.translatable("gui.socialInteractions.search_empty").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_HIDDEN = Component.translatable("gui.socialInteractions.empty_hidden").withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_BLOCKED = Component.translatable("gui.socialInteractions.empty_blocked").withStyle(ChatFormatting.GRAY);
    private static final Component BLOCKING_HINT = Component.translatable("gui.socialInteractions.blocking_hint");
    private static final int BG_BORDER_SIZE = 8;
    private static final int BG_WIDTH = 236;
    private static final int SEARCH_HEIGHT = 16;
    private static final int MARGIN_Y = 64;
    public static final int SEARCH_START = 72;
    public static final int LIST_START = 88;
    private static final int IMAGE_WIDTH = 238;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ITEM_HEIGHT = 36;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private final Screen lastScreen;
    @Nullable
    SocialInteractionsPlayerList socialInteractionsPlayerList;
    EditBox searchBox;
    private String lastSearch = "";
    private Page page = Page.ALL;
    private Button allButton;
    private Button hiddenButton;
    private Button blockedButton;
    private Button blockingHintButton;
    @Nullable
    private Component serverLabel;
    private int playerCount;

    public SocialInteractionsScreen() {
        this((Screen)null);
    }

    public SocialInteractionsScreen(@Nullable Screen $$0) {
        super(TITLE);
        this.lastScreen = $$0;
        this.updateServerLabel(Minecraft.getInstance());
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int listEnd() {
        return 80 + this.windowHeight() - 8;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    @Override
    public Component getNarrationMessage() {
        if (this.serverLabel != null) {
            return CommonComponents.a(super.getNarrationMessage(), this.serverLabel);
        }
        return super.getNarrationMessage();
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.socialInteractionsPlayerList = new SocialInteractionsPlayerList(this, this.minecraft, this.width, this.listEnd() - 88, 88, 36);
        int $$02 = this.socialInteractionsPlayerList.getRowWidth() / 3;
        int $$12 = this.socialInteractionsPlayerList.getRowLeft();
        int $$2 = this.socialInteractionsPlayerList.getRowRight();
        this.allButton = this.addRenderableWidget(Button.builder(TAB_ALL, $$0 -> this.showPage(Page.ALL)).bounds($$12, 45, $$02, 20).build());
        this.hiddenButton = this.addRenderableWidget(Button.builder(TAB_HIDDEN, $$0 -> this.showPage(Page.HIDDEN)).bounds(($$12 + $$2 - $$02) / 2 + 1, 45, $$02, 20).build());
        this.blockedButton = this.addRenderableWidget(Button.builder(TAB_BLOCKED, $$0 -> this.showPage(Page.BLOCKED)).bounds($$2 - $$02 + 1, 45, $$02, 20).build());
        String $$3 = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 74, 200, 15, SEARCH_HINT){

            @Override
            protected MutableComponent createNarrationMessage() {
                if (!SocialInteractionsScreen.this.searchBox.getValue().isEmpty() && SocialInteractionsScreen.this.socialInteractionsPlayerList.isEmpty()) {
                    return super.createNarrationMessage().append(", ").append(EMPTY_SEARCH);
                }
                return super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(-1);
        this.searchBox.setValue($$3);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.addRenderableWidget(this.searchBox);
        this.addWidget(this.socialInteractionsPlayerList);
        this.blockingHintButton = this.addRenderableWidget(Button.builder(BLOCKING_HINT, ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.BLOCKING_HELP)).bounds(this.width / 2 - 100, 64 + this.windowHeight(), 200, 20).build());
        this.showPage(this.page);
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).width(200).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    public void added() {
        if (this.socialInteractionsPlayerList != null) {
            this.socialInteractionsPlayerList.refreshHasDraftReport();
        }
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.socialInteractionsPlayerList.updateSizeAndPosition(this.width, this.listEnd() - 88, 88);
        this.searchBox.setPosition(this.marginX() + 28, 74);
        int $$0 = this.socialInteractionsPlayerList.getRowLeft();
        int $$1 = this.socialInteractionsPlayerList.getRowRight();
        int $$2 = this.socialInteractionsPlayerList.getRowWidth() / 3;
        this.allButton.setPosition($$0, 45);
        this.hiddenButton.setPosition(($$0 + $$1 - $$2) / 2 + 1, 45);
        this.blockedButton.setPosition($$1 - $$2 + 1, 45);
        this.blockingHintButton.setPosition(this.width / 2 - 100, 64 + this.windowHeight());
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void showPage(Page $$0) {
        this.page = $$0;
        this.allButton.setMessage(TAB_ALL);
        this.hiddenButton.setMessage(TAB_HIDDEN);
        this.blockedButton.setMessage(TAB_BLOCKED);
        boolean $$1 = false;
        switch ($$0.ordinal()) {
            case 0: {
                this.allButton.setMessage(TAB_ALL_SELECTED);
                Collection<UUID> $$2 = this.minecraft.player.connection.getOnlinePlayerIds();
                this.socialInteractionsPlayerList.updatePlayerList($$2, this.socialInteractionsPlayerList.scrollAmount(), true);
                break;
            }
            case 1: {
                this.hiddenButton.setMessage(TAB_HIDDEN_SELECTED);
                Set<UUID> $$3 = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
                $$1 = $$3.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList($$3, this.socialInteractionsPlayerList.scrollAmount(), false);
                break;
            }
            case 2: {
                this.blockedButton.setMessage(TAB_BLOCKED_SELECTED);
                PlayerSocialManager $$4 = this.minecraft.getPlayerSocialManager();
                Set<UUID> $$5 = this.minecraft.player.connection.getOnlinePlayerIds().stream().filter($$4::isBlocked).collect(Collectors.toSet());
                $$1 = $$5.isEmpty();
                this.socialInteractionsPlayerList.updatePlayerList($$5, this.socialInteractionsPlayerList.scrollAmount(), false);
            }
        }
        GameNarrator $$6 = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.socialInteractionsPlayerList.isEmpty() && !this.searchBox.isFocused()) {
            $$6.saySystemNow(EMPTY_SEARCH);
        } else if ($$1) {
            if ($$0 == Page.HIDDEN) {
                $$6.saySystemNow(EMPTY_HIDDEN);
            } else if ($$0 == Page.BLOCKED) {
                $$6.saySystemNow(EMPTY_BLOCKED);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.renderBackground($$0, $$1, $$2, $$3);
        int $$4 = this.marginX() + 3;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, $$4, 64, 236, this.windowHeight() + 16);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_SPRITE, $$4 + 10, 76, 12, 12);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.updateServerLabel(this.minecraft);
        if (this.serverLabel != null) {
            $$0.drawString(this.minecraft.font, this.serverLabel, this.marginX() + 8, 35, -1);
        }
        if (!this.socialInteractionsPlayerList.isEmpty()) {
            this.socialInteractionsPlayerList.render($$0, $$1, $$2, $$3);
        } else if (!this.searchBox.getValue().isEmpty()) {
            $$0.drawCenteredString(this.minecraft.font, EMPTY_SEARCH, this.width / 2, (72 + this.listEnd()) / 2, -1);
        } else if (this.page == Page.HIDDEN) {
            $$0.drawCenteredString(this.minecraft.font, EMPTY_HIDDEN, this.width / 2, (72 + this.listEnd()) / 2, -1);
        } else if (this.page == Page.BLOCKED) {
            $$0.drawCenteredString(this.minecraft.font, EMPTY_BLOCKED, this.width / 2, (72 + this.listEnd()) / 2, -1);
        }
        this.blockingHintButton.visible = this.page == Page.BLOCKED;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (!this.searchBox.isFocused() && this.minecraft.options.keySocialInteractions.matches($$0, $$1)) {
            this.onClose();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void checkSearchStringUpdate(String $$0) {
        if (!($$0 = $$0.toLowerCase(Locale.ROOT)).equals(this.lastSearch)) {
            this.socialInteractionsPlayerList.setFilter($$0);
            this.lastSearch = $$0;
            this.showPage(this.page);
        }
    }

    private void updateServerLabel(Minecraft $$0) {
        int $$1 = $$0.getConnection().getOnlinePlayers().size();
        if (this.playerCount != $$1) {
            String $$2 = "";
            ServerData $$3 = $$0.getCurrentServer();
            if ($$0.isLocalServer()) {
                $$2 = $$0.getSingleplayerServer().getMotd();
            } else if ($$3 != null) {
                $$2 = $$3.name;
            }
            this.serverLabel = $$1 > 1 ? Component.a("gui.socialInteractions.server_label.multiple", new Object[]{$$2, $$1}) : Component.a("gui.socialInteractions.server_label.single", new Object[]{$$2, $$1});
            this.playerCount = $$1;
        }
    }

    public void onAddPlayer(PlayerInfo $$0) {
        this.socialInteractionsPlayerList.addPlayer($$0, this.page);
    }

    public void onRemovePlayer(UUID $$0) {
        this.socialInteractionsPlayerList.removePlayer($$0);
    }

    public static final class Page
    extends Enum<Page> {
        public static final /* enum */ Page ALL = new Page();
        public static final /* enum */ Page HIDDEN = new Page();
        public static final /* enum */ Page BLOCKED = new Page();
        private static final /* synthetic */ Page[] $VALUES;

        public static Page[] values() {
            return (Page[])$VALUES.clone();
        }

        public static Page valueOf(String $$0) {
            return Enum.valueOf(Page.class, $$0);
        }

        private static /* synthetic */ Page[] a() {
            return new Page[]{ALL, HIDDEN, BLOCKED};
        }

        static {
            $VALUES = Page.a();
        }
    }
}

