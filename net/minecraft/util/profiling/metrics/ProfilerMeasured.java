/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.metrics;

import java.util.List;
import net.minecraft.util.profiling.metrics.MetricSampler;

public interface ProfilerMeasured {
    public List<MetricSampler> profiledMetrics();
}

