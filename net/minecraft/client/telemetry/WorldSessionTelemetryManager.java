/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.telemetry;

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.events.PerformanceMetricsEvent;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldLoadTimesEvent;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class WorldSessionTelemetryManager {
    private final UUID worldSessionId = UUID.randomUUID();
    private final TelemetryEventSender eventSender;
    private final WorldLoadEvent worldLoadEvent;
    private final WorldUnloadEvent worldUnloadEvent = new WorldUnloadEvent();
    private final PerformanceMetricsEvent performanceMetricsEvent;
    private final WorldLoadTimesEvent worldLoadTimesEvent;

    public WorldSessionTelemetryManager(TelemetryEventSender $$02, boolean $$1, @Nullable Duration $$2, @Nullable String $$3) {
        this.worldLoadEvent = new WorldLoadEvent($$3);
        this.performanceMetricsEvent = new PerformanceMetricsEvent();
        this.worldLoadTimesEvent = new WorldLoadTimesEvent($$1, $$2);
        this.eventSender = $$02.decorate($$0 -> {
            this.worldLoadEvent.addProperties((TelemetryPropertyMap.Builder)$$0);
            $$0.put(TelemetryProperty.WORLD_SESSION_ID, this.worldSessionId);
        });
    }

    public void tick() {
        this.performanceMetricsEvent.tick(this.eventSender);
    }

    public void onPlayerInfoReceived(GameType $$0, boolean $$1) {
        this.worldLoadEvent.setGameMode($$0, $$1);
        this.worldUnloadEvent.onPlayerInfoReceived();
        this.worldSessionStart();
    }

    public void onServerBrandReceived(String $$0) {
        this.worldLoadEvent.setServerBrand($$0);
        this.worldSessionStart();
    }

    public void setTime(long $$0) {
        this.worldUnloadEvent.setTime($$0);
    }

    public void worldSessionStart() {
        if (this.worldLoadEvent.send(this.eventSender)) {
            this.worldLoadTimesEvent.send(this.eventSender);
            this.performanceMetricsEvent.start();
        }
    }

    public void onDisconnect() {
        this.worldLoadEvent.send(this.eventSender);
        this.performanceMetricsEvent.stop();
        this.worldUnloadEvent.send(this.eventSender);
    }

    public void onAdvancementDone(Level $$0, AdvancementHolder $$1) {
        ResourceLocation $$22 = $$1.id();
        if ($$1.value().sendsTelemetryEvent() && "minecraft".equals($$22.getNamespace())) {
            long $$3 = $$0.getGameTime();
            this.eventSender.send(TelemetryEventType.ADVANCEMENT_MADE, $$2 -> {
                $$2.put(TelemetryProperty.ADVANCEMENT_ID, $$22.toString());
                $$2.put(TelemetryProperty.ADVANCEMENT_GAME_TIME, $$3);
            });
        }
    }
}

