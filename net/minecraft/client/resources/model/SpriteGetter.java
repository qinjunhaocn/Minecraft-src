/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelDebugName;

public interface SpriteGetter {
    public TextureAtlasSprite get(Material var1, ModelDebugName var2);

    public TextureAtlasSprite reportMissingReference(String var1, ModelDebugName var2);

    default public TextureAtlasSprite resolveSlot(TextureSlots $$0, String $$1, ModelDebugName $$2) {
        Material $$3 = $$0.getMaterial($$1);
        return $$3 != null ? this.get($$3, $$2) : this.reportMissingReference($$1, $$2);
    }
}

