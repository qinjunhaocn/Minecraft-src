/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.client;

import java.net.Proxy;
import javax.annotation.Nullable;

public class RealmsClientConfig {
    @Nullable
    private static Proxy proxy;

    @Nullable
    public static Proxy getProxy() {
        return proxy;
    }

    public static void setProxy(Proxy $$0) {
        if (proxy == null) {
            proxy = $$0;
        }
    }
}

