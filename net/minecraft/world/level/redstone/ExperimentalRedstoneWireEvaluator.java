/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package net.minecraft.world.level.redstone;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;

public class ExperimentalRedstoneWireEvaluator
extends RedstoneWireEvaluator {
    private final Deque<BlockPos> wiresToTurnOff = new ArrayDeque<BlockPos>();
    private final Deque<BlockPos> wiresToTurnOn = new ArrayDeque<BlockPos>();
    private final Object2IntMap<BlockPos> updatedWires = new Object2IntLinkedOpenHashMap();

    public ExperimentalRedstoneWireEvaluator(RedStoneWireBlock $$0) {
        super($$0);
    }

    @Override
    public void updatePowerStrength(Level $$0, BlockPos $$1, BlockState $$2, @Nullable Orientation $$3, boolean $$4) {
        Orientation $$5 = ExperimentalRedstoneWireEvaluator.getInitialOrientation($$0, $$3);
        this.calculateCurrentChanges($$0, $$1, $$5);
        ObjectIterator $$6 = this.updatedWires.object2IntEntrySet().iterator();
        boolean $$7 = true;
        while ($$6.hasNext()) {
            Object2IntMap.Entry $$8 = (Object2IntMap.Entry)$$6.next();
            BlockPos $$9 = (BlockPos)$$8.getKey();
            int $$10 = $$8.getIntValue();
            int $$11 = ExperimentalRedstoneWireEvaluator.unpackPower($$10);
            BlockState $$12 = $$0.getBlockState($$9);
            if ($$12.is(this.wireBlock) && !$$12.getValue(RedStoneWireBlock.POWER).equals($$11)) {
                int $$13 = 2;
                if (!$$4 || !$$7) {
                    $$13 |= 0x80;
                }
                $$0.setBlock($$9, (BlockState)$$12.setValue(RedStoneWireBlock.POWER, $$11), $$13);
            } else {
                $$6.remove();
            }
            $$7 = false;
        }
        this.causeNeighborUpdates($$0);
    }

    private void causeNeighborUpdates(Level $$0) {
        this.updatedWires.forEach(($$1, $$2) -> {
            Orientation $$3 = ExperimentalRedstoneWireEvaluator.unpackOrientation($$2);
            BlockState $$4 = $$0.getBlockState((BlockPos)$$1);
            for (Direction $$5 : $$3.getDirections()) {
                if (!ExperimentalRedstoneWireEvaluator.isConnected($$4, $$5)) continue;
                BlockPos $$6 = $$1.relative($$5);
                BlockState $$7 = $$0.getBlockState($$6);
                Orientation $$8 = $$3.withFrontPreserveUp($$5);
                $$0.neighborChanged($$7, $$6, this.wireBlock, $$8, false);
                if (!$$7.isRedstoneConductor($$0, $$6)) continue;
                for (Direction $$9 : $$8.getDirections()) {
                    if ($$9 == $$5.getOpposite()) continue;
                    $$0.neighborChanged($$6.relative($$9), this.wireBlock, $$8.withFrontPreserveUp($$9));
                }
            }
        });
    }

    private static boolean isConnected(BlockState $$0, Direction $$1) {
        EnumProperty<RedstoneSide> $$2 = RedStoneWireBlock.PROPERTY_BY_DIRECTION.get($$1);
        if ($$2 == null) {
            return $$1 == Direction.DOWN;
        }
        return $$0.getValue($$2).isConnected();
    }

    private static Orientation getInitialOrientation(Level $$0, @Nullable Orientation $$1) {
        Orientation $$3;
        if ($$1 != null) {
            Orientation $$2 = $$1;
        } else {
            $$3 = Orientation.random($$0.random);
        }
        return $$3.withUp(Direction.UP).withSideBias(Orientation.SideBias.LEFT);
    }

    private void calculateCurrentChanges(Level $$0, BlockPos $$1, Orientation $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        if ($$3.is(this.wireBlock)) {
            this.setPower($$1, $$3.getValue(RedStoneWireBlock.POWER), $$2);
            this.wiresToTurnOff.add($$1);
        } else {
            this.propagateChangeToNeighbors($$0, $$1, 0, $$2, true);
        }
        while (!this.wiresToTurnOff.isEmpty()) {
            int $$12;
            int $$9;
            BlockPos $$4 = this.wiresToTurnOff.removeFirst();
            int $$5 = this.updatedWires.getInt((Object)$$4);
            Orientation $$6 = ExperimentalRedstoneWireEvaluator.unpackOrientation($$5);
            int $$7 = ExperimentalRedstoneWireEvaluator.unpackPower($$5);
            int $$8 = this.getBlockSignal($$0, $$4);
            int $$10 = Math.max($$8, $$9 = this.getIncomingWireSignal($$0, $$4));
            if ($$10 < $$7) {
                if ($$8 > 0 && !this.wiresToTurnOn.contains($$4)) {
                    this.wiresToTurnOn.add($$4);
                }
                boolean $$11 = false;
            } else {
                $$12 = $$10;
            }
            if ($$12 != $$7) {
                this.setPower($$4, $$12, $$6);
            }
            this.propagateChangeToNeighbors($$0, $$4, $$12, $$6, $$7 > $$10);
        }
        while (!this.wiresToTurnOn.isEmpty()) {
            BlockPos $$13 = this.wiresToTurnOn.removeFirst();
            int $$14 = this.updatedWires.getInt((Object)$$13);
            int $$15 = ExperimentalRedstoneWireEvaluator.unpackPower($$14);
            int $$16 = this.getBlockSignal($$0, $$13);
            int $$17 = this.getIncomingWireSignal($$0, $$13);
            int $$18 = Math.max($$16, $$17);
            Orientation $$19 = ExperimentalRedstoneWireEvaluator.unpackOrientation($$14);
            if ($$18 > $$15) {
                this.setPower($$13, $$18, $$19);
            } else if ($$18 < $$15) {
                throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
            }
            this.propagateChangeToNeighbors($$0, $$13, $$18, $$19, false);
        }
    }

    private static int packOrientationAndPower(Orientation $$0, int $$1) {
        return $$0.getIndex() << 4 | $$1;
    }

    private static Orientation unpackOrientation(int $$0) {
        return Orientation.fromIndex($$0 >> 4);
    }

    private static int unpackPower(int $$0) {
        return $$0 & 0xF;
    }

    private void setPower(BlockPos $$0, int $$1, Orientation $$22) {
        this.updatedWires.compute((Object)$$0, ($$2, $$3) -> {
            if ($$3 == null) {
                return ExperimentalRedstoneWireEvaluator.packOrientationAndPower($$22, $$1);
            }
            return ExperimentalRedstoneWireEvaluator.packOrientationAndPower(ExperimentalRedstoneWireEvaluator.unpackOrientation($$3), $$1);
        });
    }

    private void propagateChangeToNeighbors(Level $$0, BlockPos $$1, int $$2, Orientation $$3, boolean $$4) {
        for (Direction $$5 : $$3.getHorizontalDirections()) {
            BlockPos $$6 = $$1.relative($$5);
            this.enqueueNeighborWire($$0, $$6, $$2, $$3.withFront($$5), $$4);
        }
        for (Direction $$7 : $$3.getVerticalDirections()) {
            BlockPos $$8 = $$1.relative($$7);
            boolean $$9 = $$0.getBlockState($$8).isRedstoneConductor($$0, $$8);
            for (Direction $$10 : $$3.getHorizontalDirections()) {
                BlockPos $$11 = $$1.relative($$10);
                if ($$7 == Direction.UP && !$$9) {
                    BlockPos $$12 = $$8.relative($$10);
                    this.enqueueNeighborWire($$0, $$12, $$2, $$3.withFront($$10), $$4);
                    continue;
                }
                if ($$7 != Direction.DOWN || $$0.getBlockState($$11).isRedstoneConductor($$0, $$11)) continue;
                BlockPos $$13 = $$8.relative($$10);
                this.enqueueNeighborWire($$0, $$13, $$2, $$3.withFront($$10), $$4);
            }
        }
    }

    private void enqueueNeighborWire(Level $$0, BlockPos $$1, int $$2, Orientation $$3, boolean $$4) {
        BlockState $$5 = $$0.getBlockState($$1);
        if ($$5.is(this.wireBlock)) {
            int $$6 = this.getWireSignal($$1, $$5);
            if ($$6 < $$2 - 1 && !this.wiresToTurnOn.contains($$1)) {
                this.wiresToTurnOn.add($$1);
                this.setPower($$1, $$6, $$3);
            }
            if ($$4 && $$6 > $$2 && !this.wiresToTurnOff.contains($$1)) {
                this.wiresToTurnOff.add($$1);
                this.setPower($$1, $$6, $$3);
            }
        }
    }

    @Override
    protected int getWireSignal(BlockPos $$0, BlockState $$1) {
        int $$2 = this.updatedWires.getOrDefault((Object)$$0, -1);
        if ($$2 != -1) {
            return ExperimentalRedstoneWireEvaluator.unpackPower($$2);
        }
        return super.getWireSignal($$0, $$1);
    }

    private static /* synthetic */ void lambda$causeNeighborUpdates$1(List $$0, BlockPos $$1, Integer $$2) {
        Orientation $$3 = ExperimentalRedstoneWireEvaluator.unpackOrientation($$2);
        $$0.add(new RedstoneWireOrientationsDebugPayload.Wire($$1, $$3));
    }
}

