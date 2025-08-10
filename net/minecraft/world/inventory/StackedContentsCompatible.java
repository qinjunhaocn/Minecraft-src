/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.StackedItemContents;

@FunctionalInterface
public interface StackedContentsCompatible {
    public void fillStackedContents(StackedItemContents var1);
}

