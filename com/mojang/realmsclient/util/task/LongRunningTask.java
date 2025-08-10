/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public abstract class LongRunningTask
implements Runnable {
    protected static final int NUMBER_OF_RETRIES = 25;
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean aborted = false;

    protected static void pause(long $$0) {
        try {
            Thread.sleep($$0 * 1000L);
        } catch (InterruptedException $$1) {
            Thread.currentThread().interrupt();
            LOGGER.error("", $$1);
        }
    }

    public static void setScreen(Screen $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        $$1.execute(() -> $$1.setScreen($$0));
    }

    protected void error(Component $$0) {
        this.abortTask();
        Minecraft $$1 = Minecraft.getInstance();
        $$1.execute(() -> $$1.setScreen(new RealmsGenericErrorScreen($$0, (Screen)new RealmsMainScreen(new TitleScreen()))));
    }

    protected void error(Exception $$0) {
        if ($$0 instanceof RealmsServiceException) {
            RealmsServiceException $$1 = (RealmsServiceException)$$0;
            this.error($$1.realmsError.errorMessage());
        } else {
            this.error(Component.literal($$0.getMessage()));
        }
    }

    protected void error(RealmsServiceException $$0) {
        this.error($$0.realmsError.errorMessage());
    }

    public abstract Component getTitle();

    public boolean aborted() {
        return this.aborted;
    }

    public void tick() {
    }

    public void init() {
    }

    public void abortTask() {
        this.aborted = true;
    }
}

