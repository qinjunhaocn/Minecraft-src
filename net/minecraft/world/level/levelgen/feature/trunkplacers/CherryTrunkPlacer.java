/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class CherryTrunkPlacer
extends TrunkPlacer {
    private static final Codec<UniformInt> BRANCH_START_CODEC = UniformInt.CODEC.codec().validate($$0 -> {
        if ($$0.getMaxValue() - $$0.getMinValue() < 1) {
            return DataResult.error(() -> "Need at least 2 blocks variation for the branch starts to fit both branches");
        }
        return DataResult.success((Object)$$0);
    });
    public static final MapCodec<CherryTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec($$02 -> CherryTrunkPlacer.trunkPlacerParts($$02).and($$02.group((App)IntProvider.codec(1, 3).fieldOf("branch_count").forGetter($$0 -> $$0.branchCount), (App)IntProvider.codec(2, 16).fieldOf("branch_horizontal_length").forGetter($$0 -> $$0.branchHorizontalLength), (App)IntProvider.validateCodec(-16, 0, BRANCH_START_CODEC).fieldOf("branch_start_offset_from_top").forGetter($$0 -> $$0.branchStartOffsetFromTop), (App)IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top").forGetter($$0 -> $$0.branchEndOffsetFromTop))).apply((Applicative)$$02, CherryTrunkPlacer::new));
    private final IntProvider branchCount;
    private final IntProvider branchHorizontalLength;
    private final UniformInt branchStartOffsetFromTop;
    private final UniformInt secondBranchStartOffsetFromTop;
    private final IntProvider branchEndOffsetFromTop;

    public CherryTrunkPlacer(int $$0, int $$1, int $$2, IntProvider $$3, IntProvider $$4, UniformInt $$5, IntProvider $$6) {
        super($$0, $$1, $$2);
        this.branchCount = $$3;
        this.branchHorizontalLength = $$4;
        this.branchStartOffsetFromTop = $$5;
        this.secondBranchStartOffsetFromTop = UniformInt.of($$5.getMinValue(), $$5.getMaxValue() - 1);
        this.branchEndOffsetFromTop = $$6;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.CHERRY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$12, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        int $$13;
        boolean $$10;
        int $$8;
        CherryTrunkPlacer.setDirtAt($$0, $$12, $$2, $$4.below(), $$5);
        int $$6 = Math.max(0, $$3 - 1 + this.branchStartOffsetFromTop.sample($$2));
        int $$7 = Math.max(0, $$3 - 1 + this.secondBranchStartOffsetFromTop.sample($$2));
        if ($$7 >= $$6) {
            ++$$7;
        }
        boolean $$9 = ($$8 = this.branchCount.sample($$2)) == 3;
        boolean bl = $$10 = $$8 >= 2;
        if ($$9) {
            int $$11 = $$3;
        } else if ($$10) {
            int $$122 = Math.max($$6, $$7) + 1;
        } else {
            $$13 = $$6 + 1;
        }
        for (int $$14 = 0; $$14 < $$13; ++$$14) {
            this.placeLog($$0, $$12, $$2, $$4.above($$14), $$5);
        }
        ArrayList<FoliagePlacer.FoliageAttachment> $$15 = new ArrayList<FoliagePlacer.FoliageAttachment>();
        if ($$9) {
            $$15.add(new FoliagePlacer.FoliageAttachment($$4.above($$13), 0, false));
        }
        BlockPos.MutableBlockPos $$16 = new BlockPos.MutableBlockPos();
        Direction $$17 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
        Function<BlockState, BlockState> $$18 = $$1 -> (BlockState)$$1.trySetValue(RotatedPillarBlock.AXIS, $$17.getAxis());
        $$15.add(this.generateBranch($$0, $$12, $$2, $$3, $$4, $$5, $$18, $$17, $$6, $$6 < $$13 - 1, $$16));
        if ($$10) {
            $$15.add(this.generateBranch($$0, $$12, $$2, $$3, $$4, $$5, $$18, $$17.getOpposite(), $$7, $$7 < $$13 - 1, $$16));
        }
        return $$15;
    }

    private FoliagePlacer.FoliageAttachment generateBranch(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5, Function<BlockState, BlockState> $$6, Direction $$7, int $$8, boolean $$9, BlockPos.MutableBlockPos $$10) {
        int $$18;
        Direction $$17;
        $$10.set($$4).move(Direction.UP, $$8);
        int $$11 = $$3 - 1 + this.branchEndOffsetFromTop.sample($$2);
        boolean $$12 = $$9 || $$11 < $$8;
        int $$13 = this.branchHorizontalLength.sample($$2) + ($$12 ? 1 : 0);
        BlockPos $$14 = $$4.relative($$7, $$13).above($$11);
        int $$15 = $$12 ? 2 : 1;
        for (int $$16 = 0; $$16 < $$15; ++$$16) {
            this.placeLog($$0, $$1, $$2, $$10.move($$7), $$5, $$6);
        }
        Direction direction = $$17 = $$14.getY() > $$10.getY() ? Direction.UP : Direction.DOWN;
        while (($$18 = $$10.distManhattan($$14)) != 0) {
            float $$19 = (float)Math.abs($$14.getY() - $$10.getY()) / (float)$$18;
            boolean $$20 = $$2.nextFloat() < $$19;
            $$10.move($$20 ? $$17 : $$7);
            this.placeLog($$0, $$1, $$2, $$10, $$5, $$20 ? Function.identity() : $$6);
        }
        return new FoliagePlacer.FoliageAttachment($$14.above(), 0, false);
    }
}

