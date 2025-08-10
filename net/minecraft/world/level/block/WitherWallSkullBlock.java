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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WitherWallSkullBlock
extends WallSkullBlock {
    public static final MapCodec<WitherWallSkullBlock> CODEC = WitherWallSkullBlock.simpleCodec(WitherWallSkullBlock::new);

    public MapCodec<WitherWallSkullBlock> codec() {
        return CODEC;
    }

    protected WitherWallSkullBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.WITHER_SKELETON, $$0);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        WitherSkullBlock.checkSpawn($$0, $$1);
    }
}

