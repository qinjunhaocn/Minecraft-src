/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import net.minecraft.network.chat.Component;

public class RealmsUploadFailedException
extends RealmsUploadException {
    private final Component errorMessage;

    public RealmsUploadFailedException(Component $$0) {
        this.errorMessage = $$0;
    }

    public RealmsUploadFailedException(String $$0) {
        this(Component.literal($$0));
    }

    @Override
    public Component getStatusMessage() {
        return Component.a("mco.upload.failed", this.errorMessage);
    }
}

