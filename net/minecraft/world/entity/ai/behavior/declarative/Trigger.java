/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.behavior.declarative;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface Trigger<E extends LivingEntity> {
    public boolean trigger(ServerLevel var1, E var2, long var3);
}

