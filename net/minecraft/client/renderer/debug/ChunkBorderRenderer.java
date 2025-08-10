/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix4f;

public class ChunkBorderRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CELL_BORDER = ARGB.color(255, 0, 155, 155);
    private static final int YELLOW = ARGB.color(255, 255, 255, 0);

    public ChunkBorderRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Entity $$5 = this.minecraft.gameRenderer.getMainCamera().getEntity();
        float $$6 = (float)((double)this.minecraft.level.getMinY() - $$3);
        float $$7 = (float)((double)(this.minecraft.level.getMaxY() + 1) - $$3);
        ChunkPos $$8 = $$5.chunkPosition();
        float $$9 = (float)((double)$$8.getMinBlockX() - $$2);
        float $$10 = (float)((double)$$8.getMinBlockZ() - $$4);
        VertexConsumer $$11 = $$1.getBuffer(RenderType.debugLineStrip(1.0));
        Matrix4f $$12 = $$0.last().pose();
        for (int $$13 = -16; $$13 <= 32; $$13 += 16) {
            for (int $$14 = -16; $$14 <= 32; $$14 += 16) {
                $$11.addVertex($$12, $$9 + (float)$$13, $$6, $$10 + (float)$$14).setColor(1.0f, 0.0f, 0.0f, 0.0f);
                $$11.addVertex($$12, $$9 + (float)$$13, $$6, $$10 + (float)$$14).setColor(1.0f, 0.0f, 0.0f, 0.5f);
                $$11.addVertex($$12, $$9 + (float)$$13, $$7, $$10 + (float)$$14).setColor(1.0f, 0.0f, 0.0f, 0.5f);
                $$11.addVertex($$12, $$9 + (float)$$13, $$7, $$10 + (float)$$14).setColor(1.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        for (int $$15 = 2; $$15 < 16; $$15 += 2) {
            int $$16 = $$15 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$11.addVertex($$12, $$9 + (float)$$15, $$6, $$10).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9 + (float)$$15, $$6, $$10).setColor($$16);
            $$11.addVertex($$12, $$9 + (float)$$15, $$7, $$10).setColor($$16);
            $$11.addVertex($$12, $$9 + (float)$$15, $$7, $$10).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9 + (float)$$15, $$6, $$10 + 16.0f).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9 + (float)$$15, $$6, $$10 + 16.0f).setColor($$16);
            $$11.addVertex($$12, $$9 + (float)$$15, $$7, $$10 + 16.0f).setColor($$16);
            $$11.addVertex($$12, $$9 + (float)$$15, $$7, $$10 + 16.0f).setColor(1.0f, 1.0f, 0.0f, 0.0f);
        }
        for (int $$17 = 2; $$17 < 16; $$17 += 2) {
            int $$18 = $$17 % 4 == 0 ? CELL_BORDER : YELLOW;
            $$11.addVertex($$12, $$9, $$6, $$10 + (float)$$17).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9, $$6, $$10 + (float)$$17).setColor($$18);
            $$11.addVertex($$12, $$9, $$7, $$10 + (float)$$17).setColor($$18);
            $$11.addVertex($$12, $$9, $$7, $$10 + (float)$$17).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9 + 16.0f, $$6, $$10 + (float)$$17).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9 + 16.0f, $$6, $$10 + (float)$$17).setColor($$18);
            $$11.addVertex($$12, $$9 + 16.0f, $$7, $$10 + (float)$$17).setColor($$18);
            $$11.addVertex($$12, $$9 + 16.0f, $$7, $$10 + (float)$$17).setColor(1.0f, 1.0f, 0.0f, 0.0f);
        }
        for (int $$19 = this.minecraft.level.getMinY(); $$19 <= this.minecraft.level.getMaxY() + 1; $$19 += 2) {
            float $$20 = (float)((double)$$19 - $$3);
            int $$21 = $$19 % 8 == 0 ? CELL_BORDER : YELLOW;
            $$11.addVertex($$12, $$9, $$20, $$10).setColor(1.0f, 1.0f, 0.0f, 0.0f);
            $$11.addVertex($$12, $$9, $$20, $$10).setColor($$21);
            $$11.addVertex($$12, $$9, $$20, $$10 + 16.0f).setColor($$21);
            $$11.addVertex($$12, $$9 + 16.0f, $$20, $$10 + 16.0f).setColor($$21);
            $$11.addVertex($$12, $$9 + 16.0f, $$20, $$10).setColor($$21);
            $$11.addVertex($$12, $$9, $$20, $$10).setColor($$21);
            $$11.addVertex($$12, $$9, $$20, $$10).setColor(1.0f, 1.0f, 0.0f, 0.0f);
        }
        $$11 = $$1.getBuffer(RenderType.debugLineStrip(2.0));
        for (int $$22 = 0; $$22 <= 16; $$22 += 16) {
            for (int $$23 = 0; $$23 <= 16; $$23 += 16) {
                $$11.addVertex($$12, $$9 + (float)$$22, $$6, $$10 + (float)$$23).setColor(0.25f, 0.25f, 1.0f, 0.0f);
                $$11.addVertex($$12, $$9 + (float)$$22, $$6, $$10 + (float)$$23).setColor(0.25f, 0.25f, 1.0f, 1.0f);
                $$11.addVertex($$12, $$9 + (float)$$22, $$7, $$10 + (float)$$23).setColor(0.25f, 0.25f, 1.0f, 1.0f);
                $$11.addVertex($$12, $$9 + (float)$$22, $$7, $$10 + (float)$$23).setColor(0.25f, 0.25f, 1.0f, 0.0f);
            }
        }
        for (int $$24 = this.minecraft.level.getMinY(); $$24 <= this.minecraft.level.getMaxY() + 1; $$24 += 16) {
            float $$25 = (float)((double)$$24 - $$3);
            $$11.addVertex($$12, $$9, $$25, $$10).setColor(0.25f, 0.25f, 1.0f, 0.0f);
            $$11.addVertex($$12, $$9, $$25, $$10).setColor(0.25f, 0.25f, 1.0f, 1.0f);
            $$11.addVertex($$12, $$9, $$25, $$10 + 16.0f).setColor(0.25f, 0.25f, 1.0f, 1.0f);
            $$11.addVertex($$12, $$9 + 16.0f, $$25, $$10 + 16.0f).setColor(0.25f, 0.25f, 1.0f, 1.0f);
            $$11.addVertex($$12, $$9 + 16.0f, $$25, $$10).setColor(0.25f, 0.25f, 1.0f, 1.0f);
            $$11.addVertex($$12, $$9, $$25, $$10).setColor(0.25f, 0.25f, 1.0f, 1.0f);
            $$11.addVertex($$12, $$9, $$25, $$10).setColor(0.25f, 0.25f, 1.0f, 0.0f);
        }
    }
}

