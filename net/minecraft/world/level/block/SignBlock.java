/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.column(8.0, 0.0, 16.0);
    private final WoodType type;

    protected SignBlock(WoodType $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.type = $$0;
    }

    protected abstract MapCodec<? extends SignBlock> codec();

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$2.scheduleTick($$3, Fluids.WATER, Fluids.WATER.getTickDelay($$1));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState $$0) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new SignBlockEntity($$0, $$1);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useItemOn(ItemStack $$0, BlockState $$1, Level $$2, BlockPos $$3, Player $$4, InteractionHand $$5, BlockHitResult $$6) {
        void $$13;
        void $$8;
        boolean $$11;
        SignApplicator $$9;
        BlockEntity blockEntity = $$2.getBlockEntity($$3);
        if (!(blockEntity instanceof SignBlockEntity)) {
            return InteractionResult.PASS;
        }
        SignBlockEntity $$7 = (SignBlockEntity)blockEntity;
        Item item = $$0.getItem();
        SignApplicator $$10 = item instanceof SignApplicator ? ($$9 = (SignApplicator)((Object)item)) : null;
        boolean bl = $$11 = $$10 != null && $$4.mayBuild();
        if (!($$2 instanceof ServerLevel)) {
            return $$11 || $$8.isWaxed() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        ServerLevel $$12 = (ServerLevel)$$2;
        if (!$$11 || $$8.isWaxed() || this.otherPlayerIsEditingSign($$4, (SignBlockEntity)$$8)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        boolean $$14 = $$8.isFacingFrontText($$4);
        if ($$10.canApplyToSign($$8.getText($$14), $$4) && $$10.tryApplyToSign((Level)$$13, (SignBlockEntity)$$8, $$14, $$4)) {
            $$8.executeClickCommandsIfPresent((ServerLevel)$$13, $$4, $$3, $$14);
            $$4.awardStat(Stats.ITEM_USED.get($$0.getItem()));
            $$13.gameEvent(GameEvent.BLOCK_CHANGE, $$8.getBlockPos(), GameEvent.Context.of($$4, $$8.getBlockState()));
            $$0.consume(1, $$4);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        void $$8;
        void $$6;
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof SignBlockEntity)) {
            return InteractionResult.PASS;
        }
        SignBlockEntity $$5 = (SignBlockEntity)blockEntity;
        if (!($$1 instanceof ServerLevel)) {
            Util.pauseInIde(new IllegalStateException("Expected to only call this on server"));
            return InteractionResult.CONSUME;
        }
        ServerLevel $$7 = (ServerLevel)$$1;
        boolean $$9 = $$6.isFacingFrontText($$3);
        boolean $$10 = $$6.executeClickCommandsIfPresent((ServerLevel)$$8, $$3, $$2, $$9);
        if ($$6.isWaxed()) {
            $$8.playSound(null, $$6.getBlockPos(), $$6.getSignInteractionFailedSoundEvent(), SoundSource.BLOCKS);
            return InteractionResult.SUCCESS_SERVER;
        }
        if ($$10) {
            return InteractionResult.SUCCESS_SERVER;
        }
        if (!this.otherPlayerIsEditingSign($$3, (SignBlockEntity)$$6) && $$3.mayBuild() && this.hasEditableText($$3, (SignBlockEntity)$$6, $$9)) {
            this.openTextEdit($$3, (SignBlockEntity)$$6, $$9);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    private boolean hasEditableText(Player $$02, SignBlockEntity $$1, boolean $$2) {
        SignText $$3 = $$1.getText($$2);
        return Arrays.stream($$3.b($$02.isTextFilteringEnabled())).allMatch($$0 -> $$0.equals(CommonComponents.EMPTY) || $$0.getContents() instanceof PlainTextContents);
    }

    public abstract float getYRotationDegrees(BlockState var1);

    public Vec3 getSignHitboxCenterPosition(BlockState $$0) {
        return new Vec3(0.5, 0.5, 0.5);
    }

    @Override
    protected FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    public WoodType type() {
        return this.type;
    }

    public static WoodType getWoodType(Block $$0) {
        WoodType $$2;
        if ($$0 instanceof SignBlock) {
            WoodType $$1 = ((SignBlock)$$0).type();
        } else {
            $$2 = WoodType.OAK;
        }
        return $$2;
    }

    public void openTextEdit(Player $$0, SignBlockEntity $$1, boolean $$2) {
        $$1.setAllowedPlayerEditor($$0.getUUID());
        $$0.openTextEdit($$1, $$2);
    }

    private boolean otherPlayerIsEditingSign(Player $$0, SignBlockEntity $$1) {
        UUID $$2 = $$1.getPlayerWhoMayEdit();
        return $$2 != null && !$$2.equals($$0.getUUID());
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return SignBlock.createTickerHelper($$2, BlockEntityType.SIGN, SignBlockEntity::tick);
    }
}

