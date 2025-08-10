/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.telemetry.events;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import org.slf4j.Logger;

public class GameLoadTimesEvent {
    public static final GameLoadTimesEvent INSTANCE = new GameLoadTimesEvent(Ticker.systemTicker());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Ticker timeSource;
    private final Map<TelemetryProperty<Measurement>, Stopwatch> measurements = new HashMap<TelemetryProperty<Measurement>, Stopwatch>();
    private OptionalLong bootstrapTime = OptionalLong.empty();

    protected GameLoadTimesEvent(Ticker $$0) {
        this.timeSource = $$0;
    }

    public synchronized void beginStep(TelemetryProperty<Measurement> $$02) {
        this.beginStep($$02, (TelemetryProperty<Measurement> $$0) -> Stopwatch.createStarted(this.timeSource));
    }

    public synchronized void beginStep(TelemetryProperty<Measurement> $$0, Stopwatch $$12) {
        this.beginStep($$0, (TelemetryProperty<Measurement> $$1) -> $$12);
    }

    private synchronized void beginStep(TelemetryProperty<Measurement> $$0, Function<TelemetryProperty<Measurement>, Stopwatch> $$1) {
        this.measurements.computeIfAbsent($$0, $$1);
    }

    public synchronized void endStep(TelemetryProperty<Measurement> $$0) {
        Stopwatch $$1 = this.measurements.get($$0);
        if ($$1 == null) {
            LOGGER.warn("Attempted to end step for {} before starting it", (Object)$$0.id());
            return;
        }
        if ($$1.isRunning()) {
            $$1.stop();
        }
    }

    public void send(TelemetryEventSender $$02) {
        $$02.send(TelemetryEventType.GAME_LOAD_TIMES, $$0 -> {
            GameLoadTimesEvent gameLoadTimesEvent = this;
            synchronized (gameLoadTimesEvent) {
                this.measurements.forEach(($$1, $$2) -> {
                    if (!$$2.isRunning()) {
                        long $$3 = $$2.elapsed(TimeUnit.MILLISECONDS);
                        $$0.put($$1, new Measurement((int)$$3));
                    } else {
                        LOGGER.warn("Measurement {} was discarded since it was still ongoing when the event {} was sent.", (Object)$$1.id(), (Object)TelemetryEventType.GAME_LOAD_TIMES.id());
                    }
                });
                this.bootstrapTime.ifPresent($$1 -> $$0.put(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS, new Measurement((int)$$1)));
                this.measurements.clear();
            }
        });
    }

    public synchronized void setBootstrapTime(long $$0) {
        this.bootstrapTime = OptionalLong.of($$0);
    }

    public record Measurement(int millis) {
        public static final Codec<Measurement> CODEC = Codec.INT.xmap(Measurement::new, $$0 -> $$0.millis);
    }
}

