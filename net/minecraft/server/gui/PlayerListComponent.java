/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class PlayerListComponent
extends JList<String> {
    private final MinecraftServer server;
    private int tickCount;

    public PlayerListComponent(MinecraftServer $$0) {
        this.server = $$0;
        $$0.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            Vector<String> $$0 = new Vector<String>();
            for (int $$1 = 0; $$1 < this.server.getPlayerList().getPlayers().size(); ++$$1) {
                $$0.add(this.server.getPlayerList().getPlayers().get($$1).getGameProfile().getName());
            }
            this.setListData($$0);
        }
    }
}

