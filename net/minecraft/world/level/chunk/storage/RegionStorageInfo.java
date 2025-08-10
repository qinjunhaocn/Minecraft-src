/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RegionStorageInfo(String level, ResourceKey<Level> dimension, String type) {
    public RegionStorageInfo withTypeSuffix(String $$0) {
        return new RegionStorageInfo(this.level, this.dimension, this.type + $$0);
    }
}

