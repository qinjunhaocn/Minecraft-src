/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class LoggedPrintStream
extends PrintStream {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String name;

    public LoggedPrintStream(String $$0, OutputStream $$1) {
        super($$1);
        this.name = $$0;
    }

    @Override
    public void println(@Nullable String $$0) {
        this.logLine($$0);
    }

    @Override
    public void println(Object $$0) {
        this.logLine(String.valueOf($$0));
    }

    protected void logLine(@Nullable String $$0) {
        LOGGER.info("[{}]: {}", (Object)this.name, (Object)$$0);
    }
}

