/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class WorldDownload
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String downloadLink;
    public String resourcePackUrl;
    public String resourcePackHash;

    public static WorldDownload parse(String $$0) {
        JsonObject $$1 = LenientJsonParser.parse($$0).getAsJsonObject();
        WorldDownload $$2 = new WorldDownload();
        try {
            $$2.downloadLink = JsonUtils.getStringOr("downloadLink", $$1, "");
            $$2.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", $$1, "");
            $$2.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", $$1, "");
        } catch (Exception $$3) {
            LOGGER.error("Could not parse WorldDownload: {}", (Object)$$3.getMessage());
        }
        return $$2;
    }
}

