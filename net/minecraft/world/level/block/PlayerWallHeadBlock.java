/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerWallHeadBlock
extends WallSkullBlock {
    public static final MapCodec<PlayerWallHeadBlock> CODEC = PlayerWallHeadBlock.simpleCodec(PlayerWallHeadBlock::new);

    public MapCodec<PlayerWallHeadBlock> codec() {
        return CODEC;
    }

    protected PlayerWallHeadBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.PLAYER, $$0);
    }
}

