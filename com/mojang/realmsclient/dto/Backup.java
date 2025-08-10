/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

public class Backup
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String backupId;
    public Date lastModifiedDate;
    public long size;
    private boolean uploadedVersion;
    public Map<String, String> metadata = Maps.newHashMap();
    public Map<String, String> changeList = Maps.newHashMap();

    public static Backup parse(JsonElement $$0) {
        JsonObject $$1 = $$0.getAsJsonObject();
        Backup $$2 = new Backup();
        try {
            $$2.backupId = JsonUtils.getStringOr("backupId", $$1, "");
            $$2.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", $$1);
            $$2.size = JsonUtils.getLongOr("size", $$1, 0L);
            if ($$1.has("metadata")) {
                JsonObject $$3 = $$1.getAsJsonObject("metadata");
                Set $$4 = $$3.entrySet();
                for (Map.Entry $$5 : $$4) {
                    if (((JsonElement)$$5.getValue()).isJsonNull()) continue;
                    $$2.metadata.put((String)$$5.getKey(), ((JsonElement)$$5.getValue()).getAsString());
                }
            }
        } catch (Exception $$6) {
            LOGGER.error("Could not parse Backup: {}", (Object)$$6.getMessage());
        }
        return $$2;
    }

    public boolean isUploadedVersion() {
        return this.uploadedVersion;
    }

    public void setUploadedVersion(boolean $$0) {
        this.uploadedVersion = $$0;
    }
}

