/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public interface ServerLevelAccessor
extends LevelAccessor {
    public ServerLevel getLevel();

    default public void addFreshEntityWithPassengers(Entity $$0) {
        $$0.getSelfAndPassengers().forEach(this::addFreshEntity);
    }
}

