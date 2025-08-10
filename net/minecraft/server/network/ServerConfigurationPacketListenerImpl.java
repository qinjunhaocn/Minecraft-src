/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.config.JoinWorldTask;
import net.minecraft.server.network.config.ServerResourcePackConfigurationTask;
import net.minecraft.server.network.config.SynchronizeRegistriesTask;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ServerConfigurationPacketListenerImpl
extends ServerCommonPacketListenerImpl
implements ServerConfigurationPacketListener,
TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component DISCONNECT_REASON_INVALID_DATA = Component.translatable("multiplayer.disconnect.invalid_player_data");
    private final GameProfile gameProfile;
    private final Queue<ConfigurationTask> configurationTasks = new ConcurrentLinkedQueue<ConfigurationTask>();
    @Nullable
    private ConfigurationTask currentTask;
    private ClientInformation clientInformation;
    @Nullable
    private SynchronizeRegistriesTask synchronizeRegistriesTask;

    public ServerConfigurationPacketListenerImpl(MinecraftServer $$0, Connection $$1, CommonListenerCookie $$2) {
        super($$0, $$1, $$2);
        this.gameProfile = $$2.gameProfile();
        this.clientInformation = $$2.clientInformation();
    }

    @Override
    protected GameProfile playerProfile() {
        return this.gameProfile;
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
        LOGGER.info("{} lost connection: {}", (Object)this.gameProfile, (Object)$$0.reason().getString());
        super.onDisconnect($$0);
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public void startConfiguration() {
        this.send(new ClientboundCustomPayloadPacket(new BrandPayload(this.server.getServerModName())));
        ServerLinks $$02 = this.server.serverLinks();
        if (!$$02.isEmpty()) {
            this.send(new ClientboundServerLinksPacket($$02.untrust()));
        }
        LayeredRegistryAccess<RegistryLayer> $$1 = this.server.registries();
        List $$2 = this.server.getResourceManager().listPacks().flatMap($$0 -> $$0.location().knownPackInfo().stream()).toList();
        this.send(new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(this.server.getWorldData().enabledFeatures())));
        this.synchronizeRegistriesTask = new SynchronizeRegistriesTask($$2, $$1);
        this.configurationTasks.add(this.synchronizeRegistriesTask);
        this.addOptionalTasks();
        this.configurationTasks.add(new JoinWorldTask());
        this.startNextTask();
    }

    public void returnToWorld() {
        this.configurationTasks.add(new JoinWorldTask());
        this.startNextTask();
    }

    private void addOptionalTasks() {
        this.server.getServerResourcePack().ifPresent($$0 -> this.configurationTasks.add(new ServerResourcePackConfigurationTask((MinecraftServer.ServerResourcePackInfo)((Object)$$0))));
    }

    @Override
    public void handleClientInformation(ServerboundClientInformationPacket $$0) {
        this.clientInformation = $$0.information();
    }

    @Override
    public void handleResourcePackResponse(ServerboundResourcePackPacket $$0) {
        super.handleResourcePackResponse($$0);
        if ($$0.action().isTerminal()) {
            this.finishCurrentTask(ServerResourcePackConfigurationTask.TYPE);
        }
    }

    @Override
    public void handleSelectKnownPacks(ServerboundSelectKnownPacks $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.server);
        if (this.synchronizeRegistriesTask == null) {
            throw new IllegalStateException("Unexpected response from client: received pack selection, but no negotiation ongoing");
        }
        this.synchronizeRegistriesTask.handleResponse($$0.knownPacks(), this::send);
        this.finishCurrentTask(SynchronizeRegistriesTask.TYPE);
    }

    @Override
    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.server);
        this.finishCurrentTask(JoinWorldTask.TYPE);
        this.connection.setupOutboundProtocol(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess())));
        try {
            PlayerList $$1 = this.server.getPlayerList();
            if ($$1.getPlayer(this.gameProfile.getId()) != null) {
                this.disconnect(PlayerList.DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
                return;
            }
            Component $$2 = $$1.canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
            if ($$2 != null) {
                this.disconnect($$2);
                return;
            }
            ServerPlayer $$3 = new ServerPlayer(this.server, this.server.overworld(), this.gameProfile, this.clientInformation);
            $$1.placeNewPlayer(this.connection, $$3, this.createCookie(this.clientInformation));
        } catch (Exception $$4) {
            LOGGER.error("Couldn't place player in world", $$4);
            this.connection.send(new ClientboundDisconnectPacket(DISCONNECT_REASON_INVALID_DATA));
            this.connection.disconnect(DISCONNECT_REASON_INVALID_DATA);
        }
    }

    @Override
    public void tick() {
        this.keepConnectionAlive();
    }

    private void startNextTask() {
        if (this.currentTask != null) {
            throw new IllegalStateException("Task " + this.currentTask.type().id() + " has not finished yet");
        }
        if (!this.isAcceptingMessages()) {
            return;
        }
        ConfigurationTask $$0 = this.configurationTasks.poll();
        if ($$0 != null) {
            this.currentTask = $$0;
            $$0.start(this::send);
        }
    }

    private void finishCurrentTask(ConfigurationTask.Type $$0) {
        ConfigurationTask.Type $$1;
        ConfigurationTask.Type type = $$1 = this.currentTask != null ? this.currentTask.type() : null;
        if (!$$0.equals((Object)$$1)) {
            throw new IllegalStateException("Unexpected request for task finish, current task: " + String.valueOf((Object)$$1) + ", requested: " + String.valueOf((Object)$$0));
        }
        this.currentTask = null;
        this.startNextTask();
    }
}

