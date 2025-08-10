/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.entity;

import net.minecraft.world.entity.Entity;

public interface EntityInLevelCallback {
    public static final EntityInLevelCallback NULL = new EntityInLevelCallback(){

        @Override
        public void onMove() {
        }

        @Override
        public void onRemove(Entity.RemovalReason $$0) {
        }
    };

    public void onMove();

    public void onRemove(Entity.RemovalReason var1);
}

