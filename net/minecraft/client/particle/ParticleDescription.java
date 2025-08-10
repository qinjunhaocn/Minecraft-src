/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
    private final List<ResourceLocation> textures;

    private ParticleDescription(List<ResourceLocation> $$0) {
        this.textures = $$0;
    }

    public List<ResourceLocation> getTextures() {
        return this.textures;
    }

    public static ParticleDescription fromJson(JsonObject $$02) {
        JsonArray $$1 = GsonHelper.getAsJsonArray($$02, "textures", null);
        if ($$1 == null) {
            return new ParticleDescription(List.of());
        }
        List $$2 = Streams.stream($$1).map($$0 -> GsonHelper.convertToString($$0, "texture")).map(ResourceLocation::parse).collect(ImmutableList.toImmutableList());
        return new ParticleDescription($$2);
    }
}

