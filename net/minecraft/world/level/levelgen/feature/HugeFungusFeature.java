/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;

public class HugeFungusFeature
extends Feature<HugeFungusConfiguration> {
    private static final float HUGE_PROBABILITY = 0.06f;

    public HugeFungusFeature(Codec<HugeFungusConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<HugeFungusConfiguration> $$0) {
        WorldGenLevel $$1 = $$0.level();
        BlockPos $$2 = $$0.origin();
        RandomSource $$3 = $$0.random();
        ChunkGenerator $$4 = $$0.chunkGenerator();
        HugeFungusConfiguration $$5 = $$0.config();
        Block $$6 = $$5.validBaseState.getBlock();
        BlockPos $$7 = null;
        BlockState $$8 = $$1.getBlockState($$2.below());
        if ($$8.is($$6)) {
            $$7 = $$2;
        }
        if ($$7 == null) {
            return false;
        }
        int $$9 = Mth.nextInt($$3, 4, 13);
        if ($$3.nextInt(12) == 0) {
            $$9 *= 2;
        }
        if (!$$5.planted) {
            int $$10 = $$4.getGenDepth();
            if ($$7.getY() + $$9 + 1 >= $$10) {
                return false;
            }
        }
        boolean $$11 = !$$5.planted && $$3.nextFloat() < 0.06f;
        $$1.setBlock($$2, Blocks.AIR.defaultBlockState(), 260);
        this.placeStem($$1, $$3, $$5, $$7, $$9, $$11);
        this.placeHat($$1, $$3, $$5, $$7, $$9, $$11);
        return true;
    }

    private static boolean isReplaceable(WorldGenLevel $$0, BlockPos $$1, HugeFungusConfiguration $$2, boolean $$3) {
        if ($$0.isStateAtPosition($$1, BlockBehaviour.BlockStateBase::canBeReplaced)) {
            return true;
        }
        if ($$3) {
            return $$2.replaceableBlocks.test($$0, $$1);
        }
        return false;
    }

    private void placeStem(WorldGenLevel $$0, RandomSource $$1, HugeFungusConfiguration $$2, BlockPos $$3, int $$4, boolean $$5) {
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        BlockState $$7 = $$2.stemState;
        int $$8 = $$5 ? 1 : 0;
        for (int $$9 = -$$8; $$9 <= $$8; ++$$9) {
            for (int $$10 = -$$8; $$10 <= $$8; ++$$10) {
                boolean $$11 = $$5 && Mth.abs($$9) == $$8 && Mth.abs($$10) == $$8;
                for (int $$12 = 0; $$12 < $$4; ++$$12) {
                    $$6.setWithOffset($$3, $$9, $$12, $$10);
                    if (!HugeFungusFeature.isReplaceable($$0, $$6, $$2, true)) continue;
                    if ($$2.planted) {
                        if (!$$0.getBlockState((BlockPos)$$6.below()).isAir()) {
                            $$0.destroyBlock($$6, true);
                        }
                        $$0.setBlock($$6, $$7, 3);
                        continue;
                    }
                    if ($$11) {
                        if (!($$1.nextFloat() < 0.1f)) continue;
                        this.setBlock($$0, $$6, $$7);
                        continue;
                    }
                    this.setBlock($$0, $$6, $$7);
                }
            }
        }
    }

    private void placeHat(WorldGenLevel $$0, RandomSource $$1, HugeFungusConfiguration $$2, BlockPos $$3, int $$4, boolean $$5) {
        int $$9;
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        boolean $$7 = $$2.hatState.is(Blocks.NETHER_WART_BLOCK);
        int $$8 = Math.min($$1.nextInt(1 + $$4 / 3) + 5, $$4);
        for (int $$10 = $$9 = $$4 - $$8; $$10 <= $$4; ++$$10) {
            int $$11;
            int n = $$11 = $$10 < $$4 - $$1.nextInt(3) ? 2 : 1;
            if ($$8 > 8 && $$10 < $$9 + 4) {
                $$11 = 3;
            }
            if ($$5) {
                ++$$11;
            }
            for (int $$12 = -$$11; $$12 <= $$11; ++$$12) {
                for (int $$13 = -$$11; $$13 <= $$11; ++$$13) {
                    boolean $$14 = $$12 == -$$11 || $$12 == $$11;
                    boolean $$15 = $$13 == -$$11 || $$13 == $$11;
                    boolean $$16 = !$$14 && !$$15 && $$10 != $$4;
                    boolean $$17 = $$14 && $$15;
                    boolean $$18 = $$10 < $$9 + 3;
                    $$6.setWithOffset($$3, $$12, $$10, $$13);
                    if (!HugeFungusFeature.isReplaceable($$0, $$6, $$2, false)) continue;
                    if ($$2.planted && !$$0.getBlockState((BlockPos)$$6.below()).isAir()) {
                        $$0.destroyBlock($$6, true);
                    }
                    if ($$18) {
                        if ($$16) continue;
                        this.placeHatDropBlock($$0, $$1, $$6, $$2.hatState, $$7);
                        continue;
                    }
                    if ($$16) {
                        this.placeHatBlock($$0, $$1, $$2, $$6, 0.1f, 0.2f, $$7 ? 0.1f : 0.0f);
                        continue;
                    }
                    if ($$17) {
                        this.placeHatBlock($$0, $$1, $$2, $$6, 0.01f, 0.7f, $$7 ? 0.083f : 0.0f);
                        continue;
                    }
                    this.placeHatBlock($$0, $$1, $$2, $$6, 5.0E-4f, 0.98f, $$7 ? 0.07f : 0.0f);
                }
            }
        }
    }

    private void placeHatBlock(LevelAccessor $$0, RandomSource $$1, HugeFungusConfiguration $$2, BlockPos.MutableBlockPos $$3, float $$4, float $$5, float $$6) {
        if ($$1.nextFloat() < $$4) {
            this.setBlock($$0, $$3, $$2.decorState);
        } else if ($$1.nextFloat() < $$5) {
            this.setBlock($$0, $$3, $$2.hatState);
            if ($$1.nextFloat() < $$6) {
                HugeFungusFeature.tryPlaceWeepingVines($$3, $$0, $$1);
            }
        }
    }

    private void placeHatDropBlock(LevelAccessor $$0, RandomSource $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.getBlockState($$2.below()).is($$3.getBlock())) {
            this.setBlock($$0, $$2, $$3);
        } else if ((double)$$1.nextFloat() < 0.15) {
            this.setBlock($$0, $$2, $$3);
            if ($$4 && $$1.nextInt(11) == 0) {
                HugeFungusFeature.tryPlaceWeepingVines($$2, $$0, $$1);
            }
        }
    }

    private static void tryPlaceWeepingVines(BlockPos $$0, LevelAccessor $$1, RandomSource $$2) {
        BlockPos.MutableBlockPos $$3 = $$0.mutable().move(Direction.DOWN);
        if (!$$1.isEmptyBlock($$3)) {
            return;
        }
        int $$4 = Mth.nextInt($$2, 1, 5);
        if ($$2.nextInt(7) == 0) {
            $$4 *= 2;
        }
        int $$5 = 23;
        int $$6 = 25;
        WeepingVinesFeature.placeWeepingVinesColumn($$1, $$2, $$3, $$4, 23, 25);
    }
}

