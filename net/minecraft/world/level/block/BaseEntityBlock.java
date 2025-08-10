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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEntityBlock
extends Block
implements EntityBlock {
    protected BaseEntityBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract MapCodec<? extends BaseEntityBlock> codec();

    @Override
    protected boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        super.triggerEvent($$0, $$1, $$2, $$3, $$4);
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 == null) {
            return false;
        }
        return $$5.triggerEvent($$3, $$4);
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        return $$3 instanceof MenuProvider ? (MenuProvider)((Object)$$3) : null;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> $$0, BlockEntityType<E> $$1, BlockEntityTicker<? super E> $$2) {
        return $$1 == $$0 ? $$2 : null;
    }
}

