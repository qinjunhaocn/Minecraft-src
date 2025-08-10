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
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class TestBlock
extends BaseEntityBlock
implements GameMasterBlock {
    public static final MapCodec<TestBlock> CODEC = TestBlock.simpleCodec(TestBlock::new);
    public static final EnumProperty<TestBlockMode> MODE = BlockStateProperties.TEST_BLOCK_MODE;

    public TestBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TestBlockEntity($$0, $$1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        TestBlockMode $$3;
        BlockItemStateProperties $$1 = $$0.getItemInHand().get(DataComponents.BLOCK_STATE);
        BlockState $$2 = this.defaultBlockState();
        if ($$1 != null && ($$3 = $$1.get(MODE)) != null) {
            $$2 = (BlockState)$$2.setValue(MODE, $$3);
        }
        return $$2;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(MODE);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if (!($$5 instanceof TestBlockEntity)) {
            return InteractionResult.PASS;
        }
        TestBlockEntity $$6 = (TestBlockEntity)$$5;
        if (!$$3.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if ($$1.isClientSide) {
            void $$7;
            $$3.openTestBlock((TestBlockEntity)$$7);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        TestBlockEntity $$4 = TestBlock.getServerTestBlockEntity($$1, $$2);
        if ($$4 == null) {
            return;
        }
        $$4.reset();
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        TestBlockEntity $$6 = TestBlock.getServerTestBlockEntity($$1, $$2);
        if ($$6 == null) {
            return;
        }
        if ($$6.getMode() == TestBlockMode.START) {
            return;
        }
        boolean $$7 = $$1.hasNeighborSignal($$2);
        boolean $$8 = $$6.isPowered();
        if ($$7 && !$$8) {
            $$6.setPowered(true);
            $$6.trigger();
        } else if (!$$7 && $$8) {
            $$6.setPowered(false);
        }
    }

    @Nullable
    private static TestBlockEntity getServerTestBlockEntity(Level $$0, BlockPos $$1) {
        ServerLevel $$2;
        BlockEntity blockEntity;
        if ($$0 instanceof ServerLevel && (blockEntity = ($$2 = (ServerLevel)$$0).getBlockEntity($$1)) instanceof TestBlockEntity) {
            TestBlockEntity $$3 = (TestBlockEntity)blockEntity;
            return $$3;
        }
        return null;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(MODE) != TestBlockMode.START) {
            return 0;
        }
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if ($$4 instanceof TestBlockEntity) {
            TestBlockEntity $$5 = (TestBlockEntity)$$4;
            return $$5.isPowered() ? 15 : 0;
        }
        return 0;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        ItemStack $$4 = super.getCloneItemStack($$0, $$1, $$2, $$3);
        return TestBlock.setModeOnStack($$4, $$2.getValue(MODE));
    }

    public static ItemStack setModeOnStack(ItemStack $$0, TestBlockMode $$1) {
        $$0.set(DataComponents.BLOCK_STATE, $$0.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).with(MODE, $$1));
        return $$0;
    }

    protected MapCodec<TestBlock> codec() {
        return CODEC;
    }
}

