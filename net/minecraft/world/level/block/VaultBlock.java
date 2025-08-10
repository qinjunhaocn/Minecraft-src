/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class VaultBlock
extends BaseEntityBlock {
    public static final MapCodec<VaultBlock> CODEC = VaultBlock.simpleCodec(VaultBlock::new);
    public static final Property<VaultState> STATE = BlockStateProperties.VAULT_STATE;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OMINOUS = BlockStateProperties.OMINOUS;

    public MapCodec<VaultBlock> codec() {
        return CODEC;
    }

    public VaultBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(STATE, VaultState.INACTIVE)).setValue(OMINOUS, false));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        if ($$0.isEmpty() || $$1.getValue(STATE) != VaultState.ACTIVE) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if ($$2 instanceof ServerLevel) {
            void $$9;
            ServerLevel $$7 = (ServerLevel)$$2;
            BlockEntity blockEntity = $$7.getBlockEntity($$3);
            if (!(blockEntity instanceof VaultBlockEntity)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            VaultBlockEntity $$8 = (VaultBlockEntity)blockEntity;
            VaultBlockEntity.Server.tryInsertKey($$7, $$3, $$1, $$9.getConfig(), $$9.getServerData(), $$9.getSharedData(), $$4, $$0);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new VaultBlockEntity($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(FACING, STATE, OMINOUS);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$02, BlockState $$12, BlockEntityType<T> $$22) {
        BlockEntityTicker<T> blockEntityTicker;
        if ($$02 instanceof ServerLevel) {
            ServerLevel $$32 = (ServerLevel)$$02;
            blockEntityTicker = VaultBlock.createTickerHelper($$22, BlockEntityType.VAULT, ($$1, $$2, $$3, $$4) -> VaultBlockEntity.Server.tick($$32, $$2, $$3, $$4.getConfig(), $$4.getServerData(), $$4.getSharedData()));
        } else {
            blockEntityTicker = VaultBlock.createTickerHelper($$22, BlockEntityType.VAULT, ($$0, $$1, $$2, $$3) -> VaultBlockEntity.Client.tick($$0, $$1, $$2, $$3.getClientData(), $$3.getSharedData()));
        }
        return blockEntityTicker;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }
}

