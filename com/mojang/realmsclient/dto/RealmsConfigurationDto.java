/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsSetting;
import com.mojang.realmsclient.dto.RealmsSlotUpdateDto;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import java.util.List;
import javax.annotation.Nullable;

public record RealmsConfigurationDto(@SerializedName(value="options") RealmsSlotUpdateDto options, @SerializedName(value="settings") List<RealmsSetting> settings, @Nullable @SerializedName(value="regionSelectionPreference") RegionSelectionPreferenceDto regionSelectionPreference, @Nullable @SerializedName(value="description") RealmsDescriptionDto description) implements ReflectionBasedSerialization
{
    @SerializedName(value="options")
    public RealmsSlotUpdateDto options() {
        return this.options;
    }

    @SerializedName(value="settings")
    public List<RealmsSetting> settings() {
        return this.settings;
    }

    @Nullable
    @SerializedName(value="regionSelectionPreference")
    public RegionSelectionPreferenceDto regionSelectionPreference() {
        return this.regionSelectionPreference;
    }

    @Nullable
    @SerializedName(value="description")
    public RealmsDescriptionDto description() {
        return this.description;
    }
}

