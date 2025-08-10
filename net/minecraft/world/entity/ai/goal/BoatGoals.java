/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.ai.goal;

final class BoatGoals
extends Enum<BoatGoals> {
    public static final /* enum */ BoatGoals GO_TO_BOAT = new BoatGoals();
    public static final /* enum */ BoatGoals GO_IN_BOAT_DIRECTION = new BoatGoals();
    private static final /* synthetic */ BoatGoals[] $VALUES;

    public static BoatGoals[] values() {
        return (BoatGoals[])$VALUES.clone();
    }

    public static BoatGoals valueOf(String $$0) {
        return Enum.valueOf(BoatGoals.class, $$0);
    }

    private static /* synthetic */ BoatGoals[] a() {
        return new BoatGoals[]{GO_TO_BOAT, GO_IN_BOAT_DIRECTION};
    }

    static {
        $VALUES = BoatGoals.a();
    }
}

