/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Map<Integer, Path> pathMap = Maps.newHashMap();
    private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
    private final Map<Integer, Long> creationMap = Maps.newHashMap();
    private static final long TIMEOUT = 5000L;
    private static final float MAX_RENDER_DIST = 80.0f;
    private static final boolean SHOW_OPEN_CLOSED = true;
    private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
    private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
    private static final boolean SHOW_GROUND_LABELS = true;
    private static final float TEXT_SCALE = 0.02f;

    public void addPath(int $$0, Path $$1, float $$2) {
        this.pathMap.put($$0, $$1);
        this.creationMap.put($$0, Util.getMillis());
        this.pathMaxDist.put($$0, Float.valueOf($$2));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        if (this.pathMap.isEmpty()) {
            return;
        }
        long $$5 = Util.getMillis();
        for (Integer $$6 : this.pathMap.keySet()) {
            Path $$7 = this.pathMap.get($$6);
            float $$8 = this.pathMaxDist.get($$6).floatValue();
            PathfindingRenderer.renderPath($$0, $$1, $$7, $$8, true, true, $$2, $$3, $$4);
        }
        for (Integer $$9 : this.creationMap.keySet().toArray(new Integer[0])) {
            if ($$5 - this.creationMap.get($$9) <= 5000L) continue;
            this.pathMap.remove($$9);
            this.creationMap.remove($$9);
        }
    }

    public static void renderPath(PoseStack $$0, MultiBufferSource $$1, Path $$2, float $$3, boolean $$4, boolean $$5, double $$6, double $$7, double $$8) {
        PathfindingRenderer.renderPathLine($$0, $$1.getBuffer(RenderType.debugLineStrip(6.0)), $$2, $$6, $$7, $$8);
        BlockPos $$9 = $$2.getTarget();
        if (PathfindingRenderer.distanceToCamera($$9, $$6, $$7, $$8) <= 80.0f) {
            DebugRenderer.renderFilledBox($$0, $$1, new AABB((float)$$9.getX() + 0.25f, (float)$$9.getY() + 0.25f, (double)$$9.getZ() + 0.25, (float)$$9.getX() + 0.75f, (float)$$9.getY() + 0.75f, (float)$$9.getZ() + 0.75f).move(-$$6, -$$7, -$$8), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int $$10 = 0; $$10 < $$2.getNodeCount(); ++$$10) {
                Node $$11 = $$2.getNode($$10);
                if (!(PathfindingRenderer.distanceToCamera($$11.asBlockPos(), $$6, $$7, $$8) <= 80.0f)) continue;
                float $$12 = $$10 == $$2.getNextNodeIndex() ? 1.0f : 0.0f;
                float $$13 = $$10 == $$2.getNextNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.renderFilledBox($$0, $$1, new AABB((float)$$11.x + 0.5f - $$3, (float)$$11.y + 0.01f * (float)$$10, (float)$$11.z + 0.5f - $$3, (float)$$11.x + 0.5f + $$3, (float)$$11.y + 0.25f + 0.01f * (float)$$10, (float)$$11.z + 0.5f + $$3).move(-$$6, -$$7, -$$8), $$12, 0.0f, $$13, 0.5f);
            }
        }
        Path.DebugData $$14 = $$2.debugData();
        if ($$4 && $$14 != null) {
            for (Node $$15 : $$14.b()) {
                if (!(PathfindingRenderer.distanceToCamera($$15.asBlockPos(), $$6, $$7, $$8) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox($$0, $$1, new AABB((float)$$15.x + 0.5f - $$3 / 2.0f, (float)$$15.y + 0.01f, (float)$$15.z + 0.5f - $$3 / 2.0f, (float)$$15.x + 0.5f + $$3 / 2.0f, (double)$$15.y + 0.1, (float)$$15.z + 0.5f + $$3 / 2.0f).move(-$$6, -$$7, -$$8), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (Node $$16 : $$14.a()) {
                if (!(PathfindingRenderer.distanceToCamera($$16.asBlockPos(), $$6, $$7, $$8) <= 80.0f)) continue;
                DebugRenderer.renderFilledBox($$0, $$1, new AABB((float)$$16.x + 0.5f - $$3 / 2.0f, (float)$$16.y + 0.01f, (float)$$16.z + 0.5f - $$3 / 2.0f, (float)$$16.x + 0.5f + $$3 / 2.0f, (double)$$16.y + 0.1, (float)$$16.z + 0.5f + $$3 / 2.0f).move(-$$6, -$$7, -$$8), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if ($$5) {
            for (int $$17 = 0; $$17 < $$2.getNodeCount(); ++$$17) {
                Node $$18 = $$2.getNode($$17);
                if (!(PathfindingRenderer.distanceToCamera($$18.asBlockPos(), $$6, $$7, $$8) <= 80.0f)) continue;
                DebugRenderer.renderFloatingText($$0, $$1, String.valueOf((Object)$$18.type), (double)$$18.x + 0.5, (double)$$18.y + 0.75, (double)$$18.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.renderFloatingText($$0, $$1, String.format(Locale.ROOT, "%.2f", Float.valueOf($$18.costMalus)), (double)$$18.x + 0.5, (double)$$18.y + 0.25, (double)$$18.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void renderPathLine(PoseStack $$0, VertexConsumer $$1, Path $$2, double $$3, double $$4, double $$5) {
        for (int $$6 = 0; $$6 < $$2.getNodeCount(); ++$$6) {
            Node $$7 = $$2.getNode($$6);
            if (PathfindingRenderer.distanceToCamera($$7.asBlockPos(), $$3, $$4, $$5) > 80.0f) continue;
            float $$8 = (float)$$6 / (float)$$2.getNodeCount() * 0.33f;
            int $$9 = $$6 == 0 ? 0 : Mth.hsvToRgb($$8, 0.9f, 0.9f);
            int $$10 = $$9 >> 16 & 0xFF;
            int $$11 = $$9 >> 8 & 0xFF;
            int $$12 = $$9 & 0xFF;
            $$1.addVertex($$0.last(), (float)((double)$$7.x - $$3 + 0.5), (float)((double)$$7.y - $$4 + 0.5), (float)((double)$$7.z - $$5 + 0.5)).setColor($$10, $$11, $$12, 255);
        }
    }

    private static float distanceToCamera(BlockPos $$0, double $$1, double $$2, double $$3) {
        return (float)(Math.abs((double)$$0.getX() - $$1) + Math.abs((double)$$0.getY() - $$2) + Math.abs((double)$$0.getZ() - $$3));
    }
}

