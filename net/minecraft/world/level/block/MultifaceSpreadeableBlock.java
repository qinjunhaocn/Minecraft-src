/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class MultifaceSpreadeableBlock
extends MultifaceBlock {
    public MultifaceSpreadeableBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    public abstract MapCodec<? extends MultifaceSpreadeableBlock> codec();

    public abstract MultifaceSpreader getSpreader();
}

