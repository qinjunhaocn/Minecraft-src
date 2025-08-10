/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickContainerAccess;

public class BlackholeTickAccess {
    private static final TickContainerAccess<Object> CONTAINER_BLACKHOLE = new TickContainerAccess<Object>(){

        @Override
        public void schedule(ScheduledTick<Object> $$0) {
        }

        @Override
        public boolean hasScheduledTick(BlockPos $$0, Object $$1) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };
    private static final LevelTickAccess<Object> LEVEL_BLACKHOLE = new LevelTickAccess<Object>(){

        @Override
        public void schedule(ScheduledTick<Object> $$0) {
        }

        @Override
        public boolean hasScheduledTick(BlockPos $$0, Object $$1) {
            return false;
        }

        @Override
        public boolean willTickThisTick(BlockPos $$0, Object $$1) {
            return false;
        }

        @Override
        public int count() {
            return 0;
        }
    };

    public static <T> TickContainerAccess<T> emptyContainer() {
        return CONTAINER_BLACKHOLE;
    }

    public static <T> LevelTickAccess<T> emptyLevelList() {
        return LEVEL_BLACKHOLE;
    }
}

