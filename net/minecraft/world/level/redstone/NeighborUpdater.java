/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

public interface NeighborUpdater {
    public static final Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

    public void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6);

    public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3);

    public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5);

    default public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, @Nullable Direction $$2, @Nullable Orientation $$3) {
        for (Direction $$4 : UPDATE_ORDER) {
            if ($$4 == $$2) continue;
            this.neighborChanged($$0.relative($$4), $$1, null);
        }
    }

    public static void executeShapeUpdate(LevelAccessor $$0, Direction $$1, BlockPos $$2, BlockPos $$3, BlockState $$4, int $$5, int $$6) {
        BlockState $$7 = $$0.getBlockState($$2);
        if (($$5 & 0x80) != 0 && $$7.is(Blocks.REDSTONE_WIRE)) {
            return;
        }
        BlockState $$8 = $$7.updateShape($$0, $$0, $$2, $$1, $$3, $$4, $$0.getRandom());
        Block.updateOrDestroy($$7, $$8, $$0, $$2, $$5, $$6);
    }

    public static void executeUpdate(Level $$0, BlockState $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        try {
            $$1.handleNeighborChanged($$0, $$2, $$3, $$4, $$5);
        } catch (Throwable $$6) {
            CrashReport $$7 = CrashReport.forThrowable($$6, "Exception while updating neighbours");
            CrashReportCategory $$8 = $$7.addCategory("Block being updated");
            $$8.setDetail("Source block type", () -> {
                try {
                    return String.format(Locale.ROOT, "ID #%s (%s // %s)", BuiltInRegistries.BLOCK.getKey($$3), $$3.getDescriptionId(), $$3.getClass().getCanonicalName());
                } catch (Throwable $$1) {
                    return "ID #" + String.valueOf(BuiltInRegistries.BLOCK.getKey($$3));
                }
            });
            CrashReportCategory.populateBlockDetails($$8, $$0, $$2, $$1);
            throw new ReportedException($$7);
        }
    }
}

