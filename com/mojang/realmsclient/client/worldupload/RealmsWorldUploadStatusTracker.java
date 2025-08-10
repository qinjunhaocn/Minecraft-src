/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.UploadStatus;

public interface RealmsWorldUploadStatusTracker {
    public UploadStatus getUploadStatus();

    public void setUploading();

    public static RealmsWorldUploadStatusTracker noOp() {
        return new RealmsWorldUploadStatusTracker(){
            private final UploadStatus uploadStatus = new UploadStatus();

            @Override
            public UploadStatus getUploadStatus() {
                return this.uploadStatus;
            }

            @Override
            public void setUploading() {
            }
        };
    }
}

