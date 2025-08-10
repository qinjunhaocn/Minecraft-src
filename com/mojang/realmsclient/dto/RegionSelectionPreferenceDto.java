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
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.ValueObject;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RegionSelectionPreferenceDto
extends ValueObject
implements ReflectionBasedSerialization {
    public static final RegionSelectionPreferenceDto DEFAULT = new RegionSelectionPreferenceDto(RegionSelectionPreference.AUTOMATIC_OWNER, null);
    private static final Logger LOGGER = LogUtils.getLogger();
    @SerializedName(value="regionSelectionPreference")
    @JsonAdapter(value=RegionSelectionPreference.RegionSelectionPreferenceJsonAdapter.class)
    public RegionSelectionPreference regionSelectionPreference;
    @SerializedName(value="preferredRegion")
    @JsonAdapter(value=RealmsRegion.RealmsRegionJsonAdapter.class)
    @Nullable
    public RealmsRegion preferredRegion;

    public RegionSelectionPreferenceDto(RegionSelectionPreference $$0, @Nullable RealmsRegion $$1) {
        this.regionSelectionPreference = $$0;
        this.preferredRegion = $$1;
    }

    private RegionSelectionPreferenceDto() {
    }

    public static RegionSelectionPreferenceDto parse(GuardedSerializer $$0, String $$1) {
        try {
            RegionSelectionPreferenceDto $$2 = $$0.fromJson($$1, RegionSelectionPreferenceDto.class);
            if ($$2 == null) {
                LOGGER.error("Could not parse RegionSelectionPreference: {}", (Object)$$1);
                return new RegionSelectionPreferenceDto();
            }
            return $$2;
        } catch (Exception $$3) {
            LOGGER.error("Could not parse RegionSelectionPreference: {}", (Object)$$3.getMessage());
            return new RegionSelectionPreferenceDto();
        }
    }

    public RegionSelectionPreferenceDto clone() {
        return new RegionSelectionPreferenceDto(this.regionSelectionPreference, this.preferredRegion);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }
}

