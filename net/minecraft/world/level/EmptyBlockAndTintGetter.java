/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class EmptyBlockAndTintGetter
extends Enum<EmptyBlockAndTintGetter>
implements BlockAndTintGetter {
    public static final /* enum */ EmptyBlockAndTintGetter INSTANCE = new EmptyBlockAndTintGetter();
    private static final /* synthetic */ EmptyBlockAndTintGetter[] $VALUES;

    public static EmptyBlockAndTintGetter[] values() {
        return (EmptyBlockAndTintGetter[])$VALUES.clone();
    }

    public static EmptyBlockAndTintGetter valueOf(String $$0) {
        return Enum.valueOf(EmptyBlockAndTintGetter.class, $$0);
    }

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        return 1.0f;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }

    @Override
    public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        return -1;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    private static /* synthetic */ EmptyBlockAndTintGetter[] a() {
        return new EmptyBlockAndTintGetter[]{INSTANCE};
    }

    static {
        $VALUES = EmptyBlockAndTintGetter.a();
    }
}

