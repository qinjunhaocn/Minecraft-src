/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class DarkOakFoliagePlacer
extends FoliagePlacer {
    public static final MapCodec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec($$0 -> DarkOakFoliagePlacer.foliagePlacerParts($$0).apply((Applicative)$$0, DarkOakFoliagePlacer::new));

    public DarkOakFoliagePlacer(IntProvider $$0, IntProvider $$1) {
        super($$0, $$1);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader $$0, FoliagePlacer.FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, int $$4, FoliagePlacer.FoliageAttachment $$5, int $$6, int $$7, int $$8) {
        BlockPos $$9 = $$5.pos().above($$8);
        boolean $$10 = $$5.doubleTrunk();
        if ($$10) {
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7 + 2, -1, $$10);
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7 + 3, 0, $$10);
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7 + 2, 1, $$10);
            if ($$2.nextBoolean()) {
                this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7, 2, $$10);
            }
        } else {
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7 + 2, -1, $$10);
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$9, $$7 + 1, 0, $$10);
        }
    }

    @Override
    public int foliageHeight(RandomSource $$0, int $$1, TreeConfiguration $$2) {
        return 4;
    }

    @Override
    protected boolean shouldSkipLocationSigned(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        if (!($$2 != 0 || !$$5 || $$1 != -$$4 && $$1 < $$4 || $$3 != -$$4 && $$3 < $$4)) {
            return true;
        }
        return super.shouldSkipLocationSigned($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        if ($$2 == -1 && !$$5) {
            return $$1 == $$4 && $$3 == $$4;
        }
        if ($$2 == 1) {
            return $$1 + $$3 > $$4 * 2 - 2;
        }
        return false;
    }
}

