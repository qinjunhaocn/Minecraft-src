/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server;

import net.minecraft.server.ServerInfo;
import net.minecraft.server.dedicated.DedicatedServerProperties;

public interface ServerInterface
extends ServerInfo {
    public DedicatedServerProperties getProperties();

    public String getServerIp();

    public int getServerPort();

    public String getServerName();

    public String[] P();

    public String getLevelIdName();

    public String getPluginNames();

    public String runCommand(String var1);
}

