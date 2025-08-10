/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package com.mojang.realmsclient.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;

public class UploadTokenCache {
    private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap();

    public static String get(long $$0) {
        return (String)TOKEN_CACHE.get($$0);
    }

    public static void invalidate(long $$0) {
        TOKEN_CACHE.remove($$0);
    }

    public static void put(long $$0, @Nullable String $$1) {
        TOKEN_CACHE.put($$0, (Object)$$1);
    }
}

