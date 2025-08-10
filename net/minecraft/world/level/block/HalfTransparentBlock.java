/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTransparentBlock
extends Block {
    public static final MapCodec<HalfTransparentBlock> CODEC = HalfTransparentBlock.simpleCodec(HalfTransparentBlock::new);

    protected MapCodec<? extends HalfTransparentBlock> codec() {
        return CODEC;
    }

    protected HalfTransparentBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        if ($$1.is(this)) {
            return true;
        }
        return super.skipRendering($$0, $$1, $$2);
    }
}

