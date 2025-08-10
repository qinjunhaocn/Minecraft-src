/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class HitResult {
    protected final Vec3 location;

    protected HitResult(Vec3 $$0) {
        this.location = $$0;
    }

    public double distanceTo(Entity $$0) {
        double $$1 = this.location.x - $$0.getX();
        double $$2 = this.location.y - $$0.getY();
        double $$3 = this.location.z - $$0.getZ();
        return $$1 * $$1 + $$2 * $$2 + $$3 * $$3;
    }

    public abstract Type getType();

    public Vec3 getLocation() {
        return this.location;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type MISS = new Type();
        public static final /* enum */ Type BLOCK = new Type();
        public static final /* enum */ Type ENTITY = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{MISS, BLOCK, ENTITY};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

