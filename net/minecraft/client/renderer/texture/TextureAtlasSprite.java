/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteTicker;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSprite {
    private final ResourceLocation atlasLocation;
    private final SpriteContents contents;
    private final boolean animated;
    final int x;
    final int y;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    protected TextureAtlasSprite(ResourceLocation $$0, SpriteContents $$1, int $$2, int $$3, int $$4, int $$5) {
        this.atlasLocation = $$0;
        this.contents = $$1;
        this.animated = $$1.metadata().getSection(AnimationMetadataSection.TYPE).isPresent();
        this.x = $$4;
        this.y = $$5;
        this.u0 = (float)$$4 / (float)$$2;
        this.u1 = (float)($$4 + $$1.width()) / (float)$$2;
        this.v0 = (float)$$5 / (float)$$3;
        this.v1 = (float)($$5 + $$1.height()) / (float)$$3;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public float getU0() {
        return this.u0;
    }

    public float getU1() {
        return this.u1;
    }

    public SpriteContents contents() {
        return this.contents;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    @Nullable
    public Ticker createTicker() {
        final SpriteTicker $$0 = this.contents.createTicker();
        if ($$0 != null) {
            return new Ticker(){

                @Override
                public void tickAndUpload(GpuTexture $$02) {
                    $$0.tickAndUpload(TextureAtlasSprite.this.x, TextureAtlasSprite.this.y, $$02);
                }

                @Override
                public void close() {
                    $$0.close();
                }
            };
        }
        return null;
    }

    public float getU(float $$0) {
        float $$1 = this.u1 - this.u0;
        return this.u0 + $$1 * $$0;
    }

    public float getUOffset(float $$0) {
        float $$1 = this.u1 - this.u0;
        return ($$0 - this.u0) / $$1;
    }

    public float getV0() {
        return this.v0;
    }

    public float getV1() {
        return this.v1;
    }

    public float getV(float $$0) {
        float $$1 = this.v1 - this.v0;
        return this.v0 + $$1 * $$0;
    }

    public float getVOffset(float $$0) {
        float $$1 = this.v1 - this.v0;
        return ($$0 - this.v0) / $$1;
    }

    public ResourceLocation atlasLocation() {
        return this.atlasLocation;
    }

    public String toString() {
        return "TextureAtlasSprite{contents='" + String.valueOf(this.contents) + "', u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + "}";
    }

    public void uploadFirstFrame(GpuTexture $$0) {
        this.contents.uploadFirstFrame(this.x, this.y, $$0);
    }

    private float atlasSize() {
        float $$0 = (float)this.contents.width() / (this.u1 - this.u0);
        float $$1 = (float)this.contents.height() / (this.v1 - this.v0);
        return Math.max($$1, $$0);
    }

    public float uvShrinkRatio() {
        return 4.0f / this.atlasSize();
    }

    public VertexConsumer wrap(VertexConsumer $$0) {
        return new SpriteCoordinateExpander($$0, this);
    }

    public static interface Ticker
    extends AutoCloseable {
        public void tickAndUpload(GpuTexture var1);

        @Override
        public void close();
    }
}

