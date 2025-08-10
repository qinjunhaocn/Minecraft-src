/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.world.item.ItemDisplayContext;

public record ModelRenderProperties(boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms) {
    public static ModelRenderProperties fromResolvedModel(ModelBaker $$0, ResolvedModel $$1, TextureSlots $$2) {
        TextureAtlasSprite $$3 = $$1.resolveParticleSprite($$2, $$0);
        return new ModelRenderProperties($$1.getTopGuiLight().lightLikeBlock(), $$3, $$1.getTopTransforms());
    }

    public void applyToLayer(ItemStackRenderState.LayerRenderState $$0, ItemDisplayContext $$1) {
        $$0.setUsesBlockLight(this.usesBlockLight);
        $$0.setParticleIcon(this.particleIcon);
        $$0.setTransform(this.transforms.getTransform($$1));
    }
}

