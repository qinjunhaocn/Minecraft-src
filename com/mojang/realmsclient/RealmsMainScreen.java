/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 *  java.lang.MatchException
 */
package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsAvailability;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.AddRealmPopupScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientActivePlayersTooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class RealmsMainScreen
extends RealmsScreen {
    static final ResourceLocation INFO_SPRITE = ResourceLocation.withDefaultNamespace("icon/info");
    static final ResourceLocation NEW_REALM_SPRITE = ResourceLocation.withDefaultNamespace("icon/new_realm");
    static final ResourceLocation EXPIRED_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/expired");
    static final ResourceLocation EXPIRES_SOON_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/expires_soon");
    static final ResourceLocation OPEN_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/open");
    static final ResourceLocation CLOSED_SPRITE = ResourceLocation.withDefaultNamespace("realm_status/closed");
    private static final ResourceLocation INVITE_SPRITE = ResourceLocation.withDefaultNamespace("icon/invite");
    private static final ResourceLocation NEWS_SPRITE = ResourceLocation.withDefaultNamespace("icon/news");
    public static final ResourceLocation HARDCORE_MODE_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation NO_REALMS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/realms/no_realms.png");
    private static final Component TITLE = Component.translatable("menu.online");
    private static final Component LOADING_TEXT = Component.translatable("mco.selectServer.loading");
    static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
    static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
    private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
    static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
    private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
    private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
    private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
    static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
    static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
    static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
    static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
    static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
    static final Component UNITIALIZED_WORLD_NARRATION = Component.a("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
    private static final Component NO_REALMS_TEXT = Component.translatable("mco.selectServer.noRealms");
    private static final Component NO_PENDING_INVITES = Component.translatable("mco.invites.nopending");
    private static final Component PENDING_INVITES = Component.translatable("mco.invites.pending");
    private static final Component INCOMPATIBLE_POPUP_TITLE = Component.translatable("mco.compatibility.incompatible.popup.title");
    private static final Component INCOMPATIBLE_RELEASE_TYPE_POPUP_MESSAGE = Component.translatable("mco.compatibility.incompatible.releaseType.popup.message");
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_COLUMNS = 3;
    private static final int BUTTON_SPACING = 4;
    private static final int CONTENT_WIDTH = 308;
    private static final int LOGO_PADDING = 5;
    private static final int HEADER_HEIGHT = 44;
    private static final int FOOTER_PADDING = 11;
    private static final int NEW_REALM_SPRITE_WIDTH = 40;
    private static final int NEW_REALM_SPRITE_HEIGHT = 20;
    private static final int ENTRY_WIDTH = 216;
    private static final int ITEM_HEIGHT = 36;
    private static final boolean SNAPSHOT;
    private static boolean snapshotToggle;
    private final CompletableFuture<RealmsAvailability.Result> availability = RealmsAvailability.get();
    @Nullable
    private DataFetcher.Subscription dataSubscription;
    private final Set<UUID> handledSeenNotifications = new HashSet<UUID>();
    private static boolean regionsPinged;
    private final RateLimiter inviteNarrationLimiter;
    private final Screen lastScreen;
    private Button playButton;
    private Button backButton;
    private Button renewButton;
    private Button configureButton;
    private Button leaveButton;
    RealmSelectionList realmSelectionList;
    RealmsServerList serverList;
    List<RealmsServer> availableSnapshotServers = List.of();
    RealmsServerPlayerLists onlinePlayersPerRealm = new RealmsServerPlayerLists();
    private volatile boolean trialsAvailable;
    @Nullable
    private volatile String newsLink;
    long lastClickTime;
    final List<RealmsNotification> notifications = new ArrayList<RealmsNotification>();
    private Button addRealmButton;
    private NotificationButton pendingInvitesButton;
    private NotificationButton newsButton;
    private LayoutState activeLayoutState;
    @Nullable
    private HeaderAndFooterLayout layout;

    public RealmsMainScreen(Screen $$0) {
        super(TITLE);
        this.lastScreen = $$0;
        this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107);
    }

    @Override
    public void init() {
        this.serverList = new RealmsServerList(this.minecraft);
        this.realmSelectionList = new RealmSelectionList();
        MutableComponent $$02 = Component.translatable("mco.invites.title");
        this.pendingInvitesButton = new NotificationButton($$02, INVITE_SPRITE, $$1 -> this.minecraft.setScreen(new RealmsPendingInvitesScreen(this, $$02)));
        MutableComponent $$12 = Component.translatable("mco.news");
        this.newsButton = new NotificationButton($$12, NEWS_SPRITE, $$0 -> {
            String $$1 = this.newsLink;
            if ($$1 == null) {
                return;
            }
            ConfirmLinkScreen.confirmLinkNow((Screen)this, $$1);
            if (this.newsButton.notificationCount() != 0) {
                RealmsPersistence.RealmsPersistenceData $$2 = RealmsPersistence.readFile();
                $$2.hasUnreadNews = false;
                RealmsPersistence.writeFile($$2);
                this.newsButton.setNotificationCount(0);
            }
        });
        this.newsButton.setTooltip(Tooltip.create($$12));
        this.playButton = Button.builder(PLAY_TEXT, $$0 -> RealmsMainScreen.play(this.getSelectedServer(), this)).width(100).build();
        this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, $$0 -> this.configureClicked(this.getSelectedServer())).width(100).build();
        this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, $$0 -> this.onRenew(this.getSelectedServer())).width(100).build();
        this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, $$0 -> this.leaveClicked(this.getSelectedServer())).width(100).build();
        this.addRealmButton = Button.builder(Component.translatable("mco.selectServer.purchase"), $$0 -> this.openTrialAvailablePopup()).size(100, 20).build();
        this.backButton = Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).width(100).build();
        if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
            this.addRenderableWidget(CycleButton.booleanBuilder(Component.literal("Snapshot"), Component.literal("Release")).create(5, 5, 100, 20, Component.literal("Realm"), ($$0, $$1) -> {
                snapshotToggle = $$1;
                this.availableSnapshotServers = List.of();
                this.debugRefreshDataFetchers();
            }));
        }
        this.updateLayout(LayoutState.LOADING);
        this.updateButtonStates();
        this.availability.thenAcceptAsync($$0 -> {
            Screen $$1 = $$0.createErrorScreen(this.lastScreen);
            if ($$1 == null) {
                this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
            } else {
                this.minecraft.setScreen($$1);
            }
        }, this.screenExecutor);
    }

    public static boolean isSnapshot() {
        return SNAPSHOT && snapshotToggle;
    }

    @Override
    protected void repositionElements() {
        if (this.layout != null) {
            this.realmSelectionList.updateSize(this.width, this.layout);
            this.layout.arrangeElements();
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void updateLayout() {
        if (this.serverList.isEmpty() && this.availableSnapshotServers.isEmpty() && this.notifications.isEmpty()) {
            this.updateLayout(LayoutState.NO_REALMS);
        } else {
            this.updateLayout(LayoutState.LIST);
        }
    }

    private void updateLayout(LayoutState $$0) {
        if (this.activeLayoutState == $$0) {
            return;
        }
        if (this.layout != null) {
            this.layout.visitWidgets($$1 -> this.removeWidget((GuiEventListener)$$1));
        }
        this.layout = this.createLayout($$0);
        this.activeLayoutState = $$0;
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    private HeaderAndFooterLayout createLayout(LayoutState $$0) {
        HeaderAndFooterLayout $$1 = new HeaderAndFooterLayout(this);
        $$1.setHeaderHeight(44);
        $$1.addToHeader(this.createHeader());
        Layout $$2 = this.createFooter($$0);
        $$2.arrangeElements();
        $$1.setFooterHeight($$2.getHeight() + 22);
        $$1.addToFooter($$2);
        switch ($$0.ordinal()) {
            case 0: {
                $$1.addToContents(new LoadingDotsWidget(this.font, LOADING_TEXT));
                break;
            }
            case 1: {
                $$1.addToContents(this.createNoRealmsContent());
                break;
            }
            case 2: {
                $$1.addToContents(this.realmSelectionList);
            }
        }
        return $$1;
    }

    private Layout createHeader() {
        int $$0 = 90;
        LinearLayout $$1 = LinearLayout.horizontal().spacing(4);
        $$1.defaultCellSetting().alignVerticallyMiddle();
        $$1.addChild(this.pendingInvitesButton);
        $$1.addChild(this.newsButton);
        LinearLayout $$2 = LinearLayout.horizontal();
        $$2.defaultCellSetting().alignVerticallyMiddle();
        $$2.addChild(SpacerElement.width(90));
        $$2.addChild(RealmsMainScreen.realmsLogo(), LayoutSettings::alignHorizontallyCenter);
        $$2.addChild(new FrameLayout(90, 44)).addChild($$1, LayoutSettings::alignHorizontallyRight);
        return $$2;
    }

    private Layout createFooter(LayoutState $$0) {
        GridLayout $$1 = new GridLayout().spacing(4);
        GridLayout.RowHelper $$2 = $$1.createRowHelper(3);
        if ($$0 == LayoutState.LIST) {
            $$2.addChild(this.playButton);
            $$2.addChild(this.configureButton);
            $$2.addChild(this.renewButton);
            $$2.addChild(this.leaveButton);
        }
        $$2.addChild(this.addRealmButton);
        $$2.addChild(this.backButton);
        return $$1;
    }

    private LinearLayout createNoRealmsContent() {
        LinearLayout $$0 = LinearLayout.vertical().spacing(8);
        $$0.defaultCellSetting().alignHorizontallyCenter();
        $$0.addChild(ImageWidget.texture(130, 64, NO_REALMS_LOCATION, 130, 64));
        FocusableTextWidget $$1 = new FocusableTextWidget(308, NO_REALMS_TEXT, this.font, false, true, 4);
        $$0.addChild($$1);
        return $$0;
    }

    void updateButtonStates() {
        RealmsServer $$0 = this.getSelectedServer();
        boolean $$1 = $$0 != null;
        this.addRealmButton.active = this.activeLayoutState != LayoutState.LOADING;
        boolean bl = this.playButton.active = $$1 && $$0.shouldPlayButtonBeActive();
        if (!this.playButton.active && $$1 && $$0.state == RealmsServer.State.CLOSED) {
            this.playButton.setTooltip(Tooltip.create(RealmsServer.WORLD_CLOSED_COMPONENT));
        }
        this.renewButton.active = $$1 && this.shouldRenewButtonBeActive($$0);
        this.leaveButton.active = $$1 && this.shouldLeaveButtonBeActive($$0);
        this.configureButton.active = $$1 && this.shouldConfigureButtonBeActive($$0);
    }

    private boolean shouldRenewButtonBeActive(RealmsServer $$0) {
        return $$0.expired && RealmsMainScreen.isSelfOwnedServer($$0);
    }

    private boolean shouldConfigureButtonBeActive(RealmsServer $$0) {
        return RealmsMainScreen.isSelfOwnedServer($$0) && $$0.state != RealmsServer.State.UNINITIALIZED;
    }

    private boolean shouldLeaveButtonBeActive(RealmsServer $$0) {
        return !RealmsMainScreen.isSelfOwnedServer($$0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.dataSubscription != null) {
            this.dataSubscription.tick();
        }
    }

    public static void refreshPendingInvites() {
        Minecraft.getInstance().realmsDataFetcher().pendingInvitesTask.reset();
    }

    public static void refreshServerList() {
        Minecraft.getInstance().realmsDataFetcher().serverListUpdateTask.reset();
    }

    private void debugRefreshDataFetchers() {
        for (DataFetcher.Task<?> $$0 : this.minecraft.realmsDataFetcher().getTasks()) {
            $$0.reset();
        }
    }

    private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher $$02) {
        DataFetcher.Subscription $$12 = $$02.dataFetcher.createSubscription();
        $$12.subscribe($$02.serverListUpdateTask, $$0 -> {
            this.serverList.updateServersList($$0.serverList());
            this.availableSnapshotServers = $$0.availableSnapshotServers();
            this.refreshListAndLayout();
            boolean $$1 = false;
            for (RealmsServer $$2 : this.serverList) {
                if (!this.isSelfOwnedNonExpiredServer($$2)) continue;
                $$1 = true;
            }
            if (!regionsPinged && $$1) {
                regionsPinged = true;
                this.pingRegions();
            }
        });
        RealmsMainScreen.callRealmsClient(RealmsClient::getNotifications, $$0 -> {
            this.notifications.clear();
            this.notifications.addAll((Collection<RealmsNotification>)$$0);
            for (RealmsNotification $$1 : $$0) {
                RealmsNotification.InfoPopup $$2;
                PopupScreen $$3;
                if (!($$1 instanceof RealmsNotification.InfoPopup) || ($$3 = ($$2 = (RealmsNotification.InfoPopup)$$1).buildScreen(this, this::dismissNotification)) == null) continue;
                this.minecraft.setScreen($$3);
                this.markNotificationsAsSeen(List.of((Object)$$1));
                break;
            }
            if (!this.notifications.isEmpty() && this.activeLayoutState != LayoutState.LOADING) {
                this.refreshListAndLayout();
            }
        });
        $$12.subscribe($$02.pendingInvitesTask, $$0 -> {
            this.pendingInvitesButton.setNotificationCount((int)$$0);
            this.pendingInvitesButton.setTooltip($$0 == 0 ? Tooltip.create(NO_PENDING_INVITES) : Tooltip.create(PENDING_INVITES));
            if ($$0 > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
                this.minecraft.getNarrator().saySystemNow(Component.a("mco.configure.world.invite.narration", $$0));
            }
        });
        $$12.subscribe($$02.trialAvailabilityTask, $$0 -> {
            this.trialsAvailable = $$0;
        });
        $$12.subscribe($$02.onlinePlayersTask, $$0 -> {
            this.onlinePlayersPerRealm = $$0;
        });
        $$12.subscribe($$02.newsTask, $$1 -> {
            $$0.newsManager.updateUnreadNews((RealmsNews)$$1);
            this.newsLink = $$0.newsManager.newsLink();
            this.newsButton.setNotificationCount($$0.newsManager.hasUnreadNews() ? Integer.MAX_VALUE : 0);
        });
        return $$12;
    }

    void markNotificationsAsSeen(Collection<RealmsNotification> $$0) {
        ArrayList<UUID> $$12 = new ArrayList<UUID>($$0.size());
        for (RealmsNotification $$2 : $$0) {
            if ($$2.seen() || this.handledSeenNotifications.contains($$2.uuid())) continue;
            $$12.add($$2.uuid());
        }
        if (!$$12.isEmpty()) {
            RealmsMainScreen.callRealmsClient($$1 -> {
                $$1.notificationsSeen($$12);
                return null;
            }, $$1 -> this.handledSeenNotifications.addAll($$12));
        }
    }

    private static <T> void callRealmsClient(RealmsCall<T> $$02, Consumer<T> $$1) {
        Minecraft $$2 = Minecraft.getInstance();
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
            try {
                return $$02.request(RealmsClient.getOrCreate($$2));
            } catch (RealmsServiceException $$2) {
                throw new RuntimeException($$2);
            }
        }).thenAcceptAsync($$1, (Executor)$$2)).exceptionally($$0 -> {
            LOGGER.error("Failed to execute call to Realms Service", (Throwable)$$0);
            return null;
        });
    }

    private void refreshListAndLayout() {
        this.realmSelectionList.refreshEntries(this, this.getSelectedServer());
        this.updateLayout();
        this.updateButtonStates();
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> $$0 = Ping.pingAllRegions();
            RealmsClient $$1 = RealmsClient.getOrCreate();
            PingResult $$2 = new PingResult();
            $$2.pingResults = $$0;
            $$2.realmIds = this.getOwnedNonExpiredRealmIds();
            try {
                $$1.sendPingResults($$2);
            } catch (Throwable $$3) {
                LOGGER.warn("Could not send ping result to Realms: ", $$3);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredRealmIds() {
        ArrayList<Long> $$0 = Lists.newArrayList();
        for (RealmsServer $$1 : this.serverList) {
            if (!this.isSelfOwnedNonExpiredServer($$1)) continue;
            $$0.add($$1.id);
        }
        return $$0;
    }

    private void onRenew(@Nullable RealmsServer $$0) {
        if ($$0 != null) {
            String $$1 = CommonLinks.extendRealms($$0.remoteSubscriptionId, this.minecraft.getUser().getProfileId(), $$0.expiredTrial);
            this.minecraft.keyboardHandler.setClipboard($$1);
            Util.getPlatform().openUri($$1);
        }
    }

    private void configureClicked(@Nullable RealmsServer $$0) {
        if ($$0 != null && this.minecraft.isLocalPlayer($$0.ownerUUID)) {
            this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, $$0.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer $$0) {
        if ($$0 != null && !this.minecraft.isLocalPlayer($$0.ownerUUID)) {
            MutableComponent $$12 = Component.translatable("mco.configure.world.leave.question.line1");
            this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, $$12, $$1 -> this.leaveServer($$0)));
        }
    }

    @Nullable
    private RealmsServer getSelectedServer() {
        Object e = this.realmSelectionList.getSelected();
        if (e instanceof ServerEntry) {
            ServerEntry $$0 = (ServerEntry)e;
            return $$0.getServer();
        }
        return null;
    }

    private void leaveServer(final RealmsServer $$0) {
        new Thread("Realms-leave-server"){

            @Override
            public void run() {
                try {
                    RealmsClient $$02 = RealmsClient.getOrCreate();
                    $$02.uninviteMyselfFrom($$0.id);
                    RealmsMainScreen.this.minecraft.execute(RealmsMainScreen::refreshServerList);
                } catch (RealmsServiceException $$1) {
                    LOGGER.error("Couldn't configure world", $$1);
                    RealmsMainScreen.this.minecraft.execute(() -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen($$1, (Screen)RealmsMainScreen.this)));
                }
            }
        }.start();
        this.minecraft.setScreen(this);
    }

    void dismissNotification(UUID $$0) {
        RealmsMainScreen.callRealmsClient($$1 -> {
            $$1.notificationsDismiss(List.of((Object)$$0));
            return null;
        }, $$12 -> {
            this.notifications.removeIf($$1 -> $$1.dismissable() && $$0.equals($$1.uuid()));
            this.refreshListAndLayout();
        });
    }

    public void resetScreen() {
        this.realmSelectionList.setSelected((Entry)null);
        RealmsMainScreen.refreshServerList();
    }

    @Override
    public Component getNarrationMessage() {
        return switch (this.activeLayoutState.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> CommonComponents.a(super.getNarrationMessage(), LOADING_TEXT);
            case 1 -> CommonComponents.a(super.getNarrationMessage(), NO_REALMS_TEXT);
            case 2 -> super.getNarrationMessage();
        };
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (RealmsMainScreen.isSnapshot()) {
            $$0.drawString(this.font, "Minecraft " + SharedConstants.getCurrentVersion().name(), 2, this.height - 10, -1);
        }
        if (this.trialsAvailable && this.addRealmButton.active) {
            AddRealmPopupScreen.renderDiamond($$0, this.addRealmButton);
        }
        switch (RealmsClient.ENVIRONMENT) {
            case STAGE: {
                this.renderEnvironment($$0, "STAGE!", -256);
                break;
            }
            case LOCAL: {
                this.renderEnvironment($$0, "LOCAL!", -8388737);
            }
        }
    }

    private void openTrialAvailablePopup() {
        this.minecraft.setScreen(new AddRealmPopupScreen(this, this.trialsAvailable));
    }

    public static void play(@Nullable RealmsServer $$0, Screen $$1) {
        RealmsMainScreen.play($$0, $$1, false);
    }

    public static void play(@Nullable RealmsServer $$0, Screen $$1, boolean $$2) {
        if ($$0 != null) {
            if (!RealmsMainScreen.isSnapshot() || $$2 || $$0.isMinigameActive()) {
                Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen($$1, new GetServerDetailsTask($$1, $$0)));
                return;
            }
            switch ($$0.compatibility) {
                case COMPATIBLE: {
                    Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen($$1, new GetServerDetailsTask($$1, $$0)));
                    break;
                }
                case UNVERIFIABLE: {
                    RealmsMainScreen.confirmToPlay($$0, $$1, Component.translatable("mco.compatibility.unverifiable.title").withColor(-171), Component.translatable("mco.compatibility.unverifiable.message"), CommonComponents.GUI_CONTINUE);
                    break;
                }
                case NEEDS_DOWNGRADE: {
                    RealmsMainScreen.confirmToPlay($$0, $$1, Component.translatable("selectWorld.backupQuestion.downgrade").withColor(-2142128), Component.a("mco.compatibility.downgrade.description", Component.literal($$0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().name()).withColor(-171)), Component.translatable("mco.compatibility.downgrade"));
                    break;
                }
                case NEEDS_UPGRADE: {
                    RealmsMainScreen.upgradeRealmAndPlay($$0, $$1);
                    break;
                }
                case INCOMPATIBLE: {
                    Minecraft.getInstance().setScreen(new PopupScreen.Builder($$1, INCOMPATIBLE_POPUP_TITLE).setMessage(Component.a("mco.compatibility.incompatible.series.popup.message", Component.literal($$0.activeVersion).withColor(-171), Component.literal(SharedConstants.getCurrentVersion().name()).withColor(-171))).addButton(CommonComponents.GUI_BACK, PopupScreen::onClose).build());
                    break;
                }
                case RELEASE_TYPE_INCOMPATIBLE: {
                    Minecraft.getInstance().setScreen(new PopupScreen.Builder($$1, INCOMPATIBLE_POPUP_TITLE).setMessage(INCOMPATIBLE_RELEASE_TYPE_POPUP_MESSAGE).addButton(CommonComponents.GUI_BACK, PopupScreen::onClose).build());
                }
            }
        }
    }

    private static void confirmToPlay(RealmsServer $$0, Screen $$1, Component $$22, Component $$3, Component $$4) {
        Minecraft.getInstance().setScreen(new PopupScreen.Builder($$1, $$22).setMessage($$3).addButton($$4, $$2 -> {
            Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen($$1, new GetServerDetailsTask($$1, $$0)));
            RealmsMainScreen.refreshServerList();
        }).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build());
    }

    private static void upgradeRealmAndPlay(RealmsServer $$0, Screen $$1) {
        MutableComponent $$2 = Component.translatable("mco.compatibility.upgrade.title").withColor(-171);
        MutableComponent $$3 = Component.translatable("mco.compatibility.upgrade");
        MutableComponent $$4 = Component.literal($$0.activeVersion).withColor(-171);
        MutableComponent $$5 = Component.literal(SharedConstants.getCurrentVersion().name()).withColor(-171);
        MutableComponent $$6 = RealmsMainScreen.isSelfOwnedServer($$0) ? Component.a("mco.compatibility.upgrade.description", $$4, $$5) : Component.a("mco.compatibility.upgrade.friend.description", $$4, $$5);
        RealmsMainScreen.confirmToPlay($$0, $$1, $$2, $$6, $$3);
    }

    public static Component getVersionComponent(String $$0, boolean $$1) {
        return RealmsMainScreen.getVersionComponent($$0, $$1 ? -8355712 : -2142128);
    }

    public static Component getVersionComponent(String $$0, int $$1) {
        if (StringUtils.isBlank($$0)) {
            return CommonComponents.EMPTY;
        }
        return Component.literal($$0).withColor($$1);
    }

    public static Component getGameModeComponent(int $$0, boolean $$1) {
        if ($$1) {
            return Component.translatable("gameMode.hardcore").withColor(-65536);
        }
        return GameType.byId($$0).getLongDisplayName();
    }

    static boolean isSelfOwnedServer(RealmsServer $$0) {
        return Minecraft.getInstance().isLocalPlayer($$0.ownerUUID);
    }

    private boolean isSelfOwnedNonExpiredServer(RealmsServer $$0) {
        return RealmsMainScreen.isSelfOwnedServer($$0) && !$$0.expired;
    }

    private void renderEnvironment(GuiGraphics $$0, String $$1, int $$2) {
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)(this.width / 2 - 25), 20.0f);
        $$0.pose().rotate(-0.34906584f);
        $$0.pose().scale(1.5f, 1.5f);
        $$0.drawString(this.font, $$1, 0, 0, $$2);
        $$0.pose().popMatrix();
    }

    static {
        snapshotToggle = SNAPSHOT = !SharedConstants.getCurrentVersion().stable();
    }

    class RealmSelectionList
    extends ObjectSelectionList<Entry> {
        public RealmSelectionList() {
            super(Minecraft.getInstance(), RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsMainScreen.this.updateButtonStates();
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        void refreshEntries(RealmsMainScreen $$0, @Nullable RealmsServer $$1) {
            this.clearEntries();
            for (RealmsNotification $$2 : RealmsMainScreen.this.notifications) {
                if (!($$2 instanceof RealmsNotification.VisitUrl)) continue;
                RealmsNotification.VisitUrl $$3 = (RealmsNotification.VisitUrl)$$2;
                this.addEntriesForNotification($$3, $$0);
                RealmsMainScreen.this.markNotificationsAsSeen(List.of((Object)$$2));
                break;
            }
            this.refreshServerEntries($$1);
        }

        private void refreshServerEntries(@Nullable RealmsServer $$0) {
            for (RealmsServer $$1 : RealmsMainScreen.this.availableSnapshotServers) {
                this.addEntry(new AvailableSnapshotEntry($$1));
            }
            for (RealmsServer $$2 : RealmsMainScreen.this.serverList) {
                ServerEntry $$4;
                if (RealmsMainScreen.isSnapshot() && !$$2.isSnapshotRealm()) {
                    if ($$2.state == RealmsServer.State.UNINITIALIZED) continue;
                    ParentEntry $$3 = new ParentEntry(RealmsMainScreen.this, $$2);
                } else {
                    $$4 = new ServerEntry($$2);
                }
                this.addEntry($$4);
                if ($$0 == null || $$0.id != $$2.id) continue;
                this.setSelected($$4);
            }
        }

        private void addEntriesForNotification(RealmsNotification.VisitUrl $$0, RealmsMainScreen $$1) {
            Component $$2 = $$0.getMessage();
            int $$3 = RealmsMainScreen.this.font.wordWrapHeight($$2, 216);
            int $$4 = Mth.positiveCeilDiv($$3 + 7, 36) - 1;
            this.addEntry(new NotificationMessageEntry($$2, $$4 + 2, $$0));
            for (int $$5 = 0; $$5 < $$4; ++$$5) {
                this.addEntry(new EmptyEntry(RealmsMainScreen.this));
            }
            this.addEntry(new ButtonEntry($$0.buildOpenLinkButton($$1)));
        }
    }

    static class NotificationButton
    extends SpriteIconButton.CenteredIcon {
        private static final ResourceLocation[] NOTIFICATION_ICONS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("notification/1"), ResourceLocation.withDefaultNamespace("notification/2"), ResourceLocation.withDefaultNamespace("notification/3"), ResourceLocation.withDefaultNamespace("notification/4"), ResourceLocation.withDefaultNamespace("notification/5"), ResourceLocation.withDefaultNamespace("notification/more")};
        private static final int UNKNOWN_COUNT = Integer.MAX_VALUE;
        private static final int SIZE = 20;
        private static final int SPRITE_SIZE = 14;
        private int notificationCount;

        public NotificationButton(Component $$0, ResourceLocation $$1, Button.OnPress $$2) {
            super(20, 20, $$0, 14, 14, $$1, $$2, null);
        }

        int notificationCount() {
            return this.notificationCount;
        }

        public void setNotificationCount(int $$0) {
            this.notificationCount = $$0;
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            super.renderWidget($$0, $$1, $$2, $$3);
            if (this.active && this.notificationCount != 0) {
                this.drawNotificationCounter($$0);
            }
        }

        private void drawNotificationCounter(GuiGraphics $$0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, NOTIFICATION_ICONS[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
        }
    }

    static final class LayoutState
    extends Enum<LayoutState> {
        public static final /* enum */ LayoutState LOADING = new LayoutState();
        public static final /* enum */ LayoutState NO_REALMS = new LayoutState();
        public static final /* enum */ LayoutState LIST = new LayoutState();
        private static final /* synthetic */ LayoutState[] $VALUES;

        public static LayoutState[] values() {
            return (LayoutState[])$VALUES.clone();
        }

        public static LayoutState valueOf(String $$0) {
            return Enum.valueOf(LayoutState.class, $$0);
        }

        private static /* synthetic */ LayoutState[] a() {
            return new LayoutState[]{LOADING, NO_REALMS, LIST};
        }

        static {
            $VALUES = LayoutState.a();
        }
    }

    static interface RealmsCall<T> {
        public T request(RealmsClient var1) throws RealmsServiceException;
    }

    class ServerEntry
    extends Entry {
        private static final Component ONLINE_PLAYERS_TOOLTIP_HEADER = Component.translatable("mco.onlinePlayers");
        private static final int PLAYERS_ONLINE_SPRITE_SIZE = 9;
        private static final int SKIN_HEAD_LARGE_WIDTH = 36;
        private final RealmsServer serverData;
        private final WidgetTooltipHolder tooltip;

        public ServerEntry(RealmsServer $$0) {
            this.tooltip = new WidgetTooltipHolder();
            this.serverData = $$0;
            boolean $$1 = RealmsMainScreen.isSelfOwnedServer($$0);
            if (RealmsMainScreen.isSnapshot() && $$1 && $$0.isSnapshotRealm()) {
                this.tooltip.set(Tooltip.create(Component.a("mco.snapshot.paired", $$0.parentWorldName)));
            } else if (!$$1 && $$0.needsDowngrade()) {
                this.tooltip.set(Tooltip.create(Component.a("mco.snapshot.friendsRealm.downgrade", $$0.activeVersion)));
            }
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, NEW_REALM_SPRITE, $$3 - 5, $$2 + $$5 / 2 - 10, 40, 20);
                int $$10 = $$2 + $$5 / 2 - ((RealmsMainScreen)RealmsMainScreen.this).font.lineHeight / 2;
                $$0.drawString(RealmsMainScreen.this.font, SERVER_UNITIALIZED_TEXT, $$3 + 40 - 2, $$10, -8388737);
                return;
            }
            RealmsUtil.renderPlayerFace($$0, $$3, $$2, 32, this.serverData.ownerUUID);
            this.renderFirstLine($$0, $$2, $$3, $$4, -1, this.serverData);
            this.renderSecondLine($$0, $$2, $$3, $$4, this.serverData);
            this.renderThirdLine($$0, $$2, $$3, this.serverData);
            this.renderStatusLights(this.serverData, $$0, $$3 + $$4, $$2, $$6, $$7);
            boolean $$11 = this.renderOnlinePlayers($$0, $$2, $$3, $$4, $$5, $$6, $$7, $$9);
            if (!$$11) {
                this.tooltip.refreshTooltipForNextRenderPass($$0, $$6, $$7, $$8, this.isFocused(), new ScreenRectangle($$3, $$2, $$4, $$5));
            }
        }

        private boolean renderOnlinePlayers(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, float $$7) {
            List<ProfileResult> $$8 = RealmsMainScreen.this.onlinePlayersPerRealm.getProfileResultsFor(this.serverData.id);
            if (!$$8.isEmpty()) {
                int $$9 = $$2 + $$3 - 21;
                int $$10 = $$1 + $$4 - 9 - 2;
                int $$11 = $$9;
                for (int $$12 = 0; $$12 < $$8.size(); ++$$12) {
                    PlayerFaceRenderer.draw($$0, Minecraft.getInstance().getSkinManager().getInsecureSkin($$8.get($$12).profile()), $$11 -= 9 + ($$12 == 0 ? 0 : 3), $$10, 9);
                }
                if ($$5 >= $$11 && $$5 <= $$9 && $$6 >= $$10 && $$6 <= $$10 + 9) {
                    $$0.setTooltipForNextFrame(RealmsMainScreen.this.font, List.of((Object)ONLINE_PLAYERS_TOOLTIP_HEADER), Optional.of(new ClientActivePlayersTooltip.ActivePlayersTooltip($$8)), $$5, $$6);
                    return true;
                }
            }
            return false;
        }

        private void playRealm() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsMainScreen.play(this.serverData, RealmsMainScreen.this);
        }

        private void createUnitializedRealm() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsCreateRealmScreen $$0 = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.serverData, this.serverData.isSnapshotRealm());
            RealmsMainScreen.this.minecraft.setScreen($$0);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                this.createUnitializedRealm();
            } else if (this.serverData.shouldPlayButtonBeActive()) {
                if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isFocused()) {
                    this.playRealm();
                }
                RealmsMainScreen.this.lastClickTime = Util.getMillis();
            }
            return true;
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (CommonInputs.selected($$0)) {
                if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                    this.createUnitializedRealm();
                    return true;
                }
                if (this.serverData.shouldPlayButtonBeActive()) {
                    this.playRealm();
                    return true;
                }
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        @Override
        public Component getNarration() {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                return UNITIALIZED_WORLD_NARRATION;
            }
            return Component.a("narrator.select", Objects.requireNonNullElse((Object)this.serverData.name, (Object)"unknown server"));
        }

        public RealmsServer getServer() {
            return this.serverData;
        }
    }

    abstract class Entry
    extends ObjectSelectionList.Entry<Entry> {
        protected static final int STATUS_LIGHT_WIDTH = 10;
        private static final int STATUS_LIGHT_HEIGHT = 28;
        protected static final int PADDING_X = 7;
        protected static final int PADDING_Y = 2;

        Entry() {
        }

        protected void renderStatusLights(RealmsServer $$0, GuiGraphics $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$6 = $$2 - 10 - 7;
            int $$7 = $$3 + 2;
            if ($$0.expired) {
                this.drawRealmStatus($$1, $$6, $$7, $$4, $$5, EXPIRED_SPRITE, () -> SERVER_EXPIRED_TOOLTIP);
            } else if ($$0.state == RealmsServer.State.CLOSED) {
                this.drawRealmStatus($$1, $$6, $$7, $$4, $$5, CLOSED_SPRITE, () -> SERVER_CLOSED_TOOLTIP);
            } else if (RealmsMainScreen.isSelfOwnedServer($$0) && $$0.daysLeft < 7) {
                this.drawRealmStatus($$1, $$6, $$7, $$4, $$5, EXPIRES_SOON_SPRITE, () -> {
                    if ($$0.daysLeft <= 0) {
                        return SERVER_EXPIRES_SOON_TOOLTIP;
                    }
                    if ($$0.daysLeft == 1) {
                        return SERVER_EXPIRES_IN_DAY_TOOLTIP;
                    }
                    return Component.a("mco.selectServer.expires.days", $$0.daysLeft);
                });
            } else if ($$0.state == RealmsServer.State.OPEN) {
                this.drawRealmStatus($$1, $$6, $$7, $$4, $$5, OPEN_SPRITE, () -> SERVER_OPEN_TOOLTIP);
            }
        }

        private void drawRealmStatus(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, ResourceLocation $$5, Supplier<Component> $$6) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5, $$1, $$2, 10, 28);
            if (RealmsMainScreen.this.realmSelectionList.isMouseOver($$3, $$4) && $$3 >= $$1 && $$3 <= $$1 + 10 && $$4 >= $$2 && $$4 <= $$2 + 28) {
                $$0.setTooltipForNextFrame($$6.get(), $$3, $$4);
            }
        }

        protected void renderFirstLine(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, RealmsServer $$5) {
            int $$6 = this.textX($$2);
            int $$7 = this.firstLineY($$1);
            Component $$8 = RealmsMainScreen.getVersionComponent($$5.activeVersion, $$5.isCompatible());
            int $$9 = this.versionTextX($$2, $$3, $$8);
            this.renderClampedString($$0, $$5.getName(), $$6, $$7, $$9, $$4);
            if ($$8 != CommonComponents.EMPTY && !$$5.isMinigameActive()) {
                $$0.drawString(RealmsMainScreen.this.font, $$8, $$9, $$7, -8355712);
            }
        }

        protected void renderSecondLine(GuiGraphics $$0, int $$1, int $$2, int $$3, RealmsServer $$4) {
            int $$5 = this.textX($$2);
            int $$6 = this.firstLineY($$1);
            int $$7 = this.secondLineY($$6);
            String $$8 = $$4.getMinigameName();
            boolean $$9 = $$4.isMinigameActive();
            if ($$9 && $$8 != null) {
                MutableComponent $$10 = Component.literal($$8).withStyle(ChatFormatting.GRAY);
                $$0.drawString(RealmsMainScreen.this.font, Component.a("mco.selectServer.minigameName", $$10).withColor(-171), $$5, $$7, -1);
            } else {
                int $$11 = this.renderGameMode($$4, $$0, $$2, $$3, $$6);
                this.renderClampedString($$0, $$4.getDescription(), $$5, this.secondLineY($$6), $$11, -8355712);
            }
        }

        protected void renderThirdLine(GuiGraphics $$0, int $$1, int $$2, RealmsServer $$3) {
            int $$4 = this.textX($$2);
            int $$5 = this.firstLineY($$1);
            int $$6 = this.thirdLineY($$5);
            if (!RealmsMainScreen.isSelfOwnedServer($$3)) {
                $$0.drawString(RealmsMainScreen.this.font, $$3.owner, $$4, this.thirdLineY($$5), -8355712);
            } else if ($$3.expired) {
                Component $$7 = $$3.expiredTrial ? TRIAL_EXPIRED_TEXT : SUBSCRIPTION_EXPIRED_TEXT;
                $$0.drawString(RealmsMainScreen.this.font, $$7, $$4, $$6, -2142128);
            }
        }

        protected void renderClampedString(GuiGraphics $$0, @Nullable String $$1, int $$2, int $$3, int $$4, int $$5) {
            if ($$1 == null) {
                return;
            }
            int $$6 = $$4 - $$2;
            if (RealmsMainScreen.this.font.width($$1) > $$6) {
                String $$7 = RealmsMainScreen.this.font.plainSubstrByWidth($$1, $$6 - RealmsMainScreen.this.font.width("... "));
                $$0.drawString(RealmsMainScreen.this.font, $$7 + "...", $$2, $$3, $$5);
            } else {
                $$0.drawString(RealmsMainScreen.this.font, $$1, $$2, $$3, $$5);
            }
        }

        protected int versionTextX(int $$0, int $$1, Component $$2) {
            return $$0 + $$1 - RealmsMainScreen.this.font.width($$2) - 20;
        }

        protected int gameModeTextX(int $$0, int $$1, Component $$2) {
            return $$0 + $$1 - RealmsMainScreen.this.font.width($$2) - 20;
        }

        protected int renderGameMode(RealmsServer $$0, GuiGraphics $$1, int $$2, int $$3, int $$4) {
            boolean $$5 = $$0.isHardcore;
            int $$6 = $$0.gameMode;
            int $$7 = $$2;
            if (GameType.isValidId($$6)) {
                Component $$8 = RealmsMainScreen.getGameModeComponent($$6, $$5);
                $$7 = this.gameModeTextX($$2, $$3, $$8);
                $$1.drawString(RealmsMainScreen.this.font, $$8, $$7, this.secondLineY($$4), -8355712);
            }
            if ($$5) {
                $$1.blitSprite(RenderPipelines.GUI_TEXTURED, HARDCORE_MODE_SPRITE, $$7 -= 10, this.secondLineY($$4), 8, 8);
            }
            return $$7;
        }

        protected int firstLineY(int $$0) {
            return $$0 + 1;
        }

        protected int lineHeight() {
            return 2 + ((RealmsMainScreen)RealmsMainScreen.this).font.lineHeight;
        }

        protected int textX(int $$0) {
            return $$0 + 36 + 2;
        }

        protected int secondLineY(int $$0) {
            return $$0 + this.lineHeight();
        }

        protected int thirdLineY(int $$0) {
            return $$0 + this.lineHeight() * 2;
        }
    }

    static class CrossButton
    extends ImageButton {
        private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/cross_button"), ResourceLocation.withDefaultNamespace("widget/cross_button_highlighted"));

        protected CrossButton(Button.OnPress $$0, Component $$1) {
            super(0, 0, 14, 14, SPRITES, $$0);
            this.setTooltip(Tooltip.create($$1));
        }
    }

    class ParentEntry
    extends Entry {
        private final RealmsServer server;
        private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

        public ParentEntry(RealmsMainScreen realmsMainScreen, RealmsServer $$0) {
            this.server = $$0;
            if (!$$0.expired) {
                this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.parent.tooltip")));
            }
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderStatusLights(this.server, $$0, $$3 + $$4, $$2, $$6, $$7);
            RealmsUtil.renderPlayerFace($$0, $$3, $$2, 32, this.server.ownerUUID);
            this.renderFirstLine($$0, $$2, $$3, $$4, -8355712, this.server);
            this.renderSecondLine($$0, $$2, $$3, $$4, this.server);
            this.renderThirdLine($$0, $$2, $$3, this.server);
            this.tooltip.refreshTooltipForNextRenderPass($$0, $$6, $$7, $$8, this.isFocused(), new ScreenRectangle($$3, $$2, $$4, $$5));
        }

        @Override
        public Component getNarration() {
            return Component.literal((String)Objects.requireNonNullElse((Object)this.server.name, (Object)"unknown server"));
        }
    }

    class AvailableSnapshotEntry
    extends Entry {
        private static final Component START_SNAPSHOT_REALM = Component.translatable("mco.snapshot.start");
        private static final int TEXT_PADDING = 5;
        private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
        private final RealmsServer parent;

        public AvailableSnapshotEntry(RealmsServer $$0) {
            this.parent = $$0;
            this.tooltip.set(Tooltip.create(Component.translatable("mco.snapshot.tooltip")));
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, NEW_REALM_SPRITE, $$3 - 5, $$2 + $$5 / 2 - 10, 40, 20);
            int $$10 = $$2 + $$5 / 2 - ((RealmsMainScreen)RealmsMainScreen.this).font.lineHeight / 2;
            $$0.drawString(RealmsMainScreen.this.font, START_SNAPSHOT_REALM, $$3 + 40 - 2, $$10 - 5, -8388737);
            $$0.drawString(RealmsMainScreen.this.font, Component.a("mco.snapshot.description", Objects.requireNonNullElse((Object)this.parent.name, (Object)"unknown server")), $$3 + 40 - 2, $$10 + 5, -8355712);
            this.tooltip.refreshTooltipForNextRenderPass($$0, $$6, $$7, $$8, this.isFocused(), new ScreenRectangle($$3, $$2, $$4, $$5));
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            this.addSnapshotRealm();
            return true;
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (CommonInputs.selected($$0)) {
                this.addSnapshotRealm();
                return false;
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        private void addSnapshotRealm() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsMainScreen.this.minecraft.setScreen(new PopupScreen.Builder(RealmsMainScreen.this, Component.translatable("mco.snapshot.createSnapshotPopup.title")).setMessage(Component.translatable("mco.snapshot.createSnapshotPopup.text")).addButton(Component.translatable("mco.selectServer.create"), $$0 -> RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.parent, true))).addButton(CommonComponents.GUI_CANCEL, PopupScreen::onClose).build());
        }

        @Override
        public Component getNarration() {
            return Component.a("gui.narrate.button", CommonComponents.a(START_SNAPSHOT_REALM, Component.a("mco.snapshot.description", Objects.requireNonNullElse((Object)this.parent.name, (Object)"unknown server"))));
        }
    }

    class ButtonEntry
    extends Entry {
        private final Button button;

        public ButtonEntry(Button $$0) {
            this.button = $$0;
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            this.button.mouseClicked($$0, $$1, $$2);
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (this.button.keyPressed($$0, $$1, $$2)) {
                return true;
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, $$2 + 4);
            this.button.render($$0, $$6, $$7, $$9);
        }

        @Override
        public void setFocused(boolean $$0) {
            super.setFocused($$0);
            this.button.setFocused($$0);
        }

        @Override
        public Component getNarration() {
            return this.button.getMessage();
        }
    }

    class EmptyEntry
    extends Entry {
        EmptyEntry(RealmsMainScreen realmsMainScreen) {
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }

    class NotificationMessageEntry
    extends Entry {
        private static final int SIDE_MARGINS = 40;
        private static final int OUTLINE_COLOR = -12303292;
        private final Component text;
        private final int frameItemHeight;
        private final List<AbstractWidget> children = new ArrayList<AbstractWidget>();
        @Nullable
        private final CrossButton dismissButton;
        private final MultiLineTextWidget textWidget;
        private final GridLayout gridLayout;
        private final FrameLayout textFrame;
        private int lastEntryWidth = -1;

        public NotificationMessageEntry(Component $$0, int $$12, RealmsNotification $$2) {
            this.text = $$0;
            this.frameItemHeight = $$12;
            this.gridLayout = new GridLayout();
            int $$3 = 7;
            this.gridLayout.addChild(ImageWidget.sprite(20, 20, INFO_SPRITE), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
            this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
            this.textFrame = this.gridLayout.addChild(new FrameLayout(0, ((RealmsMainScreen)RealmsMainScreen.this).font.lineHeight * 3 * ($$12 - 1)), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
            this.textWidget = this.textFrame.addChild(new MultiLineTextWidget($$0, RealmsMainScreen.this.font).setCentered(true), this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop());
            this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
            this.dismissButton = $$2.dismissable() ? this.gridLayout.addChild(new CrossButton($$1 -> RealmsMainScreen.this.dismissNotification($$2.uuid()), Component.translatable("mco.notification.dismiss")), 0, 2, this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0)) : null;
            this.gridLayout.visitWidgets(this.children::add);
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (this.dismissButton != null && this.dismissButton.keyPressed($$0, $$1, $$2)) {
                return true;
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        private void updateEntryWidth(int $$0) {
            if (this.lastEntryWidth != $$0) {
                this.refreshLayout($$0);
                this.lastEntryWidth = $$0;
            }
        }

        private void refreshLayout(int $$0) {
            int $$1 = $$0 - 80;
            this.textFrame.setMinWidth($$1);
            this.textWidget.setMaxWidth($$1);
            this.gridLayout.arrangeElements();
        }

        @Override
        public void renderBack(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            super.renderBack($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9);
            $$0.renderOutline($$3 - 2, $$2 - 2, $$4, 36 * this.frameItemHeight - 2, -12303292);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$42, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.gridLayout.setPosition($$3, $$2);
            this.updateEntryWidth($$42 - 4);
            this.children.forEach($$4 -> $$4.render($$0, $$6, $$7, $$9));
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (this.dismissButton != null) {
                this.dismissButton.mouseClicked($$0, $$1, $$2);
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public Component getNarration() {
            return this.text;
        }
    }
}

