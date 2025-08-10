/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.client.data.models.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.resources.ResourceLocation;

public class DelegatedModel
implements ModelInstance {
    private final ResourceLocation parent;

    public DelegatedModel(ResourceLocation $$0) {
        this.parent = $$0;
    }

    @Override
    public JsonElement get() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("parent", this.parent.toString());
        return $$0;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

