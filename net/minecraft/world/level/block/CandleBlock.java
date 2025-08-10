/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock
extends AbstractCandleBlock
implements SimpleWaterloggedBlock {
    public static final MapCodec<CandleBlock> CODEC = CandleBlock.simpleCodec(CandleBlock::new);
    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 4;
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = $$0 -> $$0.getValue(LIT) != false ? 3 * $$0.getValue(CANDLES) : 0;
    private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(4), $$0 -> {
        float $$1 = 0.0625f;
        $$0.put(1, (Object)List.of((Object)new Vec3(8.0, 8.0, 8.0).scale(0.0625)));
        $$0.put(2, (Object)List.of((Object)new Vec3(6.0, 7.0, 8.0).scale(0.0625), (Object)new Vec3(10.0, 8.0, 7.0).scale(0.0625)));
        $$0.put(3, (Object)List.of((Object)new Vec3(8.0, 5.0, 10.0).scale(0.0625), (Object)new Vec3(6.0, 7.0, 8.0).scale(0.0625), (Object)new Vec3(9.0, 8.0, 7.0).scale(0.0625)));
        $$0.put(4, (Object)List.of((Object)new Vec3(7.0, 5.0, 9.0).scale(0.0625), (Object)new Vec3(10.0, 7.0, 9.0).scale(0.0625), (Object)new Vec3(6.0, 7.0, 6.0).scale(0.0625), (Object)new Vec3(9.0, 8.0, 6.0).scale(0.0625)));
    });
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.column(2.0, 0.0, 6.0), Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0), Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0), Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0)};

    public MapCodec<CandleBlock> codec() {
        return CODEC;
    }

    public CandleBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        if ($$0.isEmpty() && $$4.getAbilities().mayBuild && $$1.getValue(LIT).booleanValue()) {
            CandleBlock.extinguish($$4, $$1, $$2, $$3);
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Override
    protected boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        if (!$$1.isSecondaryUseActive() && $$1.getItemInHand().getItem() == this.asItem() && $$0.getValue(CANDLES) < 4) {
            return true;
        }
        return super.canBeReplaced($$0, $$1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(this)) {
            return (BlockState)$$1.cycle(CANDLES);
        }
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$3 = $$2.getType() == Fluids.WATER;
        return (BlockState)super.getStateForPlacement($$0).setValue(WATERLOGGED, $$3);
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES[$$0.getValue(CANDLES) - 1];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(CANDLES, LIT, WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if ($$2.getValue(WATERLOGGED).booleanValue() || $$3.getType() != Fluids.WATER) {
            return false;
        }
        BlockState $$4 = (BlockState)$$2.setValue(WATERLOGGED, true);
        if ($$2.getValue(LIT).booleanValue()) {
            CandleBlock.extinguish(null, $$4, $$0, $$1);
        } else {
            $$0.setBlock($$1, $$4, 3);
        }
        $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
        return true;
    }

    public static boolean canLight(BlockState $$02) {
        return $$02.is(BlockTags.CANDLES, $$0 -> $$0.hasProperty(LIT) && $$0.hasProperty(WATERLOGGED)) && $$02.getValue(LIT) == false && $$02.getValue(WATERLOGGED) == false;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState $$0) {
        return (Iterable)PARTICLE_OFFSETS.get($$0.getValue(CANDLES).intValue());
    }

    @Override
    protected boolean canBeLit(BlockState $$0) {
        return $$0.getValue(WATERLOGGED) == false && super.canBeLit($$0);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return Block.canSupportCenter($$1, $$2.below(), Direction.UP);
    }
}

