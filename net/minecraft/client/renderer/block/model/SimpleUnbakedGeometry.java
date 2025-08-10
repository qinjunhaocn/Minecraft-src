/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.block.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.core.Direction;

public record SimpleUnbakedGeometry(List<BlockElement> elements) implements UnbakedGeometry
{
    @Override
    public QuadCollection bake(TextureSlots $$0, ModelBaker $$1, ModelState $$2, ModelDebugName $$3) {
        return SimpleUnbakedGeometry.bake(this.elements, $$0, $$1.sprites(), $$2, $$3);
    }

    public static QuadCollection bake(List<BlockElement> $$0, TextureSlots $$1, SpriteGetter $$2, ModelState $$3, ModelDebugName $$4) {
        QuadCollection.Builder $$5 = new QuadCollection.Builder();
        for (BlockElement $$62 : $$0) {
            $$62.faces().forEach(($$6, $$7) -> {
                TextureAtlasSprite $$8 = $$2.resolveSlot($$1, $$7.texture(), $$4);
                if ($$7.cullForDirection() == null) {
                    $$5.addUnculledFace(SimpleUnbakedGeometry.bakeFace($$62, $$7, $$8, $$6, $$3));
                } else {
                    $$5.addCulledFace(Direction.rotate($$3.transformation().getMatrix(), $$7.cullForDirection()), SimpleUnbakedGeometry.bakeFace($$62, $$7, $$8, $$6, $$3));
                }
            });
        }
        return $$5.build();
    }

    private static BakedQuad bakeFace(BlockElement $$0, BlockElementFace $$1, TextureAtlasSprite $$2, Direction $$3, ModelState $$4) {
        return FaceBakery.bakeQuad($$0.from(), $$0.to(), $$1, $$2, $$3, $$4, $$0.rotation(), $$0.shade(), $$0.lightEmission());
    }
}

