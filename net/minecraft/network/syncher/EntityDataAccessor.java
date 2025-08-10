/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.syncher;

import net.minecraft.network.syncher.EntityDataSerializer;

public record EntityDataAccessor<T>(int id, EntityDataSerializer<T> serializer) {
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || ((Object)((Object)this)).getClass() != $$0.getClass()) {
            return false;
        }
        EntityDataAccessor $$1 = (EntityDataAccessor)((Object)$$0);
        return this.id == $$1.id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "<entity data: " + this.id + ">";
    }
}

