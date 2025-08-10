/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.debugchart;

import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.util.debugchart.AbstractSampleLogger;
import net.minecraft.util.debugchart.DebugSampleSubscriptionTracker;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public class RemoteSampleLogger
extends AbstractSampleLogger {
    private final DebugSampleSubscriptionTracker subscriptionTracker;
    private final RemoteDebugSampleType sampleType;

    public RemoteSampleLogger(int $$0, DebugSampleSubscriptionTracker $$1, RemoteDebugSampleType $$2) {
        this($$0, $$1, $$2, new long[$$0]);
    }

    public RemoteSampleLogger(int $$0, DebugSampleSubscriptionTracker $$1, RemoteDebugSampleType $$2, long[] $$3) {
        super($$0, $$3);
        this.subscriptionTracker = $$1;
        this.sampleType = $$2;
    }

    @Override
    protected void useSample() {
        this.subscriptionTracker.broadcast(new ClientboundDebugSamplePacket((long[])this.sample.clone(), this.sampleType));
    }
}

