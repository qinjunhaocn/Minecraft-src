/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.resources.sounds;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import org.apache.commons.lang3.Validate;

public class SoundEventRegistrationSerializer
implements JsonDeserializer<SoundEventRegistration> {
    private static final FloatProvider DEFAULT_FLOAT = ConstantFloat.of(1.0f);

    public SoundEventRegistration deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
        JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "entry");
        boolean $$4 = GsonHelper.getAsBoolean($$3, "replace", false);
        String $$5 = GsonHelper.getAsString($$3, "subtitle", null);
        List<Sound> $$6 = this.getSounds($$3);
        return new SoundEventRegistration($$6, $$4, $$5);
    }

    private List<Sound> getSounds(JsonObject $$0) {
        ArrayList<Sound> $$1 = Lists.newArrayList();
        if ($$0.has("sounds")) {
            JsonArray $$2 = GsonHelper.getAsJsonArray($$0, "sounds");
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                JsonElement $$4 = $$2.get($$3);
                if (GsonHelper.isStringValue($$4)) {
                    ResourceLocation $$5 = ResourceLocation.parse(GsonHelper.convertToString($$4, "sound"));
                    $$1.add(new Sound($$5, DEFAULT_FLOAT, DEFAULT_FLOAT, 1, Sound.Type.FILE, false, false, 16));
                    continue;
                }
                $$1.add(this.getSound(GsonHelper.convertToJsonObject($$4, "sound")));
            }
        }
        return $$1;
    }

    private Sound getSound(JsonObject $$0) {
        ResourceLocation $$1 = ResourceLocation.parse(GsonHelper.getAsString($$0, "name"));
        Sound.Type $$2 = this.getType($$0, Sound.Type.FILE);
        float $$3 = GsonHelper.getAsFloat($$0, "volume", 1.0f);
        Validate.isTrue($$3 > 0.0f, "Invalid volume", new Object[0]);
        float $$4 = GsonHelper.getAsFloat($$0, "pitch", 1.0f);
        Validate.isTrue($$4 > 0.0f, "Invalid pitch", new Object[0]);
        int $$5 = GsonHelper.getAsInt($$0, "weight", 1);
        Validate.isTrue($$5 > 0, "Invalid weight", new Object[0]);
        boolean $$6 = GsonHelper.getAsBoolean($$0, "preload", false);
        boolean $$7 = GsonHelper.getAsBoolean($$0, "stream", false);
        int $$8 = GsonHelper.getAsInt($$0, "attenuation_distance", 16);
        return new Sound($$1, ConstantFloat.of($$3), ConstantFloat.of($$4), $$5, $$2, $$7, $$6, $$8);
    }

    private Sound.Type getType(JsonObject $$0, Sound.Type $$1) {
        Sound.Type $$2 = $$1;
        if ($$0.has("type")) {
            $$2 = Sound.Type.getByName(GsonHelper.getAsString($$0, "type"));
            Validate.notNull($$2, "Invalid type", new Object[0]);
        }
        return $$2;
    }

    public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return this.deserialize(jsonElement, type, jsonDeserializationContext);
    }
}

