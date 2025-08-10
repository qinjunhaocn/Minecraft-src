/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;

public class SupportBlockRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<Entity> surroundEntities = Collections.emptyList();

    public SupportBlockRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        LocalPlayer $$7;
        double $$5 = Util.getNanos();
        if ($$5 - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = $$5;
            Entity $$6 = this.minecraft.gameRenderer.getMainCamera().getEntity();
            this.surroundEntities = ImmutableList.copyOf($$6.level().getEntities($$6, $$6.getBoundingBox().inflate(16.0)));
        }
        if (($$7 = this.minecraft.player) != null && $$7.mainSupportingBlockPos.isPresent()) {
            this.drawHighlights($$0, $$1, $$2, $$3, $$4, $$7, () -> 0.0, 1.0f, 0.0f, 0.0f);
        }
        for (Entity $$8 : this.surroundEntities) {
            if ($$8 == $$7) continue;
            this.drawHighlights($$0, $$1, $$2, $$3, $$4, $$8, () -> this.getBias($$8), 0.0f, 1.0f, 0.0f);
        }
    }

    private void drawHighlights(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4, Entity $$5, DoubleSupplier $$6, float $$7, float $$8, float $$9) {
        $$5.mainSupportingBlockPos.ifPresent($$10 -> {
            double $$11 = $$6.getAsDouble();
            BlockPos $$12 = $$5.getOnPos();
            this.highlightPosition($$12, $$0, $$2, $$3, $$4, $$1, 0.02 + $$11, $$7, $$8, $$9);
            BlockPos $$13 = $$5.getOnPosLegacy();
            if (!$$13.equals($$12)) {
                this.highlightPosition($$13, $$0, $$2, $$3, $$4, $$1, 0.04 + $$11, 0.0f, 1.0f, 1.0f);
            }
        });
    }

    private double getBias(Entity $$0) {
        return 0.02 * (double)(String.valueOf((double)$$0.getId() + 0.132453657).hashCode() % 1000) / 1000.0;
    }

    private void highlightPosition(BlockPos $$0, PoseStack $$1, double $$2, double $$3, double $$4, MultiBufferSource $$5, double $$6, float $$7, float $$8, float $$9) {
        double $$10 = (double)$$0.getX() - $$2 - 2.0 * $$6;
        double $$11 = (double)$$0.getY() - $$3 - 2.0 * $$6;
        double $$12 = (double)$$0.getZ() - $$4 - 2.0 * $$6;
        double $$13 = $$10 + 1.0 + 4.0 * $$6;
        double $$14 = $$11 + 1.0 + 4.0 * $$6;
        double $$15 = $$12 + 1.0 + 4.0 * $$6;
        ShapeRenderer.renderLineBox($$1, $$5.getBuffer(RenderType.lines()), $$10, $$11, $$12, $$13, $$14, $$15, $$7, $$8, $$9, 0.4f);
        DebugRenderer.renderVoxelShape($$1, $$5.getBuffer(RenderType.lines()), this.minecraft.level.getBlockState($$0).getCollisionShape(this.minecraft.level, $$0, CollisionContext.empty()).move($$0), -$$2, -$$3, -$$4, $$7, $$8, $$9, 1.0f, false);
    }
}

