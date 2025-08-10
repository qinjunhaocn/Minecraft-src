/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.server;

import com.google.common.hash.HashCode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.resources.server.PackDownloader;
import net.minecraft.client.resources.server.PackLoadFeedback;
import net.minecraft.client.resources.server.PackReloadConfig;
import net.minecraft.server.packs.DownloadQueue;

public class ServerPackManager {
    private final PackDownloader downloader;
    final PackLoadFeedback packLoadFeedback;
    private final PackReloadConfig reloadConfig;
    private final Runnable updateRequest;
    private PackPromptStatus packPromptStatus;
    final List<ServerPackData> packs = new ArrayList<ServerPackData>();

    public ServerPackManager(PackDownloader $$0, PackLoadFeedback $$1, PackReloadConfig $$2, Runnable $$3, PackPromptStatus $$4) {
        this.downloader = $$0;
        this.packLoadFeedback = $$1;
        this.reloadConfig = $$2;
        this.updateRequest = $$3;
        this.packPromptStatus = $$4;
    }

    void registerForUpdate() {
        this.updateRequest.run();
    }

    private void markExistingPacksAsRemoved(UUID $$0) {
        for (ServerPackData $$1 : this.packs) {
            if (!$$1.id.equals($$0)) continue;
            $$1.setRemovalReasonIfNotSet(RemovalReason.SERVER_REPLACED);
        }
    }

    public void pushPack(UUID $$0, URL $$1, @Nullable HashCode $$2) {
        if (this.packPromptStatus == PackPromptStatus.DECLINED) {
            this.packLoadFeedback.reportFinalResult($$0, PackLoadFeedback.FinalResult.DECLINED);
            return;
        }
        this.pushNewPack($$0, new ServerPackData($$0, $$1, $$2));
    }

    /*
     * WARNING - void declaration
     */
    public void pushLocalPack(UUID $$0, Path $$1) {
        void $$4;
        if (this.packPromptStatus == PackPromptStatus.DECLINED) {
            this.packLoadFeedback.reportFinalResult($$0, PackLoadFeedback.FinalResult.DECLINED);
            return;
        }
        try {
            URL $$2 = $$1.toUri().toURL();
        } catch (MalformedURLException $$3) {
            throw new IllegalStateException("Can't convert path to URL " + String.valueOf($$1), $$3);
        }
        ServerPackData $$5 = new ServerPackData($$0, (URL)$$4, null);
        $$5.downloadStatus = PackDownloadStatus.DONE;
        $$5.path = $$1;
        this.pushNewPack($$0, $$5);
    }

    private void pushNewPack(UUID $$0, ServerPackData $$1) {
        this.markExistingPacksAsRemoved($$0);
        this.packs.add($$1);
        if (this.packPromptStatus == PackPromptStatus.ALLOWED) {
            this.acceptPack($$1);
        }
        this.registerForUpdate();
    }

    private void acceptPack(ServerPackData $$0) {
        this.packLoadFeedback.reportUpdate($$0.id, PackLoadFeedback.Update.ACCEPTED);
        $$0.promptAccepted = true;
    }

    @Nullable
    private ServerPackData findPackInfo(UUID $$0) {
        for (ServerPackData $$1 : this.packs) {
            if ($$1.isRemoved() || !$$1.id.equals($$0)) continue;
            return $$1;
        }
        return null;
    }

    public void popPack(UUID $$0) {
        ServerPackData $$1 = this.findPackInfo($$0);
        if ($$1 != null) {
            $$1.setRemovalReasonIfNotSet(RemovalReason.SERVER_REMOVED);
            this.registerForUpdate();
        }
    }

    public void popAll() {
        for (ServerPackData $$0 : this.packs) {
            $$0.setRemovalReasonIfNotSet(RemovalReason.SERVER_REMOVED);
        }
        this.registerForUpdate();
    }

    public void allowServerPacks() {
        this.packPromptStatus = PackPromptStatus.ALLOWED;
        for (ServerPackData $$0 : this.packs) {
            if ($$0.promptAccepted || $$0.isRemoved()) continue;
            this.acceptPack($$0);
        }
        this.registerForUpdate();
    }

    public void rejectServerPacks() {
        this.packPromptStatus = PackPromptStatus.DECLINED;
        for (ServerPackData $$0 : this.packs) {
            if ($$0.promptAccepted) continue;
            $$0.setRemovalReasonIfNotSet(RemovalReason.DECLINED);
        }
        this.registerForUpdate();
    }

    public void resetPromptStatus() {
        this.packPromptStatus = PackPromptStatus.PENDING;
    }

    public void tick() {
        boolean $$0 = this.updateDownloads();
        if (!$$0) {
            this.triggerReloadIfNeeded();
        }
        this.cleanupRemovedPacks();
    }

    private void cleanupRemovedPacks() {
        this.packs.removeIf($$0 -> {
            if ($$0.activationStatus != ActivationStatus.INACTIVE) {
                return false;
            }
            if ($$0.removalReason != null) {
                PackLoadFeedback.FinalResult $$1 = $$0.removalReason.serverResponse;
                if ($$1 != null) {
                    this.packLoadFeedback.reportFinalResult($$0.id, $$1);
                }
                return true;
            }
            return false;
        });
    }

    private void onDownload(Collection<ServerPackData> $$0, DownloadQueue.BatchResult $$1) {
        if (!$$1.failed().isEmpty()) {
            for (ServerPackData $$2 : this.packs) {
                if ($$2.activationStatus == ActivationStatus.ACTIVE) continue;
                if ($$1.failed().contains($$2.id)) {
                    $$2.setRemovalReasonIfNotSet(RemovalReason.DOWNLOAD_FAILED);
                    continue;
                }
                $$2.setRemovalReasonIfNotSet(RemovalReason.DISCARDED);
            }
        }
        for (ServerPackData $$3 : $$0) {
            Path $$4 = $$1.downloaded().get($$3.id);
            if ($$4 == null) continue;
            $$3.downloadStatus = PackDownloadStatus.DONE;
            $$3.path = $$4;
            if ($$3.isRemoved()) continue;
            this.packLoadFeedback.reportUpdate($$3.id, PackLoadFeedback.Update.DOWNLOADED);
        }
        this.registerForUpdate();
    }

    private boolean updateDownloads() {
        ArrayList<ServerPackData> $$0 = new ArrayList<ServerPackData>();
        boolean $$12 = false;
        for (ServerPackData $$2 : this.packs) {
            if ($$2.isRemoved() || !$$2.promptAccepted) continue;
            if ($$2.downloadStatus != PackDownloadStatus.DONE) {
                $$12 = true;
            }
            if ($$2.downloadStatus != PackDownloadStatus.REQUESTED) continue;
            $$2.downloadStatus = PackDownloadStatus.PENDING;
            $$0.add($$2);
        }
        if (!$$0.isEmpty()) {
            HashMap<UUID, DownloadQueue.DownloadRequest> $$3 = new HashMap<UUID, DownloadQueue.DownloadRequest>();
            for (ServerPackData $$4 : $$0) {
                $$3.put($$4.id, new DownloadQueue.DownloadRequest($$4.url, $$4.hash));
            }
            this.downloader.download($$3, $$1 -> this.onDownload((Collection<ServerPackData>)$$0, (DownloadQueue.BatchResult)((Object)$$1)));
        }
        return $$12;
    }

    private void triggerReloadIfNeeded() {
        boolean $$0 = false;
        final ArrayList<ServerPackData> $$1 = new ArrayList<ServerPackData>();
        final ArrayList<ServerPackData> $$2 = new ArrayList<ServerPackData>();
        for (ServerPackData $$3 : this.packs) {
            boolean $$4;
            if ($$3.activationStatus == ActivationStatus.PENDING) {
                return;
            }
            boolean bl = $$4 = $$3.promptAccepted && $$3.downloadStatus == PackDownloadStatus.DONE && !$$3.isRemoved();
            if ($$4 && $$3.activationStatus == ActivationStatus.INACTIVE) {
                $$1.add($$3);
                $$0 = true;
            }
            if ($$3.activationStatus != ActivationStatus.ACTIVE) continue;
            if (!$$4) {
                $$0 = true;
                $$2.add($$3);
                continue;
            }
            $$1.add($$3);
        }
        if ($$0) {
            for (ServerPackData $$5 : $$1) {
                if ($$5.activationStatus == ActivationStatus.ACTIVE) continue;
                $$5.activationStatus = ActivationStatus.PENDING;
            }
            for (ServerPackData $$6 : $$2) {
                $$6.activationStatus = ActivationStatus.PENDING;
            }
            this.reloadConfig.scheduleReload(new PackReloadConfig.Callbacks(){

                @Override
                public void onSuccess() {
                    for (ServerPackData $$0 : $$1) {
                        $$0.activationStatus = ActivationStatus.ACTIVE;
                        if ($$0.removalReason != null) continue;
                        ServerPackManager.this.packLoadFeedback.reportFinalResult($$0.id, PackLoadFeedback.FinalResult.APPLIED);
                    }
                    for (ServerPackData $$12 : $$2) {
                        $$12.activationStatus = ActivationStatus.INACTIVE;
                    }
                    ServerPackManager.this.registerForUpdate();
                }

                @Override
                public void onFailure(boolean $$0) {
                    if (!$$0) {
                        $$1.clear();
                        for (ServerPackData $$12 : ServerPackManager.this.packs) {
                            switch ($$12.activationStatus.ordinal()) {
                                case 2: {
                                    $$1.add($$12);
                                    break;
                                }
                                case 1: {
                                    $$12.activationStatus = ActivationStatus.INACTIVE;
                                    $$12.setRemovalReasonIfNotSet(RemovalReason.ACTIVATION_FAILED);
                                    break;
                                }
                                case 0: {
                                    $$12.setRemovalReasonIfNotSet(RemovalReason.DISCARDED);
                                }
                            }
                        }
                        ServerPackManager.this.registerForUpdate();
                    } else {
                        for (ServerPackData $$22 : ServerPackManager.this.packs) {
                            if ($$22.activationStatus != ActivationStatus.PENDING) continue;
                            $$22.activationStatus = ActivationStatus.INACTIVE;
                        }
                    }
                }

                @Override
                public List<PackReloadConfig.IdAndPath> packsToLoad() {
                    return $$1.stream().map($$0 -> new PackReloadConfig.IdAndPath($$0.id, $$0.path)).toList();
                }
            });
        }
    }

    public static final class PackPromptStatus
    extends Enum<PackPromptStatus> {
        public static final /* enum */ PackPromptStatus PENDING = new PackPromptStatus();
        public static final /* enum */ PackPromptStatus ALLOWED = new PackPromptStatus();
        public static final /* enum */ PackPromptStatus DECLINED = new PackPromptStatus();
        private static final /* synthetic */ PackPromptStatus[] $VALUES;

        public static PackPromptStatus[] values() {
            return (PackPromptStatus[])$VALUES.clone();
        }

        public static PackPromptStatus valueOf(String $$0) {
            return Enum.valueOf(PackPromptStatus.class, $$0);
        }

        private static /* synthetic */ PackPromptStatus[] a() {
            return new PackPromptStatus[]{PENDING, ALLOWED, DECLINED};
        }

        static {
            $VALUES = PackPromptStatus.a();
        }
    }

    static class ServerPackData {
        final UUID id;
        final URL url;
        @Nullable
        final HashCode hash;
        @Nullable
        Path path;
        @Nullable
        RemovalReason removalReason;
        PackDownloadStatus downloadStatus = PackDownloadStatus.REQUESTED;
        ActivationStatus activationStatus = ActivationStatus.INACTIVE;
        boolean promptAccepted;

        ServerPackData(UUID $$0, URL $$1, @Nullable HashCode $$2) {
            this.id = $$0;
            this.url = $$1;
            this.hash = $$2;
        }

        public void setRemovalReasonIfNotSet(RemovalReason $$0) {
            if (this.removalReason == null) {
                this.removalReason = $$0;
            }
        }

        public boolean isRemoved() {
            return this.removalReason != null;
        }
    }

    static final class RemovalReason
    extends Enum<RemovalReason> {
        public static final /* enum */ RemovalReason DOWNLOAD_FAILED = new RemovalReason(PackLoadFeedback.FinalResult.DOWNLOAD_FAILED);
        public static final /* enum */ RemovalReason ACTIVATION_FAILED = new RemovalReason(PackLoadFeedback.FinalResult.ACTIVATION_FAILED);
        public static final /* enum */ RemovalReason DECLINED = new RemovalReason(PackLoadFeedback.FinalResult.DECLINED);
        public static final /* enum */ RemovalReason DISCARDED = new RemovalReason(PackLoadFeedback.FinalResult.DISCARDED);
        public static final /* enum */ RemovalReason SERVER_REMOVED = new RemovalReason(null);
        public static final /* enum */ RemovalReason SERVER_REPLACED = new RemovalReason(null);
        @Nullable
        final PackLoadFeedback.FinalResult serverResponse;
        private static final /* synthetic */ RemovalReason[] $VALUES;

        public static RemovalReason[] values() {
            return (RemovalReason[])$VALUES.clone();
        }

        public static RemovalReason valueOf(String $$0) {
            return Enum.valueOf(RemovalReason.class, $$0);
        }

        private RemovalReason(PackLoadFeedback.FinalResult $$0) {
            this.serverResponse = $$0;
        }

        private static /* synthetic */ RemovalReason[] a() {
            return new RemovalReason[]{DOWNLOAD_FAILED, ACTIVATION_FAILED, DECLINED, DISCARDED, SERVER_REMOVED, SERVER_REPLACED};
        }

        static {
            $VALUES = RemovalReason.a();
        }
    }

    static final class PackDownloadStatus
    extends Enum<PackDownloadStatus> {
        public static final /* enum */ PackDownloadStatus REQUESTED = new PackDownloadStatus();
        public static final /* enum */ PackDownloadStatus PENDING = new PackDownloadStatus();
        public static final /* enum */ PackDownloadStatus DONE = new PackDownloadStatus();
        private static final /* synthetic */ PackDownloadStatus[] $VALUES;

        public static PackDownloadStatus[] values() {
            return (PackDownloadStatus[])$VALUES.clone();
        }

        public static PackDownloadStatus valueOf(String $$0) {
            return Enum.valueOf(PackDownloadStatus.class, $$0);
        }

        private static /* synthetic */ PackDownloadStatus[] a() {
            return new PackDownloadStatus[]{REQUESTED, PENDING, DONE};
        }

        static {
            $VALUES = PackDownloadStatus.a();
        }
    }

    static final class ActivationStatus
    extends Enum<ActivationStatus> {
        public static final /* enum */ ActivationStatus INACTIVE = new ActivationStatus();
        public static final /* enum */ ActivationStatus PENDING = new ActivationStatus();
        public static final /* enum */ ActivationStatus ACTIVE = new ActivationStatus();
        private static final /* synthetic */ ActivationStatus[] $VALUES;

        public static ActivationStatus[] values() {
            return (ActivationStatus[])$VALUES.clone();
        }

        public static ActivationStatus valueOf(String $$0) {
            return Enum.valueOf(ActivationStatus.class, $$0);
        }

        private static /* synthetic */ ActivationStatus[] a() {
            return new ActivationStatus[]{INACTIVE, PENDING, ACTIVE};
        }

        static {
            $VALUES = ActivationStatus.a();
        }
    }
}

