/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class SwitchMinigameTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.minigame.world.starting.screen.title");
    private final long realmId;
    private final WorldTemplate worldTemplate;
    private final RealmsConfigureWorldScreen nextScreen;

    public SwitchMinigameTask(long $$0, WorldTemplate $$1, RealmsConfigureWorldScreen $$2) {
        this.realmId = $$0;
        this.worldTemplate = $$1;
        this.nextScreen = $$2;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!$$0.putIntoMinigameMode(this.realmId, this.worldTemplate.id).booleanValue()) continue;
                SwitchMinigameTask.setScreen(this.nextScreen);
                break;
            } catch (RetryCallException $$2) {
                if (this.aborted()) {
                    return;
                }
                SwitchMinigameTask.pause($$2.delaySeconds);
                continue;
            } catch (Exception $$3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't start mini game!");
                this.error($$3);
            }
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

