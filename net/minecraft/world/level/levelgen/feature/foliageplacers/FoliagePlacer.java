/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.Fluids;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> CODEC = BuiltInRegistries.FOLIAGE_PLACER_TYPE.byNameCodec().dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
    protected final IntProvider radius;
    protected final IntProvider offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, IntProvider, IntProvider> foliagePlacerParts(RecordCodecBuilder.Instance<P> $$02) {
        return $$02.group((App)IntProvider.codec(0, 16).fieldOf("radius").forGetter($$0 -> $$0.radius), (App)IntProvider.codec(0, 16).fieldOf("offset").forGetter($$0 -> $$0.offset));
    }

    public FoliagePlacer(IntProvider $$0, IntProvider $$1) {
        this.radius = $$0;
        this.offset = $$1;
    }

    protected abstract FoliagePlacerType<?> type();

    public void createFoliage(LevelSimulatedReader $$0, FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, int $$4, FoliageAttachment $$5, int $$6, int $$7) {
        this.createFoliage($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, this.offset($$2));
    }

    protected abstract void createFoliage(LevelSimulatedReader var1, FoliageSetter var2, RandomSource var3, TreeConfiguration var4, int var5, FoliageAttachment var6, int var7, int var8, int var9);

    public abstract int foliageHeight(RandomSource var1, int var2, TreeConfiguration var3);

    public int foliageRadius(RandomSource $$0, int $$1) {
        return this.radius.sample($$0);
    }

    private int offset(RandomSource $$0) {
        return this.offset.sample($$0);
    }

    protected abstract boolean shouldSkipLocation(RandomSource var1, int var2, int var3, int var4, int var5, boolean var6);

    protected boolean shouldSkipLocationSigned(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        int $$9;
        int $$8;
        if ($$5) {
            int $$6 = Math.min(Math.abs($$1), Math.abs($$1 - 1));
            int $$7 = Math.min(Math.abs($$3), Math.abs($$3 - 1));
        } else {
            $$8 = Math.abs($$1);
            $$9 = Math.abs($$3);
        }
        return this.shouldSkipLocation($$0, $$8, $$2, $$9, $$4, $$5);
    }

    protected void placeLeavesRow(LevelSimulatedReader $$0, FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, BlockPos $$4, int $$5, int $$6, boolean $$7) {
        int $$8 = $$7 ? 1 : 0;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (int $$10 = -$$5; $$10 <= $$5 + $$8; ++$$10) {
            for (int $$11 = -$$5; $$11 <= $$5 + $$8; ++$$11) {
                if (this.shouldSkipLocationSigned($$2, $$10, $$6, $$11, $$5, $$7)) continue;
                $$9.setWithOffset($$4, $$10, $$6, $$11);
                FoliagePlacer.tryPlaceLeaf($$0, $$1, $$2, $$3, $$9);
            }
        }
    }

    protected final void placeLeavesRowWithHangingLeavesBelow(LevelSimulatedReader $$0, FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, BlockPos $$4, int $$5, int $$6, boolean $$7, float $$8, float $$9) {
        this.placeLeavesRow($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        int $$10 = $$7 ? 1 : 0;
        BlockPos $$11 = $$4.below();
        BlockPos.MutableBlockPos $$12 = new BlockPos.MutableBlockPos();
        for (Direction $$13 : Direction.Plane.HORIZONTAL) {
            Direction $$14 = $$13.getClockWise();
            int $$15 = $$14.getAxisDirection() == Direction.AxisDirection.POSITIVE ? $$5 + $$10 : $$5;
            $$12.setWithOffset($$4, 0, $$6 - 1, 0).move($$14, $$15).move($$13, -$$5);
            for (int $$16 = -$$5; $$16 < $$5 + $$10; ++$$16) {
                boolean $$17 = $$1.isSet($$12.move(Direction.UP));
                $$12.move(Direction.DOWN);
                if ($$17 && FoliagePlacer.tryPlaceExtension($$0, $$1, $$2, $$3, $$8, $$11, $$12)) {
                    $$12.move(Direction.DOWN);
                    FoliagePlacer.tryPlaceExtension($$0, $$1, $$2, $$3, $$9, $$11, $$12);
                    $$12.move(Direction.UP);
                }
                $$12.move($$13);
            }
        }
    }

    private static boolean tryPlaceExtension(LevelSimulatedReader $$0, FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, float $$4, BlockPos $$5, BlockPos.MutableBlockPos $$6) {
        if ($$6.distManhattan($$5) >= 7) {
            return false;
        }
        if ($$2.nextFloat() > $$4) {
            return false;
        }
        return FoliagePlacer.tryPlaceLeaf($$0, $$1, $$2, $$3, $$6);
    }

    protected static boolean tryPlaceLeaf(LevelSimulatedReader $$02, FoliageSetter $$1, RandomSource $$2, TreeConfiguration $$3, BlockPos $$4) {
        boolean $$5 = $$02.isStateAtPosition($$4, $$0 -> $$0.getValueOrElse(BlockStateProperties.PERSISTENT, false));
        if ($$5 || !TreeFeature.validTreePos($$02, $$4)) {
            return false;
        }
        BlockState $$6 = $$3.foliageProvider.getState($$2, $$4);
        if ($$6.hasProperty(BlockStateProperties.WATERLOGGED)) {
            $$6 = (BlockState)$$6.setValue(BlockStateProperties.WATERLOGGED, $$02.isFluidAtPosition($$4, $$0 -> $$0.isSourceOfType(Fluids.WATER)));
        }
        $$1.set($$4, $$6);
        return true;
    }

    public static interface FoliageSetter {
        public void set(BlockPos var1, BlockState var2);

        public boolean isSet(BlockPos var1);
    }

    public static final class FoliageAttachment {
        private final BlockPos pos;
        private final int radiusOffset;
        private final boolean doubleTrunk;

        public FoliageAttachment(BlockPos $$0, int $$1, boolean $$2) {
            this.pos = $$0;
            this.radiusOffset = $$1;
            this.doubleTrunk = $$2;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public int radiusOffset() {
            return this.radiusOffset;
        }

        public boolean doubleTrunk() {
            return this.doubleTrunk;
        }
    }
}

