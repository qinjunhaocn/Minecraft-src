/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface ScheduledTickAccess {
    public <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3, TickPriority var4);

    public <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3);

    public LevelTickAccess<Block> getBlockTicks();

    default public void scheduleTick(BlockPos $$0, Block $$1, int $$2, TickPriority $$3) {
        this.getBlockTicks().schedule(this.createTick($$0, $$1, $$2, $$3));
    }

    default public void scheduleTick(BlockPos $$0, Block $$1, int $$2) {
        this.getBlockTicks().schedule(this.createTick($$0, $$1, $$2));
    }

    public LevelTickAccess<Fluid> getFluidTicks();

    default public void scheduleTick(BlockPos $$0, Fluid $$1, int $$2, TickPriority $$3) {
        this.getFluidTicks().schedule(this.createTick($$0, $$1, $$2, $$3));
    }

    default public void scheduleTick(BlockPos $$0, Fluid $$1, int $$2) {
        this.getFluidTicks().schedule(this.createTick($$0, $$1, $$2));
    }
}

