/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import net.minecraft.server.LoggedPrintStream;
import org.slf4j.Logger;

public class DebugLoggedPrintStream
extends LoggedPrintStream {
    private static final Logger LOGGER = LogUtils.getLogger();

    public DebugLoggedPrintStream(String $$0, OutputStream $$1) {
        super($$0, $$1);
    }

    @Override
    protected void logLine(String $$0) {
        StackTraceElement[] $$1 = Thread.currentThread().getStackTrace();
        StackTraceElement $$2 = $$1[Math.min(3, $$1.length)];
        LOGGER.info("[{}]@.({}:{}): {}", this.name, $$2.getFileName(), $$2.getLineNumber(), $$0);
    }
}

