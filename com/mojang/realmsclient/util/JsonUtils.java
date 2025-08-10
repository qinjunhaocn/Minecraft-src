/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.util.UndashedUuid
 */
package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UndashedUuid;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

public class JsonUtils {
    public static <T> T getRequired(String $$0, JsonObject $$1, Function<JsonObject, T> $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 == null || $$3.isJsonNull()) {
            throw new IllegalStateException("Missing required property: " + $$0);
        }
        if (!$$3.isJsonObject()) {
            throw new IllegalStateException("Required property " + $$0 + " was not a JsonObject as espected");
        }
        return $$2.apply($$3.getAsJsonObject());
    }

    @Nullable
    public static <T> T getOptional(String $$0, JsonObject $$1, Function<JsonObject, T> $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 == null || $$3.isJsonNull()) {
            return null;
        }
        if (!$$3.isJsonObject()) {
            throw new IllegalStateException("Required property " + $$0 + " was not a JsonObject as espected");
        }
        return $$2.apply($$3.getAsJsonObject());
    }

    public static String getRequiredString(String $$0, JsonObject $$1) {
        String $$2 = JsonUtils.getStringOr($$0, $$1, null);
        if ($$2 == null) {
            throw new IllegalStateException("Missing required property: " + $$0);
        }
        return $$2;
    }

    public static String getRequiredStringOr(String $$0, JsonObject $$1, String $$2) {
        return JsonUtils.getStringOr($$0, $$1, $$2);
    }

    @Nullable
    public static String getStringOr(String $$0, JsonObject $$1, @Nullable String $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsString();
        }
        return $$2;
    }

    @Nullable
    public static UUID getUuidOr(String $$0, JsonObject $$1, @Nullable UUID $$2) {
        String $$3 = JsonUtils.getStringOr($$0, $$1, null);
        if ($$3 == null) {
            return $$2;
        }
        return UndashedUuid.fromStringLenient((String)$$3);
    }

    public static int getIntOr(String $$0, JsonObject $$1, int $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsInt();
        }
        return $$2;
    }

    public static long getLongOr(String $$0, JsonObject $$1, long $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsLong();
        }
        return $$2;
    }

    public static boolean getBooleanOr(String $$0, JsonObject $$1, boolean $$2) {
        JsonElement $$3 = $$1.get($$0);
        if ($$3 != null) {
            return $$3.isJsonNull() ? $$2 : $$3.getAsBoolean();
        }
        return $$2;
    }

    public static Date getDateOr(String $$0, JsonObject $$1) {
        JsonElement $$2 = $$1.get($$0);
        if ($$2 != null) {
            return new Date(Long.parseLong($$2.getAsString()));
        }
        return new Date();
    }
}

