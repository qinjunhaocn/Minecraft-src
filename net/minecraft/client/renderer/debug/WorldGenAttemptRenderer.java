/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

public class WorldGenAttemptRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final List<BlockPos> toRender = Lists.newArrayList();
    private final List<Float> scales = Lists.newArrayList();
    private final List<Float> alphas = Lists.newArrayList();
    private final List<Float> reds = Lists.newArrayList();
    private final List<Float> greens = Lists.newArrayList();
    private final List<Float> blues = Lists.newArrayList();

    public void addPos(BlockPos $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        this.toRender.add($$0);
        this.scales.add(Float.valueOf($$1));
        this.alphas.add(Float.valueOf($$5));
        this.reds.add(Float.valueOf($$2));
        this.greens.add(Float.valueOf($$3));
        this.blues.add(Float.valueOf($$4));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        VertexConsumer $$5 = $$1.getBuffer(RenderType.debugFilledBox());
        for (int $$6 = 0; $$6 < this.toRender.size(); ++$$6) {
            BlockPos $$7 = this.toRender.get($$6);
            Float $$8 = this.scales.get($$6);
            float $$9 = $$8.floatValue() / 2.0f;
            ShapeRenderer.addChainedFilledBoxVertices($$0, $$5, (double)((float)$$7.getX() + 0.5f - $$9) - $$2, (double)((float)$$7.getY() + 0.5f - $$9) - $$3, (double)((float)$$7.getZ() + 0.5f - $$9) - $$4, (double)((float)$$7.getX() + 0.5f + $$9) - $$2, (double)((float)$$7.getY() + 0.5f + $$9) - $$3, (double)((float)$$7.getZ() + 0.5f + $$9) - $$4, this.reds.get($$6).floatValue(), this.greens.get($$6).floatValue(), this.blues.get($$6).floatValue(), this.alphas.get($$6).floatValue());
        }
    }
}

