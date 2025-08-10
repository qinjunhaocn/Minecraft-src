/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.channel.ChannelFuture
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.slf4j.Logger;

public class ConnectScreen
extends Screen {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final long NARRATION_DELAY_MS = 2000L;
    public static final Component ABORT_CONNECTION = Component.translatable("connect.aborted");
    public static final Component UNKNOWN_HOST_MESSAGE = Component.a("disconnect.genericReason", Component.translatable("disconnect.unknownHost"));
    @Nullable
    volatile Connection connection;
    @Nullable
    ChannelFuture channelFuture;
    volatile boolean aborted;
    final Screen parent;
    private Component status = Component.translatable("connect.connecting");
    private long lastNarration = -1L;
    final Component connectFailedTitle;

    private ConnectScreen(Screen $$0, Component $$1) {
        super(GameNarrator.NO_TITLE);
        this.parent = $$0;
        this.connectFailedTitle = $$1;
    }

    public static void startConnecting(Screen $$0, Minecraft $$1, ServerAddress $$2, ServerData $$3, boolean $$4, @Nullable TransferState $$5) {
        Component $$8;
        if ($$1.screen instanceof ConnectScreen) {
            LOGGER.error("Attempt to connect while already connecting");
            return;
        }
        if ($$5 != null) {
            Component $$6 = CommonComponents.TRANSFER_CONNECT_FAILED;
        } else if ($$4) {
            Component $$7 = QuickPlay.ERROR_TITLE;
        } else {
            $$8 = CommonComponents.CONNECT_FAILED;
        }
        ConnectScreen $$9 = new ConnectScreen($$0, $$8);
        if ($$5 != null) {
            $$9.updateStatus(Component.translatable("connect.transferring"));
        }
        $$1.disconnectWithProgressScreen();
        $$1.prepareForMultiplayer();
        $$1.updateReportEnvironment(ReportEnvironment.thirdParty($$3.ip));
        $$1.quickPlayLog().setWorldData(QuickPlayLog.Type.MULTIPLAYER, $$3.ip, $$3.name);
        $$1.setScreen($$9);
        $$9.connect($$1, $$2, $$3, $$5);
    }

    private void connect(final Minecraft $$0, final ServerAddress $$1, final ServerData $$2, final @Nullable TransferState $$3) {
        LOGGER.info("Connecting to {}, {}", (Object)$$1.getHost(), (Object)$$1.getPort());
        Thread $$4 = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * WARNING - void declaration
             */
            @Override
            public void run() {
                InetSocketAddress $$02 = null;
                try {
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    Optional<InetSocketAddress> $$12 = ServerNameResolver.DEFAULT.resolveAddress($$1).map(ResolvedServerAddress::asInetSocketAddress);
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    if ($$12.isEmpty()) {
                        $$0.execute(() -> $$0.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, UNKNOWN_HOST_MESSAGE)));
                        return;
                    }
                    $$02 = $$12.get();
                    ConnectScreen connectScreen = ConnectScreen.this;
                    synchronized (connectScreen) {
                        if (ConnectScreen.this.aborted) {
                            return;
                        }
                        Connection $$22 = new Connection(PacketFlow.CLIENTBOUND);
                        $$22.setBandwidthLogger($$0.getDebugOverlay().getBandwidthLogger());
                        ConnectScreen.this.channelFuture = Connection.connect($$02, $$0.options.useNativeTransport(), $$22);
                    }
                    ConnectScreen.this.channelFuture.syncUninterruptibly();
                    connectScreen = ConnectScreen.this;
                    synchronized (connectScreen) {
                        void $$32;
                        if (ConnectScreen.this.aborted) {
                            $$32.disconnect(ABORT_CONNECTION);
                            return;
                        }
                        ConnectScreen.this.connection = $$32;
                        $$0.getDownloadedPackSource().configureForServerControl((Connection)$$32, 1.convertPackStatus($$2.getResourcePackStatus()));
                    }
                    ConnectScreen.this.connection.initiateServerboundPlayConnection($$02.getHostName(), $$02.getPort(), LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, new ClientHandshakePacketListenerImpl(ConnectScreen.this.connection, $$0, $$2, ConnectScreen.this.parent, false, null, ConnectScreen.this::updateStatus, $$3), $$3 != null);
                    ConnectScreen.this.connection.send(new ServerboundHelloPacket($$0.getUser().getName(), $$0.getUser().getProfileId()));
                } catch (Exception $$4) {
                    Exception $$7;
                    if (ConnectScreen.this.aborted) {
                        return;
                    }
                    Throwable throwable = $$4.getCause();
                    if (throwable instanceof Exception) {
                        Exception $$5;
                        Exception $$6 = $$5 = (Exception)throwable;
                    } else {
                        $$7 = $$4;
                    }
                    LOGGER.error("Couldn't connect to server", $$4);
                    String $$8 = $$02 == null ? $$7.getMessage() : $$7.getMessage().replaceAll($$02.getHostName() + ":" + $$02.getPort(), "").replaceAll($$02.toString(), "");
                    $$0.execute(() -> $$0.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.connectFailedTitle, Component.a("disconnect.genericReason", $$8))));
                }
            }

            private static ServerPackManager.PackPromptStatus convertPackStatus(ServerData.ServerPackStatus $$02) {
                return switch ($$02) {
                    default -> throw new MatchException(null, null);
                    case ServerData.ServerPackStatus.ENABLED -> ServerPackManager.PackPromptStatus.ALLOWED;
                    case ServerData.ServerPackStatus.DISABLED -> ServerPackManager.PackPromptStatus.DECLINED;
                    case ServerData.ServerPackStatus.PROMPT -> ServerPackManager.PackPromptStatus.PENDING;
                };
            }
        };
        $$4.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        $$4.start();
    }

    private void updateStatus(Component $$0) {
        this.status = $$0;
    }

    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            ConnectScreen connectScreen = this;
            synchronized (connectScreen) {
                this.aborted = true;
                if (this.channelFuture != null) {
                    this.channelFuture.cancel(true);
                    this.channelFuture = null;
                }
                if (this.connection != null) {
                    this.connection.disconnect(ABORT_CONNECTION);
                }
            }
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        long $$4 = Util.getMillis();
        if ($$4 - this.lastNarration > 2000L) {
            this.lastNarration = $$4;
            this.minecraft.getNarrator().saySystemNow(Component.translatable("narrator.joining"));
        }
        $$0.drawCenteredString(this.font, this.status, this.width / 2, this.height / 2 - 50, -1);
    }
}

