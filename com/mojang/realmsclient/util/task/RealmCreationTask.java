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
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RealmCreationTask
extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.create.world.wait");
    private final String name;
    private final String motd;
    private final long realmId;

    public RealmCreationTask(long $$0, String $$1, String $$2) {
        this.realmId = $$0;
        this.name = $$1;
        this.motd = $$2;
    }

    @Override
    public void run() {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        try {
            $$0.initializeRealm(this.realmId, this.name, this.motd);
        } catch (RealmsServiceException $$1) {
            LOGGER.error("Couldn't create world", $$1);
            this.error($$1);
        } catch (Exception $$2) {
            LOGGER.error("Could not create world", $$2);
            this.error($$2);
        }
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

