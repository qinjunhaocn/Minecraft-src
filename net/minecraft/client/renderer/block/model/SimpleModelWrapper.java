/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.block.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public record SimpleModelWrapper(QuadCollection quads, boolean useAmbientOcclusion, TextureAtlasSprite particleIcon) implements BlockModelPart
{
    public static SimpleModelWrapper bake(ModelBaker $$0, ResourceLocation $$1, ModelState $$2) {
        ResolvedModel $$3 = $$0.getModel($$1);
        TextureSlots $$4 = $$3.getTopTextureSlots();
        boolean $$5 = $$3.getTopAmbientOcclusion();
        TextureAtlasSprite $$6 = $$3.resolveParticleSprite($$4, $$0);
        QuadCollection $$7 = $$3.bakeTopGeometry($$4, $$0, $$2);
        return new SimpleModelWrapper($$7, $$5, $$6);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction $$0) {
        return this.quads.getQuads($$0);
    }
}

