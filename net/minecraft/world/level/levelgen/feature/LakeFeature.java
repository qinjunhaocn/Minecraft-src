/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

@Deprecated
public class LakeFeature
extends Feature<Configuration> {
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public LakeFeature(Codec<Configuration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> $$0) {
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        Configuration $$4 = $$0.config();
        if ($$1.getY() <= $$2.getMinY() + 4) {
            return false;
        }
        $$1 = $$1.below(4);
        boolean[] $$5 = new boolean[2048];
        int $$6 = $$3.nextInt(4) + 4;
        for (int $$7 = 0; $$7 < $$6; ++$$7) {
            double $$8 = $$3.nextDouble() * 6.0 + 3.0;
            double $$9 = $$3.nextDouble() * 4.0 + 2.0;
            double $$10 = $$3.nextDouble() * 6.0 + 3.0;
            double $$11 = $$3.nextDouble() * (16.0 - $$8 - 2.0) + 1.0 + $$8 / 2.0;
            double $$12 = $$3.nextDouble() * (8.0 - $$9 - 4.0) + 2.0 + $$9 / 2.0;
            double $$13 = $$3.nextDouble() * (16.0 - $$10 - 2.0) + 1.0 + $$10 / 2.0;
            for (int $$14 = 1; $$14 < 15; ++$$14) {
                for (int $$15 = 1; $$15 < 15; ++$$15) {
                    for (int $$16 = 1; $$16 < 7; ++$$16) {
                        double $$17 = ((double)$$14 - $$11) / ($$8 / 2.0);
                        double $$18 = ((double)$$16 - $$12) / ($$9 / 2.0);
                        double $$19 = ((double)$$15 - $$13) / ($$10 / 2.0);
                        double $$20 = $$17 * $$17 + $$18 * $$18 + $$19 * $$19;
                        if (!($$20 < 1.0)) continue;
                        $$5[($$14 * 16 + $$15) * 8 + $$16] = true;
                    }
                }
            }
        }
        BlockState $$21 = $$4.fluid().getState($$3, $$1);
        for (int $$22 = 0; $$22 < 16; ++$$22) {
            for (int $$23 = 0; $$23 < 16; ++$$23) {
                for (int $$24 = 0; $$24 < 8; ++$$24) {
                    boolean $$25;
                    boolean bl = $$25 = !$$5[($$22 * 16 + $$23) * 8 + $$24] && ($$22 < 15 && $$5[(($$22 + 1) * 16 + $$23) * 8 + $$24] || $$22 > 0 && $$5[(($$22 - 1) * 16 + $$23) * 8 + $$24] || $$23 < 15 && $$5[($$22 * 16 + $$23 + 1) * 8 + $$24] || $$23 > 0 && $$5[($$22 * 16 + ($$23 - 1)) * 8 + $$24] || $$24 < 7 && $$5[($$22 * 16 + $$23) * 8 + $$24 + 1] || $$24 > 0 && $$5[($$22 * 16 + $$23) * 8 + ($$24 - 1)]);
                    if (!$$25) continue;
                    BlockState $$26 = $$2.getBlockState($$1.offset($$22, $$24, $$23));
                    if ($$24 >= 4 && $$26.liquid()) {
                        return false;
                    }
                    if ($$24 >= 4 || $$26.isSolid() || $$2.getBlockState($$1.offset($$22, $$24, $$23)) == $$21) continue;
                    return false;
                }
            }
        }
        for (int $$27 = 0; $$27 < 16; ++$$27) {
            for (int $$28 = 0; $$28 < 16; ++$$28) {
                for (int $$29 = 0; $$29 < 8; ++$$29) {
                    BlockPos $$30;
                    if (!$$5[($$27 * 16 + $$28) * 8 + $$29] || !this.canReplaceBlock($$2.getBlockState($$30 = $$1.offset($$27, $$29, $$28)))) continue;
                    boolean $$31 = $$29 >= 4;
                    $$2.setBlock($$30, $$31 ? AIR : $$21, 2);
                    if (!$$31) continue;
                    $$2.scheduleTick($$30, AIR.getBlock(), 0);
                    this.markAboveForPostProcessing($$2, $$30);
                }
            }
        }
        BlockState $$32 = $$4.barrier().getState($$3, $$1);
        if (!$$32.isAir()) {
            for (int $$33 = 0; $$33 < 16; ++$$33) {
                for (int $$34 = 0; $$34 < 16; ++$$34) {
                    for (int $$35 = 0; $$35 < 8; ++$$35) {
                        BlockState $$37;
                        boolean $$36;
                        boolean bl = $$36 = !$$5[($$33 * 16 + $$34) * 8 + $$35] && ($$33 < 15 && $$5[(($$33 + 1) * 16 + $$34) * 8 + $$35] || $$33 > 0 && $$5[(($$33 - 1) * 16 + $$34) * 8 + $$35] || $$34 < 15 && $$5[($$33 * 16 + $$34 + 1) * 8 + $$35] || $$34 > 0 && $$5[($$33 * 16 + ($$34 - 1)) * 8 + $$35] || $$35 < 7 && $$5[($$33 * 16 + $$34) * 8 + $$35 + 1] || $$35 > 0 && $$5[($$33 * 16 + $$34) * 8 + ($$35 - 1)]);
                        if (!$$36 || $$35 >= 4 && $$3.nextInt(2) == 0 || !($$37 = $$2.getBlockState($$1.offset($$33, $$35, $$34))).isSolid() || $$37.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) continue;
                        BlockPos $$38 = $$1.offset($$33, $$35, $$34);
                        $$2.setBlock($$38, $$32, 2);
                        this.markAboveForPostProcessing($$2, $$38);
                    }
                }
            }
        }
        if ($$21.getFluidState().is(FluidTags.WATER)) {
            for (int $$39 = 0; $$39 < 16; ++$$39) {
                for (int $$40 = 0; $$40 < 16; ++$$40) {
                    int $$41 = 4;
                    BlockPos $$42 = $$1.offset($$39, 4, $$40);
                    if (!$$2.getBiome($$42).value().shouldFreeze($$2, $$42, false) || !this.canReplaceBlock($$2.getBlockState($$42))) continue;
                    $$2.setBlock($$42, Blocks.ICE.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }

    private boolean canReplaceBlock(BlockState $$0) {
        return !$$0.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    public record Configuration(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfiguration
    {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BlockStateProvider.CODEC.fieldOf("fluid").forGetter(Configuration::fluid), (App)BlockStateProvider.CODEC.fieldOf("barrier").forGetter(Configuration::barrier)).apply((Applicative)$$0, Configuration::new));
    }
}

