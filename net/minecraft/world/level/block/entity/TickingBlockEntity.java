/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;

public interface TickingBlockEntity {
    public void tick();

    public boolean isRemoved();

    public BlockPos getPos();

    public String getType();
}

