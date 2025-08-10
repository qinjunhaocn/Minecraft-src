/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsAvailability;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.task.DataFetcher;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;

public class RealmsNotificationsScreen
extends RealmsScreen {
    private static final ResourceLocation UNSEEN_NOTIFICATION_SPRITE = ResourceLocation.withDefaultNamespace("icon/unseen_notification");
    private static final ResourceLocation NEWS_SPRITE = ResourceLocation.withDefaultNamespace("icon/news");
    private static final ResourceLocation INVITE_SPRITE = ResourceLocation.withDefaultNamespace("icon/invite");
    private static final ResourceLocation TRIAL_AVAILABLE_SPRITE = ResourceLocation.withDefaultNamespace("icon/trial_available");
    private final CompletableFuture<Boolean> validClient = RealmsAvailability.get().thenApply($$0 -> $$0.type() == RealmsAvailability.Type.SUCCESS);
    @Nullable
    private DataFetcher.Subscription realmsDataSubscription;
    @Nullable
    private DataFetcherConfiguration currentConfiguration;
    private volatile int numberOfPendingInvites;
    private static boolean trialAvailable;
    private static boolean hasUnreadNews;
    private static boolean hasUnseenNotifications;
    private final DataFetcherConfiguration showAll = new DataFetcherConfiguration(){

        @Override
        public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher $$0) {
            DataFetcher.Subscription $$1 = $$0.dataFetcher.createSubscription();
            RealmsNotificationsScreen.this.addNewsAndInvitesSubscriptions($$0, $$1);
            RealmsNotificationsScreen.this.addNotificationsSubscriptions($$0, $$1);
            return $$1;
        }

        @Override
        public boolean showOldNotifications() {
            return true;
        }
    };
    private final DataFetcherConfiguration onlyNotifications = new DataFetcherConfiguration(){

        @Override
        public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher $$0) {
            DataFetcher.Subscription $$1 = $$0.dataFetcher.createSubscription();
            RealmsNotificationsScreen.this.addNotificationsSubscriptions($$0, $$1);
            return $$1;
        }

        @Override
        public boolean showOldNotifications() {
            return false;
        }
    };

    public RealmsNotificationsScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void init() {
        if (this.realmsDataSubscription != null) {
            this.realmsDataSubscription.forceUpdate();
        }
    }

    @Override
    public void added() {
        super.added();
        this.minecraft.realmsDataFetcher().notificationsTask.reset();
    }

    @Nullable
    private DataFetcherConfiguration getConfiguration() {
        boolean $$0;
        boolean bl = $$0 = this.inTitleScreen() && this.validClient.getNow(false) != false;
        if (!$$0) {
            return null;
        }
        return this.getRealmsNotificationsEnabled() ? this.showAll : this.onlyNotifications;
    }

    @Override
    public void tick() {
        DataFetcherConfiguration $$0 = this.getConfiguration();
        if (!Objects.equals(this.currentConfiguration, $$0)) {
            this.currentConfiguration = $$0;
            this.realmsDataSubscription = this.currentConfiguration != null ? this.currentConfiguration.initDataFetcher(this.minecraft.realmsDataFetcher()) : null;
        }
        if (this.realmsDataSubscription != null) {
            this.realmsDataSubscription.tick();
        }
    }

    private boolean getRealmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications().get();
    }

    private boolean inTitleScreen() {
        return this.minecraft.screen instanceof TitleScreen;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.validClient.getNow(false).booleanValue()) {
            this.drawIcons($$0);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
    }

    private void drawIcons(GuiGraphics $$0) {
        int $$1 = this.numberOfPendingInvites;
        int $$2 = 24;
        int $$3 = this.height / 4 + 48;
        int $$4 = this.width / 2 + 100;
        int $$5 = $$3 + 48 + 2;
        int $$6 = $$4 - 3;
        if (hasUnseenNotifications) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, UNSEEN_NOTIFICATION_SPRITE, $$6 - 12, $$5 + 3, 10, 10);
            $$6 -= 16;
        }
        if (this.currentConfiguration != null && this.currentConfiguration.showOldNotifications()) {
            if (hasUnreadNews) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, NEWS_SPRITE, $$6 - 14, $$5 + 1, 14, 14);
                $$6 -= 16;
            }
            if ($$1 != 0) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, INVITE_SPRITE, $$6 - 14, $$5 + 1, 14, 14);
                $$6 -= 16;
            }
            if (trialAvailable) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TRIAL_AVAILABLE_SPRITE, $$6 - 10, $$5 + 4, 8, 8);
            }
        }
    }

    void addNewsAndInvitesSubscriptions(RealmsDataFetcher $$02, DataFetcher.Subscription $$12) {
        $$12.subscribe($$02.pendingInvitesTask, $$0 -> {
            this.numberOfPendingInvites = $$0;
        });
        $$12.subscribe($$02.trialAvailabilityTask, $$0 -> {
            trialAvailable = $$0;
        });
        $$12.subscribe($$02.newsTask, $$1 -> {
            $$0.newsManager.updateUnreadNews((RealmsNews)$$1);
            hasUnreadNews = $$0.newsManager.hasUnreadNews();
        });
    }

    void addNotificationsSubscriptions(RealmsDataFetcher $$02, DataFetcher.Subscription $$1) {
        $$1.subscribe($$02.notificationsTask, $$0 -> {
            hasUnseenNotifications = false;
            for (RealmsNotification $$1 : $$0) {
                if ($$1.seen()) continue;
                hasUnseenNotifications = true;
                break;
            }
        });
    }

    static interface DataFetcherConfiguration {
        public DataFetcher.Subscription initDataFetcher(RealmsDataFetcher var1);

        public boolean showOldNotifications();
    }
}

