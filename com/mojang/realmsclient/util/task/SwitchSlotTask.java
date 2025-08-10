/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class SwitchSlotTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.minigame.world.slot.screen.title");
    private final long realmId;
    private final int slot;
    private final Runnable callback;

    public SwitchSlotTask(long $$0, int $$1, Runnable $$2) {
        this.realmId = $$0;
        this.slot = $$1;
        this.callback = $$2;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!$$0.switchSlot(this.realmId, this.slot)) continue;
                this.callback.run();
                break;
            } catch (RetryCallException $$2) {
                if (this.aborted()) {
                    return;
                }
                SwitchSlotTask.pause($$2.delaySeconds);
                continue;
            } catch (Exception $$3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't switch world!");
                this.error($$3);
            }
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

