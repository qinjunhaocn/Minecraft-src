/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.util.thread.TaskScheduler;
import org.slf4j.Logger;

public abstract class AbstractConsecutiveExecutor<T extends Runnable>
implements ProfilerMeasured,
TaskScheduler<T>,
Runnable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final AtomicReference<Status> status = new AtomicReference<Status>(Status.SLEEPING);
    private final StrictQueue<T> queue;
    private final Executor executor;
    private final String name;

    public AbstractConsecutiveExecutor(StrictQueue<T> $$0, Executor $$1, String $$2) {
        this.executor = $$1;
        this.queue = $$0;
        this.name = $$2;
        MetricsRegistry.INSTANCE.add(this);
    }

    private boolean canBeScheduled() {
        return !this.isClosed() && !this.queue.isEmpty();
    }

    @Override
    public void close() {
        this.status.set(Status.CLOSED);
    }

    private boolean pollTask() {
        if (!this.isRunning()) {
            return false;
        }
        Runnable $$0 = this.queue.pop();
        if ($$0 == null) {
            return false;
        }
        Util.runNamed($$0, this.name);
        return true;
    }

    @Override
    public void run() {
        try {
            this.pollTask();
        } finally {
            this.setSleeping();
            this.registerForExecution();
        }
    }

    public void runAll() {
        try {
            while (this.pollTask()) {
            }
        } finally {
            this.setSleeping();
            this.registerForExecution();
        }
    }

    @Override
    public void schedule(T $$0) {
        this.queue.push($$0);
        this.registerForExecution();
    }

    private void registerForExecution() {
        if (this.canBeScheduled() && this.setRunning()) {
            try {
                this.executor.execute(this);
            } catch (RejectedExecutionException $$0) {
                try {
                    this.executor.execute(this);
                } catch (RejectedExecutionException $$1) {
                    LOGGER.error("Could not schedule ConsecutiveExecutor", $$1);
                }
            }
        }
    }

    public int size() {
        return this.queue.size();
    }

    public boolean hasWork() {
        return this.isRunning() && !this.queue.isEmpty();
    }

    public String toString() {
        return this.name + " " + String.valueOf((Object)this.status.get()) + " " + this.queue.isEmpty();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<MetricSampler> profiledMetrics() {
        return ImmutableList.of(MetricSampler.create(this.name + "-queue-size", MetricCategory.CONSECUTIVE_EXECUTORS, this::size));
    }

    private boolean setRunning() {
        return this.status.compareAndSet(Status.SLEEPING, Status.RUNNING);
    }

    private void setSleeping() {
        this.status.compareAndSet(Status.RUNNING, Status.SLEEPING);
    }

    private boolean isRunning() {
        return this.status.get() == Status.RUNNING;
    }

    private boolean isClosed() {
        return this.status.get() == Status.CLOSED;
    }

    static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status SLEEPING = new Status();
        public static final /* enum */ Status RUNNING = new Status();
        public static final /* enum */ Status CLOSED = new Status();
        private static final /* synthetic */ Status[] $VALUES;

        public static Status[] values() {
            return (Status[])$VALUES.clone();
        }

        public static Status valueOf(String $$0) {
            return Enum.valueOf(Status.class, $$0);
        }

        private static /* synthetic */ Status[] a() {
            return new Status[]{SLEEPING, RUNNING, CLOSED};
        }

        static {
            $VALUES = Status.a();
        }
    }
}

