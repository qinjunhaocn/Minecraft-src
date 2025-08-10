/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PlayerHeadBlock
extends SkullBlock {
    public static final MapCodec<PlayerHeadBlock> CODEC = PlayerHeadBlock.simpleCodec(PlayerHeadBlock::new);

    public MapCodec<PlayerHeadBlock> codec() {
        return CODEC;
    }

    protected PlayerHeadBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.PLAYER, $$0);
    }
}

