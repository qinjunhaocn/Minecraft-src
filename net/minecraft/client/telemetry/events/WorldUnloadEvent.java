/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.telemetry.events;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;

public class WorldUnloadEvent {
    private static final int NOT_TRACKING_TIME = -1;
    private Optional<Instant> worldLoadedTime = Optional.empty();
    private long totalTicks;
    private long lastGameTime;

    public void onPlayerInfoReceived() {
        this.lastGameTime = -1L;
        if (this.worldLoadedTime.isEmpty()) {
            this.worldLoadedTime = Optional.of(Instant.now());
        }
    }

    public void setTime(long $$0) {
        if (this.lastGameTime != -1L) {
            this.totalTicks += Math.max(0L, $$0 - this.lastGameTime);
        }
        this.lastGameTime = $$0;
    }

    private int getTimeInSecondsSinceLoad(Instant $$0) {
        Duration $$1 = Duration.between($$0, Instant.now());
        return (int)$$1.toSeconds();
    }

    public void send(TelemetryEventSender $$0) {
        this.worldLoadedTime.ifPresent($$12 -> $$0.send(TelemetryEventType.WORLD_UNLOADED, $$1 -> {
            $$1.put(TelemetryProperty.SECONDS_SINCE_LOAD, this.getTimeInSecondsSinceLoad((Instant)$$12));
            $$1.put(TelemetryProperty.TICKS_SINCE_LOAD, (int)this.totalTicks);
        }));
    }
}

