/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class PlaceOnGroundDecorator
extends TreeDecorator {
    public static final MapCodec<PlaceOnGroundDecorator> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse((Object)128).forGetter($$0 -> $$0.tries), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").orElse((Object)2).forGetter($$0 -> $$0.radius), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("height").orElse((Object)1).forGetter($$0 -> $$0.height), (App)BlockStateProvider.CODEC.fieldOf("block_state_provider").forGetter($$0 -> $$0.blockStateProvider)).apply((Applicative)$$02, PlaceOnGroundDecorator::new));
    private final int tries;
    private final int radius;
    private final int height;
    private final BlockStateProvider blockStateProvider;

    public PlaceOnGroundDecorator(int $$0, int $$1, int $$2, BlockStateProvider $$3) {
        this.tries = $$0;
        this.radius = $$1;
        this.height = $$2;
        this.blockStateProvider = $$3;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.PLACE_ON_GROUND;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        List<BlockPos> $$1 = TreeFeature.getLowestTrunkOrRootOfTree($$0);
        if ($$1.isEmpty()) {
            return;
        }
        BlockPos $$2 = (BlockPos)$$1.getFirst();
        int $$3 = $$2.getY();
        int $$4 = $$2.getX();
        int $$5 = $$2.getX();
        int $$6 = $$2.getZ();
        int $$7 = $$2.getZ();
        for (BlockPos $$8 : $$1) {
            if ($$8.getY() != $$3) continue;
            $$4 = Math.min($$4, $$8.getX());
            $$5 = Math.max($$5, $$8.getX());
            $$6 = Math.min($$6, $$8.getZ());
            $$7 = Math.max($$7, $$8.getZ());
        }
        RandomSource $$9 = $$0.random();
        BoundingBox $$10 = new BoundingBox($$4, $$3, $$6, $$5, $$3, $$7).inflatedBy(this.radius, this.height, this.radius);
        BlockPos.MutableBlockPos $$11 = new BlockPos.MutableBlockPos();
        for (int $$12 = 0; $$12 < this.tries; ++$$12) {
            $$11.set($$9.nextIntBetweenInclusive($$10.minX(), $$10.maxX()), $$9.nextIntBetweenInclusive($$10.minY(), $$10.maxY()), $$9.nextIntBetweenInclusive($$10.minZ(), $$10.maxZ()));
            this.attemptToPlaceBlockAbove($$0, $$11);
        }
    }

    private void attemptToPlaceBlockAbove(TreeDecorator.Context $$02, BlockPos $$1) {
        BlockPos $$2 = $$1.above();
        if ($$02.level().isStateAtPosition($$2, $$0 -> $$0.isAir() || $$0.is(Blocks.VINE)) && $$02.checkBlock($$1, BlockBehaviour.BlockStateBase::isSolidRender) && $$02.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, $$1).getY() <= $$2.getY()) {
            $$02.setBlock($$2, this.blockStateProvider.getState($$02.random(), $$2));
        }
    }
}

