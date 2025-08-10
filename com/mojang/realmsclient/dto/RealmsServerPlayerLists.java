/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class RealmsServerPlayerLists
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public Map<Long, List<ProfileResult>> servers = Map.of();

    public static RealmsServerPlayerLists parse(String $$0) {
        RealmsServerPlayerLists $$1 = new RealmsServerPlayerLists();
        ImmutableMap.Builder $$2 = ImmutableMap.builder();
        try {
            JsonObject $$3 = GsonHelper.parse($$0);
            if (GsonHelper.isArrayNode($$3, "lists")) {
                JsonArray $$4 = $$3.getAsJsonArray("lists");
                for (JsonElement $$5 : $$4) {
                    ArrayList $$11;
                    JsonObject $$6 = $$5.getAsJsonObject();
                    String $$7 = JsonUtils.getStringOr("playerList", $$6, null);
                    if ($$7 != null) {
                        JsonElement $$8 = LenientJsonParser.parse($$7);
                        if ($$8.isJsonArray()) {
                            List<ProfileResult> $$9 = RealmsServerPlayerLists.parsePlayers($$8.getAsJsonArray());
                        } else {
                            ArrayList $$10 = Lists.newArrayList();
                        }
                    } else {
                        $$11 = Lists.newArrayList();
                    }
                    $$2.put(JsonUtils.getLongOr("serverId", $$6, -1L), $$11);
                }
            }
        } catch (Exception $$12) {
            LOGGER.error("Could not parse RealmsServerPlayerLists: {}", (Object)$$12.getMessage());
        }
        $$1.servers = $$2.build();
        return $$1;
    }

    private static List<ProfileResult> parsePlayers(JsonArray $$0) {
        ArrayList<ProfileResult> $$1 = new ArrayList<ProfileResult>($$0.size());
        MinecraftSessionService $$2 = Minecraft.getInstance().getMinecraftSessionService();
        for (JsonElement $$3 : $$0) {
            UUID $$4;
            if (!$$3.isJsonObject() || ($$4 = JsonUtils.getUuidOr("playerId", $$3.getAsJsonObject(), null)) == null || Minecraft.getInstance().isLocalPlayer($$4)) continue;
            try {
                ProfileResult $$5 = $$2.fetchProfile($$4, false);
                if ($$5 == null) continue;
                $$1.add($$5);
            } catch (Exception $$6) {
                LOGGER.error("Could not get name for {}", (Object)$$4, (Object)$$6);
            }
        }
        return $$1;
    }

    public List<ProfileResult> getProfileResultsFor(long $$0) {
        List<ProfileResult> $$1 = this.servers.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return List.of();
    }
}

