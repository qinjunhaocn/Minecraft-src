/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TintedGlassBlock
extends TransparentBlock {
    public static final MapCodec<TintedGlassBlock> CODEC = TintedGlassBlock.simpleCodec(TintedGlassBlock::new);

    public MapCodec<TintedGlassBlock> codec() {
        return CODEC;
    }

    public TintedGlassBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState $$0) {
        return false;
    }

    @Override
    protected int getLightBlock(BlockState $$0) {
        return 15;
    }
}

