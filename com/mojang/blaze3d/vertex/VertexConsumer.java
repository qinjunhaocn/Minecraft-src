/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

public interface VertexConsumer {
    public VertexConsumer addVertex(float var1, float var2, float var3);

    public VertexConsumer setColor(int var1, int var2, int var3, int var4);

    public VertexConsumer setUv(float var1, float var2);

    public VertexConsumer setUv1(int var1, int var2);

    public VertexConsumer setUv2(int var1, int var2);

    public VertexConsumer setNormal(float var1, float var2, float var3);

    default public void addVertex(float $$0, float $$1, float $$2, int $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10) {
        this.addVertex($$0, $$1, $$2);
        this.setColor($$3);
        this.setUv($$4, $$5);
        this.setOverlay($$6);
        this.setLight($$7);
        this.setNormal($$8, $$9, $$10);
    }

    default public VertexConsumer setColor(float $$0, float $$1, float $$2, float $$3) {
        return this.setColor((int)($$0 * 255.0f), (int)($$1 * 255.0f), (int)($$2 * 255.0f), (int)($$3 * 255.0f));
    }

    default public VertexConsumer setColor(int $$0) {
        return this.setColor(ARGB.red($$0), ARGB.green($$0), ARGB.blue($$0), ARGB.alpha($$0));
    }

    default public VertexConsumer setWhiteAlpha(int $$0) {
        return this.setColor(ARGB.color($$0, -1));
    }

    default public VertexConsumer setLight(int $$0) {
        return this.setUv2($$0 & 0xFFFF, $$0 >> 16 & 0xFFFF);
    }

    default public VertexConsumer setOverlay(int $$0) {
        return this.setUv1($$0 & 0xFFFF, $$0 >> 16 & 0xFFFF);
    }

    default public void putBulkData(PoseStack.Pose $$0, BakedQuad $$1, float $$2, float $$3, float $$4, float $$5, int $$6, int $$7) {
        this.a($$0, $$1, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, $$2, $$3, $$4, $$5, new int[]{$$6, $$6, $$6, $$6}, $$7, false);
    }

    default public void a(PoseStack.Pose $$0, BakedQuad $$1, float[] $$2, float $$3, float $$4, float $$5, float $$6, int[] $$7, int $$8, boolean $$9) {
        int[] $$10 = $$1.b();
        Vector3fc $$11 = $$1.direction().getUnitVec3f();
        Matrix4f $$12 = $$0.pose();
        Vector3f $$13 = $$0.transformNormal($$11, new Vector3f());
        int $$14 = 8;
        int $$15 = $$10.length / 8;
        int $$16 = (int)($$6 * 255.0f);
        int $$17 = $$1.lightEmission();
        try (MemoryStack $$18 = MemoryStack.stackPush();){
            ByteBuffer $$19 = $$18.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer $$20 = $$19.asIntBuffer();
            for (int $$21 = 0; $$21 < $$15; ++$$21) {
                float $$33;
                float $$32;
                float $$31;
                $$20.clear();
                $$20.put($$10, $$21 * 8, 8);
                float $$22 = $$19.getFloat(0);
                float $$23 = $$19.getFloat(4);
                float $$24 = $$19.getFloat(8);
                if ($$9) {
                    float $$25 = $$19.get(12) & 0xFF;
                    float $$26 = $$19.get(13) & 0xFF;
                    float $$27 = $$19.get(14) & 0xFF;
                    float $$28 = $$25 * $$2[$$21] * $$3;
                    float $$29 = $$26 * $$2[$$21] * $$4;
                    float $$30 = $$27 * $$2[$$21] * $$5;
                } else {
                    $$31 = $$2[$$21] * $$3 * 255.0f;
                    $$32 = $$2[$$21] * $$4 * 255.0f;
                    $$33 = $$2[$$21] * $$5 * 255.0f;
                }
                int $$34 = ARGB.color($$16, (int)$$31, (int)$$32, (int)$$33);
                int $$35 = LightTexture.lightCoordsWithEmission($$7[$$21], $$17);
                float $$36 = $$19.getFloat(16);
                float $$37 = $$19.getFloat(20);
                Vector3f $$38 = $$12.transformPosition($$22, $$23, $$24, new Vector3f());
                this.addVertex($$38.x(), $$38.y(), $$38.z(), $$34, $$36, $$37, $$8, $$35, $$13.x(), $$13.y(), $$13.z());
            }
        }
    }

    default public VertexConsumer addVertex(Vector3f $$0) {
        return this.addVertex($$0.x(), $$0.y(), $$0.z());
    }

    default public VertexConsumer addVertex(PoseStack.Pose $$0, Vector3f $$1) {
        return this.addVertex($$0, $$1.x(), $$1.y(), $$1.z());
    }

    default public VertexConsumer addVertex(PoseStack.Pose $$0, float $$1, float $$2, float $$3) {
        return this.addVertex($$0.pose(), $$1, $$2, $$3);
    }

    default public VertexConsumer addVertex(Matrix4f $$0, float $$1, float $$2, float $$3) {
        Vector3f $$4 = $$0.transformPosition($$1, $$2, $$3, new Vector3f());
        return this.addVertex($$4.x(), $$4.y(), $$4.z());
    }

    default public VertexConsumer addVertexWith2DPose(Matrix3x2f $$0, float $$1, float $$2, float $$3) {
        Vector2f $$4 = $$0.transformPosition($$1, $$2, new Vector2f());
        return this.addVertex($$4.x(), $$4.y(), $$3);
    }

    default public VertexConsumer setNormal(PoseStack.Pose $$0, float $$1, float $$2, float $$3) {
        Vector3f $$4 = $$0.transformNormal($$1, $$2, $$3, new Vector3f());
        return this.setNormal($$4.x(), $$4.y(), $$4.z());
    }

    default public VertexConsumer setNormal(PoseStack.Pose $$0, Vector3f $$1) {
        return this.setNormal($$0, $$1.x(), $$1.y(), $$1.z());
    }
}

