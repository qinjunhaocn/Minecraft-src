/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.HashCommon
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.HashCommon;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class PathTypeCache {
    private static final int SIZE = 4096;
    private static final int MASK = 4095;
    private final long[] positions = new long[4096];
    private final PathType[] pathTypes = new PathType[4096];

    public PathType getOrCompute(BlockGetter $$0, BlockPos $$1) {
        long $$2 = $$1.asLong();
        int $$3 = PathTypeCache.index($$2);
        PathType $$4 = this.get($$3, $$2);
        if ($$4 != null) {
            return $$4;
        }
        return this.compute($$0, $$1, $$3, $$2);
    }

    @Nullable
    private PathType get(int $$0, long $$1) {
        if (this.positions[$$0] == $$1) {
            return this.pathTypes[$$0];
        }
        return null;
    }

    private PathType compute(BlockGetter $$0, BlockPos $$1, int $$2, long $$3) {
        PathType $$4 = WalkNodeEvaluator.getPathTypeFromState($$0, $$1);
        this.positions[$$2] = $$3;
        this.pathTypes[$$2] = $$4;
        return $$4;
    }

    public void invalidate(BlockPos $$0) {
        long $$1 = $$0.asLong();
        int $$2 = PathTypeCache.index($$1);
        if (this.positions[$$2] == $$1) {
            this.pathTypes[$$2] = null;
        }
    }

    private static int index(long $$0) {
        return (int)HashCommon.mix((long)$$0) & 0xFFF;
    }
}

