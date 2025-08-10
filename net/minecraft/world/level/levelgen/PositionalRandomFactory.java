/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public interface PositionalRandomFactory {
    default public RandomSource at(BlockPos $$0) {
        return this.at($$0.getX(), $$0.getY(), $$0.getZ());
    }

    default public RandomSource fromHashOf(ResourceLocation $$0) {
        return this.fromHashOf($$0.toString());
    }

    public RandomSource fromHashOf(String var1);

    public RandomSource fromSeed(long var1);

    public RandomSource at(int var1, int var2, int var3);

    @VisibleForTesting
    public void parityConfigString(StringBuilder var1);
}

