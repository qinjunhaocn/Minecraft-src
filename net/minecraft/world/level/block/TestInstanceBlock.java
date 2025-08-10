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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TestInstanceBlock
extends BaseEntityBlock
implements GameMasterBlock {
    public static final MapCodec<TestInstanceBlock> CODEC = TestInstanceBlock.simpleCodec(TestInstanceBlock::new);

    public TestInstanceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TestInstanceBlockEntity($$0, $$1);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, BlockHitResult $$4) {
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if (!($$5 instanceof TestInstanceBlockEntity)) {
            return InteractionResult.PASS;
        }
        TestInstanceBlockEntity $$6 = (TestInstanceBlockEntity)$$5;
        if (!$$3.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if ($$3.level().isClientSide) {
            void $$7;
            $$3.openTestInstanceBlock((TestInstanceBlockEntity)$$7);
        }
        return InteractionResult.SUCCESS;
    }

    protected MapCodec<TestInstanceBlock> codec() {
        return CODEC;
    }
}

