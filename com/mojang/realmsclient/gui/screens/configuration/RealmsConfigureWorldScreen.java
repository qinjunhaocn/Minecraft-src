/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.PreferredRegionsDto;
import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RegionDataDto;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigurationTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsSettingsTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsSubscriptionTab;
import com.mojang.realmsclient.gui.screens.configuration.RealmsWorldsTab;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.LoadingTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class RealmsConfigureWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
    private final RealmsMainScreen lastScreen;
    @Nullable
    private RealmsServer serverData;
    @Nullable
    private PreferredRegionsDto regions;
    private final Map<RealmsRegion, ServiceQuality> regionServiceQuality = new LinkedHashMap<RealmsRegion, ServiceQuality>();
    private final long serverId;
    private boolean stateChanged;
    private final TabManager tabManager = new TabManager($$1 -> {
        AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
    }, $$1 -> this.removeWidget((GuiEventListener)$$1), this::onTabSelected, this::onTabDeselected);
    @Nullable
    private Button playButton;
    @Nullable
    private TabNavigationBar tabNavigationBar;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public RealmsConfigureWorldScreen(RealmsMainScreen $$0, long $$12, @Nullable RealmsServer $$2, @Nullable PreferredRegionsDto $$3) {
        super(Component.empty());
        this.lastScreen = $$0;
        this.serverId = $$12;
        this.serverData = $$2;
        this.regions = $$3;
    }

    public RealmsConfigureWorldScreen(RealmsMainScreen $$0, long $$1) {
        this($$0, $$1, null, null);
    }

    @Override
    public void init() {
        if (this.serverData == null) {
            this.fetchServerData(this.serverId);
        }
        if (this.regions == null) {
            this.fetchRegionData();
        }
        MutableComponent $$02 = Component.translatable("mco.configure.world.loading");
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).a(new LoadingTab(this.getFont(), RealmsWorldsTab.TITLE, $$02), new LoadingTab(this.getFont(), RealmsPlayersTab.TITLE, $$02), new LoadingTab(this.getFont(), RealmsSubscriptionTab.TITLE, $$02), new LoadingTab(this.getFont(), RealmsSettingsTab.TITLE, $$02)).build();
        this.addRenderableWidget(this.tabNavigationBar);
        LinearLayout $$1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.playButton = $$1.addChild(Button.builder(PLAY_TEXT, $$0 -> {
            this.onClose();
            RealmsMainScreen.play(this.serverData, this);
        }).width(150).build());
        this.playButton.active = false;
        $$1.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$0 -> {
            $$0.setTabOrderGroup(1);
            this.addRenderableWidget($$0);
        });
        this.tabNavigationBar.selectTab(0, false);
        this.repositionElements();
        if (this.serverData != null && this.regions != null) {
            this.onRealmsDataFetched();
        }
    }

    private void onTabSelected(Tab $$0) {
        if (this.serverData != null && $$0 instanceof RealmsConfigurationTab) {
            RealmsConfigurationTab $$1 = (RealmsConfigurationTab)((Object)$$0);
            $$1.onSelected(this.serverData);
        }
    }

    private void onTabDeselected(Tab $$0) {
        if (this.serverData != null && $$0 instanceof RealmsConfigurationTab) {
            RealmsConfigurationTab $$1 = (RealmsConfigurationTab)((Object)$$0);
            $$1.onDeselected(this.serverData);
        }
    }

    public int getContentHeight() {
        return this.layout.getContentHeight();
    }

    public int getHeaderHeight() {
        return this.layout.getHeaderHeight();
    }

    public Screen getLastScreen() {
        return this.lastScreen;
    }

    public Screen createErrorScreen(RealmsServiceException $$0) {
        return new RealmsGenericErrorScreen($$0, (Screen)this.lastScreen);
    }

    @Override
    public void repositionElements() {
        if (this.tabNavigationBar == null) {
            return;
        }
        this.tabNavigationBar.setWidth(this.width);
        this.tabNavigationBar.arrangeElements();
        int $$0 = this.tabNavigationBar.getRectangle().bottom();
        ScreenRectangle $$1 = new ScreenRectangle(0, $$0, this.width, this.height - this.layout.getFooterHeight() - $$0);
        this.tabManager.setTabArea($$1);
        this.layout.setHeaderHeight($$0);
        this.layout.arrangeElements();
    }

    private void updateButtonStates() {
        if (this.serverData != null && this.playButton != null) {
            this.playButton.active = this.serverData.shouldPlayButtonBeActive();
            if (!this.playButton.active && this.serverData.state == RealmsServer.State.CLOSED) {
                this.playButton.setTooltip(Tooltip.create(RealmsServer.WORLD_CLOSED_COMPONENT));
            }
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.tabNavigationBar.keyPressed($$0)) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics $$0) {
        $$0.blit(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderMenuBackground($$0, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    @Override
    public void onClose() {
        Tab tab;
        if (this.serverData != null && (tab = this.tabManager.getCurrentTab()) instanceof RealmsConfigurationTab) {
            RealmsConfigurationTab $$0 = (RealmsConfigurationTab)((Object)tab);
            $$0.onDeselected(this.serverData);
        }
        this.minecraft.setScreen(this.lastScreen);
        if (this.stateChanged) {
            this.lastScreen.resetScreen();
        }
    }

    public void fetchRegionData() {
        RealmsUtil.supplyAsync(RealmsClient::getPreferredRegionSelections, RealmsUtil.openScreenAndLogOnFailure(this::createErrorScreen, "Couldn't get realms region data")).thenAcceptAsync($$0 -> {
            this.regions = $$0;
            this.onRealmsDataFetched();
        }, (Executor)this.minecraft);
    }

    public void fetchServerData(long $$02) {
        RealmsUtil.supplyAsync($$1 -> $$1.getOwnRealm($$02), RealmsUtil.openScreenAndLogOnFailure(this::createErrorScreen, "Couldn't get own world")).thenAcceptAsync($$0 -> {
            this.serverData = $$0;
            this.onRealmsDataFetched();
        }, (Executor)this.minecraft);
    }

    private void onRealmsDataFetched() {
        if (this.serverData == null || this.regions == null) {
            return;
        }
        this.regionServiceQuality.clear();
        for (RegionDataDto $$0 : this.regions.regionData()) {
            if ($$0.region() == RealmsRegion.INVALID_REGION) continue;
            this.regionServiceQuality.put($$0.region(), $$0.serviceQuality());
        }
        if (this.tabNavigationBar != null) {
            this.removeWidget(this.tabNavigationBar);
        }
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).a(new RealmsWorldsTab(this, Objects.requireNonNull(this.minecraft), this.serverData), new RealmsPlayersTab(this, this.minecraft, this.serverData), new RealmsSubscriptionTab(this, this.minecraft, this.serverData), new RealmsSettingsTab(this, this.minecraft, this.serverData, this.regionServiceQuality)).build();
        this.addRenderableWidget(this.tabNavigationBar);
        this.tabNavigationBar.selectTab(0, false);
        this.tabNavigationBar.setTabActiveState(3, !this.serverData.expired);
        if (this.serverData.expired) {
            this.tabNavigationBar.setTabTooltip(3, Tooltip.create(Component.translatable("mco.configure.world.settings.expired")));
        } else {
            this.tabNavigationBar.setTabTooltip(3, null);
        }
        this.updateButtonStates();
        this.repositionElements();
    }

    public void saveSlotSettings(RealmsSlot $$0) {
        RealmsSlot $$1 = this.serverData.slots.get(this.serverData.activeSlot);
        $$0.options.templateId = $$1.options.templateId;
        $$0.options.templateImage = $$1.options.templateImage;
        RealmsClient $$2 = RealmsClient.getOrCreate();
        try {
            if (this.serverData.activeSlot != $$0.slotId) {
                throw new RealmsServiceException(RealmsError.CustomError.configurationError());
            }
            $$2.updateSlot(this.serverData.id, $$0.slotId, $$0.options, $$0.settings);
            this.serverData.slots.put(this.serverData.activeSlot, $$0);
            if ($$0.options.gameMode != $$1.options.gameMode || $$0.isHardcore() != $$1.isHardcore()) {
                RealmsMainScreen.refreshServerList();
            }
            this.stateChanged();
        } catch (RealmsServiceException $$3) {
            LOGGER.error("Couldn't save slot settings", $$3);
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$3, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void saveSettings(String $$0, String $$1, RegionSelectionPreference $$2, @Nullable RealmsRegion $$3) {
        String $$4 = StringUtil.isBlank($$1) ? "" : $$1;
        String $$5 = StringUtil.isBlank($$0) ? "" : $$0;
        RealmsClient $$6 = RealmsClient.getOrCreate();
        try {
            RealmsSlot $$7 = this.serverData.slots.get(this.serverData.activeSlot);
            RealmsRegion $$8 = $$2 == RegionSelectionPreference.MANUAL ? $$3 : null;
            RegionSelectionPreferenceDto $$9 = new RegionSelectionPreferenceDto($$2, $$8);
            $$6.updateConfiguration(this.serverData.id, $$5, $$4, $$9, $$7.slotId, $$7.options, $$7.settings);
            this.serverData.regionSelectionPreference = $$9;
            this.serverData.name = $$0;
            this.serverData.motd = $$4;
            this.stateChanged();
        } catch (RealmsServiceException $$10) {
            LOGGER.error("Couldn't save settings", $$10);
            this.minecraft.setScreen(new RealmsGenericErrorScreen($$10, (Screen)this));
            return;
        }
        this.minecraft.setScreen(this);
    }

    public void openTheWorld(boolean $$0) {
        RealmsConfigureWorldScreen $$1 = this.getNewScreenWithKnownData(this.serverData);
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.getNewScreen(), new OpenServerTask(this.serverData, $$1, $$0, this.minecraft)));
    }

    public void closeTheWorld() {
        RealmsConfigureWorldScreen $$0 = this.getNewScreenWithKnownData(this.serverData);
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.getNewScreen(), new CloseServerTask(this.serverData, $$0)));
    }

    public void stateChanged() {
        this.stateChanged = true;
        if (this.tabNavigationBar != null) {
            for (Tab $$0 : this.tabNavigationBar.getTabs()) {
                if (!($$0 instanceof RealmsConfigurationTab)) continue;
                RealmsConfigurationTab $$1 = (RealmsConfigurationTab)((Object)$$0);
                $$1.updateData(this.serverData);
            }
        }
    }

    public boolean invitePlayer(long $$0, String $$1) {
        RealmsClient $$2 = RealmsClient.getOrCreate();
        try {
            List<PlayerInfo> $$3 = $$2.invite($$0, $$1);
            if (this.serverData != null) {
                this.serverData.players = $$3;
            } else {
                this.serverData = $$2.getOwnRealm($$0);
            }
            this.stateChanged();
        } catch (RealmsServiceException $$4) {
            LOGGER.error("Couldn't invite user", $$4);
            return false;
        }
        return true;
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        RealmsConfigureWorldScreen $$0 = new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
        $$0.stateChanged = this.stateChanged;
        return $$0;
    }

    public RealmsConfigureWorldScreen getNewScreenWithKnownData(RealmsServer $$0) {
        RealmsConfigureWorldScreen $$1 = new RealmsConfigureWorldScreen(this.lastScreen, this.serverId, $$0, this.regions);
        $$1.stateChanged = this.stateChanged;
        return $$1;
    }
}

