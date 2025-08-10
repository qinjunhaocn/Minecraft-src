/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage;

import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.WorldData;

public record LevelDataAndDimensions(WorldData worldData, WorldDimensions.Complete dimensions) {
}

