/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsServer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class RealmsServerList
implements Iterable<RealmsServer> {
    private final Minecraft minecraft;
    private final Set<RealmsServer> removedServers = new HashSet<RealmsServer>();
    private List<RealmsServer> servers = List.of();

    public RealmsServerList(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void updateServersList(List<RealmsServer> $$0) {
        ArrayList<RealmsServer> $$1 = new ArrayList<RealmsServer>($$0);
        $$1.sort(new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
        boolean $$2 = $$1.removeAll(this.removedServers);
        if (!$$2) {
            this.removedServers.clear();
        }
        this.servers = $$1;
    }

    public void removeItem(RealmsServer $$0) {
        this.servers.remove($$0);
        this.removedServers.add($$0);
    }

    @Override
    public Iterator<RealmsServer> iterator() {
        return this.servers.iterator();
    }

    public boolean isEmpty() {
        return this.servers.isEmpty();
    }
}

