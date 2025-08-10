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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

public class CopperBulbBlock
extends Block {
    public static final MapCodec<CopperBulbBlock> CODEC = CopperBulbBlock.simpleCodec(CopperBulbBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected MapCodec<? extends CopperBulbBlock> codec() {
        return CODEC;
    }

    public CopperBulbBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(LIT, false)).setValue(POWERED, false));
    }

    @Override
    protected void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$3.getBlock() != $$0.getBlock() && $$1 instanceof ServerLevel) {
            ServerLevel $$5 = (ServerLevel)$$1;
            this.checkAndFlip($$0, $$5, $$2);
        }
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$6 = (ServerLevel)$$1;
            this.checkAndFlip($$0, $$6, $$2);
        }
    }

    public void checkAndFlip(BlockState $$0, ServerLevel $$1, BlockPos $$2) {
        boolean $$3 = $$1.hasNeighborSignal($$2);
        if ($$3 == $$0.getValue(POWERED)) {
            return;
        }
        BlockState $$4 = $$0;
        if (!$$0.getValue(POWERED).booleanValue()) {
            $$1.playSound(null, $$2, ($$4 = (BlockState)$$4.cycle(LIT)).getValue(LIT) != false ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }
        $$1.setBlock($$2, (BlockState)$$4.setValue(POWERED, $$3), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(LIT, POWERED);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$1.getBlockState($$2).getValue(LIT) != false ? 15 : 0;
    }
}

