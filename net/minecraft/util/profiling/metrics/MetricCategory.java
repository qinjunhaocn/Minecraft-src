/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.metrics;

public final class MetricCategory
extends Enum<MetricCategory> {
    public static final /* enum */ MetricCategory PATH_FINDING = new MetricCategory("pathfinding");
    public static final /* enum */ MetricCategory EVENT_LOOPS = new MetricCategory("event-loops");
    public static final /* enum */ MetricCategory CONSECUTIVE_EXECUTORS = new MetricCategory("consecutive-executors");
    public static final /* enum */ MetricCategory TICK_LOOP = new MetricCategory("ticking");
    public static final /* enum */ MetricCategory JVM = new MetricCategory("jvm");
    public static final /* enum */ MetricCategory CHUNK_RENDERING = new MetricCategory("chunk rendering");
    public static final /* enum */ MetricCategory CHUNK_RENDERING_DISPATCHING = new MetricCategory("chunk rendering dispatching");
    public static final /* enum */ MetricCategory CPU = new MetricCategory("cpu");
    public static final /* enum */ MetricCategory GPU = new MetricCategory("gpu");
    private final String description;
    private static final /* synthetic */ MetricCategory[] $VALUES;

    public static MetricCategory[] values() {
        return (MetricCategory[])$VALUES.clone();
    }

    public static MetricCategory valueOf(String $$0) {
        return Enum.valueOf(MetricCategory.class, $$0);
    }

    private MetricCategory(String $$0) {
        this.description = $$0;
    }

    public String getDescription() {
        return this.description;
    }

    private static /* synthetic */ MetricCategory[] b() {
        return new MetricCategory[]{PATH_FINDING, EVENT_LOOPS, CONSECUTIVE_EXECUTORS, TICK_LOOP, JVM, CHUNK_RENDERING, CHUNK_RENDERING_DISPATCHING, CPU, GPU};
    }

    static {
        $VALUES = MetricCategory.b();
    }
}

