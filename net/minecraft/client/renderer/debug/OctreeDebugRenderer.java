/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Octree;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class OctreeDebugRenderer {
    private final Minecraft minecraft;

    public OctreeDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(PoseStack $$0, Frustum $$1, MultiBufferSource $$2, double $$3, double $$4, double $$5) {
        Octree $$62 = this.minecraft.levelRenderer.getSectionOcclusionGraph().getOctree();
        MutableInt $$72 = new MutableInt(0);
        $$62.visitNodes(($$6, $$7, $$8, $$9) -> this.renderNode($$6, $$0, $$2, $$3, $$4, $$5, $$8, $$7, $$72, $$9), $$1, 32);
    }

    private void renderNode(Octree.Node $$0, PoseStack $$1, MultiBufferSource $$2, double $$3, double $$4, double $$5, int $$6, boolean $$7, MutableInt $$8, boolean $$9) {
        AABB $$10 = $$0.getAABB();
        double $$11 = $$10.getXsize();
        long $$12 = Math.round($$11 / 16.0);
        if ($$12 == 1L) {
            $$8.add(1);
            double $$13 = $$10.getCenter().x;
            double $$14 = $$10.getCenter().y;
            double $$15 = $$10.getCenter().z;
            int $$16 = $$9 ? -16711936 : -1;
            DebugRenderer.renderFloatingText($$1, $$2, String.valueOf($$8.getValue()), $$13, $$14, $$15, $$16, 0.3f);
        }
        VertexConsumer $$17 = $$2.getBuffer(RenderType.lines());
        long $$18 = $$12 + 5L;
        ShapeRenderer.renderLineBox($$1, $$17, $$10.deflate(0.1 * (double)$$6).move(-$$3, -$$4, -$$5), OctreeDebugRenderer.getColorComponent($$18, 0.3f), OctreeDebugRenderer.getColorComponent($$18, 0.8f), OctreeDebugRenderer.getColorComponent($$18, 0.5f), $$7 ? 0.4f : 1.0f);
    }

    private static float getColorComponent(long $$0, float $$1) {
        float $$2 = 0.1f;
        return Mth.frac($$1 * (float)$$0) * 0.9f + 0.1f;
    }
}

