/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Set;
import net.minecraft.util.LenientJsonParser;

public class Ops
extends ValueObject {
    public Set<String> ops = Sets.newHashSet();

    public static Ops parse(String $$0) {
        Ops $$1 = new Ops();
        try {
            JsonObject $$2 = LenientJsonParser.parse($$0).getAsJsonObject();
            JsonElement $$3 = $$2.get("ops");
            if ($$3.isJsonArray()) {
                for (JsonElement $$4 : $$3.getAsJsonArray()) {
                    $$1.ops.add($$4.getAsString());
                }
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}

