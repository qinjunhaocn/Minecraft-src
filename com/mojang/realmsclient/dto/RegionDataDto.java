/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ServiceQuality;

public record RegionDataDto(@SerializedName(value="regionName") @JsonAdapter(value=RealmsRegion.RealmsRegionJsonAdapter.class) RealmsRegion region, @SerializedName(value="serviceQuality") @JsonAdapter(value=ServiceQuality.RealmsServiceQualityJsonAdapter.class) ServiceQuality serviceQuality) implements ReflectionBasedSerialization
{
    @SerializedName(value="regionName")
    public RealmsRegion region() {
        return this.region;
    }

    @SerializedName(value="serviceQuality")
    public ServiceQuality serviceQuality() {
        return this.serviceQuality;
    }
}

