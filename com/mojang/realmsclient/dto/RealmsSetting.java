/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.util.List;

public record RealmsSetting(@SerializedName(value="name") String name, @SerializedName(value="value") String value) implements ReflectionBasedSerialization
{
    public static RealmsSetting hardcoreSetting(boolean $$0) {
        return new RealmsSetting("hardcore", Boolean.toString($$0));
    }

    public static boolean isHardcore(List<RealmsSetting> $$0) {
        for (RealmsSetting $$1 : $$0) {
            if (!$$1.name().equals("hardcore")) continue;
            return Boolean.parseBoolean($$1.value());
        }
        return false;
    }

    @SerializedName(value="name")
    public String name() {
        return this.name;
    }

    @SerializedName(value="value")
    public String value() {
        return this.value;
    }
}

