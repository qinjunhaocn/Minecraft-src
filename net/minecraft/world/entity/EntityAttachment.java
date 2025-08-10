/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public final class EntityAttachment
extends Enum<EntityAttachment> {
    public static final /* enum */ EntityAttachment PASSENGER = new EntityAttachment(Fallback.AT_HEIGHT);
    public static final /* enum */ EntityAttachment VEHICLE = new EntityAttachment(Fallback.AT_FEET);
    public static final /* enum */ EntityAttachment NAME_TAG = new EntityAttachment(Fallback.AT_HEIGHT);
    public static final /* enum */ EntityAttachment WARDEN_CHEST = new EntityAttachment(Fallback.AT_CENTER);
    private final Fallback fallback;
    private static final /* synthetic */ EntityAttachment[] $VALUES;

    public static EntityAttachment[] values() {
        return (EntityAttachment[])$VALUES.clone();
    }

    public static EntityAttachment valueOf(String $$0) {
        return Enum.valueOf(EntityAttachment.class, $$0);
    }

    private EntityAttachment(Fallback $$0) {
        this.fallback = $$0;
    }

    public List<Vec3> createFallbackPoints(float $$0, float $$1) {
        return this.fallback.create($$0, $$1);
    }

    private static /* synthetic */ EntityAttachment[] a() {
        return new EntityAttachment[]{PASSENGER, VEHICLE, NAME_TAG, WARDEN_CHEST};
    }

    static {
        $VALUES = EntityAttachment.a();
    }

    public static interface Fallback {
        public static final List<Vec3> ZERO = List.of((Object)Vec3.ZERO);
        public static final Fallback AT_FEET = ($$0, $$1) -> ZERO;
        public static final Fallback AT_HEIGHT = ($$0, $$1) -> List.of((Object)new Vec3(0.0, $$1, 0.0));
        public static final Fallback AT_CENTER = ($$0, $$1) -> List.of((Object)new Vec3(0.0, (double)$$1 / 2.0, 0.0));

        public List<Vec3> create(float var1, float var2);
    }
}

