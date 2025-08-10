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
import java.util.Set;

public class RealmsWorldResetDto
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="seed")
    private final String seed;
    @SerializedName(value="worldTemplateId")
    private final long worldTemplateId;
    @SerializedName(value="levelType")
    private final int levelType;
    @SerializedName(value="generateStructures")
    private final boolean generateStructures;
    @SerializedName(value="experiments")
    private final Set<String> experiments;

    public RealmsWorldResetDto(String $$0, long $$1, int $$2, boolean $$3, Set<String> $$4) {
        this.seed = $$0;
        this.worldTemplateId = $$1;
        this.levelType = $$2;
        this.generateStructures = $$3;
        this.experiments = $$4;
    }
}

