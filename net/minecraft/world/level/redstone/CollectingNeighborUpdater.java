/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.level.redstone;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import org.slf4j.Logger;

public class CollectingNeighborUpdater
implements NeighborUpdater {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Level level;
    private final int maxChainedNeighborUpdates;
    private final ArrayDeque<NeighborUpdates> stack = new ArrayDeque();
    private final List<NeighborUpdates> addedThisLayer = new ArrayList<NeighborUpdates>();
    private int count = 0;

    public CollectingNeighborUpdater(Level $$0, int $$1) {
        this.level = $$0;
        this.maxChainedNeighborUpdates = $$1;
    }

    @Override
    public void shapeUpdate(Direction $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, int $$4, int $$5) {
        this.addAndRun($$2, new ShapeUpdate($$0, $$1, $$2.immutable(), $$3.immutable(), $$4, $$5));
    }

    @Override
    public void neighborChanged(BlockPos $$0, Block $$1, @Nullable Orientation $$2) {
        this.addAndRun($$0, new SimpleNeighborUpdate($$0, $$1, $$2));
    }

    @Override
    public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, @Nullable Orientation $$3, boolean $$4) {
        this.addAndRun($$1, new FullNeighborUpdate($$0, $$1.immutable(), $$2, $$3, $$4));
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, @Nullable Direction $$2, @Nullable Orientation $$3) {
        this.addAndRun($$0, new MultiNeighborUpdate($$0.immutable(), $$1, $$3, $$2));
    }

    private void addAndRun(BlockPos $$0, NeighborUpdates $$1) {
        boolean $$2 = this.count > 0;
        boolean $$3 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;
        ++this.count;
        if (!$$3) {
            if ($$2) {
                this.addedThisLayer.add($$1);
            } else {
                this.stack.push($$1);
            }
        } else if (this.count - 1 == this.maxChainedNeighborUpdates) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + $$0.toShortString());
        }
        if (!$$2) {
            this.runUpdates();
        }
    }

    private void runUpdates() {
        try {
            block3: while (!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
                for (int $$0 = this.addedThisLayer.size() - 1; $$0 >= 0; --$$0) {
                    this.stack.push(this.addedThisLayer.get($$0));
                }
                this.addedThisLayer.clear();
                NeighborUpdates $$1 = this.stack.peek();
                while (this.addedThisLayer.isEmpty()) {
                    if ($$1.runNext(this.level)) continue;
                    this.stack.pop();
                    continue block3;
                }
            }
        } finally {
            this.stack.clear();
            this.addedThisLayer.clear();
            this.count = 0;
        }
    }

    record ShapeUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            NeighborUpdater.executeShapeUpdate($$0, this.direction, this.pos, this.neighborPos, this.neighborState, this.updateFlags, this.updateLimit);
            return false;
        }
    }

    static interface NeighborUpdates {
        public boolean runNext(Level var1);
    }

    record SimpleNeighborUpdate(BlockPos pos, Block block, @Nullable Orientation orientation) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            BlockState $$1 = $$0.getBlockState(this.pos);
            NeighborUpdater.executeUpdate($$0, $$1, this.pos, this.block, this.orientation, false);
            return false;
        }

        @Nullable
        public Orientation orientation() {
            return this.orientation;
        }
    }

    record FullNeighborUpdate(BlockState state, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            NeighborUpdater.executeUpdate($$0, this.state, this.pos, this.block, this.orientation, this.movedByPiston);
            return false;
        }

        @Nullable
        public Orientation orientation() {
            return this.orientation;
        }
    }

    static final class MultiNeighborUpdate
    implements NeighborUpdates {
        private final BlockPos sourcePos;
        private final Block sourceBlock;
        @Nullable
        private Orientation orientation;
        @Nullable
        private final Direction skipDirection;
        private int idx = 0;

        MultiNeighborUpdate(BlockPos $$0, Block $$1, @Nullable Orientation $$2, @Nullable Direction $$3) {
            this.sourcePos = $$0;
            this.sourceBlock = $$1;
            this.orientation = $$2;
            this.skipDirection = $$3;
            if (NeighborUpdater.UPDATE_ORDER[this.idx] == $$3) {
                ++this.idx;
            }
        }

        @Override
        public boolean runNext(Level $$0) {
            Direction $$1 = NeighborUpdater.UPDATE_ORDER[this.idx++];
            BlockPos $$2 = this.sourcePos.relative($$1);
            BlockState $$3 = $$0.getBlockState($$2);
            Orientation $$4 = null;
            if ($$0.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
                if (this.orientation == null) {
                    this.orientation = ExperimentalRedstoneUtils.initialOrientation($$0, this.skipDirection == null ? null : this.skipDirection.getOpposite(), null);
                }
                $$4 = this.orientation.withFront($$1);
            }
            NeighborUpdater.executeUpdate($$0, $$3, $$2, this.sourceBlock, $$4, false);
            if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
                ++this.idx;
            }
            return this.idx < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}

