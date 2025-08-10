/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class CloseServerTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.configure.world.closing");
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer $$0, RealmsConfigureWorldScreen $$1) {
        this.serverData = $$0;
        this.configureScreen = $$1;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean $$2 = $$0.close(this.serverData.id);
                if (!$$2) continue;
                this.configureScreen.stateChanged();
                this.serverData.state = RealmsServer.State.CLOSED;
                CloseServerTask.setScreen(this.configureScreen);
                break;
            } catch (RetryCallException $$3) {
                if (this.aborted()) {
                    return;
                }
                CloseServerTask.pause($$3.delaySeconds);
                continue;
            } catch (Exception $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to close server", $$4);
                this.error($$4);
            }
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

