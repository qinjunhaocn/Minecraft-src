/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;

public interface Attackable {
    @Nullable
    public LivingEntity getLastAttacker();
}

