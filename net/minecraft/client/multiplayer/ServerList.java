/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.slf4j.Logger;

public class ServerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ConsecutiveExecutor IO_EXECUTOR = new ConsecutiveExecutor(Util.backgroundExecutor(), "server-list-io");
    private static final int MAX_HIDDEN_SERVERS = 16;
    private final Minecraft minecraft;
    private final List<ServerData> serverList = Lists.newArrayList();
    private final List<ServerData> hiddenServerList = Lists.newArrayList();

    public ServerList(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void load() {
        try {
            this.serverList.clear();
            this.hiddenServerList.clear();
            CompoundTag $$02 = NbtIo.read(this.minecraft.gameDirectory.toPath().resolve("servers.dat"));
            if ($$02 == null) {
                return;
            }
            $$02.getListOrEmpty("servers").compoundStream().forEach($$0 -> {
                ServerData $$1 = ServerData.read($$0);
                if ($$0.getBooleanOr("hidden", false)) {
                    this.hiddenServerList.add($$1);
                } else {
                    this.serverList.add($$1);
                }
            });
        } catch (Exception $$1) {
            LOGGER.error("Couldn't load server list", $$1);
        }
    }

    public void save() {
        try {
            ListTag $$0 = new ListTag();
            for (ServerData $$1 : this.serverList) {
                CompoundTag $$2 = $$1.write();
                $$2.putBoolean("hidden", false);
                $$0.add($$2);
            }
            for (ServerData $$3 : this.hiddenServerList) {
                CompoundTag $$4 = $$3.write();
                $$4.putBoolean("hidden", true);
                $$0.add($$4);
            }
            CompoundTag $$5 = new CompoundTag();
            $$5.put("servers", $$0);
            Path $$6 = this.minecraft.gameDirectory.toPath();
            Path $$7 = Files.createTempFile($$6, "servers", ".dat", new FileAttribute[0]);
            NbtIo.write($$5, $$7);
            Path $$8 = $$6.resolve("servers.dat_old");
            Path $$9 = $$6.resolve("servers.dat");
            Util.safeReplaceFile($$9, $$7, $$8);
        } catch (Exception $$10) {
            LOGGER.error("Couldn't save server list", $$10);
        }
    }

    public ServerData get(int $$0) {
        return this.serverList.get($$0);
    }

    @Nullable
    public ServerData get(String $$0) {
        for (ServerData $$1 : this.serverList) {
            if (!$$1.ip.equals($$0)) continue;
            return $$1;
        }
        for (ServerData $$2 : this.hiddenServerList) {
            if (!$$2.ip.equals($$0)) continue;
            return $$2;
        }
        return null;
    }

    @Nullable
    public ServerData unhide(String $$0) {
        for (int $$1 = 0; $$1 < this.hiddenServerList.size(); ++$$1) {
            ServerData $$2 = this.hiddenServerList.get($$1);
            if (!$$2.ip.equals($$0)) continue;
            this.hiddenServerList.remove($$1);
            this.serverList.add($$2);
            return $$2;
        }
        return null;
    }

    public void remove(ServerData $$0) {
        if (!this.serverList.remove($$0)) {
            this.hiddenServerList.remove($$0);
        }
    }

    public void add(ServerData $$0, boolean $$1) {
        if ($$1) {
            this.hiddenServerList.add(0, $$0);
            while (this.hiddenServerList.size() > 16) {
                this.hiddenServerList.remove(this.hiddenServerList.size() - 1);
            }
        } else {
            this.serverList.add($$0);
        }
    }

    public int size() {
        return this.serverList.size();
    }

    public void swap(int $$0, int $$1) {
        ServerData $$2 = this.get($$0);
        this.serverList.set($$0, this.get($$1));
        this.serverList.set($$1, $$2);
        this.save();
    }

    public void replace(int $$0, ServerData $$1) {
        this.serverList.set($$0, $$1);
    }

    private static boolean set(ServerData $$0, List<ServerData> $$1) {
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ServerData $$3 = $$1.get($$2);
            if (!Objects.equals($$3.name, $$0.name) || !$$3.ip.equals($$0.ip)) continue;
            $$1.set($$2, $$0);
            return true;
        }
        return false;
    }

    public static void saveSingleServer(ServerData $$0) {
        IO_EXECUTOR.schedule(() -> {
            ServerList $$1 = new ServerList(Minecraft.getInstance());
            $$1.load();
            if (!ServerList.set($$0, $$1.serverList)) {
                ServerList.set($$0, $$1.hiddenServerList);
            }
            $$1.save();
        });
    }
}

