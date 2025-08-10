/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RestoreTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.backup.restoring");
    private final Backup backup;
    private final long realmId;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup $$0, long $$1, RealmsConfigureWorldScreen $$2) {
        this.backup = $$0;
        this.realmId = $$1;
        this.lastScreen = $$2;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }
                $$0.restoreWorld(this.realmId, this.backup.backupId);
                RestoreTask.pause(1L);
                if (this.aborted()) {
                    return;
                }
                RestoreTask.setScreen(this.lastScreen);
                return;
            } catch (RetryCallException $$2) {
                if (this.aborted()) {
                    return;
                }
                RestoreTask.pause($$2.delaySeconds);
                continue;
            } catch (RealmsServiceException $$3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", $$3);
                RestoreTask.setScreen(new RealmsGenericErrorScreen($$3, (Screen)this.lastScreen));
                return;
            } catch (Exception $$4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", $$4);
                this.error($$4);
                return;
            }
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

