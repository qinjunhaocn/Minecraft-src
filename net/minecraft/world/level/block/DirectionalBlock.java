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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class DirectionalBlock
extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    protected DirectionalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract MapCodec<? extends DirectionalBlock> codec();
}

