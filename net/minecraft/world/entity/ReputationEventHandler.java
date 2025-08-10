/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;

public interface ReputationEventHandler {
    public void onReputationEventFrom(ReputationEventType var1, Entity var2);
}

