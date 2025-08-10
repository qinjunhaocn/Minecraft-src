/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteCoordinateExpander
implements VertexConsumer {
    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public SpriteCoordinateExpander(VertexConsumer $$0, TextureAtlasSprite $$1) {
        this.delegate = $$0;
        this.sprite = $$1;
    }

    @Override
    public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
        return this.delegate.addVertex($$0, $$1, $$2);
    }

    @Override
    public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
        return this.delegate.setColor($$0, $$1, $$2, $$3);
    }

    @Override
    public VertexConsumer setUv(float $$0, float $$1) {
        return this.delegate.setUv(this.sprite.getU($$0), this.sprite.getV($$1));
    }

    @Override
    public VertexConsumer setUv1(int $$0, int $$1) {
        return this.delegate.setUv1($$0, $$1);
    }

    @Override
    public VertexConsumer setUv2(int $$0, int $$1) {
        return this.delegate.setUv2($$0, $$1);
    }

    @Override
    public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
        return this.delegate.setNormal($$0, $$1, $$2);
    }

    @Override
    public void addVertex(float $$0, float $$1, float $$2, int $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10) {
        this.delegate.addVertex($$0, $$1, $$2, $$3, this.sprite.getU($$4), this.sprite.getV($$5), $$6, $$7, $$8, $$9, $$10);
    }
}

