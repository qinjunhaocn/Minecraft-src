/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickContainerAccess;

public class WorldGenTickAccess<T>
implements LevelTickAccess<T> {
    private final Function<BlockPos, TickContainerAccess<T>> containerGetter;

    public WorldGenTickAccess(Function<BlockPos, TickContainerAccess<T>> $$0) {
        this.containerGetter = $$0;
    }

    @Override
    public boolean hasScheduledTick(BlockPos $$0, T $$1) {
        return this.containerGetter.apply($$0).hasScheduledTick($$0, $$1);
    }

    @Override
    public void schedule(ScheduledTick<T> $$0) {
        this.containerGetter.apply($$0.pos()).schedule($$0);
    }

    @Override
    public boolean willTickThisTick(BlockPos $$0, T $$1) {
        return false;
    }

    @Override
    public int count() {
        return 0;
    }
}

