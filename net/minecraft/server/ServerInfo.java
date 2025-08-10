/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server;

public interface ServerInfo {
    public String getMotd();

    public String getServerVersion();

    public int getPlayerCount();

    public int getMaxPlayers();
}

