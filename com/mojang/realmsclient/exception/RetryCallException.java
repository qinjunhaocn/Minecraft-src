/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;

public class RetryCallException
extends RealmsServiceException {
    public static final int DEFAULT_DELAY = 5;
    public final int delaySeconds;

    public RetryCallException(int $$0, int $$1) {
        super(RealmsError.CustomError.retry($$1));
        this.delaySeconds = $$0 < 0 || $$0 > 120 ? 5 : $$0;
    }
}

