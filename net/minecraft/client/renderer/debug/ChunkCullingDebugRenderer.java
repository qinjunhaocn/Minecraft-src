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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ChunkCullingDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    public static final Direction[] DIRECTIONS = Direction.values();
    private final Minecraft minecraft;

    public ChunkCullingDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Frustum $$26;
        LevelRenderer $$5 = this.minecraft.levelRenderer;
        if (this.minecraft.sectionPath || this.minecraft.sectionVisibility) {
            SectionOcclusionGraph $$6 = $$5.getSectionOcclusionGraph();
            for (SectionRenderDispatcher.RenderSection $$7 : $$5.getVisibleSections()) {
                SectionOcclusionGraph.Node $$8 = $$6.getNode($$7);
                if ($$8 == null) continue;
                BlockPos $$9 = $$7.getRenderOrigin();
                $$0.pushPose();
                $$0.translate((double)$$9.getX() - $$2, (double)$$9.getY() - $$3, (double)$$9.getZ() - $$4);
                Matrix4f $$10 = $$0.last().pose();
                if (this.minecraft.sectionPath) {
                    VertexConsumer $$11 = $$1.getBuffer(RenderType.lines());
                    int $$12 = $$8.step == 0 ? 0 : Mth.hsvToRgb((float)$$8.step / 50.0f, 0.9f, 0.9f);
                    int $$13 = $$12 >> 16 & 0xFF;
                    int $$14 = $$12 >> 8 & 0xFF;
                    int $$15 = $$12 & 0xFF;
                    for (int $$16 = 0; $$16 < DIRECTIONS.length; ++$$16) {
                        if (!$$8.hasSourceDirection($$16)) continue;
                        Direction $$17 = DIRECTIONS[$$16];
                        $$11.addVertex($$10, 8.0f, 8.0f, 8.0f).setColor($$13, $$14, $$15, 255).setNormal($$17.getStepX(), $$17.getStepY(), $$17.getStepZ());
                        $$11.addVertex($$10, (float)(8 - 16 * $$17.getStepX()), (float)(8 - 16 * $$17.getStepY()), (float)(8 - 16 * $$17.getStepZ())).setColor($$13, $$14, $$15, 255).setNormal($$17.getStepX(), $$17.getStepY(), $$17.getStepZ());
                    }
                }
                if (this.minecraft.sectionVisibility && $$7.getSectionMesh().hasRenderableLayers()) {
                    VertexConsumer $$18 = $$1.getBuffer(RenderType.lines());
                    int $$19 = 0;
                    for (Direction $$20 : DIRECTIONS) {
                        for (Direction $$21 : DIRECTIONS) {
                            boolean $$22 = $$7.getSectionMesh().facesCanSeeEachother($$20, $$21);
                            if ($$22) continue;
                            ++$$19;
                            $$18.addVertex($$10, (float)(8 + 8 * $$20.getStepX()), (float)(8 + 8 * $$20.getStepY()), (float)(8 + 8 * $$20.getStepZ())).setColor(255, 0, 0, 255).setNormal($$20.getStepX(), $$20.getStepY(), $$20.getStepZ());
                            $$18.addVertex($$10, (float)(8 + 8 * $$21.getStepX()), (float)(8 + 8 * $$21.getStepY()), (float)(8 + 8 * $$21.getStepZ())).setColor(255, 0, 0, 255).setNormal($$21.getStepX(), $$21.getStepY(), $$21.getStepZ());
                        }
                    }
                    if ($$19 > 0) {
                        VertexConsumer $$23 = $$1.getBuffer(RenderType.debugQuads());
                        float $$24 = 0.5f;
                        float $$25 = 0.2f;
                        $$23.addVertex($$10, 0.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 15.5f, 0.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 15.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 15.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                        $$23.addVertex($$10, 0.5f, 0.5f, 15.5f).setColor(0.9f, 0.9f, 0.0f, 0.2f);
                    }
                }
                $$0.popPose();
            }
        }
        if (($$26 = $$5.getCapturedFrustum()) != null) {
            $$0.pushPose();
            $$0.translate((float)($$26.getCamX() - $$2), (float)($$26.getCamY() - $$3), (float)($$26.getCamZ() - $$4));
            Matrix4f $$27 = $$0.last().pose();
            Vector4f[] $$28 = $$26.a();
            VertexConsumer $$29 = $$1.getBuffer(RenderType.debugQuads());
            this.a($$29, $$27, $$28, 0, 1, 2, 3, 0, 1, 1);
            this.a($$29, $$27, $$28, 4, 5, 6, 7, 1, 0, 0);
            this.a($$29, $$27, $$28, 0, 1, 5, 4, 1, 1, 0);
            this.a($$29, $$27, $$28, 2, 3, 7, 6, 0, 0, 1);
            this.a($$29, $$27, $$28, 0, 4, 7, 3, 0, 1, 0);
            this.a($$29, $$27, $$28, 1, 5, 6, 2, 1, 0, 1);
            VertexConsumer $$30 = $$1.getBuffer(RenderType.lines());
            this.addFrustumVertex($$30, $$27, $$28[0]);
            this.addFrustumVertex($$30, $$27, $$28[1]);
            this.addFrustumVertex($$30, $$27, $$28[1]);
            this.addFrustumVertex($$30, $$27, $$28[2]);
            this.addFrustumVertex($$30, $$27, $$28[2]);
            this.addFrustumVertex($$30, $$27, $$28[3]);
            this.addFrustumVertex($$30, $$27, $$28[3]);
            this.addFrustumVertex($$30, $$27, $$28[0]);
            this.addFrustumVertex($$30, $$27, $$28[4]);
            this.addFrustumVertex($$30, $$27, $$28[5]);
            this.addFrustumVertex($$30, $$27, $$28[5]);
            this.addFrustumVertex($$30, $$27, $$28[6]);
            this.addFrustumVertex($$30, $$27, $$28[6]);
            this.addFrustumVertex($$30, $$27, $$28[7]);
            this.addFrustumVertex($$30, $$27, $$28[7]);
            this.addFrustumVertex($$30, $$27, $$28[4]);
            this.addFrustumVertex($$30, $$27, $$28[0]);
            this.addFrustumVertex($$30, $$27, $$28[4]);
            this.addFrustumVertex($$30, $$27, $$28[1]);
            this.addFrustumVertex($$30, $$27, $$28[5]);
            this.addFrustumVertex($$30, $$27, $$28[2]);
            this.addFrustumVertex($$30, $$27, $$28[6]);
            this.addFrustumVertex($$30, $$27, $$28[3]);
            this.addFrustumVertex($$30, $$27, $$28[7]);
            $$0.popPose();
        }
    }

    private void addFrustumVertex(VertexConsumer $$0, Matrix4f $$1, Vector4f $$2) {
        $$0.addVertex($$1, $$2.x(), $$2.y(), $$2.z()).setColor(-16777216).setNormal(0.0f, 0.0f, -1.0f);
    }

    private void a(VertexConsumer $$0, Matrix4f $$1, Vector4f[] $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        float $$10 = 0.25f;
        $$0.addVertex($$1, $$2[$$3].x(), $$2[$$3].y(), $$2[$$3].z()).setColor((float)$$7, (float)$$8, (float)$$9, 0.25f);
        $$0.addVertex($$1, $$2[$$4].x(), $$2[$$4].y(), $$2[$$4].z()).setColor((float)$$7, (float)$$8, (float)$$9, 0.25f);
        $$0.addVertex($$1, $$2[$$5].x(), $$2[$$5].y(), $$2[$$5].z()).setColor((float)$$7, (float)$$8, (float)$$9, 0.25f);
        $$0.addVertex($$1, $$2[$$6].x(), $$2[$$6].y(), $$2[$$6].z()).setColor((float)$$7, (float)$$8, (float)$$9, 0.25f);
    }
}

