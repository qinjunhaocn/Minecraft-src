/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PitcherCropBlock
extends DoublePlantBlock
implements BonemealableBlock {
    public static final MapCodec<PitcherCropBlock> CODEC = PitcherCropBlock.simpleCodec(PitcherCropBlock::new);
    public static final int MAX_AGE = 4;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
    private static final int DOUBLE_PLANT_AGE_INTERSECTION = 3;
    private static final int BONEMEAL_INCREASE = 1;
    private static final VoxelShape SHAPE_BULB = Block.column(6.0, -1.0, 3.0);
    private static final VoxelShape SHAPE_CROP = Block.column(10.0, -1.0, 5.0);
    private final Function<BlockState, VoxelShape> shapes = this.makeShapes();

    public MapCodec<PitcherCropBlock> codec() {
        return CODEC;
    }

    public PitcherCropBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    private Function<BlockState, VoxelShape> makeShapes() {
        int[] $$0 = new int[]{0, 9, 11, 22, 26};
        return this.getShapeForEachState($$1 -> {
            int $$2 = ($$1.getValue(AGE) == 0 ? 4 : 6) + $$0[$$1.getValue(AGE)];
            int $$3 = $$1.getValue(AGE) == 0 ? 6 : 10;
            return switch ($$1.getValue(HALF)) {
                default -> throw new MatchException(null, null);
                case DoubleBlockHalf.LOWER -> Block.column($$3, -1.0, Math.min(16, -1 + $$2));
                case DoubleBlockHalf.UPPER -> Block.column($$3, 0.0, Math.max(0, -1 + $$2 - 16));
            };
        });
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapes.apply($$0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return $$0.getValue(AGE) == 0 ? SHAPE_BULB : SHAPE_CROP;
        }
        return Shapes.empty();
    }

    @Override
    public BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if (PitcherCropBlock.isDouble($$0.getValue(AGE))) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        return $$0.canSurvive($$1, $$3) ? $$0 : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        if (PitcherCropBlock.isLower($$0) && !PitcherCropBlock.sufficientLight($$1, $$2)) {
            return false;
        }
        return super.canSurvive($$0, $$1, $$2);
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(Blocks.FARMLAND);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE);
        super.createBlockStateDefinition($$0);
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$1;
            if ($$3 instanceof Ravager && $$5.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                $$5.destroyBlock($$2, true, $$3);
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return false;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(HALF) == DoubleBlockHalf.LOWER && !this.isMaxAge($$0);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        boolean $$5;
        float $$4 = CropBlock.getGrowthSpeed(this, $$1, $$2);
        boolean bl = $$5 = $$3.nextInt((int)(25.0f / $$4) + 1) == 0;
        if ($$5) {
            this.grow($$1, $$0, $$2, 1);
        }
    }

    private void grow(ServerLevel $$0, BlockState $$1, BlockPos $$2, int $$3) {
        int $$4 = Math.min($$1.getValue(AGE) + $$3, 4);
        if (!this.canGrow($$0, $$2, $$1, $$4)) {
            return;
        }
        BlockState $$5 = (BlockState)$$1.setValue(AGE, $$4);
        $$0.setBlock($$2, $$5, 2);
        if (PitcherCropBlock.isDouble($$4)) {
            $$0.setBlock($$2.above(), (BlockState)$$5.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        }
    }

    private static boolean canGrowInto(LevelReader $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return $$2.isAir() || $$2.is(Blocks.PITCHER_CROP);
    }

    private static boolean sufficientLight(LevelReader $$0, BlockPos $$1) {
        return CropBlock.hasSufficientLight($$0, $$1);
    }

    private static boolean isLower(BlockState $$0) {
        return $$0.is(Blocks.PITCHER_CROP) && $$0.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    private static boolean isDouble(int $$0) {
        return $$0 >= 3;
    }

    private boolean canGrow(LevelReader $$0, BlockPos $$1, BlockState $$2, int $$3) {
        return !this.isMaxAge($$2) && PitcherCropBlock.sufficientLight($$0, $$1) && (!PitcherCropBlock.isDouble($$3) || PitcherCropBlock.canGrowInto($$0, $$1.above()));
    }

    private boolean isMaxAge(BlockState $$0) {
        return $$0.getValue(AGE) >= 4;
    }

    @Nullable
    private PosAndState getLowerHalf(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        if (PitcherCropBlock.isLower($$2)) {
            return new PosAndState($$1, $$2);
        }
        BlockPos $$3 = $$1.below();
        BlockState $$4 = $$0.getBlockState($$3);
        if (PitcherCropBlock.isLower($$4)) {
            return new PosAndState($$3, $$4);
        }
        return null;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2) {
        PosAndState $$3 = this.getLowerHalf($$0, $$1, $$2);
        if ($$3 == null) {
            return false;
        }
        return this.canGrow($$0, $$3.pos, $$3.state, $$3.state.getValue(AGE) + 1);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        PosAndState $$4 = this.getLowerHalf($$0, $$2, $$3);
        if ($$4 == null) {
            return;
        }
        this.grow($$0, $$4.state, $$4.pos, 1);
    }

    static final class PosAndState
    extends Record {
        final BlockPos pos;
        final BlockState state;

        PosAndState(BlockPos $$0, BlockState $$1) {
            this.pos = $$0;
            this.state = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PosAndState.class, "pos;state", "pos", "state"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PosAndState.class, "pos;state", "pos", "state"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PosAndState.class, "pos;state", "pos", "state"}, this, $$0);
        }

        public BlockPos pos() {
            return this.pos;
        }

        public BlockState state() {
            return this.state;
        }
    }
}

