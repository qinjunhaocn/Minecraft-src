/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public class NamedThreadFactory
implements ThreadFactory {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public NamedThreadFactory(String $$0) {
        SecurityManager $$1 = System.getSecurityManager();
        this.group = $$1 != null ? $$1.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = $$0 + "-";
    }

    @Override
    public Thread newThread(Runnable $$0) {
        Thread $$12 = new Thread(this.group, $$0, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        $$12.setUncaughtExceptionHandler(($$1, $$2) -> {
            LOGGER.error("Caught exception in thread {} from {}", (Object)$$1, (Object)$$0);
            LOGGER.error("", $$2);
        });
        if ($$12.getPriority() != 5) {
            $$12.setPriority(5);
        }
        return $$12;
    }
}

