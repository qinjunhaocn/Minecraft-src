/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShapeRenderer {
    public static void renderShape(PoseStack $$0, VertexConsumer $$1, VoxelShape $$2, double $$3, double $$4, double $$5, int $$62) {
        PoseStack.Pose $$72 = $$0.last();
        $$2.forAllEdges(($$6, $$7, $$8, $$9, $$10, $$11) -> {
            Vector3f $$12 = new Vector3f((float)($$9 - $$6), (float)($$10 - $$7), (float)($$11 - $$8)).normalize();
            $$1.addVertex($$72, (float)($$6 + $$3), (float)($$7 + $$4), (float)($$8 + $$5)).setColor($$62).setNormal($$72, $$12);
            $$1.addVertex($$72, (float)($$9 + $$3), (float)($$10 + $$4), (float)($$11 + $$5)).setColor($$62).setNormal($$72, $$12);
        });
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, AABB $$2, float $$3, float $$4, float $$5, float $$6) {
        ShapeRenderer.renderLineBox($$0, $$1, $$2.minX, $$2.minY, $$2.minZ, $$2.maxX, $$2.maxY, $$2.maxZ, $$3, $$4, $$5, $$6, $$3, $$4, $$5);
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11) {
        ShapeRenderer.renderLineBox($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11, $$8, $$9, $$10);
    }

    public static void renderLineBox(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11, float $$12, float $$13, float $$14) {
        PoseStack.Pose $$15 = $$0.last();
        float $$16 = (float)$$2;
        float $$17 = (float)$$3;
        float $$18 = (float)$$4;
        float $$19 = (float)$$5;
        float $$20 = (float)$$6;
        float $$21 = (float)$$7;
        $$1.addVertex($$15, $$16, $$17, $$18).setColor($$8, $$13, $$14, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$17, $$18).setColor($$8, $$13, $$14, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$17, $$18).setColor($$12, $$9, $$14, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$20, $$18).setColor($$12, $$9, $$14, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$17, $$18).setColor($$12, $$13, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
        $$1.addVertex($$15, $$16, $$17, $$21).setColor($$12, $$13, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
        $$1.addVertex($$15, $$19, $$17, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$20, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$20, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, -1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$20, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, -1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$20, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
        $$1.addVertex($$15, $$16, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
        $$1.addVertex($$15, $$16, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, -1.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$17, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, -1.0f, 0.0f);
        $$1.addVertex($$15, $$16, $$17, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$17, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$17, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, -1.0f);
        $$1.addVertex($$15, $$19, $$17, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, -1.0f);
        $$1.addVertex($$15, $$16, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 1.0f, 0.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$17, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 1.0f, 0.0f);
        $$1.addVertex($$15, $$19, $$20, $$18).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
        $$1.addVertex($$15, $$19, $$20, $$21).setColor($$8, $$9, $$10, $$11).setNormal($$15, 0.0f, 0.0f, 1.0f);
    }

    public static void addChainedFilledBoxVertices(PoseStack $$0, VertexConsumer $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11) {
        ShapeRenderer.addChainedFilledBoxVertices($$0, $$1, (float)$$2, (float)$$3, (float)$$4, (float)$$5, (float)$$6, (float)$$7, $$8, $$9, $$10, $$11);
    }

    public static void addChainedFilledBoxVertices(PoseStack $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11) {
        Matrix4f $$12 = $$0.last().pose();
        $$1.addVertex($$12, $$2, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$3, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$2, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$4).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$7).setColor($$8, $$9, $$10, $$11);
        $$1.addVertex($$12, $$5, $$6, $$7).setColor($$8, $$9, $$10, $$11);
    }

    public static void renderFace(PoseStack $$0, VertexConsumer $$1, Direction $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, float $$11, float $$12) {
        Matrix4f $$13 = $$0.last().pose();
        switch ($$2) {
            case DOWN: {
                $$1.addVertex($$13, $$3, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$4, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$4, $$8).setColor($$9, $$10, $$11, $$12);
                break;
            }
            case UP: {
                $$1.addVertex($$13, $$3, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                break;
            }
            case NORTH: {
                $$1.addVertex($$13, $$3, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                break;
            }
            case SOUTH: {
                $$1.addVertex($$13, $$3, $$4, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$4, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                break;
            }
            case WEST: {
                $$1.addVertex($$13, $$3, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$4, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$3, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                break;
            }
            case EAST: {
                $$1.addVertex($$13, $$6, $$4, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$5).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$7, $$8).setColor($$9, $$10, $$11, $$12);
                $$1.addVertex($$13, $$6, $$4, $$8).setColor($$9, $$10, $$11, $$12);
            }
        }
    }

    public static void renderVector(PoseStack $$0, VertexConsumer $$1, Vector3f $$2, Vec3 $$3, int $$4) {
        PoseStack.Pose $$5 = $$0.last();
        $$1.addVertex($$5, $$2).setColor($$4).setNormal($$5, (float)$$3.x, (float)$$3.y, (float)$$3.z);
        $$1.addVertex($$5, (float)((double)$$2.x() + $$3.x), (float)((double)$$2.y() + $$3.y), (float)((double)$$2.z() + $$3.z)).setColor($$4).setNormal($$5, (float)$$3.x, (float)$$3.y, (float)$$3.z);
    }
}

