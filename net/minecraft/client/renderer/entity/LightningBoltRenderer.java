/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LightningBoltRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.joml.Matrix4f;

public class LightningBoltRenderer
extends EntityRenderer<LightningBolt, LightningBoltRenderState> {
    public LightningBoltRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(LightningBoltRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        float[] $$4 = new float[8];
        float[] $$5 = new float[8];
        float $$6 = 0.0f;
        float $$7 = 0.0f;
        RandomSource $$8 = RandomSource.create($$0.seed);
        for (int $$9 = 7; $$9 >= 0; --$$9) {
            $$4[$$9] = $$6;
            $$5[$$9] = $$7;
            $$6 += (float)($$8.nextInt(11) - 5);
            $$7 += (float)($$8.nextInt(11) - 5);
        }
        VertexConsumer $$10 = $$2.getBuffer(RenderType.lightning());
        Matrix4f $$11 = $$1.last().pose();
        for (int $$12 = 0; $$12 < 4; ++$$12) {
            RandomSource $$13 = RandomSource.create($$0.seed);
            for (int $$14 = 0; $$14 < 3; ++$$14) {
                int $$15 = 7;
                int $$16 = 0;
                if ($$14 > 0) {
                    $$15 = 7 - $$14;
                }
                if ($$14 > 0) {
                    $$16 = $$15 - 2;
                }
                float $$17 = $$4[$$15] - $$6;
                float $$18 = $$5[$$15] - $$7;
                for (int $$19 = $$15; $$19 >= $$16; --$$19) {
                    float $$20 = $$17;
                    float $$21 = $$18;
                    if ($$14 == 0) {
                        $$17 += (float)($$13.nextInt(11) - 5);
                        $$18 += (float)($$13.nextInt(11) - 5);
                    } else {
                        $$17 += (float)($$13.nextInt(31) - 15);
                        $$18 += (float)($$13.nextInt(31) - 15);
                    }
                    float $$22 = 0.5f;
                    float $$23 = 0.45f;
                    float $$24 = 0.45f;
                    float $$25 = 0.5f;
                    float $$26 = 0.1f + (float)$$12 * 0.2f;
                    if ($$14 == 0) {
                        $$26 *= (float)$$19 * 0.1f + 1.0f;
                    }
                    float $$27 = 0.1f + (float)$$12 * 0.2f;
                    if ($$14 == 0) {
                        $$27 *= ((float)$$19 - 1.0f) * 0.1f + 1.0f;
                    }
                    LightningBoltRenderer.quad($$11, $$10, $$17, $$18, $$19, $$20, $$21, 0.45f, 0.45f, 0.5f, $$26, $$27, false, false, true, false);
                    LightningBoltRenderer.quad($$11, $$10, $$17, $$18, $$19, $$20, $$21, 0.45f, 0.45f, 0.5f, $$26, $$27, true, false, true, true);
                    LightningBoltRenderer.quad($$11, $$10, $$17, $$18, $$19, $$20, $$21, 0.45f, 0.45f, 0.5f, $$26, $$27, true, true, false, true);
                    LightningBoltRenderer.quad($$11, $$10, $$17, $$18, $$19, $$20, $$21, 0.45f, 0.45f, 0.5f, $$26, $$27, false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4f $$0, VertexConsumer $$1, float $$2, float $$3, int $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, boolean $$12, boolean $$13, boolean $$14, boolean $$15) {
        $$1.addVertex($$0, $$2 + ($$12 ? $$11 : -$$11), (float)($$4 * 16), $$3 + ($$13 ? $$11 : -$$11)).setColor($$7, $$8, $$9, 0.3f);
        $$1.addVertex($$0, $$5 + ($$12 ? $$10 : -$$10), (float)(($$4 + 1) * 16), $$6 + ($$13 ? $$10 : -$$10)).setColor($$7, $$8, $$9, 0.3f);
        $$1.addVertex($$0, $$5 + ($$14 ? $$10 : -$$10), (float)(($$4 + 1) * 16), $$6 + ($$15 ? $$10 : -$$10)).setColor($$7, $$8, $$9, 0.3f);
        $$1.addVertex($$0, $$2 + ($$14 ? $$11 : -$$11), (float)($$4 * 16), $$3 + ($$15 ? $$11 : -$$11)).setColor($$7, $$8, $$9, 0.3f);
    }

    @Override
    public LightningBoltRenderState createRenderState() {
        return new LightningBoltRenderState();
    }

    @Override
    public void extractRenderState(LightningBolt $$0, LightningBoltRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.seed = $$0.seed;
    }

    @Override
    protected boolean affectedByCulling(LightningBolt $$0) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean affectedByCulling(Entity entity) {
        return this.affectedByCulling((LightningBolt)entity);
    }
}

