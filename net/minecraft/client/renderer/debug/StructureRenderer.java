/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<ResourceKey<Level>, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
    private final Map<ResourceKey<Level>, Map<String, StructuresDebugPayload.PieceInfo>> postPieces = Maps.newIdentityHashMap();
    private static final int MAX_RENDER_DIST = 500;

    public StructureRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Map<String, StructuresDebugPayload.PieceInfo> $$10;
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        ResourceKey<Level> $$6 = this.minecraft.level.dimension();
        BlockPos $$7 = BlockPos.containing($$5.getPosition().x, 0.0, $$5.getPosition().z);
        VertexConsumer $$8 = $$1.getBuffer(RenderType.lines());
        if (this.postMainBoxes.containsKey($$6)) {
            for (BoundingBox $$9 : this.postMainBoxes.get($$6).values()) {
                if (!$$7.closerThan($$9.getCenter(), 500.0)) continue;
                ShapeRenderer.renderLineBox($$0, $$8, (double)$$9.minX() - $$2, (double)$$9.minY() - $$3, (double)$$9.minZ() - $$4, (double)($$9.maxX() + 1) - $$2, (double)($$9.maxY() + 1) - $$3, (double)($$9.maxZ() + 1) - $$4, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (($$10 = this.postPieces.get($$6)) != null) {
            for (StructuresDebugPayload.PieceInfo $$11 : $$10.values()) {
                BoundingBox $$12 = $$11.boundingBox();
                if (!$$7.closerThan($$12.getCenter(), 500.0)) continue;
                if ($$11.isStart()) {
                    ShapeRenderer.renderLineBox($$0, $$8, (double)$$12.minX() - $$2, (double)$$12.minY() - $$3, (double)$$12.minZ() - $$4, (double)($$12.maxX() + 1) - $$2, (double)($$12.maxY() + 1) - $$3, (double)($$12.maxZ() + 1) - $$4, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
                    continue;
                }
                ShapeRenderer.renderLineBox($$0, $$8, (double)$$12.minX() - $$2, (double)$$12.minY() - $$3, (double)$$12.minZ() - $$4, (double)($$12.maxX() + 1) - $$2, (double)($$12.maxY() + 1) - $$3, (double)($$12.maxZ() + 1) - $$4, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    public void addBoundingBox(BoundingBox $$02, List<StructuresDebugPayload.PieceInfo> $$1, ResourceKey<Level> $$2) {
        this.postMainBoxes.computeIfAbsent($$2, $$0 -> new HashMap()).put($$02.toString(), $$02);
        Map $$3 = this.postPieces.computeIfAbsent($$2, $$0 -> new HashMap());
        for (StructuresDebugPayload.PieceInfo $$4 : $$1) {
            $$3.put($$4.boundingBox().toString(), $$4);
        }
    }

    @Override
    public void clear() {
        this.postMainBoxes.clear();
        this.postPieces.clear();
    }
}

