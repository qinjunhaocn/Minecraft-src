/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.ForcedUsernameChangeException
 *  com.mojang.authlib.exceptions.InsufficientPrivilegesException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.exceptions.UserBannedException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Crypt;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ClientHandshakePacketListenerImpl
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    @Nullable
    private final ServerData serverData;
    @Nullable
    private final Screen parent;
    private final Consumer<Component> updateStatus;
    private final Connection connection;
    private final boolean newWorld;
    @Nullable
    private final Duration worldLoadDuration;
    @Nullable
    private String minigameName;
    private final Map<ResourceLocation, byte[]> cookies;
    private final boolean wasTransferredTo;
    private final AtomicReference<State> state = new AtomicReference<State>(State.CONNECTING);

    public ClientHandshakePacketListenerImpl(Connection $$0, Minecraft $$1, @Nullable ServerData $$2, @Nullable Screen $$3, boolean $$4, @Nullable Duration $$5, Consumer<Component> $$6, @Nullable TransferState $$7) {
        this.connection = $$0;
        this.minecraft = $$1;
        this.serverData = $$2;
        this.parent = $$3;
        this.updateStatus = $$6;
        this.newWorld = $$4;
        this.worldLoadDuration = $$5;
        this.cookies = $$7 != null ? new HashMap<ResourceLocation, byte[]>($$7.cookies()) : new HashMap();
        this.wasTransferredTo = $$7 != null;
    }

    private void switchState(State $$0) {
        State $$12 = this.state.updateAndGet($$1 -> {
            if (!$$0.fromStates.contains($$1)) {
                throw new IllegalStateException("Tried to switch to " + String.valueOf((Object)$$0) + " from " + String.valueOf($$1) + ", but expected one of " + String.valueOf($$0.fromStates));
            }
            return $$0;
        });
        this.updateStatus.accept($$12.message);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handleHello(ClientboundHelloPacket $$0) {
        this.switchState(State.AUTHORIZING);
        try {
            SecretKey $$1 = Crypt.generateSecretKey();
            PublicKey $$2 = $$0.getPublicKey();
            String $$3 = new BigInteger(Crypt.a($$0.getServerId(), $$2, $$1)).toString(16);
            Cipher $$4 = Crypt.getCipher(2, $$1);
            Cipher $$5 = Crypt.getCipher(1, $$1);
            byte[] $$6 = $$0.f();
            ServerboundKeyPacket $$7 = new ServerboundKeyPacket($$1, $$2, $$6);
        } catch (Exception $$8) {
            throw new IllegalStateException("Protocol error", $$8);
        }
        if ($$0.shouldAuthenticate()) {
            void $$11;
            Util.ioPool().execute(() -> this.lambda$handleHello$1((String)$$11, (ServerboundKeyPacket)$$12, (Cipher)$$9, (Cipher)$$10));
        } else {
            this.setEncryption((ServerboundKeyPacket)$$12, (Cipher)$$9, (Cipher)$$10);
        }
    }

    private void setEncryption(ServerboundKeyPacket $$0, Cipher $$1, Cipher $$2) {
        this.switchState(State.ENCRYPTING);
        this.connection.send($$0, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey($$1, $$2)));
    }

    @Nullable
    private Component authenticateServer(String $$0) {
        try {
            this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getProfileId(), this.minecraft.getUser().getAccessToken(), $$0);
        } catch (AuthenticationUnavailableException $$1) {
            return Component.a("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.serversUnavailable"));
        } catch (InvalidCredentialsException $$2) {
            return Component.a("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.invalidSession"));
        } catch (InsufficientPrivilegesException $$3) {
            return Component.a("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
        } catch (ForcedUsernameChangeException | UserBannedException $$4) {
            return Component.a("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.userBanned"));
        } catch (AuthenticationException $$5) {
            return Component.a("disconnect.loginFailedInfo", $$5.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getMinecraftSessionService() {
        return this.minecraft.getMinecraftSessionService();
    }

    @Override
    public void handleLoginFinished(ClientboundLoginFinishedPacket $$0) {
        this.switchState(State.JOINING);
        GameProfile $$1 = $$0.gameProfile();
        this.connection.setupInboundProtocol(ConfigurationProtocols.CLIENTBOUND, new ClientConfigurationPacketListenerImpl(this.minecraft, this.connection, new CommonListenerCookie($$1, this.minecraft.getTelemetryManager().createWorldSessionManager(this.newWorld, this.worldLoadDuration, this.minigameName), ClientRegistryLayer.createRegistryAccess().compositeAccess(), FeatureFlags.DEFAULT_FLAGS, null, this.serverData, this.parent, this.cookies, null, Map.of(), ServerLinks.EMPTY)));
        this.connection.send(ServerboundLoginAcknowledgedPacket.INSTANCE);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
        this.connection.send(new ServerboundCustomPayloadPacket(new BrandPayload(ClientBrandRetriever.getClientModName())));
        this.connection.send(new ServerboundClientInformationPacket(this.minecraft.options.buildPlayerInformation()));
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
        Component $$1;
        Component component = $$1 = this.wasTransferredTo ? CommonComponents.TRANSFER_CONNECT_FAILED : CommonComponents.CONNECT_FAILED;
        if (this.serverData != null && this.serverData.isRealm()) {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, $$1, $$0.reason(), CommonComponents.GUI_BACK));
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, $$1, $$0));
        }
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public void handleDisconnect(ClientboundLoginDisconnectPacket $$0) {
        this.connection.disconnect($$0.reason());
    }

    @Override
    public void handleCompression(ClientboundLoginCompressionPacket $$0) {
        if (!this.connection.isMemoryConnection()) {
            this.connection.setupCompression($$0.getCompressionThreshold(), false);
        }
    }

    @Override
    public void handleCustomQuery(ClientboundCustomQueryPacket $$0) {
        this.updateStatus.accept(Component.translatable("connect.negotiating"));
        this.connection.send(new ServerboundCustomQueryAnswerPacket($$0.transactionId(), null));
    }

    public void setMinigameName(@Nullable String $$0) {
        this.minigameName = $$0;
    }

    @Override
    public void handleRequestCookie(ClientboundCookieRequestPacket $$0) {
        this.connection.send(new ServerboundCookieResponsePacket($$0.key(), this.cookies.get($$0.key())));
    }

    @Override
    public void fillListenerSpecificCrashDetails(CrashReport $$0, CrashReportCategory $$1) {
        $$1.setDetail("Server type", () -> this.serverData != null ? this.serverData.type().toString() : "<unknown>");
        $$1.setDetail("Login phase", () -> this.state.get().toString());
        $$1.setDetail("Is Local", () -> String.valueOf(this.connection.isMemoryConnection()));
    }

    private /* synthetic */ void lambda$handleHello$1(String $$0, ServerboundKeyPacket $$1, Cipher $$2, Cipher $$3) {
        Component $$4 = this.authenticateServer($$0);
        if ($$4 != null) {
            if (this.serverData != null && this.serverData.isLan()) {
                LOGGER.warn($$4.getString());
            } else {
                this.connection.disconnect($$4);
                return;
            }
        }
        this.setEncryption($$1, $$2, $$3);
    }

    static final class State
    extends Enum<State> {
        public static final /* enum */ State CONNECTING = new State(Component.translatable("connect.connecting"), Set.of());
        public static final /* enum */ State AUTHORIZING = new State(Component.translatable("connect.authorizing"), Set.of((Object)((Object)CONNECTING)));
        public static final /* enum */ State ENCRYPTING = new State(Component.translatable("connect.encrypting"), Set.of((Object)((Object)AUTHORIZING)));
        public static final /* enum */ State JOINING = new State(Component.translatable("connect.joining"), Set.of((Object)((Object)ENCRYPTING), (Object)((Object)CONNECTING)));
        final Component message;
        final Set<State> fromStates;
        private static final /* synthetic */ State[] $VALUES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String $$0) {
            return Enum.valueOf(State.class, $$0);
        }

        private State(Component $$0, Set<State> $$1) {
            this.message = $$0;
            this.fromStates = $$1;
        }

        private static /* synthetic */ State[] a() {
            return new State[]{CONNECTING, AUTHORIZING, ENCRYPTING, JOINING};
        }

        static {
            $VALUES = State.a();
        }
    }
}

