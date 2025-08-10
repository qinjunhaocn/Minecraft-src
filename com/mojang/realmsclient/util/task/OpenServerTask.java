/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class OpenServerTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.configure.world.opening");
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final Minecraft minecraft;

    public OpenServerTask(RealmsServer $$0, Screen $$1, boolean $$2, Minecraft $$3) {
        this.serverData = $$0;
        this.returnScreen = $$1;
        this.join = $$2;
        this.minecraft = $$3;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean $$2 = $$0.open(this.serverData.id);
                if (!$$2) continue;
                this.minecraft.execute(() -> {
                    Screen $$0 = this.returnScreen;
                    if ($$0 instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen $$1 = (RealmsConfigureWorldScreen)$$0;
                        $$1.stateChanged();
                    }
                    this.serverData.state = RealmsServer.State.OPEN;
                    if (this.join) {
                        RealmsMainScreen.play(this.serverData, this.returnScreen);
                    } else {
                        this.minecraft.setScreen(this.returnScreen);
                    }
                });
                break;
            } catch (RetryCallException $$3) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause($$3.delaySeconds);
                continue;
            } catch (Exception $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", $$4);
                this.error($$4);
            }
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

