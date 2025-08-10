/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class ChiseledBookShelfBlock
extends BaseEntityBlock {
    public static final MapCodec<ChiseledBookShelfBlock> CODEC = ChiseledBookShelfBlock.simpleCodec(ChiseledBookShelfBlock::new);
    private static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);

    public MapCodec<ChiseledBookShelfBlock> codec() {
        return CODEC;
    }

    public ChiseledBookShelfBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        BlockState $$1 = (BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);
        for (BooleanProperty $$2 : SLOT_OCCUPIED_PROPERTIES) {
            $$1 = (BlockState)$$1.setValue($$2, false);
        }
        this.registerDefaultState($$1);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        void $$8;
        BlockEntity blockEntity = $$2.getBlockEntity($$3);
        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity)) {
            return InteractionResult.PASS;
        }
        ChiseledBookShelfBlockEntity $$7 = (ChiseledBookShelfBlockEntity)blockEntity;
        if (!$$0.is(ItemTags.BOOKSHELF_BOOKS)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        OptionalInt $$9 = this.getHitSlot($$6, $$1);
        if ($$9.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (((Boolean)$$1.getValue(SLOT_OCCUPIED_PROPERTIES.get($$9.getAsInt()))).booleanValue()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ChiseledBookShelfBlock.addBook($$2, $$3, $$4, (ChiseledBookShelfBlockEntity)$$8, $$0, $$9.getAsInt());
        return InteractionResult.SUCCESS;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        void $$6;
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity)) {
            return InteractionResult.PASS;
        }
        ChiseledBookShelfBlockEntity $$5 = (ChiseledBookShelfBlockEntity)blockEntity;
        OptionalInt $$7 = this.getHitSlot($$4, $$0);
        if ($$7.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!((Boolean)$$0.getValue(SLOT_OCCUPIED_PROPERTIES.get($$7.getAsInt()))).booleanValue()) {
            return InteractionResult.CONSUME;
        }
        ChiseledBookShelfBlock.removeBook($$1, $$2, $$3, (ChiseledBookShelfBlockEntity)$$6, $$7.getAsInt());
        return InteractionResult.SUCCESS;
    }

    private OptionalInt getHitSlot(BlockHitResult $$02, BlockState $$1) {
        return ChiseledBookShelfBlock.getRelativeHitCoordinatesForBlockFace($$02, $$1.getValue(HorizontalDirectionalBlock.FACING)).map($$0 -> {
            int $$1 = $$0.y >= 0.5f ? 0 : 1;
            int $$2 = ChiseledBookShelfBlock.getSection($$0.x);
            return OptionalInt.of($$2 + $$1 * 3);
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult $$0, Direction $$1) {
        Direction $$2 = $$0.getDirection();
        if ($$1 != $$2) {
            return Optional.empty();
        }
        BlockPos $$3 = $$0.getBlockPos().relative($$2);
        Vec3 $$4 = $$0.getLocation().subtract($$3.getX(), $$3.getY(), $$3.getZ());
        double $$5 = $$4.x();
        double $$6 = $$4.y();
        double $$7 = $$4.z();
        return switch ($$2) {
            default -> throw new MatchException(null, null);
            case Direction.NORTH -> Optional.of(new Vec2((float)(1.0 - $$5), (float)$$6));
            case Direction.SOUTH -> Optional.of(new Vec2((float)$$5, (float)$$6));
            case Direction.WEST -> Optional.of(new Vec2((float)$$7, (float)$$6));
            case Direction.EAST -> Optional.of(new Vec2((float)(1.0 - $$7), (float)$$6));
            case Direction.DOWN, Direction.UP -> Optional.empty();
        };
    }

    private static int getSection(float $$0) {
        float $$1 = 0.0625f;
        float $$2 = 0.375f;
        if ($$0 < 0.375f) {
            return 0;
        }
        float $$3 = 0.6875f;
        if ($$0 < 0.6875f) {
            return 1;
        }
        return 2;
    }

    private static void addBook(Level $$0, BlockPos $$1, Player $$2, ChiseledBookShelfBlockEntity $$3, ItemStack $$4, int $$5) {
        if ($$0.isClientSide) {
            return;
        }
        $$2.awardStat(Stats.ITEM_USED.get($$4.getItem()));
        SoundEvent $$6 = $$4.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
        $$3.setItem($$5, $$4.consumeAndReturn(1, $$2));
        $$0.playSound(null, $$1, $$6, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    private static void removeBook(Level $$0, BlockPos $$1, Player $$2, ChiseledBookShelfBlockEntity $$3, int $$4) {
        if ($$0.isClientSide) {
            return;
        }
        ItemStack $$5 = $$3.removeItem($$4, 1);
        SoundEvent $$6 = $$5.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
        $$0.playSound(null, $$1, $$6, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (!$$2.getInventory().add($$5)) {
            $$2.drop($$5, false);
        }
        $$0.gameEvent((Entity)$$2, GameEvent.BLOCK_CHANGE, $$1);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ChiseledBookShelfBlockEntity($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(HorizontalDirectionalBlock.FACING);
        SLOT_OCCUPIED_PROPERTIES.forEach($$1 -> $$0.a((Property<?>)$$1));
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(HorizontalDirectionalBlock.FACING, $$1.rotate($$0.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        if ($$1.isClientSide()) {
            return 0;
        }
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity $$3 = (ChiseledBookShelfBlockEntity)blockEntity;
            return $$3.getLastInteractedSlot() + 1;
        }
        return 0;
    }
}

