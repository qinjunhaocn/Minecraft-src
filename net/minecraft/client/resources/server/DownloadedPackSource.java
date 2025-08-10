/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UndashedUuid
 *  java.lang.MatchException
 */
package net.minecraft.client.resources.server;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.util.UndashedUuid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.resources.server.PackDownloader;
import net.minecraft.client.resources.server.PackLoadFeedback;
import net.minecraft.client.resources.server.PackReloadConfig;
import net.minecraft.client.resources.server.ServerPackManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.packs.DownloadQueue;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;

public class DownloadedPackSource
implements AutoCloseable {
    private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final RepositorySource EMPTY_SOURCE = $$0 -> {};
    private static final PackSelectionConfig DOWNLOADED_PACK_SELECTION = new PackSelectionConfig(true, Pack.Position.TOP, true);
    private static final PackLoadFeedback LOG_ONLY_FEEDBACK = new PackLoadFeedback(){

        @Override
        public void reportUpdate(UUID $$0, PackLoadFeedback.Update $$1) {
            LOGGER.debug("Downloaded pack {} changed state to {}", (Object)$$0, (Object)$$1);
        }

        @Override
        public void reportFinalResult(UUID $$0, PackLoadFeedback.FinalResult $$1) {
            LOGGER.debug("Downloaded pack {} finished with state {}", (Object)$$0, (Object)$$1);
        }
    };
    final Minecraft minecraft;
    private RepositorySource packSource = EMPTY_SOURCE;
    @Nullable
    private PackReloadConfig.Callbacks pendingReload;
    final ServerPackManager manager;
    private final DownloadQueue downloadQueue;
    private PackSource packType = PackSource.SERVER;
    PackLoadFeedback packFeedback = LOG_ONLY_FEEDBACK;
    private int packIdSerialNumber;

    public DownloadedPackSource(Minecraft $$0, Path $$1, GameConfig.UserData $$2) {
        this.minecraft = $$0;
        try {
            this.downloadQueue = new DownloadQueue($$1);
        } catch (IOException $$3) {
            throw new UncheckedIOException("Failed to open download queue in directory " + String.valueOf($$1), $$3);
        }
        Executor $$4 = $$0::schedule;
        this.manager = new ServerPackManager(this.createDownloader(this.downloadQueue, $$4, $$2.user, $$2.proxy), new PackLoadFeedback(){

            @Override
            public void reportUpdate(UUID $$0, PackLoadFeedback.Update $$1) {
                DownloadedPackSource.this.packFeedback.reportUpdate($$0, $$1);
            }

            @Override
            public void reportFinalResult(UUID $$0, PackLoadFeedback.FinalResult $$1) {
                DownloadedPackSource.this.packFeedback.reportFinalResult($$0, $$1);
            }
        }, this.createReloadConfig(), this.createUpdateScheduler($$4), ServerPackManager.PackPromptStatus.PENDING);
    }

    HttpUtil.DownloadProgressListener createDownloadNotifier(final int $$0) {
        return new HttpUtil.DownloadProgressListener(){
            private final SystemToast.SystemToastId toastId = new SystemToast.SystemToastId();
            private Component title = Component.empty();
            @Nullable
            private Component message = null;
            private int count;
            private int failCount;
            private OptionalLong totalBytes = OptionalLong.empty();

            private void updateToast() {
                DownloadedPackSource.this.minecraft.execute(() -> SystemToast.addOrUpdate(DownloadedPackSource.this.minecraft.getToastManager(), this.toastId, this.title, this.message));
            }

            private void updateProgress(long $$02) {
                this.message = this.totalBytes.isPresent() ? Component.a("download.pack.progress.percent", $$02 * 100L / this.totalBytes.getAsLong()) : Component.a("download.pack.progress.bytes", Unit.humanReadable($$02));
                this.updateToast();
            }

            @Override
            public void requestStart() {
                ++this.count;
                this.title = Component.a("download.pack.title", this.count, $$0);
                this.updateToast();
                LOGGER.debug("Starting pack {}/{} download", (Object)this.count, (Object)$$0);
            }

            @Override
            public void downloadStart(OptionalLong $$02) {
                LOGGER.debug("File size = {} bytes", (Object)$$02);
                this.totalBytes = $$02;
                this.updateProgress(0L);
            }

            @Override
            public void downloadedBytes(long $$02) {
                LOGGER.debug("Progress for pack {}: {} bytes", (Object)this.count, (Object)$$02);
                this.updateProgress($$02);
            }

            @Override
            public void requestFinished(boolean $$02) {
                if (!$$02) {
                    LOGGER.info("Pack {} failed to download", (Object)this.count);
                    ++this.failCount;
                } else {
                    LOGGER.debug("Download ended for pack {}", (Object)this.count);
                }
                if (this.count == $$0) {
                    if (this.failCount > 0) {
                        this.title = Component.a("download.pack.failed", this.failCount, $$0);
                        this.message = null;
                        this.updateToast();
                    } else {
                        SystemToast.forceHide(DownloadedPackSource.this.minecraft.getToastManager(), this.toastId);
                    }
                }
            }
        };
    }

    private PackDownloader createDownloader(final DownloadQueue $$0, final Executor $$1, final User $$2, final Proxy $$3) {
        return new PackDownloader(){
            private static final int MAX_PACK_SIZE_BYTES = 0xFA00000;
            private static final HashFunction CACHE_HASHING_FUNCTION = Hashing.sha1();

            private Map<String, String> createDownloadHeaders() {
                WorldVersion $$02 = SharedConstants.getCurrentVersion();
                return Map.of((Object)"X-Minecraft-Username", (Object)$$2.getName(), (Object)"X-Minecraft-UUID", (Object)UndashedUuid.toString((UUID)$$2.getProfileId()), (Object)"X-Minecraft-Version", (Object)$$02.name(), (Object)"X-Minecraft-Version-ID", (Object)$$02.id(), (Object)"X-Minecraft-Pack-Format", (Object)String.valueOf($$02.packVersion(PackType.CLIENT_RESOURCES)), (Object)"User-Agent", (Object)("Minecraft Java/" + $$02.name()));
            }

            @Override
            public void download(Map<UUID, DownloadQueue.DownloadRequest> $$02, Consumer<DownloadQueue.BatchResult> $$12) {
                $$0.downloadBatch(new DownloadQueue.BatchConfig(CACHE_HASHING_FUNCTION, 0xFA00000, this.createDownloadHeaders(), $$3, DownloadedPackSource.this.createDownloadNotifier($$02.size())), $$02).thenAcceptAsync((Consumer)$$12, $$1);
            }
        };
    }

    private Runnable createUpdateScheduler(final Executor $$0) {
        return new Runnable(){
            private boolean scheduledInMainExecutor;
            private boolean hasUpdates;

            @Override
            public void run() {
                this.hasUpdates = true;
                if (!this.scheduledInMainExecutor) {
                    this.scheduledInMainExecutor = true;
                    $$0.execute(this::runAllUpdates);
                }
            }

            private void runAllUpdates() {
                while (this.hasUpdates) {
                    this.hasUpdates = false;
                    DownloadedPackSource.this.manager.tick();
                }
                this.scheduledInMainExecutor = false;
            }
        };
    }

    private PackReloadConfig createReloadConfig() {
        return this::startReload;
    }

    @Nullable
    private List<Pack> loadRequestedPacks(List<PackReloadConfig.IdAndPath> $$0) {
        ArrayList<Pack> $$1 = new ArrayList<Pack>($$0.size());
        for (PackReloadConfig.IdAndPath $$2 : Lists.reverse($$0)) {
            int $$7;
            FilePackResources.FileResourcesSupplier $$6;
            String $$3 = String.format(Locale.ROOT, "server/%08X/%s", this.packIdSerialNumber++, $$2.id());
            Path $$4 = $$2.path();
            PackLocationInfo $$5 = new PackLocationInfo($$3, SERVER_NAME, this.packType, Optional.empty());
            Pack.Metadata $$8 = Pack.readPackMetadata($$5, $$6 = new FilePackResources.FileResourcesSupplier($$4), $$7 = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES));
            if ($$8 == null) {
                LOGGER.warn("Invalid pack metadata in {}, ignoring all", (Object)$$4);
                return null;
            }
            $$1.add(new Pack($$5, $$6, $$8, DOWNLOADED_PACK_SELECTION));
        }
        return $$1;
    }

    public RepositorySource createRepositorySource() {
        return $$0 -> this.packSource.loadPacks($$0);
    }

    private static RepositorySource configureSource(List<Pack> $$0) {
        if ($$0.isEmpty()) {
            return EMPTY_SOURCE;
        }
        return $$0::forEach;
    }

    private void startReload(PackReloadConfig.Callbacks $$0) {
        this.pendingReload = $$0;
        List<PackReloadConfig.IdAndPath> $$1 = $$0.packsToLoad();
        List $$2 = this.loadRequestedPacks($$1);
        if ($$2 == null) {
            $$0.onFailure(false);
            List<PackReloadConfig.IdAndPath> $$3 = $$0.packsToLoad();
            $$2 = this.loadRequestedPacks($$3);
            if ($$2 == null) {
                LOGGER.warn("Double failure in loading server packs");
                $$2 = List.of();
            }
        }
        this.packSource = DownloadedPackSource.configureSource($$2);
        this.minecraft.reloadResourcePacks();
    }

    public void onRecovery() {
        if (this.pendingReload != null) {
            this.pendingReload.onFailure(false);
            List $$0 = this.loadRequestedPacks(this.pendingReload.packsToLoad());
            if ($$0 == null) {
                LOGGER.warn("Double failure in loading server packs");
                $$0 = List.of();
            }
            this.packSource = DownloadedPackSource.configureSource($$0);
        }
    }

    public void onRecoveryFailure() {
        if (this.pendingReload != null) {
            this.pendingReload.onFailure(true);
            this.pendingReload = null;
            this.packSource = EMPTY_SOURCE;
        }
    }

    public void onReloadSuccess() {
        if (this.pendingReload != null) {
            this.pendingReload.onSuccess();
            this.pendingReload = null;
        }
    }

    @Nullable
    private static HashCode tryParseSha1Hash(@Nullable String $$0) {
        if ($$0 != null && SHA1.matcher($$0).matches()) {
            return HashCode.fromString($$0.toLowerCase(Locale.ROOT));
        }
        return null;
    }

    public void pushPack(UUID $$0, URL $$1, @Nullable String $$2) {
        HashCode $$3 = DownloadedPackSource.tryParseSha1Hash($$2);
        this.manager.pushPack($$0, $$1, $$3);
    }

    public void pushLocalPack(UUID $$0, Path $$1) {
        this.manager.pushLocalPack($$0, $$1);
    }

    public void popPack(UUID $$0) {
        this.manager.popPack($$0);
    }

    public void popAll() {
        this.manager.popAll();
    }

    private static PackLoadFeedback createPackResponseSender(final Connection $$0) {
        return new PackLoadFeedback(){

            @Override
            public void reportUpdate(UUID $$02, PackLoadFeedback.Update $$1) {
                LOGGER.debug("Pack {} changed status to {}", (Object)$$02, (Object)$$1);
                ServerboundResourcePackPacket.Action $$2 = switch ($$1) {
                    default -> throw new MatchException(null, null);
                    case PackLoadFeedback.Update.ACCEPTED -> ServerboundResourcePackPacket.Action.ACCEPTED;
                    case PackLoadFeedback.Update.DOWNLOADED -> ServerboundResourcePackPacket.Action.DOWNLOADED;
                };
                $$0.send(new ServerboundResourcePackPacket($$02, $$2));
            }

            @Override
            public void reportFinalResult(UUID $$02, PackLoadFeedback.FinalResult $$1) {
                LOGGER.debug("Pack {} changed status to {}", (Object)$$02, (Object)$$1);
                ServerboundResourcePackPacket.Action $$2 = switch ($$1) {
                    default -> throw new MatchException(null, null);
                    case PackLoadFeedback.FinalResult.APPLIED -> ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;
                    case PackLoadFeedback.FinalResult.DOWNLOAD_FAILED -> ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD;
                    case PackLoadFeedback.FinalResult.DECLINED -> ServerboundResourcePackPacket.Action.DECLINED;
                    case PackLoadFeedback.FinalResult.DISCARDED -> ServerboundResourcePackPacket.Action.DISCARDED;
                    case PackLoadFeedback.FinalResult.ACTIVATION_FAILED -> ServerboundResourcePackPacket.Action.FAILED_RELOAD;
                };
                $$0.send(new ServerboundResourcePackPacket($$02, $$2));
            }
        };
    }

    public void configureForServerControl(Connection $$0, ServerPackManager.PackPromptStatus $$1) {
        this.packType = PackSource.SERVER;
        this.packFeedback = DownloadedPackSource.createPackResponseSender($$0);
        switch ($$1) {
            case ALLOWED: {
                this.manager.allowServerPacks();
                break;
            }
            case DECLINED: {
                this.manager.rejectServerPacks();
                break;
            }
            case PENDING: {
                this.manager.resetPromptStatus();
            }
        }
    }

    public void configureForLocalWorld() {
        this.packType = PackSource.WORLD;
        this.packFeedback = LOG_ONLY_FEEDBACK;
        this.manager.allowServerPacks();
    }

    public void allowServerPacks() {
        this.manager.allowServerPacks();
    }

    public void rejectServerPacks() {
        this.manager.rejectServerPacks();
    }

    public CompletableFuture<Void> waitForPackFeedback(final UUID $$0) {
        final CompletableFuture<Void> $$1 = new CompletableFuture<Void>();
        final PackLoadFeedback $$2 = this.packFeedback;
        this.packFeedback = new PackLoadFeedback(){

            @Override
            public void reportUpdate(UUID $$02, PackLoadFeedback.Update $$12) {
                $$2.reportUpdate($$02, $$12);
            }

            @Override
            public void reportFinalResult(UUID $$02, PackLoadFeedback.FinalResult $$12) {
                if ($$0.equals($$02)) {
                    DownloadedPackSource.this.packFeedback = $$2;
                    if ($$12 == PackLoadFeedback.FinalResult.APPLIED) {
                        $$1.complete(null);
                    } else {
                        $$1.completeExceptionally(new IllegalStateException("Failed to apply pack " + String.valueOf($$02) + ", reason: " + String.valueOf((Object)$$12)));
                    }
                }
                $$2.reportFinalResult($$02, $$12);
            }
        };
        return $$1;
    }

    public void cleanupAfterDisconnect() {
        this.manager.popAll();
        this.packFeedback = LOG_ONLY_FEEDBACK;
        this.manager.resetPromptStatus();
    }

    @Override
    public void close() throws IOException {
        this.downloadQueue.close();
    }
}

