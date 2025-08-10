/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import org.slf4j.Logger;

public final class RegionSelectionPreference
extends Enum<RegionSelectionPreference> {
    public static final /* enum */ RegionSelectionPreference AUTOMATIC_PLAYER = new RegionSelectionPreference(0, "realms.configuration.region_preference.automatic_player");
    public static final /* enum */ RegionSelectionPreference AUTOMATIC_OWNER = new RegionSelectionPreference(1, "realms.configuration.region_preference.automatic_owner");
    public static final /* enum */ RegionSelectionPreference MANUAL = new RegionSelectionPreference(2, "");
    public static final RegionSelectionPreference DEFAULT_SELECTION;
    public final int id;
    public final String translationKey;
    private static final /* synthetic */ RegionSelectionPreference[] $VALUES;

    public static RegionSelectionPreference[] values() {
        return (RegionSelectionPreference[])$VALUES.clone();
    }

    public static RegionSelectionPreference valueOf(String $$0) {
        return Enum.valueOf(RegionSelectionPreference.class, $$0);
    }

    private RegionSelectionPreference(int $$0, String $$1) {
        this.id = $$0;
        this.translationKey = $$1;
    }

    private static /* synthetic */ RegionSelectionPreference[] a() {
        return new RegionSelectionPreference[]{AUTOMATIC_PLAYER, AUTOMATIC_OWNER, MANUAL};
    }

    static {
        $VALUES = RegionSelectionPreference.a();
        DEFAULT_SELECTION = AUTOMATIC_PLAYER;
    }

    public static class RegionSelectionPreferenceJsonAdapter
    extends TypeAdapter<RegionSelectionPreference> {
        private static final Logger LOGGER = LogUtils.getLogger();

        public void write(JsonWriter $$0, RegionSelectionPreference $$1) throws IOException {
            $$0.value((long)$$1.id);
        }

        public RegionSelectionPreference read(JsonReader $$0) throws IOException {
            int $$1 = $$0.nextInt();
            for (RegionSelectionPreference $$2 : RegionSelectionPreference.values()) {
                if ($$2.id != $$1) continue;
                return $$2;
            }
            LOGGER.warn("Unsupported RegionSelectionPreference {}", (Object)$$1);
            return DEFAULT_SELECTION;
        }

        public /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            return this.read(jsonReader);
        }

        public /* synthetic */ void write(JsonWriter jsonWriter, Object object) throws IOException {
            this.write(jsonWriter, (RegionSelectionPreference)((Object)object));
        }
    }
}

