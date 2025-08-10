/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
    public static final int SCAN_DISTANCE = 4;

    public Optional<BlockState> getNext(BlockState var1);

    public float getChanceModifier();

    default public void changeOverTime(BlockState $$0, ServerLevel $$1, BlockPos $$22, RandomSource $$3) {
        float $$4 = 0.05688889f;
        if ($$3.nextFloat() < 0.05688889f) {
            this.getNextState($$0, $$1, $$22, $$3).ifPresent($$2 -> $$1.setBlockAndUpdate($$22, (BlockState)$$2));
        }
    }

    public T getAge();

    default public Optional<BlockState> getNextState(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockPos $$7;
        int $$8;
        int $$4 = ((Enum)this.getAge()).ordinal();
        int $$5 = 0;
        int $$6 = 0;
        Iterator<BlockPos> iterator = BlockPos.withinManhattan($$2, 4, 4, 4).iterator();
        while (iterator.hasNext() && ($$8 = ($$7 = iterator.next()).distManhattan($$2)) <= 4) {
            Block block;
            if ($$7.equals($$2) || !((block = $$1.getBlockState($$7).getBlock()) instanceof ChangeOverTimeBlock)) continue;
            ChangeOverTimeBlock $$9 = (ChangeOverTimeBlock)((Object)block);
            T $$10 = $$9.getAge();
            if (this.getAge().getClass() != $$10.getClass()) continue;
            int $$11 = ((Enum)$$10).ordinal();
            if ($$11 < $$4) {
                return Optional.empty();
            }
            if ($$11 > $$4) {
                ++$$6;
                continue;
            }
            ++$$5;
        }
        float $$12 = (float)($$6 + 1) / (float)($$6 + $$5 + 1);
        float $$13 = $$12 * $$12 * this.getChanceModifier();
        if ($$3.nextFloat() < $$13) {
            return this.getNext($$0);
        }
        return Optional.empty();
    }
}

