/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.dialog.DialogScreens;
import net.minecraft.client.gui.screens.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundCustomReportDetailsPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import org.slf4j.Logger;

public abstract class ClientCommonPacketListenerImpl
implements ClientCommonPacketListener {
    private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Minecraft minecraft;
    protected final Connection connection;
    @Nullable
    protected final ServerData serverData;
    @Nullable
    protected String serverBrand;
    protected final WorldSessionTelemetryManager telemetryManager;
    @Nullable
    protected final Screen postDisconnectScreen;
    protected boolean isTransferring;
    private final List<DeferredPacket> deferredPackets = new ArrayList<DeferredPacket>();
    protected final Map<ResourceLocation, byte[]> serverCookies;
    protected Map<String, String> customReportDetails;
    private ServerLinks serverLinks;

    protected ClientCommonPacketListenerImpl(Minecraft $$0, Connection $$1, CommonListenerCookie $$2) {
        this.minecraft = $$0;
        this.connection = $$1;
        this.serverData = $$2.serverData();
        this.serverBrand = $$2.serverBrand();
        this.telemetryManager = $$2.telemetryManager();
        this.postDisconnectScreen = $$2.postDisconnectScreen();
        this.serverCookies = $$2.serverCookies();
        this.customReportDetails = $$2.customReportDetails();
        this.serverLinks = $$2.serverLinks();
    }

    public ServerLinks serverLinks() {
        return this.serverLinks;
    }

    @Override
    public void onPacketError(Packet $$0, Exception $$1) {
        LOGGER.error("Failed to handle packet {}, disconnecting", (Object)$$0, (Object)$$1);
        Optional<Path> $$2 = this.storeDisconnectionReport($$0, $$1);
        Optional<URI> $$3 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT).map(ServerLinks.Entry::link);
        this.connection.disconnect(new DisconnectionDetails(Component.translatable("disconnect.packetError"), $$2, $$3));
    }

    @Override
    public DisconnectionDetails createDisconnectionInfo(Component $$0, Throwable $$1) {
        Optional<Path> $$2 = this.storeDisconnectionReport(null, $$1);
        Optional<URI> $$3 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT).map(ServerLinks.Entry::link);
        return new DisconnectionDetails($$0, $$2, $$3);
    }

    private Optional<Path> storeDisconnectionReport(@Nullable Packet $$02, Throwable $$1) {
        CrashReport $$2 = CrashReport.forThrowable($$1, "Packet handling error");
        PacketUtils.fillCrashReport($$2, this, $$02);
        Path $$3 = this.minecraft.gameDirectory.toPath().resolve("debug");
        Path $$4 = $$3.resolve("disconnect-" + Util.getFilenameFormattedDateTime() + "-client.txt");
        Optional<ServerLinks.Entry> $$5 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT);
        List $$6 = $$5.map($$0 -> List.of((Object)("Server bug reporting link: " + String.valueOf($$0.link())))).orElse(List.of());
        if ($$2.saveToFile($$4, ReportType.NETWORK_PROTOCOL_ERROR, $$6)) {
            return Optional.of($$4);
        }
        return Optional.empty();
    }

    @Override
    public boolean shouldHandleMessage(Packet<?> $$0) {
        if (ClientCommonPacketListener.super.shouldHandleMessage($$0)) {
            return true;
        }
        return this.isTransferring && ($$0 instanceof ClientboundStoreCookiePacket || $$0 instanceof ClientboundTransferPacket);
    }

    @Override
    public void handleKeepAlive(ClientboundKeepAlivePacket $$0) {
        this.sendWhen(new ServerboundKeepAlivePacket($$0.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
    }

    @Override
    public void handlePing(ClientboundPingPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.send(new ServerboundPongPacket($$0.getId()));
    }

    @Override
    public void handleCustomPayload(ClientboundCustomPayloadPacket $$0) {
        CustomPacketPayload $$1 = $$0.payload();
        if ($$1 instanceof DiscardedPayload) {
            return;
        }
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$1 instanceof BrandPayload) {
            BrandPayload $$2 = (BrandPayload)$$1;
            this.serverBrand = $$2.brand();
            this.telemetryManager.onServerBrandReceived($$2.brand());
        } else {
            this.handleCustomPayload($$1);
        }
    }

    protected abstract void handleCustomPayload(CustomPacketPayload var1);

    @Override
    public void handleResourcePackPush(ClientboundResourcePackPushPacket $$0) {
        ServerData.ServerPackStatus $$5;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        UUID $$1 = $$0.id();
        URL $$2 = ClientCommonPacketListenerImpl.parseResourcePackUrl($$0.url());
        if ($$2 == null) {
            this.connection.send(new ServerboundResourcePackPacket($$1, ServerboundResourcePackPacket.Action.INVALID_URL));
            return;
        }
        String $$3 = $$0.hash();
        boolean $$4 = $$0.required();
        ServerData.ServerPackStatus serverPackStatus = $$5 = this.serverData != null ? this.serverData.getResourcePackStatus() : ServerData.ServerPackStatus.PROMPT;
        if ($$5 == ServerData.ServerPackStatus.PROMPT || $$4 && $$5 == ServerData.ServerPackStatus.DISABLED) {
            this.minecraft.setScreen(this.addOrUpdatePackPrompt($$1, $$2, $$3, $$4, $$0.prompt().orElse(null)));
        } else {
            this.minecraft.getDownloadedPackSource().pushPack($$1, $$2, $$3);
        }
    }

    @Override
    public void handleResourcePackPop(ClientboundResourcePackPopPacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        $$02.id().ifPresentOrElse($$0 -> this.minecraft.getDownloadedPackSource().popPack((UUID)$$0), () -> this.minecraft.getDownloadedPackSource().popAll());
    }

    static Component preparePackPrompt(Component $$0, @Nullable Component $$1) {
        if ($$1 == null) {
            return $$0;
        }
        return Component.a("multiplayer.texturePrompt.serverPrompt", $$0, $$1);
    }

    @Nullable
    private static URL parseResourcePackUrl(String $$0) {
        try {
            URL $$1 = new URL($$0);
            String $$2 = $$1.getProtocol();
            if ("http".equals($$2) || "https".equals($$2)) {
                return $$1;
            }
        } catch (MalformedURLException $$3) {
            return null;
        }
        return null;
    }

    @Override
    public void handleRequestCookie(ClientboundCookieRequestPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.connection.send(new ServerboundCookieResponsePacket($$0.key(), this.serverCookies.get($$0.key())));
    }

    @Override
    public void handleStoreCookie(ClientboundStoreCookiePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.serverCookies.put($$0.key(), $$0.e());
    }

    @Override
    public void handleCustomReportDetails(ClientboundCustomReportDetailsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.customReportDetails = $$0.details();
    }

    @Override
    public void handleServerLinks(ClientboundServerLinksPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        List<ServerLinks.UntrustedEntry> $$1 = $$0.links();
        ImmutableList.Builder $$2 = ImmutableList.builderWithExpectedSize($$1.size());
        for (ServerLinks.UntrustedEntry $$3 : $$1) {
            try {
                URI $$4 = Util.parseAndValidateUntrustedUri($$3.link());
                $$2.add((Object)new ServerLinks.Entry($$3.type(), $$4));
            } catch (Exception $$5) {
                LOGGER.warn("Received invalid link for type {}:{}", $$3.type(), $$3.link(), $$5);
            }
        }
        this.serverLinks = new ServerLinks((List<ServerLinks.Entry>)((Object)$$2.build()));
    }

    @Override
    public void handleShowDialog(ClientboundShowDialogPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.showDialog($$0.dialog(), this.minecraft.screen);
    }

    protected abstract DialogConnectionAccess createDialogAccess();

    public void showDialog(Holder<Dialog> $$0, @Nullable Screen $$1) {
        this.showDialog($$0, this.createDialogAccess(), $$1);
    }

    protected void showDialog(Holder<Dialog> $$0, DialogConnectionAccess $$1, @Nullable Screen $$2) {
        Screen $$12;
        if ($$2 instanceof DialogScreen.WarningScreen) {
            Screen screen;
            DialogScreen.WarningScreen $$3 = (DialogScreen.WarningScreen)$$2;
            Screen $$4 = $$3.returnScreen();
            if ($$4 instanceof DialogScreen) {
                DialogScreen $$5 = (DialogScreen)$$4;
                screen = $$5.previousScreen();
            } else {
                screen = $$4;
            }
            Screen $$6 = screen;
            DialogScreen<Dialog> $$7 = DialogScreens.createFromData($$0.value(), $$6, $$1);
            if ($$7 != null) {
                $$3.updateReturnScreen($$7);
            } else {
                LOGGER.warn("Failed to show dialog for data {}", (Object)$$0);
            }
            return;
        }
        if ($$2 instanceof DialogScreen) {
            DialogScreen $$8 = (DialogScreen)$$2;
            Screen $$9 = $$8.previousScreen();
        } else if ($$2 instanceof WaitingForResponseScreen) {
            WaitingForResponseScreen $$10 = (WaitingForResponseScreen)$$2;
            Screen $$11 = $$10.previousScreen();
        } else {
            $$12 = $$2;
        }
        DialogScreen<Dialog> $$13 = DialogScreens.createFromData($$0.value(), $$12, $$1);
        if ($$13 != null) {
            this.minecraft.setScreen($$13);
        } else {
            LOGGER.warn("Failed to show dialog for data {}", (Object)$$0);
        }
    }

    @Override
    public void handleClearDialog(ClientboundClearDialogPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.clearDialog();
    }

    public void clearDialog() {
        Screen screen = this.minecraft.screen;
        if (screen instanceof DialogScreen.WarningScreen) {
            DialogScreen.WarningScreen $$0 = (DialogScreen.WarningScreen)screen;
            Screen $$1 = $$0.returnScreen();
            if ($$1 instanceof DialogScreen) {
                DialogScreen $$2 = (DialogScreen)$$1;
                $$0.updateReturnScreen($$2.previousScreen());
            }
        } else {
            screen = this.minecraft.screen;
            if (screen instanceof DialogScreen) {
                DialogScreen $$3 = (DialogScreen)screen;
                this.minecraft.setScreen($$3.previousScreen());
            }
        }
    }

    @Override
    public void handleTransfer(ClientboundTransferPacket $$0) {
        this.isTransferring = true;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.serverData == null) {
            throw new IllegalStateException("Cannot transfer to server from singleplayer");
        }
        this.connection.disconnect(Component.translatable("disconnect.transfer"));
        this.connection.setReadOnly();
        this.connection.handleDisconnection();
        ServerAddress $$1 = new ServerAddress($$0.host(), $$0.port());
        ConnectScreen.startConnecting((Screen)Objects.requireNonNullElseGet((Object)this.postDisconnectScreen, TitleScreen::new), this.minecraft, $$1, this.serverData, false, new TransferState(this.serverCookies));
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket $$0) {
        this.connection.disconnect($$0.reason());
    }

    protected void sendDeferredPackets() {
        Iterator<DeferredPacket> $$0 = this.deferredPackets.iterator();
        while ($$0.hasNext()) {
            DeferredPacket $$1 = $$0.next();
            if ($$1.sendCondition().getAsBoolean()) {
                this.send($$1.packet);
                $$0.remove();
                continue;
            }
            if ($$1.expirationTime() > Util.getMillis()) continue;
            $$0.remove();
        }
    }

    public void send(Packet<?> $$0) {
        this.connection.send($$0);
    }

    @Override
    public void onDisconnect(DisconnectionDetails $$0) {
        this.telemetryManager.onDisconnect();
        this.minecraft.disconnect(this.createDisconnectScreen($$0), this.isTransferring);
        LOGGER.warn("Client disconnected with reason: {}", (Object)$$0.reason().getString());
    }

    @Override
    public void fillListenerSpecificCrashDetails(CrashReport $$0, CrashReportCategory $$1) {
        $$1.setDetail("Is Local", () -> String.valueOf(this.connection.isMemoryConnection()));
        $$1.setDetail("Server type", () -> this.serverData != null ? this.serverData.type().toString() : "<none>");
        $$1.setDetail("Server brand", () -> this.serverBrand);
        if (!this.customReportDetails.isEmpty()) {
            CrashReportCategory $$2 = $$0.addCategory("Custom Server Details");
            this.customReportDetails.forEach($$2::setDetail);
        }
    }

    protected Screen createDisconnectScreen(DisconnectionDetails $$0) {
        Screen $$1 = (Screen)Objects.requireNonNullElseGet((Object)this.postDisconnectScreen, () -> new JoinMultiplayerScreen(new TitleScreen()));
        if (this.serverData != null && this.serverData.isRealm()) {
            return new DisconnectedScreen($$1, GENERIC_DISCONNECT_MESSAGE, $$0, CommonComponents.GUI_BACK);
        }
        return new DisconnectedScreen($$1, GENERIC_DISCONNECT_MESSAGE, $$0);
    }

    @Nullable
    public String serverBrand() {
        return this.serverBrand;
    }

    private void sendWhen(Packet<? extends ServerboundPacketListener> $$0, BooleanSupplier $$1, Duration $$2) {
        if ($$1.getAsBoolean()) {
            this.send($$0);
        } else {
            this.deferredPackets.add(new DeferredPacket($$0, $$1, Util.getMillis() + $$2.toMillis()));
        }
    }

    private Screen addOrUpdatePackPrompt(UUID $$0, URL $$1, String $$2, boolean $$3, @Nullable Component $$4) {
        Screen $$5 = this.minecraft.screen;
        if ($$5 instanceof PackConfirmScreen) {
            PackConfirmScreen $$6 = (PackConfirmScreen)$$5;
            return $$6.update(this.minecraft, $$0, $$1, $$2, $$3, $$4);
        }
        return new PackConfirmScreen(this.minecraft, $$5, List.of((Object)((Object)new PackConfirmScreen.PendingRequest($$0, $$1, $$2))), $$3, $$4);
    }

    static final class DeferredPacket
    extends Record {
        final Packet<? extends ServerboundPacketListener> packet;
        private final BooleanSupplier sendCondition;
        private final long expirationTime;

        DeferredPacket(Packet<? extends ServerboundPacketListener> $$0, BooleanSupplier $$1, long $$2) {
            this.packet = $$0;
            this.sendCondition = $$1;
            this.expirationTime = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DeferredPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DeferredPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DeferredPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this, $$0);
        }

        public Packet<? extends ServerboundPacketListener> packet() {
            return this.packet;
        }

        public BooleanSupplier sendCondition() {
            return this.sendCondition;
        }

        public long expirationTime() {
            return this.expirationTime;
        }
    }

    class PackConfirmScreen
    extends ConfirmScreen {
        private final List<PendingRequest> requests;
        @Nullable
        private final Screen parentScreen;

        PackConfirmScreen(@Nullable Minecraft $$0, Screen $$1, List<PendingRequest> $$2, @Nullable boolean $$3, Component $$4) {
            super($$5 -> {
                $$0.setScreen($$1);
                DownloadedPackSource $$6 = $$0.getDownloadedPackSource();
                if ($$5) {
                    if ($$4.serverData != null) {
                        $$4.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                    }
                    $$6.allowServerPacks();
                } else {
                    $$6.rejectServerPacks();
                    if ($$3) {
                        $$4.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                    } else if ($$4.serverData != null) {
                        $$4.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                    }
                }
                for (PendingRequest $$7 : $$2) {
                    $$6.pushPack($$7.id, $$7.url, $$7.hash);
                }
                if ($$4.serverData != null) {
                    ServerList.saveSingleServer($$4.serverData);
                }
            }, $$3 ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"), ClientCommonPacketListenerImpl.preparePackPrompt($$3 ? Component.translatable("multiplayer.requiredTexturePrompt.line2").a(ChatFormatting.YELLOW, ChatFormatting.BOLD) : Component.translatable("multiplayer.texturePrompt.line2"), $$4), $$3 ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES, $$3 ? CommonComponents.GUI_DISCONNECT : CommonComponents.GUI_NO);
            this.requests = $$2;
            this.parentScreen = $$1;
        }

        public PackConfirmScreen update(Minecraft $$0, UUID $$1, URL $$2, String $$3, boolean $$4, @Nullable Component $$5) {
            ImmutableCollection $$6 = ((ImmutableList.Builder)((ImmutableList.Builder)ImmutableList.builderWithExpectedSize(this.requests.size() + 1).addAll(this.requests)).add((Object)new PendingRequest($$1, $$2, $$3))).build();
            return new PackConfirmScreen($$0, this.parentScreen, (List<PendingRequest>)((Object)$$6), $$4, $$5);
        }

        static final class PendingRequest
        extends Record {
            final UUID id;
            final URL url;
            final String hash;

            PendingRequest(UUID $$0, URL $$1, String $$2) {
                this.id = $$0;
                this.url = $$1;
                this.hash = $$2;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{PendingRequest.class, "id;url;hash", "id", "url", "hash"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PendingRequest.class, "id;url;hash", "id", "url", "hash"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PendingRequest.class, "id;url;hash", "id", "url", "hash"}, this, $$0);
            }

            public UUID id() {
                return this.id;
            }

            public URL url() {
                return this.url;
            }

            public String hash() {
                return this.hash;
            }
        }
    }
}

