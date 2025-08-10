/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ServiceQuality;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public record RealmsJoinInformation(@SerializedName(value="address") @Nullable String address, @SerializedName(value="resourcePackUrl") @Nullable String resourcePackUrl, @SerializedName(value="resourcePackHash") @Nullable String resourcePackHash, @SerializedName(value="sessionRegionData") @Nullable RegionData regionData) implements ReflectionBasedSerialization
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RealmsJoinInformation EMPTY = new RealmsJoinInformation(null, null, null, null);

    public static RealmsJoinInformation parse(GuardedSerializer $$0, String $$1) {
        try {
            RealmsJoinInformation $$2 = $$0.fromJson($$1, RealmsJoinInformation.class);
            if ($$2 == null) {
                LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)$$1);
                return EMPTY;
            }
            return $$2;
        } catch (Exception $$3) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)$$3.getMessage());
            return EMPTY;
        }
    }

    @SerializedName(value="address")
    @Nullable
    public String address() {
        return this.address;
    }

    @SerializedName(value="resourcePackUrl")
    @Nullable
    public String resourcePackUrl() {
        return this.resourcePackUrl;
    }

    @SerializedName(value="resourcePackHash")
    @Nullable
    public String resourcePackHash() {
        return this.resourcePackHash;
    }

    @SerializedName(value="sessionRegionData")
    @Nullable
    public RegionData regionData() {
        return this.regionData;
    }

    public record RegionData(@SerializedName(value="regionName") @JsonAdapter(value=RealmsRegion.RealmsRegionJsonAdapter.class) @Nullable RealmsRegion region, @SerializedName(value="serviceQuality") @JsonAdapter(value=ServiceQuality.RealmsServiceQualityJsonAdapter.class) @Nullable ServiceQuality serviceQuality) implements ReflectionBasedSerialization
    {
        @SerializedName(value="regionName")
        @Nullable
        public RealmsRegion region() {
            return this.region;
        }

        @SerializedName(value="serviceQuality")
        @Nullable
        public ServiceQuality serviceQuality() {
            return this.serviceQuality;
        }
    }
}

