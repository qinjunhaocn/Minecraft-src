/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.util.UUIDTypeAdapter
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import javax.annotation.Nullable;

public class PlayerInfo
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="name")
    @Nullable
    private String name;
    @SerializedName(value="uuid")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    private UUID uuid;
    @SerializedName(value="operator")
    private boolean operator;
    @SerializedName(value="accepted")
    private boolean accepted;
    @SerializedName(value="online")
    private boolean online;

    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    public void setName(String $$0) {
        this.name = $$0;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID $$0) {
        this.uuid = $$0;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void setOperator(boolean $$0) {
        this.operator = $$0;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean $$0) {
        this.accepted = $$0;
    }

    public boolean getOnline() {
        return this.online;
    }

    public void setOnline(boolean $$0) {
        this.online = $$0;
    }
}

