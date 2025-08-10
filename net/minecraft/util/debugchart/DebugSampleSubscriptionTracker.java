/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.debugchart;

import com.google.common.collect.Maps;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public class DebugSampleSubscriptionTracker {
    public static final int STOP_SENDING_AFTER_TICKS = 200;
    public static final int STOP_SENDING_AFTER_MS = 10000;
    private final PlayerList playerList;
    private final Map<RemoteDebugSampleType, Map<ServerPlayer, SubscriptionStartedAt>> subscriptions;
    private final Queue<SubscriptionRequest> subscriptionRequestQueue = new LinkedList<SubscriptionRequest>();

    public DebugSampleSubscriptionTracker(PlayerList $$02) {
        this.playerList = $$02;
        this.subscriptions = Util.makeEnumMap(RemoteDebugSampleType.class, $$0 -> Maps.newHashMap());
    }

    public boolean shouldLogSamples(RemoteDebugSampleType $$0) {
        return !this.subscriptions.get((Object)$$0).isEmpty();
    }

    public void broadcast(ClientboundDebugSamplePacket $$0) {
        Set<ServerPlayer> $$1 = this.subscriptions.get((Object)$$0.debugSampleType()).keySet();
        for (ServerPlayer $$2 : $$1) {
            $$2.connection.send($$0);
        }
    }

    public void subscribe(ServerPlayer $$0, RemoteDebugSampleType $$1) {
        if (this.playerList.isOp($$0.getGameProfile())) {
            this.subscriptionRequestQueue.add(new SubscriptionRequest($$0, $$1));
        }
    }

    public void tick(int $$0) {
        long $$1 = Util.getMillis();
        this.handleSubscriptions($$1, $$0);
        this.handleUnsubscriptions($$1, $$0);
    }

    private void handleSubscriptions(long $$0, int $$1) {
        for (SubscriptionRequest $$2 : this.subscriptionRequestQueue) {
            this.subscriptions.get((Object)$$2.sampleType()).put($$2.player(), new SubscriptionStartedAt($$0, $$1));
        }
    }

    private void handleUnsubscriptions(long $$0, int $$1) {
        for (Map<ServerPlayer, SubscriptionStartedAt> $$22 : this.subscriptions.values()) {
            $$22.entrySet().removeIf($$2 -> {
                boolean $$3 = !this.playerList.isOp(((ServerPlayer)$$2.getKey()).getGameProfile());
                SubscriptionStartedAt $$4 = (SubscriptionStartedAt)((Object)((Object)$$2.getValue()));
                return $$3 || $$1 > $$4.tick() + 200 && $$0 > $$4.millis() + 10000L;
            });
        }
    }

    record SubscriptionRequest(ServerPlayer player, RemoteDebugSampleType sampleType) {
    }

    record SubscriptionStartedAt(long millis, int tick) {
    }
}

