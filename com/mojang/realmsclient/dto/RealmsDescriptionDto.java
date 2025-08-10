/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;
import javax.annotation.Nullable;

public class RealmsDescriptionDto
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="name")
    @Nullable
    public String name;
    @SerializedName(value="description")
    public String description;

    public RealmsDescriptionDto(@Nullable String $$0, String $$1) {
        this.name = $$0;
        this.description = $$1;
    }
}

