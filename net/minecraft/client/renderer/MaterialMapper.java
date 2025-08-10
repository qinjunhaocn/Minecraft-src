/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public record MaterialMapper(ResourceLocation sheet, String prefix) {
    public Material apply(ResourceLocation $$0) {
        return new Material(this.sheet, $$0.withPrefix(this.prefix + "/"));
    }

    public Material defaultNamespaceApply(String $$0) {
        return this.apply(ResourceLocation.withDefaultNamespace($$0));
    }
}

