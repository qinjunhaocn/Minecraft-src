/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class PortalForcer {
    public static final int TICKET_RADIUS = 3;
    private static final int NETHER_PORTAL_RADIUS = 16;
    private static final int OVERWORLD_PORTAL_RADIUS = 128;
    private static final int FRAME_HEIGHT = 5;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_BOX = 3;
    private static final int FRAME_HEIGHT_START = -1;
    private static final int FRAME_HEIGHT_END = 4;
    private static final int FRAME_WIDTH_START = -1;
    private static final int FRAME_WIDTH_END = 3;
    private static final int FRAME_BOX_START = -1;
    private static final int FRAME_BOX_END = 2;
    private static final int NOTHING_FOUND = -1;
    private final ServerLevel level;

    public PortalForcer(ServerLevel $$0) {
        this.level = $$0;
    }

    public Optional<BlockPos> findClosestPortalPosition(BlockPos $$02, boolean $$12, WorldBorder $$2) {
        PoiManager $$3 = this.level.getPoiManager();
        int $$4 = $$12 ? 16 : 128;
        $$3.ensureLoadedAndValid(this.level, $$02, $$4);
        return $$3.getInSquare($$0 -> $$0.is(PoiTypes.NETHER_PORTAL), $$02, $$4, PoiManager.Occupancy.ANY).map(PoiRecord::getPos).filter($$2::isWithinBounds).filter($$0 -> this.level.getBlockState((BlockPos)$$0).hasProperty(BlockStateProperties.HORIZONTAL_AXIS)).min(Comparator.comparingDouble($$1 -> $$1.distSqr($$02)).thenComparingInt(Vec3i::getY));
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos $$0, Direction.Axis $$1) {
        Direction $$2 = Direction.get(Direction.AxisDirection.POSITIVE, $$1);
        double $$3 = -1.0;
        BlockPos $$4 = null;
        double $$5 = -1.0;
        BlockPos $$6 = null;
        WorldBorder $$7 = this.level.getWorldBorder();
        int $$8 = Math.min(this.level.getMaxY(), this.level.getMinY() + this.level.getLogicalHeight() - 1);
        boolean $$9 = true;
        BlockPos.MutableBlockPos $$10 = $$0.mutable();
        for (BlockPos.MutableBlockPos $$11 : BlockPos.spiralAround($$0, 16, Direction.EAST, Direction.SOUTH)) {
            int $$12 = Math.min($$8, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, $$11.getX(), $$11.getZ()));
            if (!$$7.isWithinBounds($$11) || !$$7.isWithinBounds($$11.move($$2, 1))) continue;
            $$11.move($$2.getOpposite(), 1);
            for (int $$13 = $$12; $$13 >= this.level.getMinY(); --$$13) {
                int $$15;
                $$11.setY($$13);
                if (!this.canPortalReplaceBlock($$11)) continue;
                int $$14 = $$13;
                while ($$13 > this.level.getMinY() && this.canPortalReplaceBlock($$11.move(Direction.DOWN))) {
                    --$$13;
                }
                if ($$13 + 4 > $$8 || ($$15 = $$14 - $$13) > 0 && $$15 < 3) continue;
                $$11.setY($$13);
                if (!this.canHostFrame($$11, $$10, $$2, 0)) continue;
                double $$16 = $$0.distSqr($$11);
                if (this.canHostFrame($$11, $$10, $$2, -1) && this.canHostFrame($$11, $$10, $$2, 1) && ($$3 == -1.0 || $$3 > $$16)) {
                    $$3 = $$16;
                    $$4 = $$11.immutable();
                }
                if ($$3 != -1.0 || $$5 != -1.0 && !($$5 > $$16)) continue;
                $$5 = $$16;
                $$6 = $$11.immutable();
            }
        }
        if ($$3 == -1.0 && $$5 != -1.0) {
            $$4 = $$6;
            $$3 = $$5;
        }
        if ($$3 == -1.0) {
            int $$18 = $$8 - 9;
            int $$17 = Math.max(this.level.getMinY() - -1, 70);
            if ($$18 < $$17) {
                return Optional.empty();
            }
            $$4 = new BlockPos($$0.getX() - $$2.getStepX() * 1, Mth.clamp($$0.getY(), $$17, $$18), $$0.getZ() - $$2.getStepZ() * 1).immutable();
            $$4 = $$7.clampToBounds($$4);
            Direction $$19 = $$2.getClockWise();
            for (int $$20 = -1; $$20 < 2; ++$$20) {
                for (int $$21 = 0; $$21 < 2; ++$$21) {
                    for (int $$22 = -1; $$22 < 3; ++$$22) {
                        BlockState $$23 = $$22 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        $$10.setWithOffset($$4, $$21 * $$2.getStepX() + $$20 * $$19.getStepX(), $$22, $$21 * $$2.getStepZ() + $$20 * $$19.getStepZ());
                        this.level.setBlockAndUpdate($$10, $$23);
                    }
                }
            }
        }
        for (int $$24 = -1; $$24 < 3; ++$$24) {
            for (int $$25 = -1; $$25 < 4; ++$$25) {
                if ($$24 != -1 && $$24 != 2 && $$25 != -1 && $$25 != 3) continue;
                $$10.setWithOffset($$4, $$24 * $$2.getStepX(), $$25, $$24 * $$2.getStepZ());
                this.level.setBlock($$10, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
        }
        BlockState $$26 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, $$1);
        for (int $$27 = 0; $$27 < 2; ++$$27) {
            for (int $$28 = 0; $$28 < 3; ++$$28) {
                $$10.setWithOffset($$4, $$27 * $$2.getStepX(), $$28, $$27 * $$2.getStepZ());
                this.level.setBlock($$10, $$26, 18);
            }
        }
        return Optional.of(new BlockUtil.FoundRectangle($$4.immutable(), 2, 3));
    }

    private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        return $$1.canBeReplaced() && $$1.getFluidState().isEmpty();
    }

    private boolean canHostFrame(BlockPos $$0, BlockPos.MutableBlockPos $$1, Direction $$2, int $$3) {
        Direction $$4 = $$2.getClockWise();
        for (int $$5 = -1; $$5 < 3; ++$$5) {
            for (int $$6 = -1; $$6 < 4; ++$$6) {
                $$1.setWithOffset($$0, $$2.getStepX() * $$5 + $$4.getStepX() * $$3, $$6, $$2.getStepZ() * $$5 + $$4.getStepZ() * $$3);
                if ($$6 < 0 && !this.level.getBlockState($$1).isSolid()) {
                    return false;
                }
                if ($$6 < 0 || this.canPortalReplaceBlock($$1)) continue;
                return false;
            }
        }
        return true;
    }
}

