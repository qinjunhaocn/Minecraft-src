/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.math.MatrixUtil;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class FaceBakery {
    public static final int VERTEX_INT_SIZE = 8;
    public static final int VERTEX_COUNT = 4;
    private static final int COLOR_INDEX = 3;
    public static final int UV_INDEX = 4;
    private static final Vector3fc NO_RESCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final Vector3fc BLOCK_MIDDLE = new Vector3f(0.5f, 0.5f, 0.5f);

    @VisibleForTesting
    static BlockElementFace.UVs defaultFaceUV(Vector3fc $$0, Vector3fc $$1, Direction $$2) {
        return switch ($$2) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> new BlockElementFace.UVs($$0.x(), 16.0f - $$1.z(), $$1.x(), 16.0f - $$0.z());
            case Direction.UP -> new BlockElementFace.UVs($$0.x(), $$0.z(), $$1.x(), $$1.z());
            case Direction.NORTH -> new BlockElementFace.UVs(16.0f - $$1.x(), 16.0f - $$1.y(), 16.0f - $$0.x(), 16.0f - $$0.y());
            case Direction.SOUTH -> new BlockElementFace.UVs($$0.x(), 16.0f - $$1.y(), $$1.x(), 16.0f - $$0.y());
            case Direction.WEST -> new BlockElementFace.UVs($$0.z(), 16.0f - $$1.y(), $$1.z(), 16.0f - $$0.y());
            case Direction.EAST -> new BlockElementFace.UVs(16.0f - $$1.z(), 16.0f - $$1.y(), 16.0f - $$0.z(), 16.0f - $$0.y());
        };
    }

    public static BakedQuad bakeQuad(Vector3fc $$0, Vector3fc $$1, BlockElementFace $$2, TextureAtlasSprite $$3, Direction $$4, ModelState $$5, @Nullable BlockElementRotation $$6, boolean $$7, int $$8) {
        BlockElementFace.UVs $$9 = $$2.uvs();
        if ($$9 == null) {
            $$9 = FaceBakery.defaultFaceUV($$0, $$1, $$4);
        }
        $$9 = FaceBakery.shrinkUVs($$3, $$9);
        Matrix4fc $$10 = $$5.inverseFaceTransformation($$4);
        int[] $$11 = FaceBakery.a($$9, $$2.rotation(), $$10, $$3, $$4, FaceBakery.a($$0, $$1), $$5.transformation(), $$6);
        Direction $$12 = FaceBakery.a($$11);
        if ($$6 == null) {
            FaceBakery.a($$11, $$12);
        }
        return new BakedQuad($$11, $$2.tintIndex(), $$12, $$3, $$7, $$8);
    }

    private static BlockElementFace.UVs shrinkUVs(TextureAtlasSprite $$0, BlockElementFace.UVs $$1) {
        float $$2 = $$1.minU();
        float $$3 = $$1.minV();
        float $$4 = $$1.maxU();
        float $$5 = $$1.maxV();
        float $$6 = $$0.uvShrinkRatio();
        float $$7 = ($$2 + $$2 + $$4 + $$4) / 4.0f;
        float $$8 = ($$3 + $$3 + $$5 + $$5) / 4.0f;
        return new BlockElementFace.UVs(Mth.lerp($$6, $$2, $$7), Mth.lerp($$6, $$3, $$8), Mth.lerp($$6, $$4, $$7), Mth.lerp($$6, $$5, $$8));
    }

    private static int[] a(BlockElementFace.UVs $$0, Quadrant $$1, Matrix4fc $$2, TextureAtlasSprite $$3, Direction $$4, float[] $$5, Transformation $$6, @Nullable BlockElementRotation $$7) {
        FaceInfo $$8 = FaceInfo.fromFacing($$4);
        int[] $$9 = new int[32];
        for (int $$10 = 0; $$10 < 4; ++$$10) {
            FaceBakery.a($$9, $$10, $$8, $$0, $$1, $$2, $$5, $$3, $$6, $$7);
        }
        return $$9;
    }

    private static float[] a(Vector3fc $$0, Vector3fc $$1) {
        float[] $$2 = new float[Direction.values().length];
        $$2[FaceInfo.Constants.MIN_X] = $$0.x() / 16.0f;
        $$2[FaceInfo.Constants.MIN_Y] = $$0.y() / 16.0f;
        $$2[FaceInfo.Constants.MIN_Z] = $$0.z() / 16.0f;
        $$2[FaceInfo.Constants.MAX_X] = $$1.x() / 16.0f;
        $$2[FaceInfo.Constants.MAX_Y] = $$1.y() / 16.0f;
        $$2[FaceInfo.Constants.MAX_Z] = $$1.z() / 16.0f;
        return $$2;
    }

    private static void a(int[] $$0, int $$1, FaceInfo $$2, BlockElementFace.UVs $$3, Quadrant $$4, Matrix4fc $$5, float[] $$6, TextureAtlasSprite $$7, Transformation $$8, @Nullable BlockElementRotation $$9) {
        float $$18;
        float $$17;
        FaceInfo.VertexInfo $$10 = $$2.getVertexInfo($$1);
        Vector3f $$11 = new Vector3f($$6[$$10.xFace], $$6[$$10.yFace], $$6[$$10.zFace]);
        FaceBakery.applyElementRotation($$11, $$9);
        FaceBakery.applyModelRotation($$11, $$8);
        float $$12 = BlockElementFace.getU($$3, $$4, $$1);
        float $$13 = BlockElementFace.getV($$3, $$4, $$1);
        if (MatrixUtil.isIdentity($$5)) {
            float $$14 = $$12;
            float $$15 = $$13;
        } else {
            Vector3f $$16 = $$5.transformPosition(new Vector3f(FaceBakery.cornerToCenter($$12), FaceBakery.cornerToCenter($$13), 0.0f));
            $$17 = FaceBakery.centerToCorner($$16.x);
            $$18 = FaceBakery.centerToCorner($$16.y);
        }
        FaceBakery.a($$0, $$1, $$11, $$7, $$17, $$18);
    }

    private static float cornerToCenter(float $$0) {
        return $$0 - 0.5f;
    }

    private static float centerToCorner(float $$0) {
        return $$0 + 0.5f;
    }

    private static void a(int[] $$0, int $$1, Vector3f $$2, TextureAtlasSprite $$3, float $$4, float $$5) {
        int $$6 = $$1 * 8;
        $$0[$$6] = Float.floatToRawIntBits($$2.x());
        $$0[$$6 + 1] = Float.floatToRawIntBits($$2.y());
        $$0[$$6 + 2] = Float.floatToRawIntBits($$2.z());
        $$0[$$6 + 3] = -1;
        $$0[$$6 + 4] = Float.floatToRawIntBits($$3.getU($$4));
        $$0[$$6 + 4 + 1] = Float.floatToRawIntBits($$3.getV($$5));
    }

    private static void applyElementRotation(Vector3f $$0, @Nullable BlockElementRotation $$1) {
        if ($$1 == null) {
            return;
        }
        Vector3fc $$2 = $$1.axis().getPositive().getUnitVec3f();
        Matrix4f $$3 = new Matrix4f().rotation($$1.angle() * ((float)Math.PI / 180), $$2);
        Vector3fc $$4 = $$1.rescale() ? FaceBakery.computeRescale($$1) : NO_RESCALE;
        FaceBakery.rotateVertexBy($$0, (Vector3fc)$$1.origin(), (Matrix4fc)$$3, $$4);
    }

    private static Vector3fc computeRescale(BlockElementRotation $$0) {
        if ($$0.angle() == 0.0f) {
            return NO_RESCALE;
        }
        float $$1 = Math.abs($$0.angle());
        float $$2 = 1.0f / Mth.cos($$1 * ((float)Math.PI / 180));
        return switch ($$0.axis()) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> new Vector3f(1.0f, $$2, $$2);
            case Direction.Axis.Y -> new Vector3f($$2, 1.0f, $$2);
            case Direction.Axis.Z -> new Vector3f($$2, $$2, 1.0f);
        };
    }

    private static void applyModelRotation(Vector3f $$0, Transformation $$1) {
        if ($$1 == Transformation.identity()) {
            return;
        }
        FaceBakery.rotateVertexBy($$0, BLOCK_MIDDLE, $$1.getMatrix(), NO_RESCALE);
    }

    private static void rotateVertexBy(Vector3f $$0, Vector3fc $$1, Matrix4fc $$2, Vector3fc $$3) {
        $$0.sub($$1);
        $$2.transformPosition($$0);
        $$0.mul($$3);
        $$0.add($$1);
    }

    private static Direction a(int[] $$0) {
        Vector3f $$1 = FaceBakery.d($$0, 0);
        Vector3f $$2 = FaceBakery.d($$0, 8);
        Vector3f $$3 = FaceBakery.d($$0, 16);
        Vector3f $$4 = new Vector3f((Vector3fc)$$1).sub((Vector3fc)$$2);
        Vector3f $$5 = new Vector3f((Vector3fc)$$3).sub((Vector3fc)$$2);
        Vector3f $$6 = new Vector3f((Vector3fc)$$5).cross((Vector3fc)$$4).normalize();
        if (!$$6.isFinite()) {
            return Direction.UP;
        }
        Direction $$7 = null;
        float $$8 = 0.0f;
        for (Direction $$9 : Direction.values()) {
            float $$10 = $$6.dot($$9.getUnitVec3f());
            if (!($$10 >= 0.0f) || !($$10 > $$8)) continue;
            $$8 = $$10;
            $$7 = $$9;
        }
        if ($$7 == null) {
            return Direction.UP;
        }
        return $$7;
    }

    private static float a(int[] $$0, int $$1) {
        return Float.intBitsToFloat($$0[$$1]);
    }

    private static float b(int[] $$0, int $$1) {
        return Float.intBitsToFloat($$0[$$1 + 1]);
    }

    private static float c(int[] $$0, int $$1) {
        return Float.intBitsToFloat($$0[$$1 + 2]);
    }

    private static Vector3f d(int[] $$0, int $$1) {
        return new Vector3f(FaceBakery.a($$0, $$1), FaceBakery.b($$0, $$1), FaceBakery.c($$0, $$1));
    }

    private static void a(int[] $$0, Direction $$1) {
        int[] $$2 = new int[$$0.length];
        System.arraycopy($$0, 0, $$2, 0, $$0.length);
        float[] $$3 = new float[Direction.values().length];
        $$3[FaceInfo.Constants.MIN_X] = 999.0f;
        $$3[FaceInfo.Constants.MIN_Y] = 999.0f;
        $$3[FaceInfo.Constants.MIN_Z] = 999.0f;
        $$3[FaceInfo.Constants.MAX_X] = -999.0f;
        $$3[FaceInfo.Constants.MAX_Y] = -999.0f;
        $$3[FaceInfo.Constants.MAX_Z] = -999.0f;
        for (int $$4 = 0; $$4 < 4; ++$$4) {
            int $$5 = 8 * $$4;
            float $$6 = FaceBakery.a($$2, $$5);
            float $$7 = FaceBakery.b($$2, $$5);
            float $$8 = FaceBakery.c($$2, $$5);
            if ($$6 < $$3[FaceInfo.Constants.MIN_X]) {
                $$3[FaceInfo.Constants.MIN_X] = $$6;
            }
            if ($$7 < $$3[FaceInfo.Constants.MIN_Y]) {
                $$3[FaceInfo.Constants.MIN_Y] = $$7;
            }
            if ($$8 < $$3[FaceInfo.Constants.MIN_Z]) {
                $$3[FaceInfo.Constants.MIN_Z] = $$8;
            }
            if ($$6 > $$3[FaceInfo.Constants.MAX_X]) {
                $$3[FaceInfo.Constants.MAX_X] = $$6;
            }
            if ($$7 > $$3[FaceInfo.Constants.MAX_Y]) {
                $$3[FaceInfo.Constants.MAX_Y] = $$7;
            }
            if (!($$8 > $$3[FaceInfo.Constants.MAX_Z])) continue;
            $$3[FaceInfo.Constants.MAX_Z] = $$8;
        }
        FaceInfo $$9 = FaceInfo.fromFacing($$1);
        for (int $$10 = 0; $$10 < 4; ++$$10) {
            int $$11 = 8 * $$10;
            FaceInfo.VertexInfo $$12 = $$9.getVertexInfo($$10);
            float $$13 = $$3[$$12.xFace];
            float $$14 = $$3[$$12.yFace];
            float $$15 = $$3[$$12.zFace];
            $$0[$$11] = Float.floatToRawIntBits($$13);
            $$0[$$11 + 1] = Float.floatToRawIntBits($$14);
            $$0[$$11 + 2] = Float.floatToRawIntBits($$15);
            for (int $$16 = 0; $$16 < 4; ++$$16) {
                int $$17 = 8 * $$16;
                float $$18 = FaceBakery.a($$2, $$17);
                float $$19 = FaceBakery.b($$2, $$17);
                float $$20 = FaceBakery.c($$2, $$17);
                if (!Mth.equal($$13, $$18) || !Mth.equal($$14, $$19) || !Mth.equal($$15, $$20)) continue;
                $$0[$$11 + 4] = $$2[$$17 + 4];
                $$0[$$11 + 4 + 1] = $$2[$$17 + 4 + 1];
            }
        }
    }

    public static void a(int[] $$0, Consumer<Vector3f> $$1) {
        for (int $$2 = 0; $$2 < 4; ++$$2) {
            $$1.accept(FaceBakery.d($$0, 8 * $$2));
        }
    }
}

