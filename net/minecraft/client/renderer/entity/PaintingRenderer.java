/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import org.joml.Quaternionfc;

public class PaintingRenderer
extends EntityRenderer<Painting, PaintingRenderState> {
    public PaintingRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(PaintingRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        PaintingVariant $$4 = $$0.variant;
        if ($$4 == null) {
            return;
        }
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180 - $$0.direction.get2DDataValue() * 90));
        PaintingTextureManager $$5 = Minecraft.getInstance().getPaintingTextures();
        TextureAtlasSprite $$6 = $$5.getBackSprite();
        VertexConsumer $$7 = $$2.getBuffer(RenderType.entitySolidZOffsetForward($$6.atlasLocation()));
        this.a($$1, $$7, $$0.lightCoords, $$4.width(), $$4.height(), $$5.get($$4), $$6);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public PaintingRenderState createRenderState() {
        return new PaintingRenderState();
    }

    @Override
    public void extractRenderState(Painting $$0, PaintingRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        Direction $$3 = $$0.getDirection();
        PaintingVariant $$4 = $$0.getVariant().value();
        $$1.direction = $$3;
        $$1.variant = $$4;
        int $$5 = $$4.width();
        int $$6 = $$4.height();
        if ($$1.lightCoords.length != $$5 * $$6) {
            $$1.lightCoords = new int[$$5 * $$6];
        }
        float $$7 = (float)(-$$5) / 2.0f;
        float $$8 = (float)(-$$6) / 2.0f;
        Level $$9 = $$0.level();
        for (int $$10 = 0; $$10 < $$6; ++$$10) {
            for (int $$11 = 0; $$11 < $$5; ++$$11) {
                float $$12 = (float)$$11 + $$7 + 0.5f;
                float $$13 = (float)$$10 + $$8 + 0.5f;
                int $$14 = $$0.getBlockX();
                int $$15 = Mth.floor($$0.getY() + (double)$$13);
                int $$16 = $$0.getBlockZ();
                switch ($$3) {
                    case NORTH: {
                        $$14 = Mth.floor($$0.getX() + (double)$$12);
                        break;
                    }
                    case WEST: {
                        $$16 = Mth.floor($$0.getZ() - (double)$$12);
                        break;
                    }
                    case SOUTH: {
                        $$14 = Mth.floor($$0.getX() - (double)$$12);
                        break;
                    }
                    case EAST: {
                        $$16 = Mth.floor($$0.getZ() + (double)$$12);
                    }
                }
                $$1.lightCoords[$$11 + $$10 * $$5] = LevelRenderer.getLightColor($$9, new BlockPos($$14, $$15, $$16));
            }
        }
    }

    private void a(PoseStack $$0, VertexConsumer $$1, int[] $$2, int $$3, int $$4, TextureAtlasSprite $$5, TextureAtlasSprite $$6) {
        PoseStack.Pose $$7 = $$0.last();
        float $$8 = (float)(-$$3) / 2.0f;
        float $$9 = (float)(-$$4) / 2.0f;
        float $$10 = 0.03125f;
        float $$11 = $$6.getU0();
        float $$12 = $$6.getU1();
        float $$13 = $$6.getV0();
        float $$14 = $$6.getV1();
        float $$15 = $$6.getU0();
        float $$16 = $$6.getU1();
        float $$17 = $$6.getV0();
        float $$18 = $$6.getV(0.0625f);
        float $$19 = $$6.getU0();
        float $$20 = $$6.getU(0.0625f);
        float $$21 = $$6.getV0();
        float $$22 = $$6.getV1();
        double $$23 = 1.0 / (double)$$3;
        double $$24 = 1.0 / (double)$$4;
        for (int $$25 = 0; $$25 < $$3; ++$$25) {
            for (int $$26 = 0; $$26 < $$4; ++$$26) {
                float $$27 = $$8 + (float)($$25 + 1);
                float $$28 = $$8 + (float)$$25;
                float $$29 = $$9 + (float)($$26 + 1);
                float $$30 = $$9 + (float)$$26;
                int $$31 = $$2[$$25 + $$26 * $$3];
                float $$32 = $$5.getU((float)($$23 * (double)($$3 - $$25)));
                float $$33 = $$5.getU((float)($$23 * (double)($$3 - ($$25 + 1))));
                float $$34 = $$5.getV((float)($$24 * (double)($$4 - $$26)));
                float $$35 = $$5.getV((float)($$24 * (double)($$4 - ($$26 + 1))));
                this.vertex($$7, $$1, $$27, $$30, $$33, $$34, -0.03125f, 0, 0, -1, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$32, $$34, -0.03125f, 0, 0, -1, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$32, $$35, -0.03125f, 0, 0, -1, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$33, $$35, -0.03125f, 0, 0, -1, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$12, $$13, 0.03125f, 0, 0, 1, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$11, $$13, 0.03125f, 0, 0, 1, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$11, $$14, 0.03125f, 0, 0, 1, $$31);
                this.vertex($$7, $$1, $$27, $$30, $$12, $$14, 0.03125f, 0, 0, 1, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$15, $$17, -0.03125f, 0, 1, 0, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$16, $$17, -0.03125f, 0, 1, 0, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$16, $$18, 0.03125f, 0, 1, 0, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$15, $$18, 0.03125f, 0, 1, 0, $$31);
                this.vertex($$7, $$1, $$27, $$30, $$15, $$17, 0.03125f, 0, -1, 0, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$16, $$17, 0.03125f, 0, -1, 0, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$16, $$18, -0.03125f, 0, -1, 0, $$31);
                this.vertex($$7, $$1, $$27, $$30, $$15, $$18, -0.03125f, 0, -1, 0, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$20, $$21, 0.03125f, -1, 0, 0, $$31);
                this.vertex($$7, $$1, $$27, $$30, $$20, $$22, 0.03125f, -1, 0, 0, $$31);
                this.vertex($$7, $$1, $$27, $$30, $$19, $$22, -0.03125f, -1, 0, 0, $$31);
                this.vertex($$7, $$1, $$27, $$29, $$19, $$21, -0.03125f, -1, 0, 0, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$20, $$21, -0.03125f, 1, 0, 0, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$20, $$22, -0.03125f, 1, 0, 0, $$31);
                this.vertex($$7, $$1, $$28, $$30, $$19, $$22, 0.03125f, 1, 0, 0, $$31);
                this.vertex($$7, $$1, $$28, $$29, $$19, $$21, 0.03125f, 1, 0, 0, $$31);
            }
        }
    }

    private void vertex(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, float $$6, int $$7, int $$8, int $$9, int $$10) {
        $$1.addVertex($$0, $$2, $$3, $$6).setColor(-1).setUv($$4, $$5).setOverlay(OverlayTexture.NO_OVERLAY).setLight($$10).setNormal($$0, $$7, $$8, $$9);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

