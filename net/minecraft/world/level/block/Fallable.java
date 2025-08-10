/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface Fallable {
    default public void onLand(Level $$0, BlockPos $$1, BlockState $$2, BlockState $$3, FallingBlockEntity $$4) {
    }

    default public void onBrokenAfterFall(Level $$0, BlockPos $$1, FallingBlockEntity $$2) {
    }

    default public DamageSource getFallDamageSource(Entity $$0) {
        return $$0.damageSources().fallingBlock($$0);
    }
}

