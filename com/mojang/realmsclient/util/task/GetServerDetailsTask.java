/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsJoinInformation;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoConnectTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import com.mojang.realmsclient.util.task.ConnectTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class GetServerDetailsTask
extends LongRunningTask {
    private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.connect.connecting");
    private final RealmsServer server;
    private final Screen lastScreen;

    public GetServerDetailsTask(Screen $$0, RealmsServer $$1) {
        this.lastScreen = $$0;
        this.server = $$1;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void run() {
        void $$6;
        try {
            RealmsJoinInformation $$0 = this.fetchServerAddress();
        } catch (CancellationException $$1) {
            LOGGER.info("User aborted connecting to realms");
            return;
        } catch (RealmsServiceException $$2) {
            switch ($$2.realmsError.errorCode()) {
                case 6002: {
                    GetServerDetailsTask.setScreen(new RealmsTermsScreen(this.lastScreen, this.server));
                    return;
                }
                case 6006: {
                    boolean $$3 = Minecraft.getInstance().isLocalPlayer(this.server.ownerUUID);
                    GetServerDetailsTask.setScreen($$3 ? new RealmsBrokenWorldScreen(this.lastScreen, this.server.id, this.server.isMinigameActive()) : new RealmsGenericErrorScreen(Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen));
                    return;
                }
            }
            this.error($$2);
            LOGGER.error("Couldn't connect to world", $$2);
            return;
        } catch (TimeoutException $$4) {
            this.error(Component.translatable("mco.errorMessage.connectionFailure"));
            return;
        } catch (Exception $$5) {
            LOGGER.error("Couldn't connect to world", $$5);
            this.error($$5);
            return;
        }
        if ($$6.address() == null) {
            this.error(Component.translatable("mco.errorMessage.connectionFailure"));
            return;
        }
        boolean $$7 = $$6.resourcePackUrl() != null && $$6.resourcePackHash() != null;
        RealmsLongRunningMcoTaskScreen $$8 = $$7 ? this.resourcePackDownloadConfirmationScreen((RealmsJoinInformation)$$6, GetServerDetailsTask.generatePackId(this.server), this::connectScreen) : this.connectScreen((RealmsJoinInformation)$$6);
        GetServerDetailsTask.setScreen($$8);
    }

    private static UUID generatePackId(RealmsServer $$0) {
        if ($$0.minigameName != null) {
            return UUID.nameUUIDFromBytes(("minigame:" + $$0.minigameName).getBytes(StandardCharsets.UTF_8));
        }
        return UUID.nameUUIDFromBytes(("realms:" + (String)Objects.requireNonNullElse((Object)$$0.name, (Object)"") + ":" + $$0.activeSlot).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    private RealmsJoinInformation fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
        RealmsClient $$0 = RealmsClient.getOrCreate();
        for (int $$1 = 0; $$1 < 40; ++$$1) {
            if (this.aborted()) {
                throw new CancellationException();
            }
            try {
                return $$0.join(this.server.id);
            } catch (RetryCallException $$2) {
                GetServerDetailsTask.pause($$2.delaySeconds);
                continue;
            }
        }
        throw new TimeoutException();
    }

    public RealmsLongRunningMcoTaskScreen connectScreen(RealmsJoinInformation $$0) {
        return new RealmsLongRunningMcoConnectTaskScreen(this.lastScreen, $$0, (LongRunningTask)new ConnectTask(this.lastScreen, this.server, $$0));
    }

    private PopupScreen resourcePackDownloadConfirmationScreen(RealmsJoinInformation $$0, UUID $$1, Function<RealmsJoinInformation, Screen> $$2) {
        MutableComponent $$32 = Component.translatable("mco.configure.world.resourcepack.question");
        return RealmsPopups.infoPopupScreen(this.lastScreen, $$32, $$3 -> {
            GetServerDetailsTask.setScreen(new GenericMessageScreen(APPLYING_PACK_TEXT));
            ((CompletableFuture)this.scheduleResourcePackDownload($$0, $$1).thenRun(() -> GetServerDetailsTask.setScreen((Screen)$$2.apply($$0)))).exceptionally($$1 -> {
                Minecraft.getInstance().getDownloadedPackSource().cleanupAfterDisconnect();
                LOGGER.error("Failed to download resource pack from {}", (Object)$$0, $$1);
                GetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.download.resourcePack.fail"), this.lastScreen));
                return null;
            });
        });
    }

    private CompletableFuture<?> scheduleResourcePackDownload(RealmsJoinInformation $$0, UUID $$1) {
        try {
            if ($$0.resourcePackUrl() == null) {
                return CompletableFuture.failedFuture((Throwable)new IllegalStateException("resourcePackUrl was null"));
            }
            if ($$0.resourcePackHash() == null) {
                return CompletableFuture.failedFuture((Throwable)new IllegalStateException("resourcePackHash was null"));
            }
            DownloadedPackSource $$2 = Minecraft.getInstance().getDownloadedPackSource();
            CompletableFuture<Void> $$3 = $$2.waitForPackFeedback($$1);
            $$2.allowServerPacks();
            $$2.pushPack($$1, new URL($$0.resourcePackUrl()), $$0.resourcePackHash());
            return $$3;
        } catch (Exception $$4) {
            return CompletableFuture.failedFuture((Throwable)$$4);
        }
    }
}

