/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;

public record ModelLayerLocation(ResourceLocation model, String layer) {
    public String toString() {
        return String.valueOf(this.model) + "#" + this.layer;
    }
}

