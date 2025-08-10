/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import net.minecraft.network.chat.Component;

public class RealmsUploadTooLargeException
extends RealmsUploadException {
    final long sizeLimit;

    public RealmsUploadTooLargeException(long $$0) {
        this.sizeLimit = $$0;
    }

    @Override
    public Component[] b() {
        return new Component[]{Component.translatable("mco.upload.failed.too_big.title"), Component.a("mco.upload.failed.too_big.description", Unit.humanReadable(this.sizeLimit, Unit.getLargest(this.sizeLimit)))};
    }
}

