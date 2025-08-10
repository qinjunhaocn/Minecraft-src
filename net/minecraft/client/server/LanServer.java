/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.server;

import net.minecraft.Util;

public class LanServer {
    private final String motd;
    private final String address;
    private long pingTime;

    public LanServer(String $$0, String $$1) {
        this.motd = $$0;
        this.address = $$1;
        this.pingTime = Util.getMillis();
    }

    public String getMotd() {
        return this.motd;
    }

    public String getAddress() {
        return this.address;
    }

    public void updatePingTime() {
        this.pingTime = Util.getMillis();
    }
}

