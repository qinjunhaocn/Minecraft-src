/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class FallingBlockRenderState
extends EntityRenderState
implements BlockAndTintGetter {
    public BlockPos startBlockPos = BlockPos.ZERO;
    public BlockPos blockPos = BlockPos.ZERO;
    public BlockState blockState = Blocks.SAND.defaultBlockState();
    @Nullable
    public Holder<Biome> biome;
    public BlockAndTintGetter level = EmptyBlockAndTintGetter.INSTANCE;

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        return this.level.getShade($$0, $$1);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        if (this.biome == null) {
            return -1;
        }
        return $$1.getColor(this.biome.value(), $$0.getX(), $$0.getZ());
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        if ($$0.equals(this.blockPos)) {
            return this.blockState;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return this.getBlockState($$0).getFluidState();
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getMinY() {
        return this.blockPos.getY();
    }
}

