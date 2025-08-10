/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;

public class ServerActivity
extends ValueObject {
    @Nullable
    public String profileUuid;
    public long joinTime;
    public long leaveTime;

    public static ServerActivity parse(JsonObject $$0) {
        ServerActivity $$1 = new ServerActivity();
        try {
            $$1.profileUuid = JsonUtils.getStringOr("profileUuid", $$0, null);
            $$1.joinTime = JsonUtils.getLongOr("joinTime", $$0, Long.MIN_VALUE);
            $$1.leaveTime = JsonUtils.getLongOr("leaveTime", $$0, Long.MIN_VALUE);
        } catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}

