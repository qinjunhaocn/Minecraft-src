/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import net.minecraft.network.chat.Component;

public class RealmsUploadWorldNotClosedException
extends RealmsUploadException {
    @Override
    public Component getStatusMessage() {
        return Component.translatable("mco.upload.close.failure");
    }
}

