/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.LegacyServerPinger;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import org.slf4j.Logger;

public class ServerStatusPinger {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component CANT_CONNECT_MESSAGE = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
    private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

    public void pingServer(final ServerData $$0, final Runnable $$1, final Runnable $$2) throws UnknownHostException {
        final ServerAddress $$3 = ServerAddress.parseString($$0.ip);
        Optional<InetSocketAddress> $$4 = ServerNameResolver.DEFAULT.resolveAddress($$3).map(ResolvedServerAddress::asInetSocketAddress);
        if ($$4.isEmpty()) {
            this.onPingFailed(ConnectScreen.UNKNOWN_HOST_MESSAGE, $$0);
            return;
        }
        final InetSocketAddress $$5 = $$4.get();
        final Connection $$6 = Connection.connectToServer($$5, false, null);
        this.connections.add($$6);
        $$0.motd = Component.translatable("multiplayer.status.pinging");
        $$0.playerList = Collections.emptyList();
        ClientStatusPacketListener $$7 = new ClientStatusPacketListener(){
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket $$02) {
                if (this.receivedPing) {
                    $$6.disconnect(Component.translatable("multiplayer.status.unrequested"));
                    return;
                }
                this.receivedPing = true;
                ServerStatus $$12 = $$02.status();
                $$0.motd = $$12.description();
                $$12.version().ifPresentOrElse($$1 -> {
                    $$02.version = Component.literal($$1.name());
                    $$02.protocol = $$1.protocol();
                }, () -> {
                    $$02.version = Component.translatable("multiplayer.status.old");
                    $$02.protocol = 0;
                });
                $$12.players().ifPresentOrElse($$1 -> {
                    $$02.status = ServerStatusPinger.formatPlayerCount($$1.online(), $$1.max());
                    $$02.players = $$1;
                    if (!$$1.sample().isEmpty()) {
                        ArrayList<Component> $$22 = new ArrayList<Component>($$1.sample().size());
                        for (GameProfile $$32 : $$1.sample()) {
                            $$22.add(Component.literal($$32.getName()));
                        }
                        if ($$1.sample().size() < $$1.online()) {
                            $$22.add(Component.a("multiplayer.status.and_more", $$1.online() - $$1.sample().size()));
                        }
                        $$02.playerList = $$22;
                    } else {
                        $$02.playerList = List.of();
                    }
                }, () -> {
                    $$02.status = Component.translatable("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY);
                });
                $$12.favicon().ifPresent($$2 -> {
                    if (!Arrays.equals($$2.a(), $$0.c())) {
                        $$0.a(ServerData.b($$2.a()));
                        $$1.run();
                    }
                });
                this.pingStart = Util.getMillis();
                $$6.send(new ServerboundPingRequestPacket(this.pingStart));
                this.success = true;
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket $$02) {
                long $$12 = this.pingStart;
                long $$22 = Util.getMillis();
                $$0.ping = $$22 - $$12;
                $$6.disconnect(Component.translatable("multiplayer.status.finished"));
                $$2.run();
            }

            @Override
            public void onDisconnect(DisconnectionDetails $$02) {
                if (!this.success) {
                    ServerStatusPinger.this.onPingFailed($$02.reason(), $$0);
                    ServerStatusPinger.this.pingLegacyServer($$5, $$3, $$0);
                }
            }

            @Override
            public boolean isAcceptingMessages() {
                return $$6.isConnected();
            }
        };
        try {
            $$6.initiateServerboundStatusConnection($$3.getHost(), $$3.getPort(), $$7);
            $$6.send(ServerboundStatusRequestPacket.INSTANCE);
        } catch (Throwable $$8) {
            LOGGER.error("Failed to ping server {}", (Object)$$3, (Object)$$8);
        }
    }

    void onPingFailed(Component $$0, ServerData $$1) {
        LOGGER.error("Can't ping {}: {}", (Object)$$1.ip, (Object)$$0.getString());
        $$1.motd = CANT_CONNECT_MESSAGE;
        $$1.status = CommonComponents.EMPTY;
    }

    void pingLegacyServer(InetSocketAddress $$0, final ServerAddress $$1, final ServerData $$2) {
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(this){

            protected void initChannel(Channel $$0) {
                try {
                    $$0.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                } catch (ChannelException channelException) {
                    // empty catch block
                }
                $$0.pipeline().addLast(new ChannelHandler[]{new LegacyServerPinger($$1, ($$1, $$2, $$3, $$4, $$5) -> {
                    $$2.setState(ServerData.State.INCOMPATIBLE);
                    $$0.version = Component.literal($$2);
                    $$0.motd = Component.literal($$3);
                    $$0.status = ServerStatusPinger.formatPlayerCount($$4, $$5);
                    $$0.players = new ServerStatus.Players($$5, $$4, List.of());
                })});
            }
        })).channel(NioSocketChannel.class)).connect($$0.getAddress(), $$0.getPort());
    }

    public static Component formatPlayerCount(int $$0, int $$1) {
        MutableComponent $$2 = Component.literal(Integer.toString($$0)).withStyle(ChatFormatting.GRAY);
        MutableComponent $$3 = Component.literal(Integer.toString($$1)).withStyle(ChatFormatting.GRAY);
        return Component.a("multiplayer.status.player_count", $$2, $$3).withStyle(ChatFormatting.DARK_GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator<Connection> $$0 = this.connections.iterator();
            while ($$0.hasNext()) {
                Connection $$1 = $$0.next();
                if ($$1.isConnected()) {
                    $$1.tick();
                    continue;
                }
                $$0.remove();
                $$1.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator<Connection> $$0 = this.connections.iterator();
            while ($$0.hasNext()) {
                Connection $$1 = $$0.next();
                if (!$$1.isConnected()) continue;
                $$0.remove();
                $$1.disconnect(Component.translatable("multiplayer.status.cancelled"));
            }
        }
    }
}

