/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AmethystBlock
extends Block {
    public static final MapCodec<AmethystBlock> CODEC = AmethystBlock.simpleCodec(AmethystBlock::new);

    public MapCodec<? extends AmethystBlock> codec() {
        return CODEC;
    }

    public AmethystBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    protected void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        if (!$$0.isClientSide) {
            BlockPos $$4 = $$2.getBlockPos();
            $$0.playSound(null, $$4, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 0.5f + $$0.random.nextFloat() * 1.2f);
        }
    }
}

