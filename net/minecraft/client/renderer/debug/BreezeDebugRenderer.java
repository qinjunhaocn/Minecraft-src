/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BreezeDebugRenderer {
    private static final int JUMP_TARGET_LINE_COLOR = ARGB.color(255, 255, 100, 255);
    private static final int TARGET_LINE_COLOR = ARGB.color(255, 100, 255, 255);
    private static final int INNER_CIRCLE_COLOR = ARGB.color(255, 0, 255, 0);
    private static final int MIDDLE_CIRCLE_COLOR = ARGB.color(255, 255, 165, 0);
    private static final int OUTER_CIRCLE_COLOR = ARGB.color(255, 255, 0, 0);
    private static final int CIRCLE_VERTICES = 20;
    private static final float SEGMENT_SIZE_RADIANS = 0.31415927f;
    private final Minecraft minecraft;
    private final Map<Integer, BreezeDebugPayload.BreezeInfo> perEntity = new HashMap<Integer, BreezeDebugPayload.BreezeInfo>();

    public BreezeDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(PoseStack $$02, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        LocalPlayer $$5 = this.minecraft.player;
        $$5.level().getEntities(EntityType.BREEZE, $$5.getBoundingBox().inflate(100.0), $$0 -> true).forEach($$62 -> {
            Optional<BreezeDebugPayload.BreezeInfo> $$7 = Optional.ofNullable(this.perEntity.get($$62.getId()));
            $$7.map(BreezeDebugPayload.BreezeInfo::attackTarget).map($$1 -> $$5.level().getEntity((int)$$1)).map($$0 -> $$0.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true))).ifPresent($$6 -> {
                BreezeDebugRenderer.drawLine($$02, $$1, $$2, $$3, $$4, $$62.position(), $$6, TARGET_LINE_COLOR);
                Vec3 $$7 = $$6.add(0.0, 0.01f, 0.0);
                BreezeDebugRenderer.drawCircle($$02.last().pose(), $$2, $$3, $$4, $$1.getBuffer(RenderType.debugLineStrip(2.0)), $$7, 4.0f, INNER_CIRCLE_COLOR);
                BreezeDebugRenderer.drawCircle($$02.last().pose(), $$2, $$3, $$4, $$1.getBuffer(RenderType.debugLineStrip(2.0)), $$7, 8.0f, MIDDLE_CIRCLE_COLOR);
                BreezeDebugRenderer.drawCircle($$02.last().pose(), $$2, $$3, $$4, $$1.getBuffer(RenderType.debugLineStrip(2.0)), $$7, 24.0f, OUTER_CIRCLE_COLOR);
            });
            $$7.map(BreezeDebugPayload.BreezeInfo::jumpTarget).ifPresent($$6 -> {
                BreezeDebugRenderer.drawLine($$02, $$1, $$2, $$3, $$4, $$62.position(), $$6.getCenter(), JUMP_TARGET_LINE_COLOR);
                DebugRenderer.renderFilledBox($$02, $$1, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf($$6)).move(-$$2, -$$3, -$$4), 1.0f, 0.0f, 0.0f, 1.0f);
            });
        });
    }

    private static void drawLine(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4, Vec3 $$5, Vec3 $$6, int $$7) {
        VertexConsumer $$8 = $$1.getBuffer(RenderType.debugLineStrip(2.0));
        $$8.addVertex($$0.last(), (float)($$5.x - $$2), (float)($$5.y - $$3), (float)($$5.z - $$4)).setColor($$7);
        $$8.addVertex($$0.last(), (float)($$6.x - $$2), (float)($$6.y - $$3), (float)($$6.z - $$4)).setColor($$7);
    }

    private static void drawCircle(Matrix4f $$0, double $$1, double $$2, double $$3, VertexConsumer $$4, Vec3 $$5, float $$6, int $$7) {
        for (int $$8 = 0; $$8 < 20; ++$$8) {
            BreezeDebugRenderer.drawCircleVertex($$8, $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        BreezeDebugRenderer.drawCircleVertex(0, $$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private static void drawCircleVertex(int $$0, Matrix4f $$1, double $$2, double $$3, double $$4, VertexConsumer $$5, Vec3 $$6, float $$7, int $$8) {
        float $$9 = (float)$$0 * 0.31415927f;
        Vec3 $$10 = $$6.add((double)$$7 * Math.cos($$9), 0.0, (double)$$7 * Math.sin($$9));
        $$5.addVertex($$1, (float)($$10.x - $$2), (float)($$10.y - $$3), (float)($$10.z - $$4)).setColor($$8);
    }

    public void clear() {
        this.perEntity.clear();
    }

    public void add(BreezeDebugPayload.BreezeInfo $$0) {
        this.perEntity.put($$0.id(), $$0);
    }
}

