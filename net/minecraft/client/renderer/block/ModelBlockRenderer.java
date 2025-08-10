/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.List;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModelBlockRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockColors blockColors;
    private static final int CACHE_SIZE = 100;
    static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

    public ModelBlockRenderer(BlockColors $$0) {
        this.blockColors = $$0;
    }

    public void tesselateBlock(BlockAndTintGetter $$0, List<BlockModelPart> $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, int $$7) {
        if ($$1.isEmpty()) {
            return;
        }
        boolean $$8 = Minecraft.useAmbientOcclusion() && $$2.getLightEmission() == 0 && ((BlockModelPart)$$1.getFirst()).useAmbientOcclusion();
        $$4.translate($$2.getOffset($$3));
        try {
            if ($$8) {
                this.tesselateWithAO($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            } else {
                this.tesselateWithoutAO($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            }
        } catch (Throwable $$9) {
            CrashReport $$10 = CrashReport.forThrowable($$9, "Tesselating block model");
            CrashReportCategory $$11 = $$10.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails($$11, $$0, $$3, $$2);
            $$11.setDetail("Using AO", $$8);
            throw new ReportedException($$10);
        }
    }

    private static boolean shouldRenderFace(BlockAndTintGetter $$0, BlockState $$1, boolean $$2, Direction $$3, BlockPos $$4) {
        if (!$$2) {
            return true;
        }
        BlockState $$5 = $$0.getBlockState($$4);
        return Block.shouldRenderFace($$1, $$5, $$3);
    }

    public void tesselateWithAO(BlockAndTintGetter $$0, List<BlockModelPart> $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, int $$7) {
        AmbientOcclusionRenderStorage $$8 = new AmbientOcclusionRenderStorage();
        int $$9 = 0;
        int $$10 = 0;
        for (BlockModelPart $$11 : $$1) {
            for (Direction $$12 : DIRECTIONS) {
                List<BakedQuad> $$16;
                boolean $$15;
                int $$13 = 1 << $$12.ordinal();
                boolean $$14 = ($$9 & $$13) == 1;
                boolean bl = $$15 = ($$10 & $$13) == 1;
                if ($$14 && !$$15 || ($$16 = $$11.getQuads($$12)).isEmpty()) continue;
                if (!$$14) {
                    $$15 = ModelBlockRenderer.shouldRenderFace($$0, $$2, $$6, $$12, $$8.scratchPos.setWithOffset((Vec3i)$$3, $$12));
                    $$9 |= $$13;
                    if ($$15) {
                        $$10 |= $$13;
                    }
                }
                if (!$$15) continue;
                this.renderModelFaceAO($$0, $$2, $$3, $$4, $$5, $$16, $$8, $$7);
            }
            List<BakedQuad> $$17 = $$11.getQuads(null);
            if ($$17.isEmpty()) continue;
            this.renderModelFaceAO($$0, $$2, $$3, $$4, $$5, $$17, $$8, $$7);
        }
    }

    public void tesselateWithoutAO(BlockAndTintGetter $$0, List<BlockModelPart> $$1, BlockState $$2, BlockPos $$3, PoseStack $$4, VertexConsumer $$5, boolean $$6, int $$7) {
        CommonRenderStorage $$8 = new CommonRenderStorage();
        int $$9 = 0;
        int $$10 = 0;
        for (BlockModelPart $$11 : $$1) {
            for (Direction $$12 : DIRECTIONS) {
                List<BakedQuad> $$16;
                boolean $$15;
                int $$13 = 1 << $$12.ordinal();
                boolean $$14 = ($$9 & $$13) == 1;
                boolean bl = $$15 = ($$10 & $$13) == 1;
                if ($$14 && !$$15 || ($$16 = $$11.getQuads($$12)).isEmpty()) continue;
                BlockPos.MutableBlockPos $$17 = $$8.scratchPos.setWithOffset((Vec3i)$$3, $$12);
                if (!$$14) {
                    $$15 = ModelBlockRenderer.shouldRenderFace($$0, $$2, $$6, $$12, $$17);
                    $$9 |= $$13;
                    if ($$15) {
                        $$10 |= $$13;
                    }
                }
                if (!$$15) continue;
                int $$18 = $$8.cache.getLightColor($$2, $$0, $$17);
                this.renderModelFaceFlat($$0, $$2, $$3, $$18, $$7, false, $$4, $$5, $$16, $$8);
            }
            List<BakedQuad> $$19 = $$11.getQuads(null);
            if ($$19.isEmpty()) continue;
            this.renderModelFaceFlat($$0, $$2, $$3, -1, $$7, true, $$4, $$5, $$19, $$8);
        }
    }

    private void renderModelFaceAO(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, PoseStack $$3, VertexConsumer $$4, List<BakedQuad> $$5, AmbientOcclusionRenderStorage $$6, int $$7) {
        for (BakedQuad $$8 : $$5) {
            ModelBlockRenderer.a($$0, $$1, $$2, $$8.b(), $$8.direction(), $$6);
            $$6.calculate($$0, $$1, $$2, $$8.direction(), $$8.shade());
            this.putQuadData($$0, $$1, $$2, $$4, $$3.last(), $$8, $$6, $$7);
        }
    }

    private void putQuadData(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, VertexConsumer $$3, PoseStack.Pose $$4, BakedQuad $$5, CommonRenderStorage $$6, int $$7) {
        float $$16;
        float $$15;
        float $$14;
        int $$8 = $$5.tintIndex();
        if ($$8 != -1) {
            int $$10;
            if ($$6.tintCacheIndex == $$8) {
                int $$9 = $$6.tintCacheValue;
            } else {
                $$10 = this.blockColors.getColor($$1, $$0, $$2, $$8);
                $$6.tintCacheIndex = $$8;
                $$6.tintCacheValue = $$10;
            }
            float $$11 = ARGB.redFloat($$10);
            float $$12 = ARGB.greenFloat($$10);
            float $$13 = ARGB.blueFloat($$10);
        } else {
            $$14 = 1.0f;
            $$15 = 1.0f;
            $$16 = 1.0f;
        }
        $$3.a($$4, $$5, $$6.brightness, $$14, $$15, $$16, 1.0f, $$6.lightmap, $$7, true);
    }

    private static void a(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, int[] $$3, Direction $$4, CommonRenderStorage $$5) {
        float $$6 = 32.0f;
        float $$7 = 32.0f;
        float $$8 = 32.0f;
        float $$9 = -32.0f;
        float $$10 = -32.0f;
        float $$11 = -32.0f;
        for (int $$12 = 0; $$12 < 4; ++$$12) {
            float $$13 = Float.intBitsToFloat($$3[$$12 * 8]);
            float $$14 = Float.intBitsToFloat($$3[$$12 * 8 + 1]);
            float $$15 = Float.intBitsToFloat($$3[$$12 * 8 + 2]);
            $$6 = Math.min($$6, $$13);
            $$7 = Math.min($$7, $$14);
            $$8 = Math.min($$8, $$15);
            $$9 = Math.max($$9, $$13);
            $$10 = Math.max($$10, $$14);
            $$11 = Math.max($$11, $$15);
        }
        if ($$5 instanceof AmbientOcclusionRenderStorage) {
            AmbientOcclusionRenderStorage $$16 = (AmbientOcclusionRenderStorage)$$5;
            $$16.faceShape[SizeInfo.WEST.index] = $$6;
            $$16.faceShape[SizeInfo.EAST.index] = $$9;
            $$16.faceShape[SizeInfo.DOWN.index] = $$7;
            $$16.faceShape[SizeInfo.UP.index] = $$10;
            $$16.faceShape[SizeInfo.NORTH.index] = $$8;
            $$16.faceShape[SizeInfo.SOUTH.index] = $$11;
            $$16.faceShape[SizeInfo.FLIP_WEST.index] = 1.0f - $$6;
            $$16.faceShape[SizeInfo.FLIP_EAST.index] = 1.0f - $$9;
            $$16.faceShape[SizeInfo.FLIP_DOWN.index] = 1.0f - $$7;
            $$16.faceShape[SizeInfo.FLIP_UP.index] = 1.0f - $$10;
            $$16.faceShape[SizeInfo.FLIP_NORTH.index] = 1.0f - $$8;
            $$16.faceShape[SizeInfo.FLIP_SOUTH.index] = 1.0f - $$11;
        }
        float $$17 = 1.0E-4f;
        float $$18 = 0.9999f;
        $$5.facePartial = switch ($$4) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN, Direction.UP -> {
                if ($$6 >= 1.0E-4f || $$8 >= 1.0E-4f || $$9 <= 0.9999f || $$11 <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case Direction.NORTH, Direction.SOUTH -> {
                if ($$6 >= 1.0E-4f || $$7 >= 1.0E-4f || $$9 <= 0.9999f || $$10 <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST, Direction.EAST -> $$7 >= 1.0E-4f || $$8 >= 1.0E-4f || $$10 <= 0.9999f || $$11 <= 0.9999f;
        };
        $$5.faceCubic = switch ($$4) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> {
                if ($$7 == $$10 && ($$7 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2))) {
                    yield true;
                }
                yield false;
            }
            case Direction.UP -> {
                if ($$7 == $$10 && ($$10 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2))) {
                    yield true;
                }
                yield false;
            }
            case Direction.NORTH -> {
                if ($$8 == $$11 && ($$8 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2))) {
                    yield true;
                }
                yield false;
            }
            case Direction.SOUTH -> {
                if ($$8 == $$11 && ($$11 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2))) {
                    yield true;
                }
                yield false;
            }
            case Direction.WEST -> {
                if ($$6 == $$9 && ($$6 < 1.0E-4f || $$1.isCollisionShapeFullBlock($$0, $$2))) {
                    yield true;
                }
                yield false;
            }
            case Direction.EAST -> $$6 == $$9 && ($$9 > 0.9999f || $$1.isCollisionShapeFullBlock($$0, $$2));
        };
    }

    private void renderModelFaceFlat(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, int $$3, int $$4, boolean $$5, PoseStack $$6, VertexConsumer $$7, List<BakedQuad> $$8, CommonRenderStorage $$9) {
        for (BakedQuad $$10 : $$8) {
            float $$12;
            if ($$5) {
                ModelBlockRenderer.a($$0, $$1, $$2, $$10.b(), $$10.direction(), $$9);
                BlockPos $$11 = $$9.faceCubic ? $$9.scratchPos.setWithOffset((Vec3i)$$2, $$10.direction()) : $$2;
                $$3 = $$9.cache.getLightColor($$1, $$0, $$11);
            }
            $$9.brightness[0] = $$12 = $$0.getShade($$10.direction(), $$10.shade());
            $$9.brightness[1] = $$12;
            $$9.brightness[2] = $$12;
            $$9.brightness[3] = $$12;
            $$9.lightmap[0] = $$3;
            $$9.lightmap[1] = $$3;
            $$9.lightmap[2] = $$3;
            $$9.lightmap[3] = $$3;
            this.putQuadData($$0, $$1, $$2, $$7, $$6.last(), $$10, $$9, $$4);
        }
    }

    public static void renderModel(PoseStack.Pose $$0, VertexConsumer $$1, BlockStateModel $$2, float $$3, float $$4, float $$5, int $$6, int $$7) {
        for (BlockModelPart $$8 : $$2.collectParts(RandomSource.create(42L))) {
            for (Direction $$9 : DIRECTIONS) {
                ModelBlockRenderer.renderQuadList($$0, $$1, $$3, $$4, $$5, $$8.getQuads($$9), $$6, $$7);
            }
            ModelBlockRenderer.renderQuadList($$0, $$1, $$3, $$4, $$5, $$8.getQuads(null), $$6, $$7);
        }
    }

    private static void renderQuadList(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, List<BakedQuad> $$5, int $$6, int $$7) {
        for (BakedQuad $$8 : $$5) {
            float $$14;
            float $$13;
            float $$12;
            if ($$8.isTinted()) {
                float $$9 = Mth.clamp($$2, 0.0f, 1.0f);
                float $$10 = Mth.clamp($$3, 0.0f, 1.0f);
                float $$11 = Mth.clamp($$4, 0.0f, 1.0f);
            } else {
                $$12 = 1.0f;
                $$13 = 1.0f;
                $$14 = 1.0f;
            }
            $$1.putBulkData($$0, $$8, $$12, $$13, $$14, 1.0f, $$6, $$7);
        }
    }

    public static void enableCaching() {
        CACHE.get().enable();
    }

    public static void clearCache() {
        CACHE.get().disable();
    }

    static class AmbientOcclusionRenderStorage
    extends CommonRenderStorage {
        final float[] faceShape = new float[SizeInfo.COUNT];

        public void calculate(BlockAndTintGetter $$0, BlockState $$1, BlockPos $$2, Direction $$3, boolean $$4) {
            int $$47;
            float $$46;
            int $$42;
            float $$41;
            int $$37;
            float $$36;
            int $$32;
            float $$31;
            boolean $$27;
            BlockPos $$5 = this.faceCubic ? $$2.relative($$3) : $$2;
            AdjacencyInfo $$6 = AdjacencyInfo.fromFacing($$3);
            BlockPos.MutableBlockPos $$7 = this.scratchPos;
            $$7.setWithOffset((Vec3i)$$5, $$6.corners[0]);
            BlockState $$8 = $$0.getBlockState($$7);
            int $$9 = this.cache.getLightColor($$8, $$0, $$7);
            float $$10 = this.cache.getShadeBrightness($$8, $$0, $$7);
            $$7.setWithOffset((Vec3i)$$5, $$6.corners[1]);
            BlockState $$11 = $$0.getBlockState($$7);
            int $$12 = this.cache.getLightColor($$11, $$0, $$7);
            float $$13 = this.cache.getShadeBrightness($$11, $$0, $$7);
            $$7.setWithOffset((Vec3i)$$5, $$6.corners[2]);
            BlockState $$14 = $$0.getBlockState($$7);
            int $$15 = this.cache.getLightColor($$14, $$0, $$7);
            float $$16 = this.cache.getShadeBrightness($$14, $$0, $$7);
            $$7.setWithOffset((Vec3i)$$5, $$6.corners[3]);
            BlockState $$17 = $$0.getBlockState($$7);
            int $$18 = this.cache.getLightColor($$17, $$0, $$7);
            float $$19 = this.cache.getShadeBrightness($$17, $$0, $$7);
            BlockState $$20 = $$0.getBlockState($$7.setWithOffset((Vec3i)$$5, $$6.corners[0]).move($$3));
            boolean $$21 = !$$20.isViewBlocking($$0, $$7) || $$20.getLightBlock() == 0;
            BlockState $$22 = $$0.getBlockState($$7.setWithOffset((Vec3i)$$5, $$6.corners[1]).move($$3));
            boolean $$23 = !$$22.isViewBlocking($$0, $$7) || $$22.getLightBlock() == 0;
            BlockState $$24 = $$0.getBlockState($$7.setWithOffset((Vec3i)$$5, $$6.corners[2]).move($$3));
            boolean $$25 = !$$24.isViewBlocking($$0, $$7) || $$24.getLightBlock() == 0;
            BlockState $$26 = $$0.getBlockState($$7.setWithOffset((Vec3i)$$5, $$6.corners[3]).move($$3));
            boolean bl = $$27 = !$$26.isViewBlocking($$0, $$7) || $$26.getLightBlock() == 0;
            if ($$25 || $$21) {
                $$7.setWithOffset((Vec3i)$$5, $$6.corners[0]).move($$6.corners[2]);
                BlockState $$28 = $$0.getBlockState($$7);
                float $$29 = this.cache.getShadeBrightness($$28, $$0, $$7);
                int $$30 = this.cache.getLightColor($$28, $$0, $$7);
            } else {
                $$31 = $$10;
                $$32 = $$9;
            }
            if ($$27 || $$21) {
                $$7.setWithOffset((Vec3i)$$5, $$6.corners[0]).move($$6.corners[3]);
                BlockState $$33 = $$0.getBlockState($$7);
                float $$34 = this.cache.getShadeBrightness($$33, $$0, $$7);
                int $$35 = this.cache.getLightColor($$33, $$0, $$7);
            } else {
                $$36 = $$10;
                $$37 = $$9;
            }
            if ($$25 || $$23) {
                $$7.setWithOffset((Vec3i)$$5, $$6.corners[1]).move($$6.corners[2]);
                BlockState $$38 = $$0.getBlockState($$7);
                float $$39 = this.cache.getShadeBrightness($$38, $$0, $$7);
                int $$40 = this.cache.getLightColor($$38, $$0, $$7);
            } else {
                $$41 = $$10;
                $$42 = $$9;
            }
            if ($$27 || $$23) {
                $$7.setWithOffset((Vec3i)$$5, $$6.corners[1]).move($$6.corners[3]);
                BlockState $$43 = $$0.getBlockState($$7);
                float $$44 = this.cache.getShadeBrightness($$43, $$0, $$7);
                int $$45 = this.cache.getLightColor($$43, $$0, $$7);
            } else {
                $$46 = $$10;
                $$47 = $$9;
            }
            int $$48 = this.cache.getLightColor($$1, $$0, $$2);
            $$7.setWithOffset((Vec3i)$$2, $$3);
            BlockState $$49 = $$0.getBlockState($$7);
            if (this.faceCubic || !$$49.isSolidRender()) {
                $$48 = this.cache.getLightColor($$49, $$0, $$7);
            }
            float $$50 = this.faceCubic ? this.cache.getShadeBrightness($$0.getBlockState($$5), $$0, $$5) : this.cache.getShadeBrightness($$0.getBlockState($$2), $$0, $$2);
            AmbientVertexRemap $$51 = AmbientVertexRemap.fromFacing($$3);
            if (!this.facePartial || !$$6.doNonCubicWeight) {
                float $$52 = ($$19 + $$10 + $$36 + $$50) * 0.25f;
                float $$53 = ($$16 + $$10 + $$31 + $$50) * 0.25f;
                float $$54 = ($$16 + $$13 + $$41 + $$50) * 0.25f;
                float $$55 = ($$19 + $$13 + $$46 + $$50) * 0.25f;
                this.lightmap[$$51.vert0] = AmbientOcclusionRenderStorage.blend($$18, $$9, $$37, $$48);
                this.lightmap[$$51.vert1] = AmbientOcclusionRenderStorage.blend($$15, $$9, $$32, $$48);
                this.lightmap[$$51.vert2] = AmbientOcclusionRenderStorage.blend($$15, $$12, $$42, $$48);
                this.lightmap[$$51.vert3] = AmbientOcclusionRenderStorage.blend($$18, $$12, $$47, $$48);
                this.brightness[$$51.vert0] = $$52;
                this.brightness[$$51.vert1] = $$53;
                this.brightness[$$51.vert2] = $$54;
                this.brightness[$$51.vert3] = $$55;
            } else {
                float $$56 = ($$19 + $$10 + $$36 + $$50) * 0.25f;
                float $$57 = ($$16 + $$10 + $$31 + $$50) * 0.25f;
                float $$58 = ($$16 + $$13 + $$41 + $$50) * 0.25f;
                float $$59 = ($$19 + $$13 + $$46 + $$50) * 0.25f;
                float $$60 = this.faceShape[$$6.vert0Weights[0].index] * this.faceShape[$$6.vert0Weights[1].index];
                float $$61 = this.faceShape[$$6.vert0Weights[2].index] * this.faceShape[$$6.vert0Weights[3].index];
                float $$62 = this.faceShape[$$6.vert0Weights[4].index] * this.faceShape[$$6.vert0Weights[5].index];
                float $$63 = this.faceShape[$$6.vert0Weights[6].index] * this.faceShape[$$6.vert0Weights[7].index];
                float $$64 = this.faceShape[$$6.vert1Weights[0].index] * this.faceShape[$$6.vert1Weights[1].index];
                float $$65 = this.faceShape[$$6.vert1Weights[2].index] * this.faceShape[$$6.vert1Weights[3].index];
                float $$66 = this.faceShape[$$6.vert1Weights[4].index] * this.faceShape[$$6.vert1Weights[5].index];
                float $$67 = this.faceShape[$$6.vert1Weights[6].index] * this.faceShape[$$6.vert1Weights[7].index];
                float $$68 = this.faceShape[$$6.vert2Weights[0].index] * this.faceShape[$$6.vert2Weights[1].index];
                float $$69 = this.faceShape[$$6.vert2Weights[2].index] * this.faceShape[$$6.vert2Weights[3].index];
                float $$70 = this.faceShape[$$6.vert2Weights[4].index] * this.faceShape[$$6.vert2Weights[5].index];
                float $$71 = this.faceShape[$$6.vert2Weights[6].index] * this.faceShape[$$6.vert2Weights[7].index];
                float $$72 = this.faceShape[$$6.vert3Weights[0].index] * this.faceShape[$$6.vert3Weights[1].index];
                float $$73 = this.faceShape[$$6.vert3Weights[2].index] * this.faceShape[$$6.vert3Weights[3].index];
                float $$74 = this.faceShape[$$6.vert3Weights[4].index] * this.faceShape[$$6.vert3Weights[5].index];
                float $$75 = this.faceShape[$$6.vert3Weights[6].index] * this.faceShape[$$6.vert3Weights[7].index];
                this.brightness[$$51.vert0] = Math.clamp((float)($$56 * $$60 + $$57 * $$61 + $$58 * $$62 + $$59 * $$63), (float)0.0f, (float)1.0f);
                this.brightness[$$51.vert1] = Math.clamp((float)($$56 * $$64 + $$57 * $$65 + $$58 * $$66 + $$59 * $$67), (float)0.0f, (float)1.0f);
                this.brightness[$$51.vert2] = Math.clamp((float)($$56 * $$68 + $$57 * $$69 + $$58 * $$70 + $$59 * $$71), (float)0.0f, (float)1.0f);
                this.brightness[$$51.vert3] = Math.clamp((float)($$56 * $$72 + $$57 * $$73 + $$58 * $$74 + $$59 * $$75), (float)0.0f, (float)1.0f);
                int $$76 = AmbientOcclusionRenderStorage.blend($$18, $$9, $$37, $$48);
                int $$77 = AmbientOcclusionRenderStorage.blend($$15, $$9, $$32, $$48);
                int $$78 = AmbientOcclusionRenderStorage.blend($$15, $$12, $$42, $$48);
                int $$79 = AmbientOcclusionRenderStorage.blend($$18, $$12, $$47, $$48);
                this.lightmap[$$51.vert0] = AmbientOcclusionRenderStorage.blend($$76, $$77, $$78, $$79, $$60, $$61, $$62, $$63);
                this.lightmap[$$51.vert1] = AmbientOcclusionRenderStorage.blend($$76, $$77, $$78, $$79, $$64, $$65, $$66, $$67);
                this.lightmap[$$51.vert2] = AmbientOcclusionRenderStorage.blend($$76, $$77, $$78, $$79, $$68, $$69, $$70, $$71);
                this.lightmap[$$51.vert3] = AmbientOcclusionRenderStorage.blend($$76, $$77, $$78, $$79, $$72, $$73, $$74, $$75);
            }
            float $$80 = $$0.getShade($$3, $$4);
            int $$81 = 0;
            while ($$81 < this.brightness.length) {
                int n = $$81++;
                this.brightness[n] = this.brightness[n] * $$80;
            }
        }

        private static int blend(int $$0, int $$1, int $$2, int $$3) {
            if ($$0 == 0) {
                $$0 = $$3;
            }
            if ($$1 == 0) {
                $$1 = $$3;
            }
            if ($$2 == 0) {
                $$2 = $$3;
            }
            return $$0 + $$1 + $$2 + $$3 >> 2 & 0xFF00FF;
        }

        private static int blend(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5, float $$6, float $$7) {
            int $$8 = (int)((float)($$0 >> 16 & 0xFF) * $$4 + (float)($$1 >> 16 & 0xFF) * $$5 + (float)($$2 >> 16 & 0xFF) * $$6 + (float)($$3 >> 16 & 0xFF) * $$7) & 0xFF;
            int $$9 = (int)((float)($$0 & 0xFF) * $$4 + (float)($$1 & 0xFF) * $$5 + (float)($$2 & 0xFF) * $$6 + (float)($$3 & 0xFF) * $$7) & 0xFF;
            return $$8 << 16 | $$9;
        }
    }

    static class CommonRenderStorage {
        public final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
        public boolean faceCubic;
        public boolean facePartial;
        public final float[] brightness = new float[4];
        public final int[] lightmap = new int[4];
        public int tintCacheIndex = -1;
        public int tintCacheValue;
        public final Cache cache = CACHE.get();

        CommonRenderStorage() {
        }
    }

    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap colorCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap $$0 = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int $$0) {
                }
            };
            $$0.defaultReturnValue(Integer.MAX_VALUE);
            return $$0;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap $$0 = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int $$0) {
                }
            };
            $$0.defaultReturnValue(Float.NaN);
            return $$0;
        });
        private final LevelRenderer.BrightnessGetter cachedBrightnessGetter = ($$0, $$1) -> {
            long $$2 = $$1.asLong();
            int $$3 = this.colorCache.get($$2);
            if ($$3 != Integer.MAX_VALUE) {
                return $$3;
            }
            int $$4 = LevelRenderer.BrightnessGetter.DEFAULT.packedBrightness($$0, $$1);
            if (this.colorCache.size() == 100) {
                this.colorCache.removeFirstInt();
            }
            this.colorCache.put($$2, $$4);
            return $$4;
        };

        private Cache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.colorCache.clear();
            this.brightnessCache.clear();
        }

        public int getLightColor(BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2) {
            return LevelRenderer.getLightColor(this.enabled ? this.cachedBrightnessGetter : LevelRenderer.BrightnessGetter.DEFAULT, $$1, $$0, $$2);
        }

        public float getShadeBrightness(BlockState $$0, BlockAndTintGetter $$1, BlockPos $$2) {
            float $$4;
            long $$3 = $$2.asLong();
            if (this.enabled && !Float.isNaN($$4 = this.brightnessCache.get($$3))) {
                return $$4;
            }
            float $$5 = $$0.getShadeBrightness($$1, $$2);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }
                this.brightnessCache.put($$3, $$5);
            }
            return $$5;
        }
    }

    protected static final class SizeInfo
    extends Enum<SizeInfo> {
        public static final /* enum */ SizeInfo DOWN = new SizeInfo(0);
        public static final /* enum */ SizeInfo UP = new SizeInfo(1);
        public static final /* enum */ SizeInfo NORTH = new SizeInfo(2);
        public static final /* enum */ SizeInfo SOUTH = new SizeInfo(3);
        public static final /* enum */ SizeInfo WEST = new SizeInfo(4);
        public static final /* enum */ SizeInfo EAST = new SizeInfo(5);
        public static final /* enum */ SizeInfo FLIP_DOWN = new SizeInfo(6);
        public static final /* enum */ SizeInfo FLIP_UP = new SizeInfo(7);
        public static final /* enum */ SizeInfo FLIP_NORTH = new SizeInfo(8);
        public static final /* enum */ SizeInfo FLIP_SOUTH = new SizeInfo(9);
        public static final /* enum */ SizeInfo FLIP_WEST = new SizeInfo(10);
        public static final /* enum */ SizeInfo FLIP_EAST = new SizeInfo(11);
        public static final int COUNT;
        final int index;
        private static final /* synthetic */ SizeInfo[] $VALUES;

        public static SizeInfo[] values() {
            return (SizeInfo[])$VALUES.clone();
        }

        public static SizeInfo valueOf(String $$0) {
            return Enum.valueOf(SizeInfo.class, $$0);
        }

        private SizeInfo(int $$0) {
            this.index = $$0;
        }

        private static /* synthetic */ SizeInfo[] a() {
            return new SizeInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
        }

        static {
            $VALUES = SizeInfo.a();
            COUNT = SizeInfo.values().length;
        }
    }

    protected static final class AdjacencyInfo
    extends Enum<AdjacencyInfo> {
        public static final /* enum */ AdjacencyInfo DOWN = new AdjacencyInfo(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.WEST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_WEST, SizeInfo.NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_EAST, SizeInfo.SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.EAST, SizeInfo.SOUTH});
        public static final /* enum */ AdjacencyInfo UP = new AdjacencyInfo(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new SizeInfo[]{SizeInfo.EAST, SizeInfo.SOUTH, SizeInfo.EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_EAST, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.EAST, SizeInfo.NORTH, SizeInfo.EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_EAST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.NORTH, SizeInfo.WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_WEST, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.WEST, SizeInfo.SOUTH, SizeInfo.WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_WEST, SizeInfo.SOUTH});
        public static final /* enum */ AdjacencyInfo NORTH = new AdjacencyInfo(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST});
        public static final /* enum */ AdjacencyInfo SOUTH = new AdjacencyInfo(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.FLIP_WEST, SizeInfo.FLIP_UP, SizeInfo.WEST, SizeInfo.UP, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_WEST, SizeInfo.FLIP_DOWN, SizeInfo.WEST, SizeInfo.DOWN, SizeInfo.WEST}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_EAST, SizeInfo.FLIP_DOWN, SizeInfo.EAST, SizeInfo.DOWN, SizeInfo.EAST}, new SizeInfo[]{SizeInfo.UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.FLIP_EAST, SizeInfo.FLIP_UP, SizeInfo.EAST, SizeInfo.UP, SizeInfo.EAST});
        public static final /* enum */ AdjacencyInfo WEST = new AdjacencyInfo(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.UP, SizeInfo.SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_UP, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.UP, SizeInfo.NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.FLIP_DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.DOWN, SizeInfo.SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.SOUTH});
        public static final /* enum */ AdjacencyInfo EAST = new AdjacencyInfo(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.SOUTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.FLIP_SOUTH, SizeInfo.DOWN, SizeInfo.SOUTH}, new SizeInfo[]{SizeInfo.FLIP_DOWN, SizeInfo.NORTH, SizeInfo.FLIP_DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.FLIP_NORTH, SizeInfo.DOWN, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.NORTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.FLIP_NORTH, SizeInfo.UP, SizeInfo.NORTH}, new SizeInfo[]{SizeInfo.FLIP_UP, SizeInfo.SOUTH, SizeInfo.FLIP_UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.FLIP_SOUTH, SizeInfo.UP, SizeInfo.SOUTH});
        final Direction[] corners;
        final boolean doNonCubicWeight;
        final SizeInfo[] vert0Weights;
        final SizeInfo[] vert1Weights;
        final SizeInfo[] vert2Weights;
        final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING;
        private static final /* synthetic */ AdjacencyInfo[] $VALUES;

        public static AdjacencyInfo[] values() {
            return (AdjacencyInfo[])$VALUES.clone();
        }

        public static AdjacencyInfo valueOf(String $$0) {
            return Enum.valueOf(AdjacencyInfo.class, $$0);
        }

        private AdjacencyInfo(Direction[] $$0, float $$1, boolean $$2, SizeInfo[] $$3, SizeInfo[] $$4, SizeInfo[] $$5, SizeInfo[] $$6) {
            this.corners = $$0;
            this.doNonCubicWeight = $$2;
            this.vert0Weights = $$3;
            this.vert1Weights = $$4;
            this.vert2Weights = $$5;
            this.vert3Weights = $$6;
        }

        public static AdjacencyInfo fromFacing(Direction $$0) {
            return BY_FACING[$$0.get3DDataValue()];
        }

        private static /* synthetic */ AdjacencyInfo[] a() {
            return new AdjacencyInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        }

        static {
            $VALUES = AdjacencyInfo.a();
            BY_FACING = Util.make(new AdjacencyInfo[6], $$0 -> {
                $$0[Direction.DOWN.get3DDataValue()] = DOWN;
                $$0[Direction.UP.get3DDataValue()] = UP;
                $$0[Direction.NORTH.get3DDataValue()] = NORTH;
                $$0[Direction.SOUTH.get3DDataValue()] = SOUTH;
                $$0[Direction.WEST.get3DDataValue()] = WEST;
                $$0[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }

    static final class AmbientVertexRemap
    extends Enum<AmbientVertexRemap> {
        public static final /* enum */ AmbientVertexRemap DOWN = new AmbientVertexRemap(0, 1, 2, 3);
        public static final /* enum */ AmbientVertexRemap UP = new AmbientVertexRemap(2, 3, 0, 1);
        public static final /* enum */ AmbientVertexRemap NORTH = new AmbientVertexRemap(3, 0, 1, 2);
        public static final /* enum */ AmbientVertexRemap SOUTH = new AmbientVertexRemap(0, 1, 2, 3);
        public static final /* enum */ AmbientVertexRemap WEST = new AmbientVertexRemap(3, 0, 1, 2);
        public static final /* enum */ AmbientVertexRemap EAST = new AmbientVertexRemap(1, 2, 3, 0);
        final int vert0;
        final int vert1;
        final int vert2;
        final int vert3;
        private static final AmbientVertexRemap[] BY_FACING;
        private static final /* synthetic */ AmbientVertexRemap[] $VALUES;

        public static AmbientVertexRemap[] values() {
            return (AmbientVertexRemap[])$VALUES.clone();
        }

        public static AmbientVertexRemap valueOf(String $$0) {
            return Enum.valueOf(AmbientVertexRemap.class, $$0);
        }

        private AmbientVertexRemap(int $$0, int $$1, int $$2, int $$3) {
            this.vert0 = $$0;
            this.vert1 = $$1;
            this.vert2 = $$2;
            this.vert3 = $$3;
        }

        public static AmbientVertexRemap fromFacing(Direction $$0) {
            return BY_FACING[$$0.get3DDataValue()];
        }

        private static /* synthetic */ AmbientVertexRemap[] a() {
            return new AmbientVertexRemap[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
        }

        static {
            $VALUES = AmbientVertexRemap.a();
            BY_FACING = Util.make(new AmbientVertexRemap[6], $$0 -> {
                $$0[Direction.DOWN.get3DDataValue()] = DOWN;
                $$0[Direction.UP.get3DDataValue()] = UP;
                $$0[Direction.NORTH.get3DDataValue()] = NORTH;
                $$0[Direction.SOUTH.get3DDataValue()] = SOUTH;
                $$0[Direction.WEST.get3DDataValue()] = WEST;
                $$0[Direction.EAST.get3DDataValue()] = EAST;
            });
        }
    }
}

