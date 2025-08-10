/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class LightSectionDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final Duration REFRESH_INTERVAL = Duration.ofMillis(500L);
    private static final int RADIUS = 10;
    private static final Vector4f LIGHT_AND_BLOCKS_COLOR = new Vector4f(1.0f, 1.0f, 0.0f, 0.25f);
    private static final Vector4f LIGHT_ONLY_COLOR = new Vector4f(0.25f, 0.125f, 0.0f, 0.125f);
    private final Minecraft minecraft;
    private final LightLayer lightLayer;
    private Instant lastUpdateTime = Instant.now();
    @Nullable
    private SectionData data;

    public LightSectionDebugRenderer(Minecraft $$0, LightLayer $$1) {
        this.minecraft = $$0;
        this.lightLayer = $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Instant $$5 = Instant.now();
        if (this.data == null || Duration.between(this.lastUpdateTime, $$5).compareTo(REFRESH_INTERVAL) > 0) {
            this.lastUpdateTime = $$5;
            this.data = new SectionData(this.minecraft.level.getLightEngine(), SectionPos.of(this.minecraft.player.blockPosition()), 10, this.lightLayer);
        }
        LightSectionDebugRenderer.renderEdges($$0, this.data.lightAndBlocksShape, this.data.minPos, $$1, $$2, $$3, $$4, LIGHT_AND_BLOCKS_COLOR);
        LightSectionDebugRenderer.renderEdges($$0, this.data.lightShape, this.data.minPos, $$1, $$2, $$3, $$4, LIGHT_ONLY_COLOR);
        VertexConsumer $$6 = $$1.getBuffer(RenderType.debugSectionQuads());
        LightSectionDebugRenderer.renderFaces($$0, this.data.lightAndBlocksShape, this.data.minPos, $$6, $$2, $$3, $$4, LIGHT_AND_BLOCKS_COLOR);
        LightSectionDebugRenderer.renderFaces($$0, this.data.lightShape, this.data.minPos, $$6, $$2, $$3, $$4, LIGHT_ONLY_COLOR);
    }

    private static void renderFaces(PoseStack $$0, DiscreteVoxelShape $$1, SectionPos $$2, VertexConsumer $$3, double $$4, double $$5, double $$6, Vector4f $$72) {
        $$1.forAllFaces(($$7, $$8, $$9, $$10) -> {
            int $$11 = $$8 + $$2.getX();
            int $$12 = $$9 + $$2.getY();
            int $$13 = $$10 + $$2.getZ();
            LightSectionDebugRenderer.renderFace($$0, $$3, $$7, $$4, $$5, $$6, $$11, $$12, $$13, $$72);
        });
    }

    private static void renderEdges(PoseStack $$0, DiscreteVoxelShape $$1, SectionPos $$2, MultiBufferSource $$3, double $$4, double $$5, double $$6, Vector4f $$72) {
        $$1.forAllEdges(($$7, $$8, $$9, $$10, $$11, $$12) -> {
            int $$13 = $$7 + $$2.getX();
            int $$14 = $$8 + $$2.getY();
            int $$15 = $$9 + $$2.getZ();
            int $$16 = $$10 + $$2.getX();
            int $$17 = $$11 + $$2.getY();
            int $$18 = $$12 + $$2.getZ();
            VertexConsumer $$19 = $$3.getBuffer(RenderType.debugLineStrip(1.0));
            LightSectionDebugRenderer.renderEdge($$0, $$19, $$4, $$5, $$6, $$13, $$14, $$15, $$16, $$17, $$18, $$72);
        }, true);
    }

    private static void renderFace(PoseStack $$0, VertexConsumer $$1, Direction $$2, double $$3, double $$4, double $$5, int $$6, int $$7, int $$8, Vector4f $$9) {
        float $$10 = (float)((double)SectionPos.sectionToBlockCoord($$6) - $$3);
        float $$11 = (float)((double)SectionPos.sectionToBlockCoord($$7) - $$4);
        float $$12 = (float)((double)SectionPos.sectionToBlockCoord($$8) - $$5);
        ShapeRenderer.renderFace($$0, $$1, $$2, $$10, $$11, $$12, $$10 + 16.0f, $$11 + 16.0f, $$12 + 16.0f, $$9.x(), $$9.y(), $$9.z(), $$9.w());
    }

    private static void renderEdge(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10, Vector4f $$11) {
        float $$12 = (float)((double)SectionPos.sectionToBlockCoord($$5) - $$2);
        float $$13 = (float)((double)SectionPos.sectionToBlockCoord($$6) - $$3);
        float $$14 = (float)((double)SectionPos.sectionToBlockCoord($$7) - $$4);
        float $$15 = (float)((double)SectionPos.sectionToBlockCoord($$8) - $$2);
        float $$16 = (float)((double)SectionPos.sectionToBlockCoord($$9) - $$3);
        float $$17 = (float)((double)SectionPos.sectionToBlockCoord($$10) - $$4);
        Matrix4f $$18 = $$0.last().pose();
        $$1.addVertex($$18, $$12, $$13, $$14).setColor($$11.x(), $$11.y(), $$11.z(), 1.0f);
        $$1.addVertex($$18, $$15, $$16, $$17).setColor($$11.x(), $$11.y(), $$11.z(), 1.0f);
    }

    static final class SectionData {
        final DiscreteVoxelShape lightAndBlocksShape;
        final DiscreteVoxelShape lightShape;
        final SectionPos minPos;

        SectionData(LevelLightEngine $$0, SectionPos $$1, int $$2, LightLayer $$3) {
            int $$4 = $$2 * 2 + 1;
            this.lightAndBlocksShape = new BitSetDiscreteVoxelShape($$4, $$4, $$4);
            this.lightShape = new BitSetDiscreteVoxelShape($$4, $$4, $$4);
            for (int $$5 = 0; $$5 < $$4; ++$$5) {
                for (int $$6 = 0; $$6 < $$4; ++$$6) {
                    for (int $$7 = 0; $$7 < $$4; ++$$7) {
                        SectionPos $$8 = SectionPos.of($$1.x() + $$7 - $$2, $$1.y() + $$6 - $$2, $$1.z() + $$5 - $$2);
                        LayerLightSectionStorage.SectionType $$9 = $$0.getDebugSectionType($$3, $$8);
                        if ($$9 == LayerLightSectionStorage.SectionType.LIGHT_AND_DATA) {
                            this.lightAndBlocksShape.fill($$7, $$6, $$5);
                            this.lightShape.fill($$7, $$6, $$5);
                            continue;
                        }
                        if ($$9 != LayerLightSectionStorage.SectionType.LIGHT_ONLY) continue;
                        this.lightShape.fill($$7, $$6, $$5);
                    }
                }
            }
            this.minPos = SectionPos.of($$1.x() - $$2, $$1.y() - $$2, $$1.z() - $$2);
        }
    }
}

