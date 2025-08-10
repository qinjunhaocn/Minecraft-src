/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.List;
import net.minecraft.util.LenientJsonParser;

public class ServerActivityList
extends ValueObject {
    public long periodInMillis;
    public List<ServerActivity> serverActivities = Lists.newArrayList();

    public static ServerActivityList parse(String $$0) {
        ServerActivityList $$1 = new ServerActivityList();
        try {
            JsonElement $$2 = LenientJsonParser.parse($$0);
            JsonObject $$3 = $$2.getAsJsonObject();
            $$1.periodInMillis = JsonUtils.getLongOr("periodInMillis", $$3, -1L);
            JsonElement $$4 = $$3.get("playerActivityDto");
            if ($$4 != null && $$4.isJsonArray()) {
                JsonArray $$5 = $$4.getAsJsonArray();
                for (JsonElement $$6 : $$5) {
                    ServerActivity $$7 = ServerActivity.parse($$6.getAsJsonObject());
                    $$1.serverActivities.add($$7);
                }
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}

