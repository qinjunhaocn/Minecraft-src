/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;

public class InactiveMetricsRecorder
implements MetricsRecorder {
    public static final MetricsRecorder INSTANCE = new InactiveMetricsRecorder();

    @Override
    public void end() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public ProfilerFiller getProfiler() {
        return InactiveProfiler.INSTANCE;
    }

    @Override
    public void endTick() {
    }
}

