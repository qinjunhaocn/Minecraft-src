/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 */
package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.RealmsSetting;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RealmsSlot
implements ReflectionBasedSerialization {
    @SerializedName(value="slotId")
    public int slotId;
    @SerializedName(value="options")
    @JsonAdapter(value=RealmsWorldOptionsJsonAdapter.class)
    public RealmsWorldOptions options;
    @SerializedName(value="settings")
    public List<RealmsSetting> settings;

    public RealmsSlot(int $$0, RealmsWorldOptions $$1, List<RealmsSetting> $$2) {
        this.slotId = $$0;
        this.options = $$1;
        this.settings = $$2;
    }

    public static RealmsSlot defaults(int $$0) {
        return new RealmsSlot($$0, RealmsWorldOptions.createEmptyDefaults(), List.of((Object)RealmsSetting.hardcoreSetting(false)));
    }

    public RealmsSlot clone() {
        return new RealmsSlot(this.slotId, this.options.clone(), new ArrayList<RealmsSetting>(this.settings));
    }

    public boolean isHardcore() {
        return RealmsSetting.isHardcore(this.settings);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

    static class RealmsWorldOptionsJsonAdapter
    extends TypeAdapter<RealmsWorldOptions> {
        private RealmsWorldOptionsJsonAdapter() {
        }

        public void write(JsonWriter $$0, RealmsWorldOptions $$1) throws IOException {
            $$0.jsonValue(new GuardedSerializer().toJson($$1));
        }

        public RealmsWorldOptions read(JsonReader $$0) throws IOException {
            String $$1 = $$0.nextString();
            return RealmsWorldOptions.parse(new GuardedSerializer(), $$1);
        }

        public /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
            return this.read(jsonReader);
        }

        public /* synthetic */ void write(JsonWriter jsonWriter, Object object) throws IOException {
            this.write(jsonWriter, (RealmsWorldOptions)object);
        }
    }
}

