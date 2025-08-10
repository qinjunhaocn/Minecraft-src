/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class OreFeature
extends Feature<OreConfiguration> {
    public OreFeature(Codec<OreConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreConfiguration> $$0) {
        RandomSource $$1 = $$0.random();
        BlockPos $$2 = $$0.origin();
        WorldGenLevel $$3 = $$0.level();
        OreConfiguration $$4 = $$0.config();
        float $$5 = $$1.nextFloat() * (float)Math.PI;
        float $$6 = (float)$$4.size / 8.0f;
        int $$7 = Mth.ceil(((float)$$4.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double $$8 = (double)$$2.getX() + Math.sin($$5) * (double)$$6;
        double $$9 = (double)$$2.getX() - Math.sin($$5) * (double)$$6;
        double $$10 = (double)$$2.getZ() + Math.cos($$5) * (double)$$6;
        double $$11 = (double)$$2.getZ() - Math.cos($$5) * (double)$$6;
        int $$12 = 2;
        double $$13 = $$2.getY() + $$1.nextInt(3) - 2;
        double $$14 = $$2.getY() + $$1.nextInt(3) - 2;
        int $$15 = $$2.getX() - Mth.ceil($$6) - $$7;
        int $$16 = $$2.getY() - 2 - $$7;
        int $$17 = $$2.getZ() - Mth.ceil($$6) - $$7;
        int $$18 = 2 * (Mth.ceil($$6) + $$7);
        int $$19 = 2 * (2 + $$7);
        for (int $$20 = $$15; $$20 <= $$15 + $$18; ++$$20) {
            for (int $$21 = $$17; $$21 <= $$17 + $$18; ++$$21) {
                if ($$16 > $$3.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, $$20, $$21)) continue;
                return this.doPlace($$3, $$1, $$4, $$8, $$9, $$10, $$11, $$13, $$14, $$15, $$16, $$17, $$18, $$19);
            }
        }
        return false;
    }

    protected boolean doPlace(WorldGenLevel $$0, RandomSource $$1, OreConfiguration $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8, int $$9, int $$10, int $$11, int $$12, int $$13) {
        int $$14 = 0;
        BitSet $$15 = new BitSet($$12 * $$13 * $$12);
        BlockPos.MutableBlockPos $$16 = new BlockPos.MutableBlockPos();
        int $$17 = $$2.size;
        double[] $$18 = new double[$$17 * 4];
        for (int $$19 = 0; $$19 < $$17; ++$$19) {
            float $$20 = (float)$$19 / (float)$$17;
            double $$21 = Mth.lerp((double)$$20, $$3, $$4);
            double $$22 = Mth.lerp((double)$$20, $$7, $$8);
            double $$23 = Mth.lerp((double)$$20, $$5, $$6);
            double $$24 = $$1.nextDouble() * (double)$$17 / 16.0;
            double $$25 = ((double)(Mth.sin((float)Math.PI * $$20) + 1.0f) * $$24 + 1.0) / 2.0;
            $$18[$$19 * 4 + 0] = $$21;
            $$18[$$19 * 4 + 1] = $$22;
            $$18[$$19 * 4 + 2] = $$23;
            $$18[$$19 * 4 + 3] = $$25;
        }
        for (int $$26 = 0; $$26 < $$17 - 1; ++$$26) {
            if ($$18[$$26 * 4 + 3] <= 0.0) continue;
            for (int $$27 = $$26 + 1; $$27 < $$17; ++$$27) {
                double $$30;
                double $$29;
                double $$28;
                double $$31;
                if ($$18[$$27 * 4 + 3] <= 0.0 || !(($$31 = $$18[$$26 * 4 + 3] - $$18[$$27 * 4 + 3]) * $$31 > ($$28 = $$18[$$26 * 4 + 0] - $$18[$$27 * 4 + 0]) * $$28 + ($$29 = $$18[$$26 * 4 + 1] - $$18[$$27 * 4 + 1]) * $$29 + ($$30 = $$18[$$26 * 4 + 2] - $$18[$$27 * 4 + 2]) * $$30)) continue;
                if ($$31 > 0.0) {
                    $$18[$$27 * 4 + 3] = -1.0;
                    continue;
                }
                $$18[$$26 * 4 + 3] = -1.0;
            }
        }
        try (BulkSectionAccess $$32 = new BulkSectionAccess($$0);){
            for (int $$33 = 0; $$33 < $$17; ++$$33) {
                double $$34 = $$18[$$33 * 4 + 3];
                if ($$34 < 0.0) continue;
                double $$35 = $$18[$$33 * 4 + 0];
                double $$36 = $$18[$$33 * 4 + 1];
                double $$37 = $$18[$$33 * 4 + 2];
                int $$38 = Math.max(Mth.floor($$35 - $$34), $$9);
                int $$39 = Math.max(Mth.floor($$36 - $$34), $$10);
                int $$40 = Math.max(Mth.floor($$37 - $$34), $$11);
                int $$41 = Math.max(Mth.floor($$35 + $$34), $$38);
                int $$42 = Math.max(Mth.floor($$36 + $$34), $$39);
                int $$43 = Math.max(Mth.floor($$37 + $$34), $$40);
                for (int $$44 = $$38; $$44 <= $$41; ++$$44) {
                    double $$45 = ((double)$$44 + 0.5 - $$35) / $$34;
                    if (!($$45 * $$45 < 1.0)) continue;
                    for (int $$46 = $$39; $$46 <= $$42; ++$$46) {
                        double $$47 = ((double)$$46 + 0.5 - $$36) / $$34;
                        if (!($$45 * $$45 + $$47 * $$47 < 1.0)) continue;
                        block11: for (int $$48 = $$40; $$48 <= $$43; ++$$48) {
                            LevelChunkSection $$51;
                            int $$50;
                            double $$49 = ((double)$$48 + 0.5 - $$37) / $$34;
                            if (!($$45 * $$45 + $$47 * $$47 + $$49 * $$49 < 1.0) || $$0.isOutsideBuildHeight($$46) || $$15.get($$50 = $$44 - $$9 + ($$46 - $$10) * $$12 + ($$48 - $$11) * $$12 * $$13)) continue;
                            $$15.set($$50);
                            $$16.set($$44, $$46, $$48);
                            if (!$$0.ensureCanWrite($$16) || ($$51 = $$32.getSection($$16)) == null) continue;
                            int $$52 = SectionPos.sectionRelative($$44);
                            int $$53 = SectionPos.sectionRelative($$46);
                            int $$54 = SectionPos.sectionRelative($$48);
                            BlockState $$55 = $$51.getBlockState($$52, $$53, $$54);
                            for (OreConfiguration.TargetBlockState $$56 : $$2.targetStates) {
                                if (!OreFeature.canPlaceOre($$55, $$32::getBlockState, $$1, $$2, $$56, $$16)) continue;
                                $$51.setBlockState($$52, $$53, $$54, $$56.state, false);
                                ++$$14;
                                continue block11;
                            }
                        }
                    }
                }
            }
        }
        return $$14 > 0;
    }

    public static boolean canPlaceOre(BlockState $$0, Function<BlockPos, BlockState> $$1, RandomSource $$2, OreConfiguration $$3, OreConfiguration.TargetBlockState $$4, BlockPos.MutableBlockPos $$5) {
        if (!$$4.target.test($$0, $$2)) {
            return false;
        }
        if (OreFeature.shouldSkipAirCheck($$2, $$3.discardChanceOnAirExposure)) {
            return true;
        }
        return !OreFeature.isAdjacentToAir($$1, $$5);
    }

    protected static boolean shouldSkipAirCheck(RandomSource $$0, float $$1) {
        if ($$1 <= 0.0f) {
            return true;
        }
        if ($$1 >= 1.0f) {
            return false;
        }
        return $$0.nextFloat() >= $$1;
    }
}

