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
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class DiskFeature
extends Feature<DiskConfiguration> {
    public DiskFeature(Codec<DiskConfiguration> $$0) {
        super($$0);
    }

    @Override
    public boolean place(FeaturePlaceContext<DiskConfiguration> $$0) {
        DiskConfiguration $$1 = $$0.config();
        BlockPos $$2 = $$0.origin();
        WorldGenLevel $$3 = $$0.level();
        RandomSource $$4 = $$0.random();
        boolean $$5 = false;
        int $$6 = $$2.getY();
        int $$7 = $$6 + $$1.halfHeight();
        int $$8 = $$6 - $$1.halfHeight() - 1;
        int $$9 = $$1.radius().sample($$4);
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        for (BlockPos $$11 : BlockPos.betweenClosed($$2.offset(-$$9, 0, -$$9), $$2.offset($$9, 0, $$9))) {
            int $$13;
            int $$12 = $$11.getX() - $$2.getX();
            if ($$12 * $$12 + ($$13 = $$11.getZ() - $$2.getZ()) * $$13 > $$9 * $$9) continue;
            $$5 |= this.placeColumn($$1, $$3, $$4, $$7, $$8, $$10.set($$11));
        }
        return $$5;
    }

    protected boolean placeColumn(DiskConfiguration $$0, WorldGenLevel $$1, RandomSource $$2, int $$3, int $$4, BlockPos.MutableBlockPos $$5) {
        boolean $$6 = false;
        boolean $$7 = false;
        for (int $$8 = $$3; $$8 > $$4; --$$8) {
            $$5.setY($$8);
            if ($$0.target().test($$1, $$5)) {
                BlockState $$9 = $$0.stateProvider().getState($$1, $$2, $$5);
                $$1.setBlock($$5, $$9, 2);
                if (!$$7) {
                    this.markAboveForPostProcessing($$1, $$5);
                }
                $$6 = true;
                $$7 = true;
                continue;
            }
            $$7 = false;
        }
        return $$6;
    }
}

