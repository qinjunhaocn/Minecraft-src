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
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock
extends BaseEntityBlock {
    public static final MapCodec<JukeboxBlock> CODEC = JukeboxBlock.simpleCodec(JukeboxBlock::new);
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

    public MapCodec<JukeboxBlock> codec() {
        return CODEC;
    }

    protected JukeboxBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, false));
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        CustomData $$5 = $$4.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if ($$5.contains("RecordItem")) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(HAS_RECORD, true), 2);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity blockEntity;
        if ($$0.getValue(HAS_RECORD).booleanValue() && (blockEntity = $$1.getBlockEntity($$2)) instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity $$5 = (JukeboxBlockEntity)blockEntity;
            $$5.popOutTheItem();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        if ($$1.getValue(HAS_RECORD).booleanValue()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ItemStack $$7 = $$4.getItemInHand($$5);
        InteractionResult $$8 = JukeboxPlayable.tryInsertIntoJukebox($$2, $$3, $$7, $$4);
        if (!$$8.consumesAction()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return $$8;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState $$0, ServerLevel $$1, BlockPos $$2, boolean $$3) {
        Containers.updateNeighboursAfterDestroy($$0, $$1, $$2);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new JukeboxBlockEntity($$0, $$1);
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        JukeboxBlockEntity $$4;
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof JukeboxBlockEntity && ($$4 = (JukeboxBlockEntity)blockEntity).getSongPlayer().isPlaying()) {
            return 15;
        }
        return 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity $$3 = (JukeboxBlockEntity)blockEntity;
            return $$3.getComparatorOutput();
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(HAS_RECORD);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        if ($$1.getValue(HAS_RECORD).booleanValue()) {
            return JukeboxBlock.createTickerHelper($$2, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick);
        }
        return null;
    }
}

