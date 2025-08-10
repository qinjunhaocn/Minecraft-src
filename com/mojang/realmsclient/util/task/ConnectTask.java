/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.dto.RealmsJoinInformation;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsConnect;

public class ConnectTask
extends LongRunningTask {
    private static final Component TITLE = Component.translatable("mco.connect.connecting");
    private final RealmsConnect realmsConnect;
    private final RealmsServer server;
    private final RealmsJoinInformation address;

    public ConnectTask(Screen $$0, RealmsServer $$1, RealmsJoinInformation $$2) {
        this.server = $$1;
        this.address = $$2;
        this.realmsConnect = new RealmsConnect($$0);
    }

    @Override
    public void run() {
        if (this.address.address() != null) {
            this.realmsConnect.connect(this.server, ServerAddress.parseString(this.address.address()));
        } else {
            this.abortTask();
        }
    }

    @Override
    public void abortTask() {
        super.abortTask();
        this.realmsConnect.abort();
        Minecraft.getInstance().getDownloadedPackSource().cleanupAfterDisconnect();
    }

    @Override
    public void tick() {
        this.realmsConnect.tick();
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
}

