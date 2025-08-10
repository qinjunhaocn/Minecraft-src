/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class WorldTemplatePaginatedList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<WorldTemplate> templates;
    public int page;
    public int size;
    public int total;

    public WorldTemplatePaginatedList() {
    }

    public WorldTemplatePaginatedList(int $$0) {
        this.templates = Collections.emptyList();
        this.page = 0;
        this.size = $$0;
        this.total = -1;
    }

    public boolean isLastPage() {
        return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
    }

    public static WorldTemplatePaginatedList parse(String $$0) {
        WorldTemplatePaginatedList $$1 = new WorldTemplatePaginatedList();
        $$1.templates = Lists.newArrayList();
        try {
            JsonObject $$2 = LenientJsonParser.parse($$0).getAsJsonObject();
            if ($$2.get("templates").isJsonArray()) {
                for (JsonElement $$3 : $$2.get("templates").getAsJsonArray()) {
                    $$1.templates.add(WorldTemplate.parse($$3.getAsJsonObject()));
                }
            }
            $$1.page = JsonUtils.getIntOr("page", $$2, 0);
            $$1.size = JsonUtils.getIntOr("size", $$2, 0);
            $$1.total = JsonUtils.getIntOr("total", $$2, 0);
        } catch (Exception $$4) {
            LOGGER.error("Could not parse WorldTemplatePaginatedList: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }
}

