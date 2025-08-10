/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MangroveRootPlacer
extends RootPlacer {
    public static final int ROOT_WIDTH_LIMIT = 8;
    public static final int ROOT_LENGTH_LIMIT = 15;
    public static final MapCodec<MangroveRootPlacer> CODEC = RecordCodecBuilder.mapCodec($$02 -> MangroveRootPlacer.rootPlacerParts($$02).and((App)MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter($$0 -> $$0.mangroveRootPlacement)).apply((Applicative)$$02, MangroveRootPlacer::new));
    private final MangroveRootPlacement mangroveRootPlacement;

    public MangroveRootPlacer(IntProvider $$0, BlockStateProvider $$1, Optional<AboveRootPlacement> $$2, MangroveRootPlacement $$3) {
        super($$0, $$1, $$2);
        this.mangroveRootPlacement = $$3;
    }

    @Override
    public boolean placeRoots(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos $$3, BlockPos $$4, TreeConfiguration $$5) {
        ArrayList<BlockPos> $$6 = Lists.newArrayList();
        BlockPos.MutableBlockPos $$7 = $$3.mutable();
        while ($$7.getY() < $$4.getY()) {
            if (!this.canPlaceRoot($$0, $$7)) {
                return false;
            }
            $$7.move(Direction.UP);
        }
        $$6.add($$4.below());
        for (Direction $$8 : Direction.Plane.HORIZONTAL) {
            ArrayList<BlockPos> $$10;
            BlockPos $$9 = $$4.relative($$8);
            if (!this.simulateRoots($$0, $$2, $$9, $$8, $$4, $$10 = Lists.newArrayList(), 0)) {
                return false;
            }
            $$6.addAll($$10);
            $$6.add($$4.relative($$8));
        }
        for (BlockPos $$11 : $$6) {
            this.placeRoot($$0, $$1, $$2, $$11, $$5);
        }
        return true;
    }

    private boolean simulateRoots(LevelSimulatedReader $$0, RandomSource $$1, BlockPos $$2, Direction $$3, BlockPos $$4, List<BlockPos> $$5, int $$6) {
        int $$7 = this.mangroveRootPlacement.maxRootLength();
        if ($$6 == $$7 || $$5.size() > $$7) {
            return false;
        }
        List<BlockPos> $$8 = this.potentialRootPositions($$2, $$3, $$1, $$4);
        for (BlockPos $$9 : $$8) {
            if (!this.canPlaceRoot($$0, $$9)) continue;
            $$5.add($$9);
            if (this.simulateRoots($$0, $$1, $$9, $$3, $$4, $$5, $$6 + 1)) continue;
            return false;
        }
        return true;
    }

    protected List<BlockPos> potentialRootPositions(BlockPos $$0, Direction $$1, RandomSource $$2, BlockPos $$3) {
        BlockPos $$4 = $$0.below();
        BlockPos $$5 = $$0.relative($$1);
        int $$6 = $$0.distManhattan($$3);
        int $$7 = this.mangroveRootPlacement.maxRootWidth();
        float $$8 = this.mangroveRootPlacement.randomSkewChance();
        if ($$6 > $$7 - 3 && $$6 <= $$7) {
            return $$2.nextFloat() < $$8 ? List.of((Object)$$4, (Object)$$5.below()) : List.of((Object)$$4);
        }
        if ($$6 > $$7) {
            return List.of((Object)$$4);
        }
        if ($$2.nextFloat() < $$8) {
            return List.of((Object)$$4);
        }
        return $$2.nextBoolean() ? List.of((Object)$$5) : List.of((Object)$$4);
    }

    @Override
    protected boolean canPlaceRoot(LevelSimulatedReader $$02, BlockPos $$1) {
        return super.canPlaceRoot($$02, $$1) || $$02.isStateAtPosition($$1, $$0 -> $$0.is(this.mangroveRootPlacement.canGrowThrough()));
    }

    @Override
    protected void placeRoot(LevelSimulatedReader $$02, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, BlockPos $$3, TreeConfiguration $$4) {
        if ($$02.isStateAtPosition($$3, $$0 -> $$0.is(this.mangroveRootPlacement.muddyRootsIn()))) {
            BlockState $$5 = this.mangroveRootPlacement.muddyRootsProvider().getState($$2, $$3);
            $$1.accept($$3, this.getPotentiallyWaterloggedState($$02, $$3, $$5));
        } else {
            super.placeRoot($$02, $$1, $$2, $$3, $$4);
        }
    }

    @Override
    protected RootPlacerType<?> type() {
        return RootPlacerType.MANGROVE_ROOT_PLACER;
    }
}

