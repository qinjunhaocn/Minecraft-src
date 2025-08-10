/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.gui.RealmsNewsManager;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.gui.task.RepeatedDelayStrategy;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Util;

public class RealmsDataFetcher {
    public final DataFetcher dataFetcher = new DataFetcher(Util.ioPool(), TimeUnit.MILLISECONDS, Util.timeSource);
    private final List<DataFetcher.Task<?>> tasks;
    public final DataFetcher.Task<List<RealmsNotification>> notificationsTask;
    public final DataFetcher.Task<ServerListData> serverListUpdateTask;
    public final DataFetcher.Task<Integer> pendingInvitesTask;
    public final DataFetcher.Task<Boolean> trialAvailabilityTask;
    public final DataFetcher.Task<RealmsNews> newsTask;
    public final DataFetcher.Task<RealmsServerPlayerLists> onlinePlayersTask;
    public final RealmsNewsManager newsManager = new RealmsNewsManager(new RealmsPersistence());

    public RealmsDataFetcher(RealmsClient $$0) {
        this.serverListUpdateTask = this.dataFetcher.createTask("server list", () -> {
            RealmsServerList $$1 = $$0.listRealms();
            if (RealmsMainScreen.isSnapshot()) {
                return new ServerListData($$1.servers, $$0.listSnapshotEligibleRealms());
            }
            return new ServerListData($$1.servers, List.of());
        }, Duration.ofSeconds(60L), RepeatedDelayStrategy.CONSTANT);
        this.pendingInvitesTask = this.dataFetcher.createTask("pending invite count", $$0::pendingInvitesCount, Duration.ofSeconds(10L), RepeatedDelayStrategy.exponentialBackoff(360));
        this.trialAvailabilityTask = this.dataFetcher.createTask("trial availablity", $$0::trialAvailable, Duration.ofSeconds(60L), RepeatedDelayStrategy.exponentialBackoff(60));
        this.newsTask = this.dataFetcher.createTask("unread news", $$0::getNews, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
        this.notificationsTask = this.dataFetcher.createTask("notifications", $$0::getNotifications, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
        this.onlinePlayersTask = this.dataFetcher.createTask("online players", $$0::getLiveStats, Duration.ofSeconds(10L), RepeatedDelayStrategy.CONSTANT);
        this.tasks = List.of(this.notificationsTask, this.serverListUpdateTask, this.pendingInvitesTask, this.trialAvailabilityTask, this.newsTask, this.onlinePlayersTask);
    }

    public List<DataFetcher.Task<?>> getTasks() {
        return this.tasks;
    }

    public record ServerListData(List<RealmsServer> serverList, List<RealmsServer> availableSnapshotServers) {
    }
}

