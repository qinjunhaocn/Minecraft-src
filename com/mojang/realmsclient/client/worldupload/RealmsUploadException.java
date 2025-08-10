/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client.worldupload;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public abstract class RealmsUploadException
extends RuntimeException {
    @Nullable
    public Component getStatusMessage() {
        return null;
    }

    @Nullable
    public Component[] b() {
        return null;
    }
}

