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
import javax.annotation.Nullable;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class RealmsNews
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    public String newsLink;

    public static RealmsNews parse(String $$0) {
        RealmsNews $$1 = new RealmsNews();
        try {
            JsonObject $$2 = LenientJsonParser.parse($$0).getAsJsonObject();
            $$1.newsLink = JsonUtils.getStringOr("newsLink", $$2, null);
        } catch (Exception $$3) {
            LOGGER.error("Could not parse RealmsNews: {}", (Object)$$3.getMessage());
        }
        return $$1;
    }
}

