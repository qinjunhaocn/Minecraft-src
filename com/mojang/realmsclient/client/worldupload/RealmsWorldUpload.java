/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.worldupload.RealmsUploadCanceledException;
import com.mojang.realmsclient.client.worldupload.RealmsUploadFailedException;
import com.mojang.realmsclient.client.worldupload.RealmsUploadWorldNotClosedException;
import com.mojang.realmsclient.client.worldupload.RealmsUploadWorldPacker;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUploadStatusTracker;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.User;
import org.slf4j.Logger;

public class RealmsWorldUpload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int UPLOAD_RETRIES = 20;
    private final RealmsClient client = RealmsClient.getOrCreate();
    private final Path worldFolder;
    private final RealmsSlot realmsSlot;
    private final User user;
    private final long realmId;
    private final RealmsWorldUploadStatusTracker statusCallback;
    private volatile boolean cancelled;
    @Nullable
    private FileUpload uploadTask;

    public RealmsWorldUpload(Path $$0, RealmsSlot $$1, User $$2, long $$3, RealmsWorldUploadStatusTracker $$4) {
        this.worldFolder = $$0;
        this.realmsSlot = $$1;
        this.user = $$2;
        this.realmId = $$3;
        this.statusCallback = $$4;
    }

    public CompletableFuture<?> packAndUpload() {
        return CompletableFuture.runAsync(() -> {
            File $$0 = null;
            try {
                FileUpload $$2;
                UploadInfo $$1 = this.requestUploadInfoWithRetries();
                $$0 = RealmsUploadWorldPacker.pack(this.worldFolder, () -> this.cancelled);
                this.statusCallback.setUploading();
                this.uploadTask = $$2 = new FileUpload($$0, this.realmId, this.realmsSlot.slotId, $$1, this.user, SharedConstants.getCurrentVersion().name(), this.realmsSlot.options.version, this.statusCallback.getUploadStatus());
                UploadResult $$3 = $$2.upload();
                String $$4 = $$3.getSimplifiedErrorMessage();
                if ($$4 != null) {
                    throw new RealmsUploadFailedException($$4);
                }
                UploadTokenCache.invalidate(this.realmId);
                this.client.updateSlot(this.realmId, this.realmsSlot.slotId, this.realmsSlot.options, this.realmsSlot.settings);
            } catch (IOException $$5) {
                throw new RealmsUploadFailedException($$5.getMessage());
            } catch (RealmsServiceException $$6) {
                throw new RealmsUploadFailedException($$6.realmsError.errorMessage());
            } catch (InterruptedException | CancellationException $$7) {
                throw new RealmsUploadCanceledException();
            } finally {
                if ($$0 != null) {
                    LOGGER.debug("Deleting file {}", (Object)$$0.getAbsolutePath());
                    $$0.delete();
                }
            }
        }, Util.backgroundExecutor());
    }

    public void cancel() {
        this.cancelled = true;
        if (this.uploadTask != null) {
            this.uploadTask.cancel();
            this.uploadTask = null;
        }
    }

    private UploadInfo requestUploadInfoWithRetries() throws RealmsServiceException, InterruptedException {
        for (int $$0 = 0; $$0 < 20; ++$$0) {
            try {
                UploadInfo $$1 = this.client.requestUploadInfo(this.realmId);
                if (this.cancelled) {
                    throw new RealmsUploadCanceledException();
                }
                if ($$1 == null) continue;
                if (!$$1.isWorldClosed()) {
                    throw new RealmsUploadWorldNotClosedException();
                }
                return $$1;
            } catch (RetryCallException $$2) {
                Thread.sleep((long)$$2.delaySeconds * 1000L);
            }
        }
        throw new RealmsUploadWorldNotClosedException();
    }
}

