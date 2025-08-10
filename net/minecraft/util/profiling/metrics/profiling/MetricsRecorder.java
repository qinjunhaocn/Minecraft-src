/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.ProfilerFiller;

public interface MetricsRecorder {
    public void end();

    public void cancel();

    public void startTick();

    public boolean isRecording();

    public ProfilerFiller getProfiler();

    public void endTick();
}

