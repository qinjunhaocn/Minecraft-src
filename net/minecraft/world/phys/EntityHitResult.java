/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityHitResult
extends HitResult {
    private final Entity entity;

    public EntityHitResult(Entity $$0) {
        this($$0, $$0.position());
    }

    public EntityHitResult(Entity $$0, Vec3 $$1) {
        super($$1);
        this.entity = $$0;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public HitResult.Type getType() {
        return HitResult.Type.ENTITY;
    }
}

