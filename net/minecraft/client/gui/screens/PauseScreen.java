/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DialogTags;
import net.minecraft.util.CommonLinks;

public class PauseScreen
extends Screen {
    private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
    private static final int COLUMNS = 2;
    private static final int MENU_PADDING_TOP = 50;
    private static final int BUTTON_PADDING = 4;
    private static final int BUTTON_WIDTH_FULL = 204;
    private static final int BUTTON_WIDTH_HALF = 98;
    private static final Component RETURN_TO_GAME = Component.translatable("menu.returnToGame");
    private static final Component ADVANCEMENTS = Component.translatable("gui.advancements");
    private static final Component STATS = Component.translatable("gui.stats");
    private static final Component SEND_FEEDBACK = Component.translatable("menu.sendFeedback");
    private static final Component REPORT_BUGS = Component.translatable("menu.reportBugs");
    private static final Component FEEDBACK_SUBSCREEN = Component.translatable("menu.feedback");
    private static final Component OPTIONS = Component.translatable("menu.options");
    private static final Component SHARE_TO_LAN = Component.translatable("menu.shareToLan");
    private static final Component PLAYER_REPORTING = Component.translatable("menu.playerReporting");
    private static final Component GAME = Component.translatable("menu.game");
    private static final Component PAUSED = Component.translatable("menu.paused");
    private static final Tooltip CUSTOM_OPTIONS_TOOLTIP = Tooltip.create(Component.translatable("menu.custom_options.tooltip"));
    private final boolean showPauseMenu;
    @Nullable
    private Button disconnectButton;

    public PauseScreen(boolean $$0) {
        super($$0 ? GAME : PAUSED);
        this.showPauseMenu = $$0;
    }

    public boolean showsPauseMenu() {
        return this.showPauseMenu;
    }

    @Override
    protected void init() {
        if (this.showPauseMenu) {
            this.createPauseMenu();
        }
        this.addRenderableWidget(new StringWidget(0, this.showPauseMenu ? 40 : 10, this.width, this.font.lineHeight, this.title, this.font));
    }

    private void createPauseMenu() {
        GridLayout $$02 = new GridLayout();
        $$02.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper $$1 = $$02.createRowHelper(2);
        $$1.addChild(Button.builder(RETURN_TO_GAME, $$0 -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).width(204).build(), 2, $$02.newCellSettings().paddingTop(50));
        $$1.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
        $$1.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
        Optional<? extends Holder<Dialog>> $$2 = this.getCustomAdditions();
        if ($$2.isEmpty()) {
            PauseScreen.addFeedbackButtons(this, $$1);
        } else {
            this.addFeedbackSubscreenAndCustomDialogButtons(this.minecraft, $$2.get(), $$1);
        }
        $$1.addChild(this.openScreenButton(OPTIONS, () -> new OptionsScreen(this, this.minecraft.options)));
        if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
            $$1.addChild(this.openScreenButton(SHARE_TO_LAN, () -> new ShareToLanScreen(this)));
        } else {
            $$1.addChild(this.openScreenButton(PLAYER_REPORTING, () -> new SocialInteractionsScreen(this)));
        }
        this.disconnectButton = $$1.addChild(Button.builder(CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), $$0 -> {
            $$0.active = false;
            this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, () -> PauseScreen.disconnectFromWorld(this.minecraft, ClientLevel.DEFAULT_QUIT_MESSAGE), true);
        }).width(204).build(), 2);
        $$02.arrangeElements();
        FrameLayout.alignInRectangle($$02, 0, 0, this.width, this.height, 0.5f, 0.25f);
        $$02.visitWidgets(this::addRenderableWidget);
    }

    private Optional<? extends Holder<Dialog>> getCustomAdditions() {
        HolderSet $$2;
        HolderLookup.RegistryLookup $$0 = this.minecraft.player.connection.registryAccess().lookupOrThrow(Registries.DIALOG);
        Optional $$1 = $$0.get(DialogTags.PAUSE_SCREEN_ADDITIONS);
        if ($$1.isPresent() && ($$2 = (HolderSet)$$1.get()).size() > 0) {
            if ($$2.size() == 1) {
                return Optional.of($$2.get(0));
            }
            return $$0.get(Dialogs.CUSTOM_OPTIONS);
        }
        ServerLinks $$3 = this.minecraft.player.connection.serverLinks();
        if (!$$3.isEmpty()) {
            return $$0.get(Dialogs.SERVER_LINKS);
        }
        return Optional.empty();
    }

    static void addFeedbackButtons(Screen $$0, GridLayout.RowHelper $$1) {
        $$1.addChild(PauseScreen.openLinkButton($$0, SEND_FEEDBACK, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK));
        $$1.addChild(PauseScreen.openLinkButton((Screen)$$0, (Component)PauseScreen.REPORT_BUGS, (URI)CommonLinks.SNAPSHOT_BUGS_FEEDBACK)).active = !SharedConstants.getCurrentVersion().dataVersion().isSideSeries();
    }

    private void addFeedbackSubscreenAndCustomDialogButtons(Minecraft $$0, Holder<Dialog> $$1, GridLayout.RowHelper $$22) {
        $$22.addChild(this.openScreenButton(FEEDBACK_SUBSCREEN, () -> new FeedbackSubScreen(this)));
        $$22.addChild(Button.builder($$1.value().common().computeExternalTitle(), $$2 -> $$0.player.connection.showDialog($$1, this)).width(98).tooltip(CUSTOM_OPTIONS_TOOLTIP).build());
    }

    public static void disconnectFromWorld(Minecraft $$0, Component $$1) {
        boolean $$2 = $$0.isLocalServer();
        ServerData $$3 = $$0.getCurrentServer();
        if ($$0.level != null) {
            $$0.level.disconnect($$1);
        }
        if ($$2) {
            $$0.disconnectWithSavingScreen();
        } else {
            $$0.disconnectWithProgressScreen();
        }
        TitleScreen $$4 = new TitleScreen();
        if ($$2) {
            $$0.setScreen($$4);
        } else if ($$3 != null && $$3.isRealm()) {
            $$0.setScreen(new RealmsMainScreen($$4));
        } else {
            $$0.setScreen(new JoinMultiplayerScreen($$4));
        }
    }

    @Override
    public void tick() {
        if (this.rendersNowPlayingToast()) {
            NowPlayingToast.tickMusicNotes();
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.rendersNowPlayingToast()) {
            NowPlayingToast.renderToast($$0, this.font);
        }
        if (this.showPauseMenu && this.minecraft != null && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_SPRITE, this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17, this.disconnectButton.getY() + 3, 15, 15);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.showPauseMenu) {
            super.renderBackground($$0, $$1, $$2, $$3);
        }
    }

    public boolean rendersNowPlayingToast() {
        Options $$0 = this.minecraft.options;
        return $$0.showNowPlayingToast().get() != false && $$0.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0f && this.showPauseMenu;
    }

    private Button openScreenButton(Component $$0, Supplier<Screen> $$12) {
        return Button.builder($$0, $$1 -> this.minecraft.setScreen((Screen)$$12.get())).width(98).build();
    }

    private static Button openLinkButton(Screen $$0, Component $$1, URI $$2) {
        return Button.builder($$1, ConfirmLinkScreen.confirmLink($$0, $$2)).width(98).build();
    }

    static class FeedbackSubScreen
    extends Screen {
        private static final Component TITLE = Component.translatable("menu.feedback.title");
        public final Screen parent;
        private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

        protected FeedbackSubScreen(Screen $$0) {
            super(TITLE);
            this.parent = $$0;
        }

        @Override
        protected void init() {
            this.layout.addTitleHeader(TITLE, this.font);
            GridLayout $$02 = this.layout.addToContents(new GridLayout());
            $$02.defaultCellSetting().padding(4, 4, 4, 0);
            GridLayout.RowHelper $$1 = $$02.createRowHelper(2);
            PauseScreen.addFeedbackButtons(this, $$1);
            this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).width(200).build());
            this.layout.visitWidgets(this::addRenderableWidget);
            this.repositionElements();
        }

        @Override
        protected void repositionElements() {
            this.layout.arrangeElements();
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(this.parent);
        }
    }
}

