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
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class PendingInvitesList
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<PendingInvite> pendingInvites = Lists.newArrayList();

    public static PendingInvitesList parse(String $$0) {
        PendingInvitesList $$1 = new PendingInvitesList();
        try {
            JsonObject $$2 = LenientJsonParser.parse($$0).getAsJsonObject();
            if ($$2.get("invites").isJsonArray()) {
                for (JsonElement $$3 : $$2.get("invites").getAsJsonArray()) {
                    $$1.pendingInvites.add(PendingInvite.parse($$3.getAsJsonObject()));
                }
            }
        } catch (Exception $$4) {
            LOGGER.error("Could not parse PendingInvitesList: {}", (Object)$$4.getMessage());
        }
        return $$1;
    }
}

