/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.KnownPacksManager;
import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ClientConfigurationPacketListenerImpl
extends ClientCommonPacketListenerImpl
implements ClientConfigurationPacketListener,
TickablePacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    private final GameProfile localGameProfile;
    private FeatureFlagSet enabledFeatures;
    private final RegistryAccess.Frozen receivedRegistries;
    private final RegistryDataCollector registryDataCollector = new RegistryDataCollector();
    @Nullable
    private KnownPacksManager knownPacks;
    @Nullable
    protected ChatComponent.State chatState;

    public ClientConfigurationPacketListenerImpl(Minecraft $$0, Connection $$1, CommonListenerCookie $$2) {
        super($$0, $$1, $$2);
        this.localGameProfile = $$2.localGameProfile();
        this.receivedRegistries = $$2.receivedRegistries();
        this.enabledFeatures = $$2.enabledFeatures();
        this.chatState = $$2.chatState();
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    protected void handleCustomPayload(CustomPacketPayload $$0) {
        this.handleUnknownCustomPayload($$0);
    }

    private void handleUnknownCustomPayload(CustomPacketPayload $$0) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)$$0.type().id());
    }

    @Override
    public void handleRegistryData(ClientboundRegistryDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.registryDataCollector.appendContents($$0.registry(), $$0.entries());
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.registryDataCollector.appendTags($$0.getTags());
    }

    @Override
    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket $$0) {
        this.enabledFeatures = FeatureFlags.REGISTRY.fromNames($$0.features());
    }

    @Override
    public void handleSelectKnownPacks(ClientboundSelectKnownPacks $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.knownPacks == null) {
            this.knownPacks = new KnownPacksManager();
        }
        List<KnownPack> $$1 = this.knownPacks.trySelectingPacks($$0.knownPacks());
        this.send(new ServerboundSelectKnownPacks($$1));
    }

    @Override
    public void handleResetChat(ClientboundResetChatPacket $$0) {
        this.chatState = null;
    }

    private <T> T runWithResources(Function<ResourceProvider, T> $$0) {
        if (this.knownPacks == null) {
            return $$0.apply(ResourceProvider.EMPTY);
        }
        try (CloseableResourceManager $$1 = this.knownPacks.createResourceManager();){
            T t = $$0.apply($$1);
            return t;
        }
    }

    @Override
    public void handleConfigurationFinished(ClientboundFinishConfigurationPacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        RegistryAccess.Frozen $$1 = this.runWithResources($$0 -> this.registryDataCollector.collectGameRegistries((ResourceProvider)$$0, this.receivedRegistries, this.connection.isMemoryConnection()));
        this.connection.setupInboundProtocol(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator($$1)), new ClientPacketListener(this.minecraft, this.connection, new CommonListenerCookie(this.localGameProfile, this.telemetryManager, $$1, this.enabledFeatures, this.serverBrand, this.serverData, this.postDisconnectScreen, this.serverCookies, this.chatState, this.customReportDetails, this.serverLinks())));
        this.connection.send(ServerboundFinishConfigurationPacket.INSTANCE);
        this.connection.setupOutboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator($$1), new GameProtocols.Context(this){

            @Override
            public boolean hasInfiniteMaterials() {
                return true;
            }
        }));
    }

    @Override
    public void tick() {
        this.sendDeferredPackets();
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
        super.onDisconnect($$0);
        this.minecraft.clearDownloadedResourcePacks();
    }

    @Override
    protected DialogConnectionAccess createDialogAccess() {
        return new DialogConnectionAccess(){

            @Override
            public void disconnect(Component $$0) {
                ClientConfigurationPacketListenerImpl.this.connection.disconnect($$0);
            }

            @Override
            public void runCommand(String $$0, @Nullable Screen $$1) {
                LOGGER.warn("Commands are not supported in configuration phase, trying to run '{}'", (Object)$$0);
            }

            @Override
            public void openDialog(Holder<Dialog> $$0, @Nullable Screen $$1) {
                ClientConfigurationPacketListenerImpl.this.showDialog($$0, this, $$1);
            }

            @Override
            public void sendCustomAction(ResourceLocation $$0, Optional<Tag> $$1) {
                ClientConfigurationPacketListenerImpl.this.send(new ServerboundCustomClickActionPacket($$0, $$1));
            }

            @Override
            public ServerLinks serverLinks() {
                return ClientConfigurationPacketListenerImpl.this.serverLinks();
            }
        };
    }
}

