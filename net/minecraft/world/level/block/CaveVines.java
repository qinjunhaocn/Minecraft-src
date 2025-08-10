/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CaveVines {
    public static final VoxelShape SHAPE = Block.column(14.0, 0.0, 16.0);
    public static final BooleanProperty BERRIES = BlockStateProperties.BERRIES;

    public static InteractionResult use(@Nullable Entity $$0, BlockState $$1, Level $$2, BlockPos $$3) {
        if ($$1.getValue(BERRIES).booleanValue()) {
            Block.popResource($$2, $$3, new ItemStack(Items.GLOW_BERRIES, 1));
            float $$4 = Mth.randomBetween($$2.random, 0.8f, 1.2f);
            $$2.playSound(null, $$3, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, $$4);
            BlockState $$5 = (BlockState)$$1.setValue(BERRIES, false);
            $$2.setBlock($$3, $$5, 2);
            $$2.gameEvent(GameEvent.BLOCK_CHANGE, $$3, GameEvent.Context.of($$0, $$5));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static boolean hasGlowBerries(BlockState $$0) {
        return $$0.hasProperty(BERRIES) && $$0.getValue(BERRIES) != false;
    }

    public static ToIntFunction<BlockState> emission(int $$0) {
        return $$1 -> $$1.getValue(BlockStateProperties.BERRIES) != false ? $$0 : 0;
    }
}

