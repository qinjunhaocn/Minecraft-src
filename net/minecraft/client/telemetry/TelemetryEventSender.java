/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.telemetry;

import java.util.function.Consumer;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryPropertyMap;

@FunctionalInterface
public interface TelemetryEventSender {
    public static final TelemetryEventSender DISABLED = ($$0, $$1) -> {};

    default public TelemetryEventSender decorate(Consumer<TelemetryPropertyMap.Builder> $$0) {
        return ($$1, $$22) -> this.send($$1, $$2 -> {
            $$22.accept($$2);
            $$0.accept((TelemetryPropertyMap.Builder)$$2);
        });
    }

    public void send(TelemetryEventType var1, Consumer<TelemetryPropertyMap.Builder> var2);
}

