/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.ExclusionStrategy
 *  com.google.gson.FieldAttributes
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 */
package com.mojang.realmsclient.dto;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.realmsclient.dto.Exclude;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import javax.annotation.Nullable;

public class GuardedSerializer {
    ExclusionStrategy strategy = new ExclusionStrategy(this){

        public boolean shouldSkipClass(Class<?> $$0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes $$0) {
            return $$0.getAnnotation(Exclude.class) != null;
        }
    };
    private final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(this.strategy).addDeserializationExclusionStrategy(this.strategy).create();

    public String toJson(ReflectionBasedSerialization $$0) {
        return this.gson.toJson((Object)$$0);
    }

    public String toJson(JsonElement $$0) {
        return this.gson.toJson($$0);
    }

    @Nullable
    public <T extends ReflectionBasedSerialization> T fromJson(String $$0, Class<T> $$1) {
        return (T)((ReflectionBasedSerialization)this.gson.fromJson($$0, $$1));
    }
}

