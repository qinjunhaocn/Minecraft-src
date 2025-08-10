/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl
implements ServerLoginPacketListener,
TickablePacketListener {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private final byte[] challenge;
    final MinecraftServer server;
    final Connection connection;
    private volatile State state = State.HELLO;
    private int tick;
    @Nullable
    String requestedUsername;
    @Nullable
    private GameProfile authenticatedProfile;
    private final String serverId = "";
    private final boolean transferred;

    public ServerLoginPacketListenerImpl(MinecraftServer $$0, Connection $$1, boolean $$2) {
        this.server = $$0;
        this.connection = $$1;
        this.challenge = Ints.toByteArray(RandomSource.create().nextInt());
        this.transferred = $$2;
    }

    @Override
    public void tick() {
        if (this.state == State.VERIFYING) {
            this.verifyLoginAndFinishConnectionSetup(Objects.requireNonNull(this.authenticatedProfile));
        }
        if (this.state == State.WAITING_FOR_DUPE_DISCONNECT && !this.isPlayerAlreadyInWorld(Objects.requireNonNull(this.authenticatedProfile))) {
            this.finishLoginAndWaitForClient(this.authenticatedProfile);
        }
        if (this.tick++ == 600) {
            this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public void disconnect(Component $$0) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getUserName(), (Object)$$0.getString());
            this.connection.send(new ClientboundLoginDisconnectPacket($$0));
            this.connection.disconnect($$0);
        } catch (Exception $$1) {
            LOGGER.error("Error whilst disconnecting player", $$1);
        }
    }

    private boolean isPlayerAlreadyInWorld(GameProfile $$0) {
        return this.server.getPlayerList().getPlayer($$0.getId()) != null;
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
        LOGGER.info("{} lost connection: {}", (Object)this.getUserName(), (Object)$$0.reason().getString());
    }

    public String getUserName() {
        String $$0 = this.connection.getLoggableAddress(this.server.logIPs());
        if (this.requestedUsername != null) {
            return this.requestedUsername + " (" + $$0 + ")";
        }
        return $$0;
    }

    @Override
    public void handleHello(ServerboundHelloPacket $$0) {
        Validate.validState(this.state == State.HELLO, "Unexpected hello packet", new Object[0]);
        Validate.validState(StringUtil.isValidPlayerName($$0.name()), "Invalid characters in username", new Object[0]);
        this.requestedUsername = $$0.name();
        GameProfile $$1 = this.server.getSingleplayerProfile();
        if ($$1 != null && this.requestedUsername.equalsIgnoreCase($$1.getName())) {
            this.startClientVerification($$1);
            return;
        }
        if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge, true));
        } else {
            this.startClientVerification(UUIDUtil.createOfflineProfile(this.requestedUsername));
        }
    }

    void startClientVerification(GameProfile $$0) {
        this.authenticatedProfile = $$0;
        this.state = State.VERIFYING;
    }

    private void verifyLoginAndFinishConnectionSetup(GameProfile $$0) {
        PlayerList $$1 = this.server.getPlayerList();
        Component $$2 = $$1.canPlayerLogin(this.connection.getRemoteAddress(), $$0);
        if ($$2 != null) {
            this.disconnect($$2);
        } else {
            boolean $$3;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> this.connection.setupCompression(this.server.getCompressionThreshold(), true)));
            }
            if ($$3 = $$1.disconnectAllPlayersWithProfile($$0)) {
                this.state = State.WAITING_FOR_DUPE_DISCONNECT;
            } else {
                this.finishLoginAndWaitForClient($$0);
            }
        }
    }

    private void finishLoginAndWaitForClient(GameProfile $$0) {
        this.state = State.PROTOCOL_SWITCHING;
        this.connection.send(new ClientboundLoginFinishedPacket($$0));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handleKey(ServerboundKeyPacket $$0) {
        void $$7;
        Validate.validState(this.state == State.KEY, "Unexpected key packet", new Object[0]);
        try {
            PrivateKey $$1 = this.server.getKeyPair().getPrivate();
            if (!$$0.a(this.challenge, $$1)) {
                throw new IllegalStateException("Protocol error");
            }
            SecretKey $$2 = $$0.getSecretKey($$1);
            Cipher $$3 = Crypt.getCipher(2, $$2);
            Cipher $$4 = Crypt.getCipher(1, $$2);
            String $$5 = new BigInteger(Crypt.a("", this.server.getKeyPair().getPublic(), $$2)).toString(16);
            this.state = State.AUTHENTICATING;
            this.connection.setEncryptionKey($$3, $$4);
        } catch (CryptException $$6) {
            throw new IllegalStateException("Protocol error", $$6);
        }
        Thread $$8 = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet(), (String)$$7){
            final /* synthetic */ String val$digest;
            {
                this.val$digest = string;
                super($$1);
            }

            @Override
            public void run() {
                String $$0 = Objects.requireNonNull(ServerLoginPacketListenerImpl.this.requestedUsername, "Player name not initialized");
                try {
                    ProfileResult $$1 = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer($$0, this.val$digest, this.getAddress());
                    if ($$1 != null) {
                        GameProfile $$2 = $$1.profile();
                        LOGGER.info("UUID of player {} is {}", (Object)$$2.getName(), (Object)$$2.getId());
                        ServerLoginPacketListenerImpl.this.startClientVerification($$2);
                    } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.startClientVerification(UUIDUtil.createOfflineProfile($$0));
                    } else {
                        ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)$$0);
                    }
                } catch (AuthenticationUnavailableException $$3) {
                    if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.startClientVerification(UUIDUtil.createOfflineProfile($$0));
                    }
                    ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress $$0 = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
                return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && $$0 instanceof InetSocketAddress ? ((InetSocketAddress)$$0).getAddress() : null;
            }
        };
        $$8.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        $$8.start();
    }

    @Override
    public void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket $$0) {
        this.disconnect(ServerCommonPacketListenerImpl.DISCONNECT_UNEXPECTED_QUERY);
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket $$0) {
        Validate.validState(this.state == State.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet", new Object[0]);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.CLIENTBOUND);
        CommonListenerCookie $$1 = CommonListenerCookie.createInitial(Objects.requireNonNull(this.authenticatedProfile), this.transferred);
        ServerConfigurationPacketListenerImpl $$2 = new ServerConfigurationPacketListenerImpl(this.server, this.connection, $$1);
        this.connection.setupInboundProtocol(ConfigurationProtocols.SERVERBOUND, $$2);
        $$2.startConfiguration();
        this.state = State.ACCEPTED;
    }

    @Override
    public void fillListenerSpecificCrashDetails(CrashReport $$0, CrashReportCategory $$1) {
        $$1.setDetail("Login phase", () -> this.state.toString());
    }

    @Override
    public void handleCookieResponse(ServerboundCookieResponsePacket $$0) {
        this.disconnect(ServerCommonPacketListenerImpl.DISCONNECT_UNEXPECTED_QUERY);
    }

    static final class State
    extends Enum<State> {
        public static final /* enum */ State HELLO = new State();
        public static final /* enum */ State KEY = new State();
        public static final /* enum */ State AUTHENTICATING = new State();
        public static final /* enum */ State NEGOTIATING = new State();
        public static final /* enum */ State VERIFYING = new State();
        public static final /* enum */ State WAITING_FOR_DUPE_DISCONNECT = new State();
        public static final /* enum */ State PROTOCOL_SWITCHING = new State();
        public static final /* enum */ State ACCEPTED = new State();
        private static final /* synthetic */ State[] $VALUES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String $$0) {
            return Enum.valueOf(State.class, $$0);
        }

        private static /* synthetic */ State[] a() {
            return new State[]{HELLO, KEY, AUTHENTICATING, NEGOTIATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED};
        }

        static {
            $VALUES = State.a();
        }
    }
}

