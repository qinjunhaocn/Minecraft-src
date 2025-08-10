/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ambient;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public abstract class AmbientCreature
extends Mob {
    protected AmbientCreature(EntityType<? extends AmbientCreature> $$0, Level $$1) {
        super((EntityType<? extends Mob>)$$0, $$1);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}

