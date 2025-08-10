/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.telemetry.events;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;

public class WorldLoadTimesEvent {
    private final boolean newWorld;
    @Nullable
    private final Duration worldLoadDuration;

    public WorldLoadTimesEvent(boolean $$0, @Nullable Duration $$1) {
        this.worldLoadDuration = $$1;
        this.newWorld = $$0;
    }

    public void send(TelemetryEventSender $$02) {
        if (this.worldLoadDuration != null) {
            $$02.send(TelemetryEventType.WORLD_LOAD_TIMES, $$0 -> {
                $$0.put(TelemetryProperty.WORLD_LOAD_TIME_MS, (int)this.worldLoadDuration.toMillis());
                $$0.put(TelemetryProperty.NEW_WORLD, this.newWorld);
            });
        }
    }
}

