/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.minecraft.client.telemetry.TelemetryEventInstance;
import net.minecraft.client.telemetry.TelemetryEventLogger;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TelemetryEventLog
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final JsonEventLog<TelemetryEventInstance> log;
    private final ConsecutiveExecutor consecutiveExecutor;

    public TelemetryEventLog(FileChannel $$0, Executor $$1) {
        this.log = new JsonEventLog<TelemetryEventInstance>(TelemetryEventInstance.CODEC, $$0);
        this.consecutiveExecutor = new ConsecutiveExecutor($$1, "telemetry-event-log");
    }

    public TelemetryEventLogger logger() {
        return $$0 -> this.consecutiveExecutor.schedule(() -> {
            try {
                this.log.write($$0);
            } catch (IOException $$1) {
                LOGGER.error("Failed to write telemetry event to log", $$1);
            }
        });
    }

    @Override
    public void close() {
        this.consecutiveExecutor.schedule(() -> IOUtils.closeQuietly(this.log));
        this.consecutiveExecutor.close();
    }
}

