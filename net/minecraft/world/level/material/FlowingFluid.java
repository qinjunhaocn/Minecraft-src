/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 */
package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid
extends Fluid {
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
    private static final int CACHE_SIZE = 200;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<BlockStatePairKey> $$0 = new Object2ByteLinkedOpenHashMap<BlockStatePairKey>(200){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue((byte)127);
        return $$0;
    });
    private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> $$0) {
        $$0.a(FALLING);
    }

    @Override
    public Vec3 getFlow(BlockGetter $$0, BlockPos $$1, FluidState $$2) {
        double $$3 = 0.0;
        double $$4 = 0.0;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            $$5.setWithOffset((Vec3i)$$1, $$6);
            FluidState $$7 = $$0.getFluidState($$5);
            if (!this.affectsFlow($$7)) continue;
            float $$8 = $$7.getOwnHeight();
            float $$9 = 0.0f;
            if ($$8 == 0.0f) {
                Vec3i $$10;
                FluidState $$11;
                if (!$$0.getBlockState($$5).blocksMotion() && this.affectsFlow($$11 = $$0.getFluidState((BlockPos)($$10 = $$5.below()))) && ($$8 = $$11.getOwnHeight()) > 0.0f) {
                    $$9 = $$2.getOwnHeight() - ($$8 - 0.8888889f);
                }
            } else if ($$8 > 0.0f) {
                $$9 = $$2.getOwnHeight() - $$8;
            }
            if ($$9 == 0.0f) continue;
            $$3 += (double)((float)$$6.getStepX() * $$9);
            $$4 += (double)((float)$$6.getStepZ() * $$9);
        }
        Vec3 $$12 = new Vec3($$3, 0.0, $$4);
        if ($$2.getValue(FALLING).booleanValue()) {
            for (Direction $$13 : Direction.Plane.HORIZONTAL) {
                $$5.setWithOffset((Vec3i)$$1, $$13);
                if (!this.isSolidFace($$0, $$5, $$13) && !this.isSolidFace($$0, (BlockPos)$$5.above(), $$13)) continue;
                $$12 = $$12.normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return $$12.normalize();
    }

    private boolean affectsFlow(FluidState $$0) {
        return $$0.isEmpty() || $$0.getType().isSame(this);
    }

    protected boolean isSolidFace(BlockGetter $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        FluidState $$4 = $$0.getFluidState($$1);
        if ($$4.getType().isSame(this)) {
            return false;
        }
        if ($$2 == Direction.UP) {
            return true;
        }
        if ($$3.getBlock() instanceof IceBlock) {
            return false;
        }
        return $$3.isFaceSturdy($$0, $$1, $$2);
    }

    protected void spread(ServerLevel $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        FluidState $$7;
        Fluid $$8;
        FluidState $$6;
        BlockState $$5;
        if ($$3.isEmpty()) {
            return;
        }
        BlockPos $$4 = $$1.below();
        if (this.canMaybePassThrough($$0, $$1, $$2, Direction.DOWN, $$4, $$5 = $$0.getBlockState($$4), $$6 = $$5.getFluidState()) && $$6.canBeReplacedWith($$0, $$4, $$8 = ($$7 = this.getNewLiquid($$0, $$4, $$5)).getType(), Direction.DOWN) && FlowingFluid.canHoldSpecificFluid($$0, $$4, $$5, $$8)) {
            this.spreadTo($$0, $$4, $$5, Direction.DOWN, $$7);
            if (this.sourceNeighborCount($$0, $$1) >= 3) {
                this.spreadToSides($$0, $$1, $$3, $$2);
            }
            return;
        }
        if ($$3.isSource() || !this.isWaterHole($$0, $$1, $$2, $$4, $$5)) {
            this.spreadToSides($$0, $$1, $$3, $$2);
        }
    }

    private void spreadToSides(ServerLevel $$0, BlockPos $$1, FluidState $$2, BlockState $$3) {
        int $$4 = $$2.getAmount() - this.getDropOff($$0);
        if ($$2.getValue(FALLING).booleanValue()) {
            $$4 = 7;
        }
        if ($$4 <= 0) {
            return;
        }
        Map<Direction, FluidState> $$5 = this.getSpread($$0, $$1, $$3);
        for (Map.Entry<Direction, FluidState> $$6 : $$5.entrySet()) {
            Direction $$7 = $$6.getKey();
            FluidState $$8 = $$6.getValue();
            BlockPos $$9 = $$1.relative($$7);
            this.spreadTo($$0, $$9, $$0.getBlockState($$9), $$7, $$8);
        }
    }

    protected FluidState getNewLiquid(ServerLevel $$0, BlockPos $$1, BlockState $$2) {
        BlockPos.MutableBlockPos $$12;
        BlockState $$13;
        FluidState $$14;
        int $$3 = 0;
        int $$4 = 0;
        BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            BlockPos.MutableBlockPos $$7 = $$5.setWithOffset((Vec3i)$$1, $$6);
            BlockState $$8 = $$0.getBlockState($$7);
            FluidState $$9 = $$8.getFluidState();
            if (!$$9.getType().isSame(this) || !FlowingFluid.canPassThroughWall($$6, $$0, $$1, $$2, $$7, $$8)) continue;
            if ($$9.isSource()) {
                ++$$4;
            }
            $$3 = Math.max($$3, $$9.getAmount());
        }
        if ($$4 >= 2 && this.canConvertToSource($$0)) {
            BlockState $$10 = $$0.getBlockState($$5.setWithOffset((Vec3i)$$1, Direction.DOWN));
            FluidState $$11 = $$10.getFluidState();
            if ($$10.isSolid() || this.isSourceBlockOfThisType($$11)) {
                return this.getSource(false);
            }
        }
        if (!($$14 = ($$13 = $$0.getBlockState($$12 = $$5.setWithOffset((Vec3i)$$1, Direction.UP))).getFluidState()).isEmpty() && $$14.getType().isSame(this) && FlowingFluid.canPassThroughWall(Direction.UP, $$0, $$1, $$2, $$12, $$13)) {
            return this.getFlowing(8, true);
        }
        int $$15 = $$3 - this.getDropOff($$0);
        if ($$15 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return this.getFlowing($$15, false);
    }

    private static boolean canPassThroughWall(Direction $$0, BlockGetter $$1, BlockPos $$2, BlockState $$3, BlockPos $$4, BlockState $$5) {
        boolean $$13;
        Object $$12;
        Object2ByteLinkedOpenHashMap<BlockStatePairKey> $$9;
        VoxelShape $$6 = $$5.getCollisionShape($$1, $$4);
        if ($$6 == Shapes.block()) {
            return false;
        }
        VoxelShape $$7 = $$3.getCollisionShape($$1, $$2);
        if ($$7 == Shapes.block()) {
            return false;
        }
        if ($$7 == Shapes.empty() && $$6 == Shapes.empty()) {
            return true;
        }
        if ($$3.getBlock().hasDynamicShape() || $$5.getBlock().hasDynamicShape()) {
            Object $$8 = null;
        } else {
            $$9 = OCCLUSION_CACHE.get();
        }
        if ($$9 != null) {
            BlockStatePairKey $$10 = new BlockStatePairKey($$3, $$5, $$0);
            byte $$11 = $$9.getAndMoveToFirst((Object)$$10);
            if ($$11 != 127) {
                return $$11 != 0;
            }
        } else {
            $$12 = null;
        }
        boolean bl = $$13 = !Shapes.mergedFaceOccludes($$7, $$6, $$0);
        if ($$9 != null) {
            if ($$9.size() == 200) {
                $$9.removeLastByte();
            }
            $$9.putAndMoveToFirst($$12, (byte)($$13 ? 1 : 0));
        }
        return $$13;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int $$0, boolean $$1) {
        return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, $$0)).setValue(FALLING, $$1);
    }

    public abstract Fluid getSource();

    public FluidState getSource(boolean $$0) {
        return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, $$0);
    }

    protected abstract boolean canConvertToSource(ServerLevel var1);

    protected void spreadTo(LevelAccessor $$0, BlockPos $$1, BlockState $$2, Direction $$3, FluidState $$4) {
        Block block = $$2.getBlock();
        if (block instanceof LiquidBlockContainer) {
            LiquidBlockContainer $$5 = (LiquidBlockContainer)((Object)block);
            $$5.placeLiquid($$0, $$1, $$2, $$4);
        } else {
            if (!$$2.isAir()) {
                this.beforeDestroyingBlock($$0, $$1, $$2);
            }
            $$0.setBlock($$1, $$4.createLegacyBlock(), 3);
        }
    }

    protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

    protected int getSlopeDistance(LevelReader $$0, BlockPos $$1, int $$2, Direction $$3, BlockState $$4, SpreadContext $$5) {
        int $$6 = 1000;
        for (Direction $$7 : Direction.Plane.HORIZONTAL) {
            int $$11;
            if ($$7 == $$3) continue;
            BlockPos $$8 = $$1.relative($$7);
            BlockState $$9 = $$5.getBlockState($$8);
            FluidState $$10 = $$9.getFluidState();
            if (!this.canPassThrough($$0, this.getFlowing(), $$1, $$4, $$7, $$8, $$9, $$10)) continue;
            if ($$5.isHole($$8)) {
                return $$2;
            }
            if ($$2 >= this.getSlopeFindDistance($$0) || ($$11 = this.getSlopeDistance($$0, $$8, $$2 + 1, $$7.getOpposite(), $$9, $$5)) >= $$6) continue;
            $$6 = $$11;
        }
        return $$6;
    }

    boolean isWaterHole(BlockGetter $$0, BlockPos $$1, BlockState $$2, BlockPos $$3, BlockState $$4) {
        if (!FlowingFluid.canPassThroughWall(Direction.DOWN, $$0, $$1, $$2, $$3, $$4)) {
            return false;
        }
        if ($$4.getFluidState().getType().isSame(this)) {
            return true;
        }
        return FlowingFluid.canHoldFluid($$0, $$3, $$4, this.getFlowing());
    }

    private boolean canPassThrough(BlockGetter $$0, Fluid $$1, BlockPos $$2, BlockState $$3, Direction $$4, BlockPos $$5, BlockState $$6, FluidState $$7) {
        return this.canMaybePassThrough($$0, $$2, $$3, $$4, $$5, $$6, $$7) && FlowingFluid.canHoldSpecificFluid($$0, $$5, $$6, $$1);
    }

    private boolean canMaybePassThrough(BlockGetter $$0, BlockPos $$1, BlockState $$2, Direction $$3, BlockPos $$4, BlockState $$5, FluidState $$6) {
        return !this.isSourceBlockOfThisType($$6) && FlowingFluid.canHoldAnyFluid($$5) && FlowingFluid.canPassThroughWall($$3, $$0, $$1, $$2, $$4, $$5);
    }

    private boolean isSourceBlockOfThisType(FluidState $$0) {
        return $$0.getType().isSame(this) && $$0.isSource();
    }

    protected abstract int getSlopeFindDistance(LevelReader var1);

    private int sourceNeighborCount(LevelReader $$0, BlockPos $$1) {
        int $$2 = 0;
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            BlockPos $$4 = $$1.relative($$3);
            FluidState $$5 = $$0.getFluidState($$4);
            if (!this.isSourceBlockOfThisType($$5)) continue;
            ++$$2;
        }
        return $$2;
    }

    protected Map<Direction, FluidState> getSpread(ServerLevel $$0, BlockPos $$1, BlockState $$2) {
        int $$3 = 1000;
        EnumMap<Direction, FluidState> $$4 = Maps.newEnumMap(Direction.class);
        SpreadContext $$5 = null;
        for (Direction $$6 : Direction.Plane.HORIZONTAL) {
            int $$12;
            FluidState $$10;
            FluidState $$9;
            BlockState $$8;
            BlockPos $$7;
            if (!this.canMaybePassThrough($$0, $$1, $$2, $$6, $$7 = $$1.relative($$6), $$8 = $$0.getBlockState($$7), $$9 = $$8.getFluidState()) || !FlowingFluid.canHoldSpecificFluid($$0, $$7, $$8, ($$10 = this.getNewLiquid($$0, $$7, $$8)).getType())) continue;
            if ($$5 == null) {
                $$5 = new SpreadContext($$0, $$1);
            }
            if ($$5.isHole($$7)) {
                boolean $$11 = false;
            } else {
                $$12 = this.getSlopeDistance($$0, $$7, 1, $$6.getOpposite(), $$8, $$5);
            }
            if ($$12 < $$3) {
                $$4.clear();
            }
            if ($$12 > $$3) continue;
            if ($$9.canBeReplacedWith($$0, $$7, $$10.getType(), $$6)) {
                $$4.put($$6, $$10);
            }
            $$3 = $$12;
        }
        return $$4;
    }

    private static boolean canHoldAnyFluid(BlockState $$0) {
        Block $$1 = $$0.getBlock();
        if ($$1 instanceof LiquidBlockContainer) {
            return true;
        }
        if ($$0.blocksMotion()) {
            return false;
        }
        return !($$1 instanceof DoorBlock) && !$$0.is(BlockTags.SIGNS) && !$$0.is(Blocks.LADDER) && !$$0.is(Blocks.SUGAR_CANE) && !$$0.is(Blocks.BUBBLE_COLUMN) && !$$0.is(Blocks.NETHER_PORTAL) && !$$0.is(Blocks.END_PORTAL) && !$$0.is(Blocks.END_GATEWAY) && !$$0.is(Blocks.STRUCTURE_VOID);
    }

    private static boolean canHoldFluid(BlockGetter $$0, BlockPos $$1, BlockState $$2, Fluid $$3) {
        return FlowingFluid.canHoldAnyFluid($$2) && FlowingFluid.canHoldSpecificFluid($$0, $$1, $$2, $$3);
    }

    private static boolean canHoldSpecificFluid(BlockGetter $$0, BlockPos $$1, BlockState $$2, Fluid $$3) {
        Block $$4 = $$2.getBlock();
        if ($$4 instanceof LiquidBlockContainer) {
            LiquidBlockContainer $$5 = (LiquidBlockContainer)((Object)$$4);
            return $$5.canPlaceLiquid(null, $$0, $$1, $$2, $$3);
        }
        return true;
    }

    protected abstract int getDropOff(LevelReader var1);

    protected int getSpreadDelay(Level $$0, BlockPos $$1, FluidState $$2, FluidState $$3) {
        return this.getTickDelay($$0);
    }

    @Override
    public void tick(ServerLevel $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if (!$$3.isSource()) {
            FluidState $$4 = this.getNewLiquid($$0, $$1, $$0.getBlockState($$1));
            int $$5 = this.getSpreadDelay($$0, $$1, $$3, $$4);
            if ($$4.isEmpty()) {
                $$3 = $$4;
                $$2 = Blocks.AIR.defaultBlockState();
                $$0.setBlock($$1, $$2, 3);
            } else if ($$4 != $$3) {
                $$3 = $$4;
                $$2 = $$3.createLegacyBlock();
                $$0.setBlock($$1, $$2, 3);
                $$0.scheduleTick($$1, $$3.getType(), $$5);
            }
        }
        this.spread($$0, $$1, $$2, $$3);
    }

    protected static int getLegacyLevel(FluidState $$0) {
        if ($$0.isSource()) {
            return 0;
        }
        return 8 - Math.min($$0.getAmount(), 8) + ($$0.getValue(FALLING) != false ? 8 : 0);
    }

    private static boolean hasSameAbove(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getType().isSame($$1.getFluidState($$2.above()).getType());
    }

    @Override
    public float getHeight(FluidState $$0, BlockGetter $$1, BlockPos $$2) {
        if (FlowingFluid.hasSameAbove($$0, $$1, $$2)) {
            return 1.0f;
        }
        return $$0.getOwnHeight();
    }

    @Override
    public float getOwnHeight(FluidState $$0) {
        return (float)$$0.getAmount() / 9.0f;
    }

    @Override
    public abstract int getAmount(FluidState var1);

    @Override
    public VoxelShape getShape(FluidState $$0, BlockGetter $$1, BlockPos $$22) {
        if ($$0.getAmount() == 9 && FlowingFluid.hasSameAbove($$0, $$1, $$22)) {
            return Shapes.block();
        }
        return this.shapes.computeIfAbsent($$0, $$2 -> Shapes.box(0.0, 0.0, 0.0, 1.0, $$2.getHeight($$1, $$22), 1.0));
    }

    record BlockStatePairKey(BlockState first, BlockState second, Direction direction) {
        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object $$0) {
            if (!($$0 instanceof BlockStatePairKey)) return false;
            BlockStatePairKey $$1 = (BlockStatePairKey)((Object)$$0);
            if (this.first != $$1.first) return false;
            if (this.second != $$1.second) return false;
            if (this.direction != $$1.direction) return false;
            return true;
        }

        public int hashCode() {
            int $$0 = System.identityHashCode(this.first);
            $$0 = 31 * $$0 + System.identityHashCode(this.second);
            $$0 = 31 * $$0 + this.direction.hashCode();
            return $$0;
        }
    }

    protected class SpreadContext {
        private final BlockGetter level;
        private final BlockPos origin;
        private final Short2ObjectMap<BlockState> stateCache = new Short2ObjectOpenHashMap();
        private final Short2BooleanMap holeCache = new Short2BooleanOpenHashMap();

        SpreadContext(BlockGetter $$1, BlockPos $$2) {
            this.level = $$1;
            this.origin = $$2;
        }

        public BlockState getBlockState(BlockPos $$0) {
            return this.getBlockState($$0, this.getCacheKey($$0));
        }

        private BlockState getBlockState(BlockPos $$0, short $$12) {
            return (BlockState)this.stateCache.computeIfAbsent($$12, $$1 -> this.level.getBlockState($$0));
        }

        public boolean isHole(BlockPos $$0) {
            return this.holeCache.computeIfAbsent(this.getCacheKey($$0), $$1 -> {
                BlockState $$2 = this.getBlockState($$0, $$1);
                BlockPos $$3 = $$0.below();
                BlockState $$4 = this.level.getBlockState($$3);
                return FlowingFluid.this.isWaterHole(this.level, $$0, $$2, $$3, $$4);
            });
        }

        private short getCacheKey(BlockPos $$0) {
            int $$1 = $$0.getX() - this.origin.getX();
            int $$2 = $$0.getZ() - this.origin.getZ();
            return (short)(($$1 + 128 & 0xFF) << 8 | $$2 + 128 & 0xFF);
        }
    }
}

