/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.realms;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetSocketAddress;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class RealmsConnect {
    static final Logger LOGGER = LogUtils.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    @Nullable
    Connection connection;

    public RealmsConnect(Screen $$0) {
        this.onlineScreen = $$0;
    }

    public void connect(final RealmsServer $$0, ServerAddress $$1) {
        final Minecraft $$2 = Minecraft.getInstance();
        $$2.prepareForMultiplayer();
        $$2.getNarrator().saySystemNow(Component.translatable("mco.connect.success"));
        final String $$3 = $$1.getHost();
        final int $$4 = $$1.getPort();
        new Thread("Realms-connect-task"){

            @Override
            public void run() {
                InetSocketAddress $$02 = null;
                try {
                    $$02 = new InetSocketAddress($$3, $$4);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection = Connection.connectToServer($$02, $$2.options.useNativeTransport(), $$2.getDebugOverlay().getBandwidthLogger());
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    ClientHandshakePacketListenerImpl $$1 = new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, $$2, $$0.toServerData($$3), RealmsConnect.this.onlineScreen, false, null, $$0 -> {}, null);
                    if ($$0.isMinigameActive()) {
                        $$1.setMinigameName($$0.minigameName);
                    }
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.initiateServerboundPlayConnection($$3, $$4, $$1);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new ServerboundHelloPacket($$2.getUser().getName(), $$2.getUser().getProfileId()));
                    $$2.updateReportEnvironment(ReportEnvironment.realm($$0));
                    $$2.quickPlayLog().setWorldData(QuickPlayLog.Type.REALMS, String.valueOf($$0.id), (String)Objects.requireNonNullElse((Object)$$0.name, (Object)"unknown"));
                    $$2.getDownloadedPackSource().configureForServerControl(RealmsConnect.this.connection, ServerPackManager.PackPromptStatus.ALLOWED);
                } catch (Exception $$22) {
                    $$2.getDownloadedPackSource().cleanupAfterDisconnect();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", $$22);
                    String $$32 = $$22.toString();
                    if ($$02 != null) {
                        String $$42 = String.valueOf($$02) + ":" + $$4;
                        $$32 = $$32.replaceAll($$42, "");
                    }
                    DisconnectedScreen $$5 = new DisconnectedScreen(RealmsConnect.this.onlineScreen, (Component)Component.translatable("mco.connect.failed"), Component.a("disconnect.genericReason", $$32), CommonComponents.GUI_BACK);
                    $$2.execute(() -> $$2.setScreen($$5));
                }
            }
        }.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isConnected()) {
            this.connection.disconnect(Component.translatable("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}

