/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerPinger;
import org.slf4j.Logger;

public class LanServerDetection {
    static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();

    public static class LanServerDetector
    extends Thread {
        private final LanServerList serverList;
        private final InetAddress pingGroup;
        private final MulticastSocket socket;

        public LanServerDetector(LanServerList $$0) throws IOException {
            super("LanServerDetector #" + UNIQUE_THREAD_ID.incrementAndGet());
            this.serverList = $$0;
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            this.socket = new MulticastSocket(4445);
            this.pingGroup = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.pingGroup);
        }

        @Override
        public void run() {
            byte[] $$0 = new byte[1024];
            while (!this.isInterrupted()) {
                DatagramPacket $$1 = new DatagramPacket($$0, $$0.length);
                try {
                    this.socket.receive($$1);
                } catch (SocketTimeoutException $$2) {
                    continue;
                } catch (IOException $$3) {
                    LOGGER.error("Couldn't ping server", $$3);
                    break;
                }
                String $$4 = new String($$1.getData(), $$1.getOffset(), $$1.getLength(), StandardCharsets.UTF_8);
                LOGGER.debug("{}: {}", (Object)$$1.getAddress(), (Object)$$4);
                this.serverList.addServer($$4, $$1.getAddress());
            }
            try {
                this.socket.leaveGroup(this.pingGroup);
            } catch (IOException iOException) {
                // empty catch block
            }
            this.socket.close();
        }
    }

    public static class LanServerList {
        private final List<LanServer> servers = Lists.newArrayList();
        private boolean isDirty;

        @Nullable
        public synchronized List<LanServer> takeDirtyServers() {
            if (this.isDirty) {
                List $$0 = List.copyOf(this.servers);
                this.isDirty = false;
                return $$0;
            }
            return null;
        }

        public synchronized void addServer(String $$0, InetAddress $$1) {
            String $$2 = LanServerPinger.parseMotd($$0);
            Object $$3 = LanServerPinger.parseAddress($$0);
            if ($$3 == null) {
                return;
            }
            $$3 = $$1.getHostAddress() + ":" + (String)$$3;
            boolean $$4 = false;
            for (LanServer $$5 : this.servers) {
                if (!$$5.getAddress().equals($$3)) continue;
                $$5.updatePingTime();
                $$4 = true;
                break;
            }
            if (!$$4) {
                this.servers.add(new LanServer($$2, (String)$$3));
                this.isDirty = true;
            }
        }
    }
}

