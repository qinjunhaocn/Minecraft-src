/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class RealmsText {
    private static final String TRANSLATION_KEY = "translationKey";
    private static final String ARGS = "args";
    private final String translationKey;
    @Nullable
    private final String[] args;

    private RealmsText(String $$0, @Nullable String[] $$1) {
        this.translationKey = $$0;
        this.args = $$1;
    }

    public Component createComponent(Component $$0) {
        return (Component)Objects.requireNonNullElse((Object)this.createComponent(), (Object)$$0);
    }

    @Nullable
    public Component createComponent() {
        if (!I18n.exists(this.translationKey)) {
            return null;
        }
        if (this.args == null) {
            return Component.translatable(this.translationKey);
        }
        return Component.a(this.translationKey, this.args);
    }

    public static RealmsText parse(JsonObject $$0) {
        String[] $$5;
        String $$1 = JsonUtils.getRequiredString(TRANSLATION_KEY, $$0);
        JsonElement $$2 = $$0.get(ARGS);
        if ($$2 == null || $$2.isJsonNull()) {
            Object $$3 = null;
        } else {
            JsonArray $$4 = $$2.getAsJsonArray();
            $$5 = new String[$$4.size()];
            for (int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                $$5[$$6] = $$4.get($$6).getAsString();
            }
        }
        return new RealmsText($$1, $$5);
    }

    public String toString() {
        return this.translationKey;
    }
}

