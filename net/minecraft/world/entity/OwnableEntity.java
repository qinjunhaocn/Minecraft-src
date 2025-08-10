/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 */
package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface OwnableEntity {
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference();

    public Level level();

    @Nullable
    default public LivingEntity getOwner() {
        return EntityReference.get(this.getOwnerReference(), this.level(), LivingEntity.class);
    }

    @Nullable
    default public LivingEntity getRootOwner() {
        ObjectArraySet $$0 = new ObjectArraySet();
        LivingEntity $$1 = this.getOwner();
        $$0.add(this);
        while ($$1 instanceof OwnableEntity) {
            OwnableEntity $$2 = (OwnableEntity)((Object)$$1);
            LivingEntity $$3 = $$2.getOwner();
            if ($$0.contains($$3)) {
                return null;
            }
            $$0.add($$1);
            $$1 = $$2.getOwner();
        }
        return $$1;
    }
}

