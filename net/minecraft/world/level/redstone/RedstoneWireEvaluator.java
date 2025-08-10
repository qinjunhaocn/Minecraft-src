/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.redstone;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

public abstract class RedstoneWireEvaluator {
    protected final RedStoneWireBlock wireBlock;

    protected RedstoneWireEvaluator(RedStoneWireBlock $$0) {
        this.wireBlock = $$0;
    }

    public abstract void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4, boolean var5);

    protected int getBlockSignal(Level $$0, BlockPos $$1) {
        return this.wireBlock.getBlockSignal($$0, $$1);
    }

    protected int getWireSignal(BlockPos $$0, BlockState $$1) {
        return $$1.is(this.wireBlock) ? $$1.getValue(RedStoneWireBlock.POWER) : 0;
    }

    protected int getIncomingWireSignal(Level $$0, BlockPos $$1) {
        int $$2 = 0;
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            BlockPos $$4 = $$1.relative($$3);
            BlockState $$5 = $$0.getBlockState($$4);
            $$2 = Math.max($$2, this.getWireSignal($$4, $$5));
            BlockPos $$6 = $$1.above();
            if ($$5.isRedstoneConductor($$0, $$4) && !$$0.getBlockState($$6).isRedstoneConductor($$0, $$6)) {
                BlockPos $$7 = $$4.above();
                $$2 = Math.max($$2, this.getWireSignal($$7, $$0.getBlockState($$7)));
                continue;
            }
            if ($$5.isRedstoneConductor($$0, $$4)) continue;
            BlockPos $$8 = $$4.below();
            $$2 = Math.max($$2, this.getWireSignal($$8, $$0.getBlockState($$8)));
        }
        return Math.max(0, $$2 - 1);
    }
}

