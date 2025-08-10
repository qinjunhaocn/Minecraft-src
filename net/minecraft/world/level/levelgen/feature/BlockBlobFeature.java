/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature
extends Feature<BlockStateConfiguration> {
    public BlockBlobFeature(Codec<BlockStateConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> $$0) {
        BlockState $$5;
        BlockPos $$1 = $$0.origin();
        WorldGenLevel $$2 = $$0.level();
        RandomSource $$3 = $$0.random();
        BlockStateConfiguration $$4 = $$0.config();
        while ($$1.getY() > $$2.getMinY() + 3 && ($$2.isEmptyBlock($$1.below()) || !BlockBlobFeature.isDirt($$5 = $$2.getBlockState($$1.below())) && !BlockBlobFeature.isStone($$5))) {
            $$1 = $$1.below();
        }
        if ($$1.getY() <= $$2.getMinY() + 3) {
            return false;
        }
        for (int $$6 = 0; $$6 < 3; ++$$6) {
            int $$7 = $$3.nextInt(2);
            int $$8 = $$3.nextInt(2);
            int $$9 = $$3.nextInt(2);
            float $$10 = (float)($$7 + $$8 + $$9) * 0.333f + 0.5f;
            for (BlockPos $$11 : BlockPos.betweenClosed($$1.offset(-$$7, -$$8, -$$9), $$1.offset($$7, $$8, $$9))) {
                if (!($$11.distSqr($$1) <= (double)($$10 * $$10))) continue;
                $$2.setBlock($$11, $$4.state, 3);
            }
            $$1 = $$1.offset(-1 + $$3.nextInt(2), -$$3.nextInt(2), -1 + $$3.nextInt(2));
        }
        return true;
    }
}

