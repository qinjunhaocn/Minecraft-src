/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public abstract class ResettingWorldTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long serverId;
    private final Component title;
    private final Runnable callback;

    public ResettingWorldTask(long $$0, Component $$1, Runnable $$2) {
        this.serverId = $$0;
        this.title = $$1;
        this.callback = $$2;
    }

    protected abstract void sendResetRequest(RealmsClient var1, long var2) throws RealmsServiceException;

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                this.sendResetRequest($$0, this.serverId);
                if (this.aborted()) {
                    return;
                }
                this.callback.run();
                return;
            } catch (RetryCallException $$2) {
                if (this.aborted()) {
                    return;
                }
                ResettingWorldTask.pause($$2.delaySeconds);
                continue;
            } catch (Exception $$3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't reset world");
                this.error($$3);
                return;
            }
        }
    }

    @Override
    public Component getTitle() {
        return this.title;
    }
}

