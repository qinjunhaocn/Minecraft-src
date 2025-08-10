/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;

public class RealmsServiceException
extends Exception {
    public final RealmsError realmsError;

    public RealmsServiceException(RealmsError $$0) {
        this.realmsError = $$0;
    }

    @Override
    public String getMessage() {
        return this.realmsError.logMessage();
    }
}

