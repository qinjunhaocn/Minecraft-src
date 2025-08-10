/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.placement;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public abstract class PlacementFilter
extends PlacementModifier {
    @Override
    public final Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        if (this.shouldPlace($$0, $$1, $$2)) {
            return Stream.of($$2);
        }
        return Stream.of(new BlockPos[0]);
    }

    protected abstract boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3);
}

