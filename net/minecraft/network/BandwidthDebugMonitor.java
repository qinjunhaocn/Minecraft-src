/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.debugchart.LocalSampleLogger;

public class BandwidthDebugMonitor {
    private final AtomicInteger bytesReceived = new AtomicInteger();
    private final LocalSampleLogger bandwidthLogger;

    public BandwidthDebugMonitor(LocalSampleLogger $$0) {
        this.bandwidthLogger = $$0;
    }

    public void onReceive(int $$0) {
        this.bytesReceived.getAndAdd($$0);
    }

    public void tick() {
        this.bandwidthLogger.logSample(this.bytesReceived.getAndSet(0));
    }
}

