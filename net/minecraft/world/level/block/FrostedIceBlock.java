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
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;

public class FrostedIceBlock
extends IceBlock {
    public static final MapCodec<FrostedIceBlock> CODEC = FrostedIceBlock.simpleCodec(FrostedIceBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final int NEIGHBORS_TO_AGE = 4;
    private static final int NEIGHBORS_TO_MELT = 2;

    public MapCodec<FrostedIceBlock> codec() {
        return CODEC;
    }

    public FrostedIceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, Mth.nextInt($$1.getRandom(), 60, 120));
    }

    @Override
    protected void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (($$3.nextInt(3) == 0 || this.fewerNeigboursThan($$1, $$2, 4)) && $$1.getMaxLocalRawBrightness($$2) > 11 - $$0.getValue(AGE) - $$0.getLightBlock() && this.slightlyMelt($$0, $$1, $$2)) {
            BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
            for (Direction $$5 : Direction.values()) {
                $$4.setWithOffset((Vec3i)$$2, $$5);
                BlockState $$6 = $$1.getBlockState($$4);
                if (!$$6.is(this) || this.slightlyMelt($$6, $$1, $$4)) continue;
                $$1.scheduleTick((BlockPos)$$4, this, Mth.nextInt($$3, 20, 40));
            }
            return;
        }
        $$1.scheduleTick($$2, this, Mth.nextInt($$3, 20, 40));
    }

    private boolean slightlyMelt(BlockState $$0, Level $$1, BlockPos $$2) {
        int $$3 = $$0.getValue(AGE);
        if ($$3 < 3) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(AGE, $$3 + 1), 2);
            return false;
        }
        this.melt($$0, $$1, $$2);
        return true;
    }

    @Override
    protected void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, @Nullable Orientation $$4, boolean $$5) {
        if ($$3.defaultBlockState().is(this) && this.fewerNeigboursThan($$1, $$2, 2)) {
            this.melt($$0, $$1, $$2);
        }
        super.neighborChanged($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private boolean fewerNeigboursThan(BlockGetter $$0, BlockPos $$1, int $$2) {
        int $$3 = 0;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : Direction.values()) {
            $$4.setWithOffset((Vec3i)$$1, $$5);
            if (!$$0.getBlockState($$4).is(this) || ++$$3 < $$2) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AGE);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return ItemStack.EMPTY;
    }
}

