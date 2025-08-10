/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;

public class OutlineBufferSource
implements MultiBufferSource {
    private final MultiBufferSource.BufferSource bufferSource;
    private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    private int teamR = 255;
    private int teamG = 255;
    private int teamB = 255;
    private int teamA = 255;

    public OutlineBufferSource(MultiBufferSource.BufferSource $$0) {
        this.bufferSource = $$0;
    }

    @Override
    public VertexConsumer getBuffer(RenderType $$0) {
        if ($$0.isOutline()) {
            VertexConsumer $$1 = this.outlineBufferSource.getBuffer($$0);
            return new EntityOutlineGenerator($$1, this.teamR, this.teamG, this.teamB, this.teamA);
        }
        VertexConsumer $$2 = this.bufferSource.getBuffer($$0);
        Optional<RenderType> $$3 = $$0.outline();
        if ($$3.isPresent()) {
            VertexConsumer $$4 = this.outlineBufferSource.getBuffer($$3.get());
            EntityOutlineGenerator $$5 = new EntityOutlineGenerator($$4, this.teamR, this.teamG, this.teamB, this.teamA);
            return VertexMultiConsumer.create($$5, $$2);
        }
        return $$2;
    }

    public void setColor(int $$0, int $$1, int $$2, int $$3) {
        this.teamR = $$0;
        this.teamG = $$1;
        this.teamB = $$2;
        this.teamA = $$3;
    }

    public void endOutlineBatch() {
        this.outlineBufferSource.endBatch();
    }

    record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer
    {
        public EntityOutlineGenerator(VertexConsumer $$0, int $$1, int $$2, int $$3, int $$4) {
            this($$0, ARGB.color($$4, $$1, $$2, $$3));
        }

        @Override
        public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
            this.delegate.addVertex($$0, $$1, $$2).setColor(this.color);
            return this;
        }

        @Override
        public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
            return this;
        }

        @Override
        public VertexConsumer setUv(float $$0, float $$1) {
            this.delegate.setUv($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int $$0, int $$1) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int $$0, int $$1) {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
            return this;
        }
    }
}

