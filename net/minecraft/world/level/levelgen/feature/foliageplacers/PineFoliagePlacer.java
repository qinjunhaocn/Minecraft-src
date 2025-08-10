/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PineFoliagePlacer
extends FoliagePlacer {
    public static final MapCodec<PineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec($$02 -> PineFoliagePlacer.foliagePlacerParts($$02).and((App)IntProvider.codec(0, 24).fieldOf("height").forGetter($$0 -> $$0.height)).apply((Applicative)$$02, PineFoliagePlacer::new));
    private final IntProvider height;

    public PineFoliagePlacer(IntProvider $$0, IntProvider $$1, IntProvider $$2) {
        super($$0, $$1);
        this.height = $$2;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader $$0, FoliagePlacer.FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, int $$4, FoliagePlacer.FoliageAttachment $$5, int $$6, int $$7, int $$8) {
        int $$9 = 0;
        for (int $$10 = $$8; $$10 >= $$8 - $$6; --$$10) {
            this.placeLeavesRow($$0, $$1, $$2, $$3, $$5.pos(), $$9, $$10, $$5.doubleTrunk());
            if ($$9 >= 1 && $$10 == $$8 - $$6 + 1) {
                --$$9;
                continue;
            }
            if ($$9 >= $$7 + $$5.radiusOffset()) continue;
            ++$$9;
        }
    }

    @Override
    public int foliageRadius(RandomSource $$0, int $$1) {
        return super.foliageRadius($$0, $$1) + $$0.nextInt(Math.max($$1 + 1, 1));
    }

    @Override
    public int foliageHeight(RandomSource $$0, int $$1, TreeConfiguration $$2) {
        return this.height.sample($$0);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        return $$1 == $$4 && $$3 == $$4 && $$4 > 0;
    }
}

