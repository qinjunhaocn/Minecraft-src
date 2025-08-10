/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingHangingSignBlock
extends SignBlock {
    public static final MapCodec<CeilingHangingSignBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), CeilingHangingSignBlock.propertiesCodec()).apply((Applicative)$$0, CeilingHangingSignBlock::new));
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    private static final VoxelShape SHAPE_DEFAULT = Block.column(10.0, 0.0, 16.0);
    private static final Map<Integer, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.column(14.0, 2.0, 0.0, 10.0)).entrySet().stream().collect(Collectors.toMap($$0 -> RotationSegment.convertToSegment((Direction)$$0.getKey()), Map.Entry::getValue));

    public MapCodec<CeilingHangingSignBlock> codec() {
        return CODEC;
    }

    public CeilingHangingSignBlock(WoodType $$0, BlockBehaviour.Properties $$1) {
        super($$0, $$1.sound($$0.hangingSignSoundType()));
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(ATTACHED, false)).setValue(WATERLOGGED, false));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        SignBlockEntity $$7;
        BlockEntity blockEntity = $$2.getBlockEntity($$3);
        if (blockEntity instanceof SignBlockEntity && this.shouldTryToChainAnotherHangingSign($$4, $$6, $$7 = (SignBlockEntity)blockEntity, $$0)) {
            return InteractionResult.PASS;
        }
        return super.useItemOn($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    private boolean shouldTryToChainAnotherHangingSign(Player $$0, BlockHitResult $$1, SignBlockEntity $$2, ItemStack $$3) {
        return !$$2.canExecuteClickCommands($$2.isFacingFrontText($$0), $$0) && $$3.getItem() instanceof HangingSignItem && $$1.getDirection().equals(Direction.DOWN);
    }

    @Override
    protected boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState($$2.above()).isFaceSturdy($$1, $$2.above(), Direction.DOWN, SupportType.CENTER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        boolean $$7;
        Level $$1 = $$0.getLevel();
        FluidState $$2 = $$1.getFluidState($$0.getClickedPos());
        BlockPos $$3 = $$0.getClickedPos().above();
        BlockState $$4 = $$1.getBlockState($$3);
        boolean $$5 = $$4.is(BlockTags.ALL_HANGING_SIGNS);
        Direction $$6 = Direction.fromYRot($$0.getRotation());
        boolean bl = $$7 = !Block.isFaceFull($$4.getCollisionShape($$1, $$3), Direction.DOWN) || $$0.isSecondaryUseActive();
        if ($$5 && !$$0.isSecondaryUseActive()) {
            Optional<Direction> $$9;
            if ($$4.hasProperty(WallHangingSignBlock.FACING)) {
                Direction $$8 = $$4.getValue(WallHangingSignBlock.FACING);
                if ($$8.getAxis().test($$6)) {
                    $$7 = false;
                }
            } else if ($$4.hasProperty(ROTATION) && ($$9 = RotationSegment.convertToDirection($$4.getValue(ROTATION))).isPresent() && $$9.get().getAxis().test($$6)) {
                $$7 = false;
            }
        }
        int $$10 = !$$7 ? RotationSegment.convertToSegment($$6.getOpposite()) : RotationSegment.convertToSegment($$0.getRotation() + 180.0f);
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, $$7)).setValue(ROTATION, $$10)).setValue(WATERLOGGED, $$2.getType() == Fluids.WATER);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.getOrDefault($$0.getValue(ROTATION), SHAPE_DEFAULT);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.getShape($$0, $$1, $$2, CollisionContext.empty());
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$4 == Direction.UP && !this.canSurvive($$0, $$1, $$3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public float getYRotationDegrees(BlockState $$0) {
        return RotationSegment.convertToDegrees($$0.getValue(ROTATION));
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.rotate($$0.getValue(ROTATION), 16));
    }

    @Override
    protected BlockState mirror(BlockState $$0, Mirror $$1) {
        return (BlockState)$$0.setValue(ROTATION, $$1.mirror($$0.getValue(ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(ROTATION, ATTACHED, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new HangingSignBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return CeilingHangingSignBlock.createTickerHelper($$2, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
    }
}

