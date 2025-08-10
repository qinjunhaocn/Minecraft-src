/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.placement;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public abstract class RepeatingPlacement
extends PlacementModifier {
    protected abstract int count(RandomSource var1, BlockPos var2);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$12, BlockPos $$2) {
        return IntStream.range(0, this.count($$12, $$2)).mapToObj($$1 -> $$2);
    }
}

