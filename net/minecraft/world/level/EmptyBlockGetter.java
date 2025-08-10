/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class EmptyBlockGetter
extends Enum<EmptyBlockGetter>
implements BlockGetter {
    public static final /* enum */ EmptyBlockGetter INSTANCE = new EmptyBlockGetter();
    private static final /* synthetic */ EmptyBlockGetter[] $VALUES;

    public static EmptyBlockGetter[] values() {
        return (EmptyBlockGetter[])$VALUES.clone();
    }

    public static EmptyBlockGetter valueOf(String $$0) {
        return Enum.valueOf(EmptyBlockGetter.class, $$0);
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
    public int getMinY() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    private static /* synthetic */ EmptyBlockGetter[] a() {
        return new EmptyBlockGetter[]{INSTANCE};
    }

    static {
        $VALUES = EmptyBlockGetter.a();
    }
}

