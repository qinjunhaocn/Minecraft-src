/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.platform;

import java.io.File;
import java.time.Duration;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.ServerWatchdog;

public class ClientShutdownWatchdog {
    private static final Duration CRASH_REPORT_PRELOAD_LOAD = Duration.ofSeconds(15L);

    public static void startShutdownWatchdog(File $$0, long $$1) {
        Thread $$2 = new Thread(() -> {
            try {
                Thread.sleep((Duration)CRASH_REPORT_PRELOAD_LOAD);
            } catch (InterruptedException $$2) {
                return;
            }
            CrashReport $$3 = ServerWatchdog.createWatchdogCrashReport("Client shutdown", $$1);
            Minecraft.saveReport($$0, $$3);
        });
        $$2.setDaemon(true);
        $$2.setName("Client shutdown watchdog");
        $$2.start();
    }
}

