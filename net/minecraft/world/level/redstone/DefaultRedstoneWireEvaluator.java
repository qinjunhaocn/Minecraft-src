/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.redstone;

import com.google.common.collect.Sets;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;

public class DefaultRedstoneWireEvaluator
extends RedstoneWireEvaluator {
    public DefaultRedstoneWireEvaluator(RedStoneWireBlock $$0) {
        super($$0);
    }

    @Override
    public void updatePowerStrength(Level $$0, BlockPos $$1, BlockState $$2, @Nullable Orientation $$3, boolean $$4) {
        int $$5 = this.calculateTargetStrength($$0, $$1);
        if ($$2.getValue(RedStoneWireBlock.POWER) != $$5) {
            if ($$0.getBlockState($$1) == $$2) {
                $$0.setBlock($$1, (BlockState)$$2.setValue(RedStoneWireBlock.POWER, $$5), 2);
            }
            HashSet<BlockPos> $$6 = Sets.newHashSet();
            $$6.add($$1);
            for (Direction $$7 : Direction.values()) {
                $$6.add($$1.relative($$7));
            }
            for (BlockPos $$8 : $$6) {
                $$0.updateNeighborsAt($$8, this.wireBlock);
            }
        }
    }

    private int calculateTargetStrength(Level $$0, BlockPos $$1) {
        int $$2 = this.getBlockSignal($$0, $$1);
        if ($$2 == 15) {
            return $$2;
        }
        return Math.max($$2, this.getIncomingWireSignal($$0, $$1));
    }
}

