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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;

public class RedstoneLampBlock
extends Block {
    public static final MapCodec<RedstoneLampBlock> CODEC = RedstoneLampBlock.simpleCodec(RedstoneLampBlock::new);
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public MapCodec<RedstoneLampBlock> codec() {
        return CODEC;
    }

    public RedstoneLampBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(LIT, $$0.getLevel().hasNeighborSignal($$0.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$1.isClientSide) {
            return;
        }
        boolean $$6 = $$0.getValue(LIT);
        if ($$6 != $$1.hasNeighborSignal($$2)) {
            if ($$6) {
                $$1.scheduleTick($$2, this, 4);
            } else {
                $$1.setBlock($$2, (BlockState)$$0.cycle(LIT), 2);
            }
        }
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(LIT).booleanValue() && !$$1.hasNeighborSignal($$2)) {
            $$1.setBlock($$2, (BlockState)$$0.cycle(LIT), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(LIT);
    }
}

